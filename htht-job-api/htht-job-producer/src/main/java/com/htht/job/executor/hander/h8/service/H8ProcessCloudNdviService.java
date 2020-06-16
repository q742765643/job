package com.htht.job.executor.hander.h8.service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.MarkMSSDomCompleteHandler;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.htht.util.ServerImpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service("H8ProcessCloudNdviService")
public class H8ProcessCloudNdviService {
    private Logger logger = LoggerFactory.getLogger(H8ProcessCloudNdviService.class);

    public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
        /** 1.获取参数列表 **/
        LinkedHashMap<?, ?> fixmap = triggerParam.getFixedParameter();
        LinkedHashMap<?, ?> dymap = triggerParam.getDynamicParameter();
        ArrayList<String> issueList = new ArrayList<>();
        /** 2.解析产品参数 **/
        String exePath = (String) fixmap.get("exePath");

        String InputPath = (String) dymap.get("InputPath");
        String DateTime = (String) dymap.get("DateTime");
        String OutputPath = FileUtil.formatePath((String) dymap.get("OutputPath"));
        String LogFilePath = FileUtil.formatePath((String) dymap.get("LogFilePath"));
        String RecallLogFilePath = FileUtil.formatePath((String) dymap.get("RecallLogFilePath"));
        String InvalidValues = FileUtil.formatePath((String) dymap.get("InvalidValues"));
        InvalidValues = InvalidValues.substring(0,InvalidValues.length()-1);
        String BandList = FileUtil.formatePath((String) dymap.get("BandList"));
        BandList = BandList.substring(0,BandList.length()-1);
        String ProductType = FileUtil.formatePath((String) dymap.get("ProductType"));
        ProductType = ProductType.substring(0,ProductType.length()-1);

        //获取上一天数据文件夹
        Calendar instance = Calendar.getInstance();
        Date date = new Date();
        instance.add(Calendar.DATE,-1);
        String MMdd = getMMddSimpleDateFormat().format(instance.getTime());
        File fileDir = new File(InputPath + "/" + MMdd);
        File scanDir = new File(InputPath + "/" + MMdd + "/temp");
        if(!scanDir.exists()){
            scanDir.mkdirs();
        }
        ArrayList<String> fileData = new ArrayList<>();

        if(fileDir.exists() && fileDir.isDirectory()){
            //正则获取指定时间段tif
            String regEx = "[0][0-7][0-5][0]";
            Pattern pattern = Pattern.compile(regEx);
            File[] files = fileDir.listFiles();
            for (File file : files) {
                if (!file.isDirectory() && file.getName().length() > 16) {
                    String fileName = file.getName();
                    String name = fileName.substring(12, 16);
                    Matcher matcher = pattern.matcher(name);
                    boolean matches = matcher.matches();
                    if (matches) {
                  //      String[] scanfiles = scanDir.list();
                 //       if (!Arrays.asList(scanfiles).contains(fileName)) {
                            String inputxmlpath = "";
                            String logFilePath = "";
                            if(ProductType.equals("CLOUD")){
                                 inputxmlpath = InputPath + "/temp/" + MMdd + "/"+"CLOUD" + name + ".xml";
                                logFilePath = LogFilePath+ MMdd + "/" +"CLOUD" + name + ".log";
                            }else {
                                inputxmlpath = InputPath + "/temp/" + MMdd + "/"+"NDVI" + name + ".xml";
                                logFilePath = LogFilePath+ MMdd + "/" +"NDVI" + name + ".log";
                            }

                            //拼装map,生成xml参数文件
                            HashMap<String, Object> inputxmlParamMap = new HashMap<>();
                            inputxmlParamMap.put("InputPath", fileDir.getAbsolutePath());
                            inputxmlParamMap.put("DateTime", MMdd + name);
                            inputxmlParamMap.put("OutputPath", OutputPath);
                            inputxmlParamMap.put("LogFilePath",logFilePath);
                            inputxmlParamMap.put("InvalidValues", InvalidValues);
                            inputxmlParamMap.put("BandList", BandList);
                            inputxmlParamMap.put("ProductType", ProductType);

                            if (!createAlgorithmXml("H8ProcessParam", inputxmlParamMap, inputxmlpath)) {
                                result.setMessage("create inputxml failed");
                                return result;
                            }

                            /** 设置调度日志 **/
                            String outputlogpath = triggerParam.getLogFileName();
                            triggerParam.setLogFileName(outputlogpath);

                            ServerImpUtil.executeCmd(exePath, inputxmlpath);

//                            fileData.add(name);
//                            try {
//                                new File(scanDir + "/" + fileName).createNewFile();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }
              //      }
                }
            }

//            if (fileData.isEmpty()){
//                result.setErrorMessage("无未处理的数据");
//            }
        }else{
            result.setErrorMessage("输入文件夹不存在");
            return result;
        }
        /** 返回结果 **/
        return result;
    }

    private SimpleDateFormat getMMddSimpleDateFormat(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat;
    }

    private boolean createAlgorithmXml(String rootTag, HashMap<String, Object> inputxmlParamMap, String inputxmlpath) {
        File file = new File(inputxmlpath);
        if(!FileUtil.createFile(file)) {
            return false;
        }

        // 创建文档对象
        Document doc = DocumentHelper.createDocument();
        // 创建根节点
        Element root = doc.addElement("xml");
        root.addAttribute("identify",rootTag);
        Set<String> keys = inputxmlParamMap.keySet();
        for (String key : keys) {
            root.addElement(key).setText((String) inputxmlParamMap.get(key));
        }
        // 设置XML文档格式
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        // 设置XML编码方式,即是用指定的编码方式保存XML文档到字符串(String),这里也可以指定为GBK或是ISO8859-1
        outputFormat.setEncoding("UTF-8");
        outputFormat.setNewlines(true); // 设置是否换行
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(outputFormat);
            FileOutputStream fos = new FileOutputStream(inputxmlpath);
            assert writer != null;
            writer.setOutputStream(fos);
            writer.write(doc);
            writer.close();
            logger.info("写入消灭了成功{}", inputxmlpath);
            return true;
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
