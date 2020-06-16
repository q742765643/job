package com.htht.job.executor.hander;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JobHandler(value = "MarkMSSDomCompleteHandler")
@Service
public class MarkMSSDomCompleteHandler extends IJobHandler {

    public static void main(String[] args) {
        String index = "1";
        String PIEPrj = "/zzj/vm/prj/duguangpu.PIEPrj";
        File file = new File(PIEPrj);
        String parent = file.getParent();
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document;
        List<String> pathList = new ArrayList<>();
        try {
            document = (Document) reader.read(PIEPrj);
            Element root = document.getRootElement();
            //获取 SAT_PAIRS 节点
            Element SAT_PAIRS_element = (Element) root.selectSingleNode("//PIE_ORTHO_PRJ/SAT_PAIRS");
            ArrayList SAT_PAIRS_list = (ArrayList) SAT_PAIRS_element.elements();
            for (Object SAT_PAIRS_list_object : SAT_PAIRS_list) {
                //获取 PAIR 节点
                Element PAIR_element = (Element) SAT_PAIRS_list_object;
                List PAIR_elements_list = PAIR_element.elements();
                for (Object PAIR_elements_list_object : PAIR_elements_list) {
                    //获取 MSS_SRC 节点
                    Element element = (Element) PAIR_elements_list_object;
                    if (element.getName().equals("MSS_SRC")) {
                        List MSS_SRCElements = element.elements();
                        for (Object object : MSS_SRCElements) {
                            //获取 MSS_PATH 节点
                            Element MSS_PATHElement = (Element) object;
                            if (MSS_PATHElement.getName().equals("MSS_PATH")) {
                                pathList.add(MSS_PATHElement.getText());
                            }
                        }
                    }
                }
            }
            String prjMssPath = pathList.get(Integer.valueOf(index));
            prjMssPath = prjMssPath.replaceAll("\\\\", "/");
            List<String> list = new ArrayList<String>();
            Pattern p = Pattern.compile("(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)");
            Matcher m = p.matcher(prjMssPath);
            while (m.find()) {
                int i = 1;
                list.add(m.group(i));
                i++;
            }
            String mssName = prjMssPath.substring(prjMssPath.lastIndexOf("/") + 1, prjMssPath.indexOf(".tiff"));
            String recordDir = parent + "/temp";
            File tempDir = new File(recordDir);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            File recordFile = new File(tempDir + "/" + list.get(0) + "-" + mssName + ".txt");
            if (!recordFile.exists()) {
                recordFile.createNewFile();
            } else {
                recordFile.delete();
                recordFile.createNewFile();

            }
            String projectFolder = PIEPrj.substring(0, PIEPrj.lastIndexOf(".PIEPrj"));
            String completeFolder = projectFolder + "/DOM/mult/" + mssName + "_ortho.img";
            try(
            	FileWriter fileWritter = new FileWriter(recordFile.getAbsolutePath(), true);
            	){
            	fileWritter.write(completeFolder);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        LinkedHashMap dymap = triggerParam.getDynamicParameter();
        String index = (String) dymap.get("多光谱影像y索引");
        String PIEPrj = (String) dymap.get("工程文件路径");
        File file = new File(PIEPrj);
        String parent = file.getParent();
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document;
        List<String> pathList = new ArrayList<>();
        try {
            document = (Document) reader.read(PIEPrj);
            Element root = document.getRootElement();
            //获取 SAT_PAIRS 节点
            Element SAT_PAIRS_element = (Element) root.selectSingleNode("//PIE_ORTHO_PRJ/SAT_PAIRS");
            ArrayList SAT_PAIRS_list = (ArrayList) SAT_PAIRS_element.elements();
            for (Object SAT_PAIRS_list_object : SAT_PAIRS_list) {
                //获取 PAIR 节点
                Element PAIR_element = (Element) SAT_PAIRS_list_object;
                List PAIR_elements_list = PAIR_element.elements();
                for (Object PAIR_elements_list_object : PAIR_elements_list) {
                    //获取 MSS_SRC 节点
                    Element element = (Element) PAIR_elements_list_object;
                    if (element.getName().equals("MSS_SRC")) {
                        List MSS_SRCElements = element.elements();
                        for (Object object : MSS_SRCElements) {
                            //获取 MSS_PATH 节点
                            Element MSS_PATHElement = (Element) object;
                            if (MSS_PATHElement.getName().equals("MSS_PATH")) {
                                pathList.add(MSS_PATHElement.getText());
                            }
                        }
                    }
                }
            }
            String prjMssPath = pathList.get(Integer.valueOf(index));
            prjMssPath = prjMssPath.replaceAll("\\\\", "/");
            List<String> list = new ArrayList<String>();
            Pattern p = Pattern.compile("(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)");
            Matcher m = p.matcher(prjMssPath);
            while (m.find()) {
                int i = 1;
                list.add(m.group(i));
                i++;
            }
            String mssName = prjMssPath.substring(prjMssPath.lastIndexOf("/") + 1, prjMssPath.indexOf(".tiff"));
            String recordDir = parent + "/temp";
            File tempDir = new File(recordDir);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            File recordFile = new File(tempDir + "/" + list.get(0) + "-" + mssName + ".txt");
            if (!recordFile.exists()) {
                recordFile.createNewFile();
            } else {
                recordFile.delete();
                recordFile.createNewFile();

            }
            String projectFolder = PIEPrj.substring(0, PIEPrj.lastIndexOf(".PIEPrj"));
            String completeFolder = projectFolder + "/DOM/mult/" + mssName + "_ortho.img";
            try(
            	FileWriter fileWritter = new FileWriter(recordFile.getAbsolutePath(), true);
            	){
            	fileWritter.write(completeFolder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, triggerParam.getParallelLogId());
            return stopResult;
        }

        return ReturnT.SUCCESS;
    }

}
