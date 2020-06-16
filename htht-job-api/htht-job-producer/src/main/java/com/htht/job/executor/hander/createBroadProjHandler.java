package com.htht.job.executor.hander;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.CmdMessage;
import com.htht.job.core.util.CreateXmlUtils;
import com.htht.job.core.util.ExecProcess;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ScriptUtil;
import com.htht.job.executor.hander.resolvehandler.ResolvePieprjService;
import com.htht.job.executor.util.DubboIpUtil;

@JobHandler(value = "createBroadProjHandler")
@Service
public class createBroadProjHandler extends IJobHandler{
    private static final String RESULT_START = "<result>";
    private static final String RESULT_END = "</result>";
    private static final String windows = "Z:";
    private static final String linux = "/RSData6";
    @Autowired
    private ResolvePieprjService resolvePieprjService;
	
	
    public static void main(String[] args) throws IOException {
        String mss_xml_path = "";
        Date nowDate=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        Date beforeDate = calendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(df.format(nowDate));
        System.out.println(df.format(beforeDate));
        List<File> allFlie=new ArrayList<>();
        List<File> nowFiles = FileUtil.getAllFiles(mss_xml_path+"/"+df.format(nowDate), "*.tiff");
        List<File> beforeFiles = FileUtil.getAllFiles(mss_xml_path+"/"+df.format(beforeDate), "*.tiff");
        if(null!=nowFiles&&!nowFiles.isEmpty()){
            allFlie.addAll(nowFiles);
        }
        if(null!=beforeFiles&&!beforeFiles.isEmpty()){
            allFlie.addAll(beforeFiles);
        }




    }


	
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result = new ResultUtil();
        String deploysyetem= DubboIpUtil.getOsName();

        LinkedHashMap fixmap = triggerParam.getFixedParameter();
        LinkedHashMap dymap = triggerParam.getDynamicParameter();
        String pan_xml_path = (String) dymap.get("全色影像xml路径");
        String mss_xml_path = (String) dymap.get("多光谱影像xml路径");
        String pieprjPath = (String) dymap.get("projectPath");
        
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	Date date = new Date();
    	String format = simpleDateFormat.format(date);
    	
        String replace = pieprjPath.replace("\\", "/");
        String name = replace.substring(replace.lastIndexOf("/")+1, replace.indexOf(".PIEPrj"));
       
        pieprjPath = replace.replace(".PIEPrj", format+".PIEPrj");
        pieprjPath = pieprjPath.replace(name+format+".PIEPrj", "PIEPrj/"+name+format+".PIEPrj");
        if(!pan_xml_path.contains(".xml")) {
        	ReturnT<String> createPan_xml_path = this.createPan_xml_path(pan_xml_path,"/tiffXml/"+name+"/"+format);
        	if(createPan_xml_path.getCode() == 4) {
        		return createPan_xml_path;
        	}else {
        		pan_xml_path = createPan_xml_path.getMsg();
        	}
        }
        if(!mss_xml_path.contains(".xml")) {
        	ReturnT<String> createMss_xml_path = this.createMss_xml_path(mss_xml_path,"/tiffXml/"+name+"/"+format,pieprjPath);
        	if(createMss_xml_path.getCode() == 4) {
        		return createMss_xml_path;
        	}else {
        		mss_xml_path = createMss_xml_path.getMsg();
        	}
        }
        
