package com.htht.job.admin.core.thread;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.htht.job.admin.core.util.MailUtil;
import com.htht.job.core.biz.model.ReturnT;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * job monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper {
    private static Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);

    private static JobFailMonitorHelper instance = new JobFailMonitorHelper();
    private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(0xfff8);
    private Thread monitorThread;
    private volatile boolean toStop = false;

    public static JobFailMonitorHelper getInstance() {
        return instance;
    }

    // producer
    public static void monitor(int jobLogId) {
        boolean flag =getInstance().queue.offer(jobLogId);
        if(!flag){
            logger.error("job monitor false");
        }
    }

    public void start() {
        monitorThread = new Thread(()-> {
                // monitor
                while (!toStop) {
                    try {
                        List<Integer> jobLogIdList = new ArrayList<>();
                        JobFailMonitorHelper.instance.queue.drainTo(jobLogIdList);
                        JobFailMonitorHelper.getInstance().jobMonitor(jobLogIdList);

                        TimeUnit.SECONDS.sleep(10);
                    } catch (Exception e) {
                        logger.error("job monitor error:{}", e);
                    }
                }

                // monitor all clear
                List<Integer> jobLogIdList = new ArrayList<>();
                getInstance().queue.drainTo(jobLogIdList);
                JobFailMonitorHelper.getInstance().sendFailMail(jobLogIdList);

        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }
    private void jobMonitor(List<Integer> jobLogIdList){
        if (CollectionUtils.isEmpty(jobLogIdList)) {
          return;
        }
        for (Integer jobLogId : jobLogIdList) {
            XxlJobLog log = XxlJobDynamicScheduler.xxlJobLogDao.load(jobLogId);
            if (jobLogId == null || jobLogId == 0 || log == null) {
                continue;
            }
            if (ReturnT.SUCCESS_CODE == log.getTriggerCode() && log.getHandleCode() == 0) {
                JobFailMonitorHelper.monitor(jobLogId);
                logger.info(">>>>>>>>>>> job monitor, job running, JobLogId:{}", jobLogId);
            } else if (ReturnT.SUCCESS_CODE == log.getTriggerCode() && ReturnT.SUCCESS_CODE == log.getHandleCode()) {
                // job success, pass
                logger.info(">>>>>>>>>>> job monitor, job success, JobLogId:{}", jobLogId);
            } else if (ReturnT.FAIL_CODE == log.getTriggerCode() || ReturnT.FAIL_CODE == log.getHandleCode()) {
                // job fail,
                failAlarm(log);
                logger.info(">>>>>>>>>>> job monitor, job fail, JobLogId:{}", jobLogId);
            } else {
                JobFailMonitorHelper.monitor(jobLogId);
                logger.info(">>>>>>>>>>> job monitor, job status unknown, JobLogId:{}", jobLogId);
            }
        }
    }
    private void  sendFailMail(List<Integer> jobLogIdList){
        if (jobLogIdList != null && !jobLogIdList.isEmpty()) {
            for (Integer jobLogId : jobLogIdList) {
                XxlJobLog log = XxlJobDynamicScheduler.xxlJobLogDao.load(jobLogId);
                if (ReturnT.FAIL_CODE == log.getTriggerCode() || ReturnT.FAIL_CODE == log.getHandleCode()) {
                    // job fail,
                    failAlarm(log);
                    logger.info(">>>>>>>>>>> job monitor last, job fail, JobLogId:{}", jobLogId);
                }
            }
        }
    }
    /**
     * fail alarm
     *
     * @param jobLog
     */
    private void failAlarm(XxlJobLog jobLog) {

        // send monitor email
        XxlJobInfo info = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(jobLog.getJobId());
        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {

            Set<String> emailSet = new HashSet<>(Arrays.asList(info.getAlarmEmail().split(",")));
            for (String email : emailSet) {
                String title = "《调度监控报警》(业务支撑系统)";
                String body = " 调度地址:{0}\n" +
                              " 任务id:{1}\n" +
                              " 算法id:{2}\n" +
                              " 任务描述:{3}\n" +
                              " 失败原因:{4}\n";

                String content = MessageFormat.format(body,jobLog.getExecutorAddress(),
                        info.getId(),info.getModelId(), info.getJobDesc(),jobLog.getHandleMsg());
                MailUtil.sendMail(email, title, content, false, null);
            }
        }


    }

    public void toStop(){
        toStop = true;
        // interrupt and wait
        monitorThread.interrupt();

        try {
            monitorThread.join();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }

}
