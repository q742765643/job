package com.htht.job.core.log;

import com.htht.job.core.util.DateConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xuxueli on 17/4/28.
 */
public class XxlJobLogger {
    private static Logger logger = LoggerFactory.getLogger("xxl-job logger");

    private XxlJobLogger() {
    }

    /**
     * append log
     *
     * @param appendLog
     */
    public static void log(String appendLog) {
        SimpleDateFormat xxlJobLoggerFormat = new SimpleDateFormat(DateConstant.YYYY_MM_DD_HH_MM_SS);
        // logFileName
        String logFileName = XxlJobFileAppender.contextHolder.get();
        if (logFileName == null || logFileName.trim().length() == 0) {
            return;
        }

        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement callInfo = stackTraceElements[1];

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(xxlJobLoggerFormat.format(new Date())).append(" ")
                .append("[" + callInfo.getClassName() + "]").append("-")
                .append("[" + callInfo.getMethodName() + "]").append("-")
                .append("[" + callInfo.getLineNumber() + "]").append("-")
                .append("[" + Thread.currentThread().getName() + "]").append(" ")
                .append(appendLog != null ? appendLog : "");
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        XxlJobFileAppender.appendLog(logFileName, formatAppendLog);
    }

    public static void logp(String appendLog) {
        SimpleDateFormat xxlJobLoggerFormat = new SimpleDateFormat(DateConstant.YYYY_MM_DD_HH_MM_SS);
        // logFileName
        String logFileName = XxlJobFileAppender.contextHolder.get();
        if (logFileName == null || logFileName.trim().length() == 0) {
            return;
        }

        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement callInfo = stackTraceElements[1];

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(xxlJobLoggerFormat.format(new Date())).append(" ")
                .append("[" + callInfo.getClassName() + "]").append("-")
                .append("[" + callInfo.getMethodName() + "]").append("-")
                .append("[" + callInfo.getLineNumber() + "]").append("-")
                .append("[" + Thread.currentThread().getName() + "]").append(" ")
                .append(appendLog != null ? appendLog : "");
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        XxlJobFileAppender.appendLogBypath(logFileName, formatAppendLog);
    }

    public static void logByfile(String logFileName, String appendLog) {
        SimpleDateFormat xxlJobLoggerFormat = new SimpleDateFormat(DateConstant.YYYY_MM_DD_HH_MM_SS);
        // logFileName
        if (logFileName == null || logFileName.trim().length() == 0) {
            return;
        }

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(xxlJobLoggerFormat.format(new Date())).append(" ")
                .append(appendLog != null ? appendLog : "");
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        XxlJobFileAppender.appendLogBypath(logFileName, formatAppendLog);

        logger.debug(">>>>>>>>>>> [{}]: {}", logFileName, formatAppendLog);
    }

    public static void logByfileNoname(String logFileName, String appendLog) {

        // logFileName
        if (logFileName == null || logFileName.trim().length() == 0) {
            return;
        }

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(appendLog);
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        XxlJobFileAppender.appendLogBypath(logFileName, formatAppendLog);

        logger.debug(">>>>>>>>>>> [{}]: {}", logFileName, formatAppendLog);
    }

    /**
     * append log with pattern
     *
     * @param appendLogPattern   like "aaa {0} bbb {1} ccc"
     * @param appendLogArguments like "111, true"
     * @
     */
    public static void log(String appendLogPattern, Object... appendLogArguments) {
        String appendLog = appendLogPattern;
        if (appendLogArguments != null && appendLogArguments.length > 0) {
            appendLog = MessageFormat.format(appendLogPattern, appendLogArguments);
        }

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        logDetail(callInfo, appendLog);
    }

    public static void log(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String appendLog = stringWriter.toString();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        logDetail(callInfo, appendLog);
    }

    private static void logDetail(StackTraceElement callInfo, String appendLog) {

        SimpleDateFormat xxlJobLoggerFormat = new SimpleDateFormat(DateConstant.YYYY_MM_DD_HH_MM_SS);

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(xxlJobLoggerFormat.format(new Date())).append(" ")
                .append("[" + callInfo.getClassName() + "#" + callInfo.getMethodName() + "]").append("-")
                .append("[" + callInfo.getLineNumber() + "]").append("-")
                .append("[" + Thread.currentThread().getName() + "]").append(" ")
                .append(appendLog != null ? appendLog : "");
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        String logFileName = XxlJobFileAppender.contextHolder.get();
        if (logFileName != null && logFileName.trim().length() > 0) {
            XxlJobFileAppender.appendLog(logFileName, formatAppendLog);
        } else {
            logger.info(">>>>>>>>>>> {}", formatAppendLog);
        }
    }

}
