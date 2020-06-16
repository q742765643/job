package com.htht.job.core.biz;

import com.htht.job.core.biz.model.LogResult;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.vo.NodeMonitor;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {

    /**
     * beat
     * @return
     */
    public ReturnT<String> beat();

    /**
     * idle beat
     *
     * @param jobId
     * @return
     */
    public ReturnT<String> idleBeat(int jobId);

    /**
     * kill
     * @param jobId
     * @return
     */
    public ReturnT<String> kill(int jobId);

    /**
     * log
     * @param logDateTim
     * @param logId
     * @param fromLineNum
     * @return
     */
    public ReturnT<LogResult> log(long logDateTim, int logId, int fromLineNum);

    /**
     * run
     * @param triggerParam
     * @return
     */
    public ReturnT<String> run(TriggerParam triggerParam);

    public ReturnT<LogResult> logbypath(long logDateTim, int logId, int fromLineNum, String logFileName);

    public ReturnT<LogResult> logbypath(long logDateTim, String parallelLogId, int fromLineNum);

	public ReturnT<LogResult> logbypath( String logFilePath,int fromLineNum);

    public ReturnT<String> deployAlgo(String uploadAlgoPath, String fileName,String executePath);
    
    public ReturnT<String> getOs();

    public ReturnT<NodeMonitor> getSystemMessage();

    public ReturnT<String> isAvailable();


}
