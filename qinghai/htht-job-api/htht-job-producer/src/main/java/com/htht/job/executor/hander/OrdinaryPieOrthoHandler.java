package com.htht.job.executor.hander;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.*;

import com.htht.job.executor.util.DubboIpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zzj on 2018/1/31.
 */
@JobHandler(value = "ordinaryPieOrthoHandler")
@Service
public class OrdinaryPieOrthoHandler extends IJobHandler {
    private static final String RESULT_START = "<result>";
    private static final String RESULT_END = "</result>";
    private static final String windows = "Z:";
    private static final String linux = "/RSData6";


    @Value("${cluster.job.executor.logpath}")
    private String logpath;
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result=new 	ResultUtil();
        String deploysyetem= DubboIpUtil.getOsName();
        LinkedHashMap fixmap = triggerParam.getFixedParameter();
        LinkedHashMap dymap=triggerParam.getDynamicParameter();

        String exePath = (String) fixmap.get("执行路径");
        List<String> output=new ArrayList<String>();
        String param="";
      Iterator<Map.Entry<String, Object>> it = dymap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if(entry.getValue()==null){
                if(exePath.indexOf("AlgoLoader")>=0){
                    param+= "|";
                }else if(exePath.indexOf(".jar")>=0){
                    param+= "\"\""+" ";
                }else{
                    param+=" ";
                }


            }else{
                if(exePath.indexOf("AlgoLoader")>=0){
                    if(deploysyetem.equals("windows")){
                        param+= entry.getValue().toString().replaceAll(linux,windows).replaceAll("/", "\\\\")+"|";
                    }else{
                        param+= entry.getValue().toString().replaceAll(windows,linux).replaceAll("\\\\","/")+"|";
                    }
                }else {
                    if(deploysyetem.equals("windows")) {
                        param += entry.getValue().toString().replaceAll(linux, windows).replaceAll("/", "\\\\") + " ";
                    }else{
                        param += entry.getValue().toString().replaceAll(windows, linux).replaceAll("\\\\", "/") + " ";
                    }
                }
            }
        }
        String outputLog=triggerParam.getLogFileName();
        if(deploysyetem.equals("linux")){
            ScriptUtil.execCmd("chmod 555 "+exePath,outputLog);
            XxlJobLogger.logByfileNoname(outputLog, "chmod 555 "+exePath);

        }
        if(exePath.indexOf(".jar")>=0){
            exePath ="java -Dfile.encoding=utf-8 -jar "+exePath +" "+param;
        }else if(exePath.indexOf("AlgoLoader")>=0){
            String[] expaths=exePath.split(" ");
            exePath="\""+expaths[0]+"\""+" "+triggerParam.getParallelLogId()+" "+expaths[1]+" "+"\""+param.substring(0,param.length()-1)+"\"";
        }else{
            exePath=exePath +" "+param;
        }

        XxlJobLogger.logByfileNoname(outputLog, exePath);
        int exitValue=ScriptUtil.execCmd(exePath,outputLog);
        if(exePath.indexOf(".jar")>=0){
            if(exitValue==0){
                String out =XxlJobFileAppender.readLogResult(outputLog);
                List<String> results = new ArrayList<String>();
                this.addResult(out, results);
                triggerParam.setOutput(results);

            }else{
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, triggerParam.getParallelLogId());
                return stopResult;
            }

        }else if(exePath.indexOf("AlgoLoader")>=0){
            if (exitValue == 0) {
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, triggerParam.getParallelLogId());
                return stopResult;

            }
        }else{
            if(exitValue != 0){
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, triggerParam.getParallelLogId());
                return stopResult;
            }
        }
       /* ExecProcess processor = new ExecProcess();
        CmdMessage cmdMsg = processor.execCmd(exePath,true,outputLog);
        if (exePath.indexOf(".jar")>=0) {
            if (cmdMsg.getCode() == 0) {
                String out = cmdMsg.getOutput();
                List<String> resultss = new ArrayList<String>();
                this.addResult(out, results);
                triggerParam.setOutput(results);
            } else {
                String out = cmdMsg.getError();
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, out);
                return stopResult;

            }
        }else if(exePath.indexOf("AlgoLoader")>=0){
            if (cmdMsg.getCode() == 0) {
                String out = cmdMsg.getError();
                if(StringUtils.isEmpty(out)){
                    out="AlgoLoader执行出错";
                }
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, out);
                return stopResult;

            }
        }else {
            if (cmdMsg.getCode() == 1) {
                String out = cmdMsg.getError();
                if(StringUtils.isEmpty(out)){
                    out="执行出错";
                }
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, out);
                return stopResult;

            }
        }*/
        //Thread.sleep(10000);
        return ReturnT.SUCCESS;
    }

    public void addResult(String newMsg,List<String> results){
        if(newMsg.indexOf(RESULT_START) != -1 && newMsg.indexOf(RESULT_END) != -1){
            int startIndex = 0,endIndex = 0;
            while (newMsg != null && newMsg.length() > 0 && newMsg.indexOf(RESULT_END) > 0) {
                startIndex = newMsg.indexOf(RESULT_START) + RESULT_START.length();
                endIndex = newMsg.indexOf(RESULT_END);
                // returnResult
                String returnResult = newMsg.substring(startIndex,endIndex);
                results.add(returnResult);
                // 移除第一个参数值
                newMsg = newMsg.substring(newMsg.indexOf(RESULT_END) + RESULT_END.length());
            }
        }
    }
    public static void main(String[] args) throws IOException {
        ExecProcess processor = new ExecProcess();
        //CmdMessage cmdMsg = processor.execCmd("ping 127.0.0.1",true,"/zzj/data/logs/111.log");
        CmdMessage cmdMsg = processor.execCmd("java -jar /zzj/data/logs/Demo.jar 111 1111",true,"/zzj/data/logs/111.log");
        //ScriptUtil.execCmd("java -jar /zzj/data/logs/Demo.jar 111 1111","/zzj/data/logs/111.log");
    }
}
