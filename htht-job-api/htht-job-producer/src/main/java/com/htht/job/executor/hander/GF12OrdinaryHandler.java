package com.htht.job.executor.hander;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.CmdMessage;
import com.htht.job.core.util.ExecProcess;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ScriptUtil;
import com.htht.job.executor.util.DubboIpUtil;
import com.mysql.jdbc.StringUtils;

@JobHandler(value = "GF12OrdinaryHandler")
@Service
public class GF12OrdinaryHandler extends IJobHandler {
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
        String projectPath = "";
        String ImgMosaicFormat = "";
        String readChildFolder = "";
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
                    	if("projectPath".equals(entry.getKey())) {
                    		projectPath = entry.getValue().toString();
                    		//正则匹配14位时间数字
                    		String regEx = "^\\d{14}$";
                    		Pattern compile = Pattern.compile(regEx);
                    		String str = entry.getValue().toString();
                    		Matcher matcher = compile.matcher(str);
                    		if(matcher.matches()) {
                    			param += entry.getValue().toString().replaceAll(linux, windows).replaceAll("/", "\\\\") + " ";
                    		}else {
                    	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    	    	Date date = new Date();
                    	    	String format = simpleDateFormat.format(date);
                    	    	
                    	        String replace = projectPath.replace("\\", "/");
                    	        String name = replace.substring(replace.lastIndexOf("/")+1, replace.indexOf(".PIEPrj"));
                    	        
                    	        projectPath = replace.replace(".PIEPrj", format+".PIEPrj");
                    	        projectPath = projectPath.replace(name+format+".PIEPrj", "PIEPrj/"+name+format+".PIEPrj");
                    	        param += projectPath.replaceAll(linux, windows).replaceAll("/", "\\\\") + " ";
                    		}
                    	}else if("镶嵌输出格式".equals(entry.getKey())) {
                    		ImgMosaicFormat = entry.getValue().toString();
                    	}else if("镶嵌输出路径".equals(entry.getKey())){
                            if(!StringUtils.isNullOrEmpty(projectPath)) {
                            	projectPath = projectPath.replaceAll("\\\\", "/");
                            	projectPath = projectPath.substring(projectPath.lastIndexOf("/")+1);
                            	projectPath = projectPath.substring(0, projectPath.length()-7);
                            	File file = new File(entry.getValue()+"/mosaic/");
                            	if(!file.exists()) {
                            		file.mkdirs();
                            	}
                            	projectPath =entry.getValue()+"/mosaic/"+projectPath+"_mosaic.";
                            	if(!StringUtils.isNullOrEmpty(ImgMosaicFormat)) {
                            		projectPath += ImgMosaicFormat;
                            	}else {
                            		projectPath += "img";
                            	}
                            	param += projectPath +" ";
                            }
                    	}else {
                    		if("数据文件目录".equals(entry.getKey())) {
                    			readChildFolder = entry.getValue().toString();
                    		}
                    		param += entry.getValue().toString().replaceAll(linux, windows).replaceAll("/", "\\\\") + " ";
                    	}
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
                List<String> scanList = new ArrayList<>();
                this.addResult(out, results);
                //扫描未进行处理的影像
                if(!StringUtils.isNullOrEmpty(readChildFolder)) {
                	readChildFolder = readChildFolder.replace("\\", "/");
                	readChildFolder += "/scanFolder";
                	File readFolder = new File(readChildFolder);
                	if(!readFolder.exists()) {
                		readFolder.mkdirs();
                	}
                	String string = results.get(0);
                	String[] split = string.split("#HT#");
                	for (String fileName : split) {
                		 fileName = fileName.replace("\\", "/");
                		 String substring = fileName.substring(fileName.lastIndexOf("/")+1, fileName.length());
                		List<File> allFiles = FileUtil.getAllFiles(readChildFolder, substring);
                        if(allFiles.size() == 0) {
                        	scanList.add(fileName);
                        	File file = new File(readChildFolder+"/"+substring);
                        	try {
    							file.createNewFile();
    						} catch (IOException e) {
    							e.printStackTrace();
    						}
                        }
					}
                    if(scanList.size() == 0 && results.size() != 0) {
                    	return new ReturnT<>(4,"未找到没有处理的影像");
                    }
                }
                if(scanList.size()!=0) {
                	 List<String> newScanList = new ArrayList<>();
                	 StringBuffer stringBuffer = new StringBuffer();
                	 for(int i=0;i<scanList.size();i++) {
                		 if(i==0) {
                			 stringBuffer.append(scanList.get(i));
                		 }else {
                			 stringBuffer.append("#HT#"+scanList.get(i));
                		 }
                	 }
                	 newScanList.add(0, stringBuffer.toString());
                	triggerParam.setOutput(newScanList);
                }else {
                	triggerParam.setOutput(results);
                }

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
            }else {
            	if(!projectPath.equals("")) {
            		List<String> results = new ArrayList<String>();
            		 results.add(projectPath);
            		 triggerParam.setOutput(results);
            	}
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
