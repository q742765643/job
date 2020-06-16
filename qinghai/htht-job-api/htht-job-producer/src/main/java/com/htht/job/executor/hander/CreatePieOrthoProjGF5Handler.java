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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zzj on 2018/1/31.
 */
@JobHandler(value = "CreatePieOrthoProjGF5Handler")
@Service
public class CreatePieOrthoProjGF5Handler extends IJobHandler {
    private static final String RESULT_START = "<result>";
    private static final String RESULT_END = "</result>";
    private static final String windows = "Z:";
    private static final String linux = "/RSData6";
    @Autowired
    private ResolvePieprjService resolvePieprjService;





    public static void main(String[] args) throws IOException {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	Date date = new Date();
    	String format = simpleDateFormat.format(date);
    	System.out.println(format);
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
        String mss_xml_path = (String) dymap.get("高光谱影像路径集合xml");
        String pieprjPath = (String) dymap.get("projectPath");
        String outPath = (String) dymap.get("正射影像输出路径");
        
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	Date date = new Date();
    	String format = simpleDateFormat.format(date);
    	
        String replace = pieprjPath.replace("\\", "/");
        String name = replace.substring(replace.lastIndexOf("/")+1, replace.indexOf(".PIEPrj"));
       
        pieprjPath = replace.replace(".PIEPrj", format+".PIEPrj");
        pieprjPath = pieprjPath.replace(name+format+".PIEPrj", "PIEPrj/"+name+format+".PIEPrj");
        outPath = outPath +"/"+name+"/"+format;

        if(!mss_xml_path.contains(".xml")) {
        	ReturnT<String> createMss_xml_path = this.createMss_xml_path(mss_xml_path,"/tiffXml/"+name+"/"+format);
        	if(createMss_xml_path.getCode() == 4) {
        		return createMss_xml_path;
        	}else {
        		mss_xml_path = createMss_xml_path.getMsg();
        	}
        }
        
        dymap.remove("mss_image_index");
        dymap.remove("mss_image_type");
        dymap.remove("ref_image_index");

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

                if ((entry.getKey()).equals("高光谱影像路径集合xml")) {
                    value = mss_xml_path;
                }
                if((entry.getKey()).equals("projectPath")) {
                	value = pieprjPath;
                }
                if((entry.getKey()).equals("正射影像输出路径")) {
                	value = outPath;
                }
                
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

            List<String> projList = resolvePieprjService.executeGF5(pieprjPath);
            results.addAll(projList);
            System.out.println(projList);
            triggerParam.setOutput(results);
        }

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

    public ReturnT<String> createMss_xml_path(String mss_xml_path, String dateFolder) {
        //存储全色影像路径list
        ArrayList<String> arrayList = new ArrayList<>();

        File rootPanPath = new File(mss_xml_path);
        if (rootPanPath.exists() && rootPanPath.isDirectory()) {
        	String scanFolderPath = mss_xml_path + dateFolder;
        	scanFolderPath = scanFolderPath.replace("\\", "/");
        	scanFolderPath = scanFolderPath.substring(0, scanFolderPath.lastIndexOf("/")+1)+"scanFolder";
        	File scanFolder = new File(scanFolderPath);
        	if(!scanFolder.exists()) {
        		scanFolder.mkdirs();
        	}
        	
            List<File> files = FileUtil.getAllFiles(mss_xml_path, "*.tiff");
            for (File childFile : files) {
                String name = childFile.getName();
                String upperCaseName = name.toUpperCase();
                String[] split = upperCaseName.split("\\.");
                if (upperCaseName.contains("MSS") && upperCaseName.endsWith(".TIFF")) {
                    //获取文件绝对路径
                    String absolutePath = childFile.getAbsolutePath();
                    List<File> allFiles = FileUtil.getAllFiles(scanFolderPath, upperCaseName);
                    if(allFiles.size() == 0) {
                    	arrayList.add(absolutePath);
                    	File file = new File(scanFolderPath+"/"+upperCaseName);
                    	try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
                    }
                }else if(upperCaseName.contains("GF5_AHSI") && split.length ==4 && upperCaseName.endsWith(".TIFF")) {
                    //获取文件绝对路径
                    String absolutePath = childFile.getAbsolutePath();
                    List<File> allFiles = FileUtil.getAllFiles(scanFolderPath, upperCaseName);
                    if(allFiles.size() == 0) {
                    	arrayList.add(absolutePath);
                    	File file = new File(scanFolderPath+"/"+upperCaseName);
                    	try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
                    }
				}

            }
            if(arrayList.size() == 0) {
            	return new ReturnT<>(4,"未找到没有处理的影像");
            }
            //将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
            String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("mss_path.xml", mss_xml_path+dateFolder, arrayList);
            return new ReturnT<>(200, testCreateMssPathXml);
        } else {
            return new ReturnT<>(500, "文件夹路径不存在");
        }
    }

}
