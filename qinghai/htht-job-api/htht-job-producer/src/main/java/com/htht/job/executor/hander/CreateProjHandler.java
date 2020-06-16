package com.htht.job.executor.hander;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.*;
import com.htht.job.executor.hander.resolvehandler.ResolvePieprjService;
import com.htht.job.executor.util.DubboIpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by zzj on 2018/1/31.
 */
@JobHandler(value = "createProjHandler")
@Service
public class CreateProjHandler extends IJobHandler {
    private static final String RESULT_START = "<result>";
    private static final String RESULT_END = "</result>";
    private static final String windows = "Z:";
    private static final String linux = "/RSData6";
    @Autowired
    private ResolvePieprjService resolvePieprjService;





    public static void main(String[] args) throws IOException {
        ExecProcess processor = new ExecProcess();
        //CmdMessage cmdMsg = processor.execCmd("ping 127.0.0.1",true,"/zzj/data/logs/111.log");
        CmdMessage cmdMsg = processor.execCmd("java -jar /zzj/data/logs/Demo.jar 111 1111", true, "/zzj/data/logs/111.log");
        //ScriptUtil.execCmd("java -jar /zzj/data/logs/Demo.jar 111 1111","/zzj/data/logs/111.log");
    }

    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result = new ResultUtil();
        String deploysyetem= DubboIpUtil.getOsName();

        LinkedHashMap fixmap = triggerParam.getFixedParameter();
        LinkedHashMap dymap = triggerParam.getDynamicParameter();
        //String pan_xml_path = (String) dymap.get("高分辨率影像路径");
        //String mss_xml_path = (String) dymap.get("多光谱影像路径");
        String pieprjPath = (String) dymap.get("工程文件路径");

       // pan_xml_path = this.createPan_xml_path(pan_xml_path).getMsg();

       // mss_xml_path = this.createMss_xml_path(mss_xml_path).getMsg();
        dymap.remove("全色影像索引");
        dymap.remove("全色影像类型");
        dymap.remove("多光谱影像索引");
        dymap.remove("多光谱影像类型");
        dymap.remove("基准影像影响索引");

        String exePath = (String) fixmap.get("执行路径");
        List<String> output = new ArrayList<String>();
        String param = "";
        Iterator<Map.Entry<String, Object>> it = dymap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (entry.getValue() == null) {
                param += " ";


            } else {

                String value = entry.getValue().toString();
                /*if ((entry.getKey()).equals("高分辨率影像路径")) {
                    value = pan_xml_path;
                }
                if ((entry.getKey()).equals("多光谱影像路径")) {
                    value = mss_xml_path;

                }*/
                if (deploysyetem.equals("windows")) {
                    param += value.replaceAll(linux, windows).replaceAll("/", "\\\\") + " ";
                } else {
                    param += value.replaceAll(windows, linux).replaceAll("\\\\", "/") + " ";
                }
            }

        }
        exePath = exePath + " " + param;


        String outputLog=triggerParam.getLogFileName();
        XxlJobLogger.logByfileNoname(outputLog, exePath);
        int exitValue = ScriptUtil.execCmd(exePath, outputLog);
        if (exitValue != 0) {
            ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, triggerParam.getParallelLogId());
            return stopResult;
        } else {
            List<String> results = new ArrayList<String>();
            results.add(pieprjPath);


            List<String> projList = resolvePieprjService.execute(pieprjPath);
            results.addAll(projList);
            System.out.println(projList);
            triggerParam.setOutput(results);
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

    public void addResult(String newMsg, List<String> results) {
        if (newMsg.indexOf(RESULT_START) != -1 && newMsg.indexOf(RESULT_END) != -1) {
            int startIndex = 0, endIndex = 0;
            while (newMsg != null && newMsg.length() > 0 && newMsg.indexOf(RESULT_END) > 0) {
                startIndex = newMsg.indexOf(RESULT_START) + RESULT_START.length();
                endIndex = newMsg.indexOf(RESULT_END);
                // returnResult
                String returnResult = newMsg.substring(startIndex, endIndex);
                results.add(returnResult);
                // 移除第一个参数值
                newMsg = newMsg.substring(newMsg.indexOf(RESULT_END) + RESULT_END.length());
            }
        }
    }

    public ReturnT<String> createPan_xml_path(String pan_xml_path) {
        //存储全色影像路径list
        ArrayList<String> arrayList = new ArrayList<>();

        File rootPanPath = new File(pan_xml_path);
        if (rootPanPath.exists() && rootPanPath.isDirectory()) {
            List<File> files = FileUtil.getAllFiles(pan_xml_path, "*.TIFF");
            for (File childFile : files) {
                String name = childFile.getName();
                String upperCaseName = name.toUpperCase();
                if (upperCaseName.contains("PAN") && upperCaseName.endsWith(".TIFF")) {
                    //获取文件绝对路径
                    String absolutePath = childFile.getAbsolutePath();
                    arrayList.add(absolutePath);
                }
            }
            //将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
            String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("pan_path.xml", pan_xml_path, arrayList);
            return new ReturnT<>(200, testCreateMssPathXml);
        } else {
            return new ReturnT<>(500, "文件夹路径不存在");
        }
    }

    public ReturnT<String> createMss_xml_path(String mss_xml_path) {
        //存储全色影像路径list
        ArrayList<String> arrayList = new ArrayList<>();

        File rootPanPath = new File(mss_xml_path);
        if (rootPanPath.exists() && rootPanPath.isDirectory()) {
            List<File> files = FileUtil.getAllFiles(mss_xml_path, "*.tiff");
            for (File childFile : files) {
                String name = childFile.getName();
                String upperCaseName = name.toUpperCase();
               // if (upperCaseName.contains("MSS") && upperCaseName.endsWith(".TIFF")) {
                    //获取文件绝对路径
                    String absolutePath = childFile.getAbsolutePath();
                    arrayList.add(absolutePath);
               // }

            }
            //将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
            String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("mss_path.xml", mss_xml_path, arrayList);
            return new ReturnT<>(200, testCreateMssPathXml);
        } else {
            return new ReturnT<>(500, "文件夹路径不存在");
        }
    }

}
