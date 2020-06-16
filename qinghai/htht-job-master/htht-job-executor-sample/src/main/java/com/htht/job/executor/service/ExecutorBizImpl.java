package com.htht.job.executor.service;

import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.LogResult;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.executor.XxlJobExecutor;
import com.htht.job.core.glue.GlueFactory;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.impl.GlueJobHandler;
import com.htht.job.core.handler.impl.ScriptJobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.thread.JobThread;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.PropertiesUtil;
import com.htht.job.core.util.ZipFileUtil;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.util.DubboIpUtil;
import com.htht.job.executor.util.monitor.SystemInfo;
import com.htht.job.vo.NodeMonitor;

import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuxueli on 17/3/1.
 */
@Service("executorBiz")
public class ExecutorBizImpl implements ExecutorBiz {
    @Autowired
	private RedisService redisService;
    private static Logger logger = LoggerFactory.getLogger(ExecutorBizImpl.class);
    private static ConcurrentHashMap<String, Sigar> xSigar = new ConcurrentHashMap<String, Sigar>();

    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> idleBeat(int jobId) {

        // isRunningOrHasQueue
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = XxlJobExecutor.loadJobThread(jobId);
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        if (isRunningOrHasQueue) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "job thread is running or has trigger queue.");
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> kill(int jobId) {
        // kill handlerThread, and create new one
        JobThread jobThread = XxlJobExecutor.loadJobThread(jobId);
        if (jobThread != null) {
            XxlJobExecutor.removeJobThread(jobId, "人工手动终止");
            return ReturnT.SUCCESS;
        }

        return new ReturnT<String>(ReturnT.SUCCESS_CODE, "job thread aleady killed.");
    }

    @Override
    public ReturnT<LogResult> log(long logDateTim, int logId, int fromLineNum) {
        // log filename: yyyy-MM-dd/9999.log
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(logDateTim), logId);