        dymap.remove("pan_image_index");
        dymap.remove("pan_image_type");
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
                if ((entry.getKey()).equals("全色影像xml路径")) {
                    value = pan_xml_path;
                }
                if ((entry.getKey()).equals("多光谱影像xml路径")) {
                    value = mss_xml_path;

                }
                if((entry.getKey()).equals("projectPath")) {
                	value = pieprjPath;
                }
                if((entry.getKey()).equals("DEM文件路径")) {
                	if(new File(value).isDirectory()) {
                		value = "null";
                	}
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

    public ReturnT<String> createPan_xml_path(String pan_xml_path, String dateFolder) {
        //存储全色影像路径list
        ArrayList<String> normalArrayList = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        File rootPanPath = new File(pan_xml_path);
        if (rootPanPath.exists() && rootPanPath.isDirectory()) {
        	
        	String scanFolderPath = pan_xml_path + dateFolder;
        	scanFolderPath = scanFolderPath.replace("\\", "/");
        	scanFolderPath = scanFolderPath.substring(0, scanFolderPath.lastIndexOf("/")+1)+"scanFolder";
        	File scanFolder = new File(scanFolderPath);
        	if(!scanFolder.exists()) {
        		scanFolder.mkdirs();
        	}
        	
            List<File> files = FileUtil.getAllFiles(pan_xml_path, "*.TIFF");
            for (File childFile : files) {
                String name = childFile.getName();
                String upperCaseName = name.toUpperCase();
                if (upperCaseName.contains("PAN") && upperCaseName.endsWith(".TIFF")) {
                    //获取文件绝对路径
                    String absolutePath = childFile.getAbsolutePath();
                    normalArrayList.add(absolutePath);
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
            if(arrayList.size() == 0 && normalArrayList.size() != 0) {
            	return new ReturnT<>(4,"未找到没有处理的影像");
            }
            //将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
            String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("pan_path.xml", pan_xml_path+dateFolder, arrayList);
            return new ReturnT<>(200, testCreateMssPathXml);
        } else {
            return new ReturnT<>(500, "文件夹路径不存在");
        }
    }

    public ReturnT<String> createMss_xml_path(String mss_xml_path, String dateFolder,String pieprjPath) {
        //存储全色影像路径list
        ArrayList<String> normalArrayList = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        File rootPanPath = new File(mss_xml_path);
        if (rootPanPath.exists() && rootPanPath.isDirectory()) {
        	String projectFolder = pieprjPath.substring(0,pieprjPath.lastIndexOf(".PIEPrj"));
        	File file = new File(projectFolder);
        	if(!file.exists()) {
        		file.mkdirs();
        	}
        	String scanFolderPath = file.getParentFile().getAbsolutePath()+"/temp";
        	File file2 = new File(scanFolderPath);
        	if(!file2.exists()) {
        		file2.mkdirs();
        	}
        	
            List<File> files = this.getDateAllFiles(mss_xml_path);
            for (File childFile : files) {
                String name = childFile.getName();
                String upperCaseName = name.toUpperCase();
                if (upperCaseName.contains("MSS") && upperCaseName.endsWith(".TIFF") || upperCaseName.contains("WFV") && upperCaseName.endsWith(".TIFF")) {
                    //获取文件绝对路径
                    String absolutePath = childFile.getAbsolutePath();
                    normalArrayList.add(absolutePath);
                    List<File> allFiles = new ArrayList<File>();
                    FileUtil.getScanFiles(file2, childFile,allFiles);
                    if(allFiles.size() == 0) {
                    	arrayList.add(absolutePath);
                    }
                }

            }
            if(arrayList.size() == 0 && normalArrayList.size() != 0) {
            	return new ReturnT<>(4,"未找到没有处理的影像");
            }
            //将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
            String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("mss_path.xml", mss_xml_path+dateFolder, arrayList);
            return new ReturnT<>(200, testCreateMssPathXml);
        } else {
            return new ReturnT<>(500, "文件夹路径不存在");
        }
    }
  public List<File> getDateAllFiles(String mss_xml_path){
        Date nowDate=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        Date beforeDate = calendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(df.format(nowDate));
        System.out.println(df.format(beforeDate));
        List<File> allFlie=new ArrayList<>();
        List<File> nowFiles = FileUtil.getAllFiles(mss_xml_path+"/"+df.format(nowDate), "*.tiff");
        List<File> beforeFiles = FileUtil.getAllFiles(mss_xml_path+"/"+df.format(beforeDate), "*.tiff");
        if(null!=nowFiles&&!nowFiles.isEmpty()){
            allFlie.addAll(nowFiles);
        }
        if(null!=beforeFiles&&!beforeFiles.isEmpty()){
            allFlie.addAll(beforeFiles);
        }
        System.out.println("长度为===="+allFlie.size());
        return  allFlie;
    }
}
