package com.htht.job.executor.service;

import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.LogResult;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.PropertiesUtil;
import com.htht.job.core.util.ZipFileUtil;
import com.htht.job.executor.hander.mq.StartQueueingService;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.shard.BroadCastService;
import com.htht.job.executor.util.DubboIpUtil;
import com.htht.job.executor.util.monitor.SystemInfo;
import com.htht.job.vo.NodeMonitor;
import org.hyperic.sigar.Sigar;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuxueli on 17/3/1.
 */
@Service("executorBiz")
public class ExecutorBizImpl implements ExecutorBiz {
    private static Logger logger = LoggerFactory.getLogger(ExecutorBizImpl.class);
    private static ConcurrentHashMap<String, Sigar> xSigar = new ConcurrentHashMap<>();
    @Autowired
    private RedisService redisService;
    @Autowired
    private BroadCastService broadCastService;
    @Autowired
    private StartQueueingService startQueueingService;


    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> idleBeat(int jobId) {

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> kill(int jobId) {
        // kill handlerThread, and create new one

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "job thread aleady killed.");
    }

    @Override
    public ReturnT<LogResult> log(long logDateTim, int logId, int fromLineNum) {
        // log filename: yyyy-MM-dd/9999.log
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(logDateTim), logId);

        LogResult logResult = XxlJobFileAppender.readLog(logFileName, fromLineNum);
        return new ReturnT<>(logResult);
    }

    @Override
    public ReturnT<LogResult> logbypath(long logDateTim, int logId, int fromLineNum, String logFileName) {
        // log filename: yyyy-MM-dd/9999.log

        LogResult logResult = XxlJobFileAppender.readLogbypath(logFileName, fromLineNum);
        return new ReturnT<>(logResult);
    }

    @Override
    public ReturnT<LogResult> logbypath(long logDateTim, String parallelLogId, int fromLineNum) {
        // log filename: yyyy-MM-dd/9999.log
        String path = PropertiesUtil.getString("cluster.job.executor.logpath");
        Date date = new Date(logDateTim);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowFormat = sdf.format(date);
        String logFileName = path + nowFormat + '/' + parallelLogId + ".log";

        LogResult logResult = XxlJobFileAppender.readLogbypath(logFileName, fromLineNum);
        return new ReturnT<>(logResult);
    }

    @Override
    public ReturnT<LogResult> logbypath(String logFilePath, int fromLineNum) {
        LogResult logResult = XxlJobFileAppender.readLogbypath(logFilePath, fromLineNum);
        return new ReturnT<>(logResult);
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        return startQueueingService.run(triggerParam);
    }

    @Override
    public ReturnT<String> deployAlgo(String uploadAlgoPath, String fileName, String executePath) {
        ReturnT<String> executeResult = null;
        try {
            uploadAlgoPath = uploadAlgoPath + '/' + fileName;
            File executeFolder = new File(executePath);
            if (!executeFolder.exists()) {
                executeFolder.mkdirs();
            }
            Path uploadPath = Paths.get(uploadAlgoPath);
            Path downPath = Paths.get(executePath + '/' + fileName);
            //删除节点算法zip包
            File file = new File(executePath + '/' + fileName);
            Files.deleteIfExists(file.toPath());
            //删除节点算法文件夹
            File folder = new File(executePath + '/' + fileName.substring(0, fileName.indexOf('.')));
            if (folder.exists()) {
                FileUtil.delDir(folder.getAbsolutePath());
            }
            logger.debug("上传路径{}",uploadPath);
            logger.debug("下载路径{}",downPath);
            Files.copy(uploadPath, downPath);
            ZipFileUtil.decompressZipFiles(executePath, executePath + "/" + fileName);
            executeResult = new ReturnT<>(200, "部署成功");
        } catch (Exception e) {
            executeResult = new ReturnT<>(500, "部署失败");
            logger.error(e.getMessage(),e);
        }
        return executeResult;
    }

    @Override
    public ReturnT<String> getOs() {
        String osName = DubboIpUtil.getOsName();
        return new ReturnT<>(osName);
    }

    @Override
    public ReturnT<NodeMonitor> getSystemMessage() {
        ReturnT<NodeMonitor> returnT = new ReturnT<>();
        try {
            synchronized (this) {
                String ip = DubboIpUtil.getIp();
                NodeMonitor monitor = (NodeMonitor) redisService.get(ip + "SystemInfoT");
                if (monitor == null) {
                    monitor = new NodeMonitor();
                } else {
                    returnT.setContent(monitor);
                    return returnT;
                }
                Sigar sigar = xSigar.get(ip);
                if (null == sigar) {
                    String file = this.getClass().getClassLoader().getResource("sigar/.sigar_shellrc").getFile();
                    File classPath = new File(file).getParentFile();
                    System.setProperty("java.library.path", classPath.getCanonicalPath());
                    sigar = new Sigar();
                    xSigar.put(ip, sigar);
                }
                Map<String, Long> systemInfo = SystemInfo.usage(sigar);
                monitor.setHardDiskUsage(systemInfo.get("diskUsage"));
                monitor.setCpuUsage(systemInfo.get("cpuUsage"));
                monitor.setMemoryUsage(systemInfo.get("ramUsage"));
                monitor.setJvmUsage(systemInfo.get("jvmUsage"));
                returnT.setContent(monitor);
                redisService.set(ip + "SystemInfoT", monitor, 4L);
            }
        } catch (Exception e) {
            NodeMonitor monitor = new NodeMonitor();
            returnT.setContent(monitor);
            return returnT;
        }
        return returnT;


    }

    @Override
    public void finAllExeuteList(TriggerParam triggerParam, ProcessStepsDTO processStepsDTO, List<CommonParameter> flowParams,
                                 int jobId, AtomicAlgorithmDTO atomicAlgorithmDTO, String dynamicParameter) {
        try {
            broadCastService.execute(triggerParam, processStepsDTO, flowParams, jobId, atomicAlgorithmDTO, dynamicParameter);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