        LogResult logResult = XxlJobFileAppender.readLog(logFileName, fromLineNum);
        return new ReturnT<LogResult>(logResult);
    }
    @Override
    public ReturnT<LogResult> logbypath(long logDateTim, int logId, int fromLineNum,String logFileName) {
        // log filename: yyyy-MM-dd/9999.log

        LogResult logResult = XxlJobFileAppender.readLogbypath(logFileName, fromLineNum);
        return new ReturnT<LogResult>(logResult);
    }
    @Override
    public ReturnT<LogResult> logbypath(long logDateTim,String parallelLogId, int fromLineNum) {
        // log filename: yyyy-MM-dd/9999.log
            String path= PropertiesUtil.getString("cluster.job.executor.logpath");
            Date date=new Date(logDateTim);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowFormat = sdf.format(date);
            String logFileName=path+nowFormat+"/"+parallelLogId+".log";

        LogResult logResult = XxlJobFileAppender.readLogbypath(logFileName, fromLineNum);
        return new ReturnT<LogResult>(logResult);
    }
    @Override
	public ReturnT<LogResult> logbypath(String logFilePath, int fromLineNum) {
    	 LogResult logResult = XxlJobFileAppender.readLogbypath(logFilePath, fromLineNum);
         return new ReturnT<LogResult>(logResult);
	}
    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        // load old：jobHandler + jobThread
        JobThread jobThread = XxlJobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread!=null?jobThread.getHandler():null;
        String removeOldReason = null;

        // valid：jobHandler + jobThread
        if (GlueTypeEnum.BEAN==GlueTypeEnum.match(triggerParam.getGlueType())) {

            // new jobhandler  获取mq执行器
           // IJobHandler newJobHandler = XxlJobExecutor.loadJobHandler(triggerParam.getExecutorHandler());
        	IJobHandler newJobHandler = XxlJobExecutor.loadJobHandler("mqHandler");
            // valid old jobThread
            if (jobThread!=null && jobHandler != newJobHandler) {
                // change handler, need kill old thread
                removeOldReason = "更换JobHandler或更换任务模式,终止旧任务线程";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = newJobHandler;
                if (jobHandler == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }

        } else if (GlueTypeEnum.GLUE_GROOVY==GlueTypeEnum.match(triggerParam.getGlueType())) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof GlueJobHandler
                        && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                // change handler or gluesource updated, need kill old thread
                removeOldReason = "更新任务逻辑或更换任务模式,终止旧任务线程";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                try {
                    IJobHandler originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
                    jobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatetime());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
                }
            }
        } else if (GlueTypeEnum.GLUE_SHELL==GlueTypeEnum.match(triggerParam.getGlueType())
                || GlueTypeEnum.GLUE_PYTHON==GlueTypeEnum.match(triggerParam.getGlueType())
                || GlueTypeEnum.GLUE_NODEJS==GlueTypeEnum.match(triggerParam.getGlueType())) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof ScriptJobHandler
                            && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                // change script or gluesource updated, need kill old thread
                removeOldReason = "更新任务逻辑或更换任务模式,终止旧任务线程";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            }
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }

        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy(), null);
            /*if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "阻塞处理策略-生效："+ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                // kill running jobThread
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "阻塞处理策略-生效：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }*/
        }

        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = XxlJobExecutor.registJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }

        // push data to queue 往队列中添加任务参数
        ReturnT<String> pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }

	@Override
	public ReturnT<String> deployAlgo(String uploadAlgoPath,String fileName,String executePath) {
		ReturnT<String> executeResult =null;
		try {
			uploadAlgoPath= uploadAlgoPath+"/"+fileName;
			File executeFolder = new File(executePath);
			if (!executeFolder.exists()) {
				executeFolder.mkdirs();
			}
			Path uploadPath = Paths.get(uploadAlgoPath);
			Path downPath = Paths.get(executePath+"/"+fileName);
			//删除节点算法zip包
			File file = new File(executePath+"/"+fileName);
			if(file.exists()){
				file.delete();
			}
			//删除节点算法文件夹
			File folder = new File(executePath+"/"+fileName.substring(0,fileName.indexOf(".")));
			if(folder.exists()){
				FileUtil.Deldir(folder.getAbsolutePath());
			}
			System.out.println("上传路径"+uploadPath);
            System.out.println("下载路径"+downPath);
            Files.copy(uploadPath, downPath);
			ZipFileUtil.decompressZipFiles(executePath,executePath+"/"+fileName);
			executeResult = new ReturnT<>(200, "部署成功");
		} catch (Exception e) {
			executeResult = new ReturnT<>(500, "部署失败");
			e.printStackTrace();
		}
		return executeResult;
	}

	@Override
	public ReturnT<String> getOs() {
		String osName = DubboIpUtil.getOsName();
		return new ReturnT<String>(osName);
	}
    @Override
    public ReturnT<NodeMonitor> getSystemMessage()  {
        ReturnT<NodeMonitor> returnT=new ReturnT<NodeMonitor>();
        try {
            synchronized(this) {
                String ip = DubboIpUtil.getIp();
                NodeMonitor monitor = (NodeMonitor) redisService.get(ip + "SystemInfoT");
                if (monitor == null) {
                    monitor = new NodeMonitor();
                } else {
                    returnT.setContent(monitor);
                    return returnT;
                }
                Sigar sigar=xSigar.get(ip);
                System.out.println("获取sigar");
                if(null==sigar) {
                    String file = this.getClass().getClassLoader().getResource("sigar/.sigar_shellrc").getFile();
                    File classPath = new File(file).getParentFile();
                    System.setProperty("java.library.path", classPath.getCanonicalPath());
                    sigar = new Sigar();
                    xSigar.put(ip,sigar);
                }
                Map<String, Long> systemInfo = SystemInfo.usage(sigar);
                monitor.setHardDiskUsage(systemInfo.get("diskUsage"));
                monitor.setCpuUsage(systemInfo.get("cpuUsage"));
                monitor.setMemoryUsage(systemInfo.get("ramUsage"));
                monitor.setJvmUsage(systemInfo.get("jvmUsage"));
                returnT.setContent(monitor);
                redisService.set(ip + "SystemInfoT", monitor, 4L);
                Thread.sleep(50);
            }
        } catch (Exception e) {
            NodeMonitor monitor=new NodeMonitor();
            returnT.setContent(monitor);
            return returnT;
        }
        return returnT;


    }
    public ReturnT<String> isAvailable(){
        ReturnT<String> returnT=new ReturnT<String>();
        return ReturnT.SUCCESS;
    }

}
