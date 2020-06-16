package com.htht.job.executor.service.hander;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.MatchTime;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.core.util.ScriptUtil;
import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import com.htht.job.executor.service.fileinfo.FileInfoService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;


@Transactional
@Service("demoHanderService")
public class DemoHanderService {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductFileInfoService productFileInfoService;
    @Autowired
    private FileInfoService fileInfoService;

    @SuppressWarnings({"rawtypes"})
    public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
        try {
            LinkedHashMap dymap = triggerParam.getDynamicParameter();
            LinkedHashMap fixmap = triggerParam.getFixedParameter();
            String inputxml = (String) fixmap.get("inputxml");
            String outputlog = (String) dymap.get("outputlog");
            String outputxml = (String) dymap.get("outputxml");
            String exePath = (String) fixmap.get("exePath");
            String scriptFile = (String) fixmap.get("scriptFile");

            /**=======1.拼装dymap===========**/
            String fileName = this.getDynamicMap(dymap, outputxml, outputlog, triggerParam, result);
            if (!result.isSuccess()) {
                return result;
            }
            inputxml = inputxml + fileName + ".xml";
            outputlog = (String) dymap.get("outputlog");
            outputxml = (String) dymap.get("outputxml");
            /**=======2.创建日志文件===========**/
            XxlJobFileAppender.makeLogFileNameByPath(outputlog);
            //回调需要用到
            triggerParam.setLogFileName(outputlog);
            /**=======3.判断是否存在文件===========**/
            List<ProductFileInfoDTO> productFileInfoDTOS = productFileInfoService.findByWhere(triggerParam.getProductId(), (String) dymap.get("issue"));
            if (productFileInfoDTOS.size() > 0) {
                XxlJobLogger.logByfile(outputlog, "	已经执行");
                return result;
            }
            /**=======4.生成文件===========**/
            this.makeFile(inputxml, outputxml, dymap, result);
            if (!result.isSuccess()) {
                return result;
            }
            /**=======5.执行脚本===========**/
            XxlJobLogger.logByfile(outputlog, "开始执行");


            int exitValue = ScriptUtil.execToFile(exePath, scriptFile, outputlog, inputxml);
            if (exitValue != 0) {
                result.setErrorMessage("脚本执行错误");
                return result;
            }
            /**========6.脚本  结束入库=======**/
            List<Map> list = new ArrayList<Map>();
            List<ProductFileInfoDTO> productFileInfoDTOList = new ArrayList<ProductFileInfoDTO>();
            this.parsingXml(outputxml, result, list);
            this.batchSaveProductFileInfo(triggerParam.getProductId(), dymap, list, result);
            if (!result.isSuccess()) {
                return result;
            }

        } catch (Exception e) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
            throw new RuntimeException();
        }
        return result;


    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String getDynamicMap(Map dymap, String outputxml, String outputlog, TriggerParam triggerParam, ResultUtil<String> result) {
        String fileName = null;
        try {
            /***========1.获取原参数================***/
            String infileeast = (String) dymap.get("infileeast");
            String infilewest = (String) dymap.get("infilewest");
            String issue = (String) dymap.get("issue");

            /***========2.根据时间替换变量============***/
            if (issue.indexOf("{yyyy}") > 0) {
                issue = MatchTime.matchIssue(new Date(), issue, "COOD");
            }
            infileeast = infileeast.replace("{yyyy}{mm}{dd}", issue.substring(0, 8));
            infilewest = infilewest.replace("{yyyy}{mm}{dd}", issue.substring(0, 8));
            infileeast = infileeast.replace("{yyyy}", issue.substring(0, 4));
            infilewest = infilewest.replace("{yyyy}", issue.substring(0, 4));
            /***========3.文件名称截取============***/
            String infileeastName[] = infileeast.split("/");
            fileName = infileeastName[infileeastName.length - 1].substring(0, infileeastName[infileeastName.length - 1].lastIndexOf("."));
            dymap.put("issue", issue);
            dymap.put("infileeast", infileeast);
            dymap.put("infilewest", infilewest);
            dymap.put("outputlog", outputlog + fileName + "(" + triggerParam.getLogId() + ").log");
            dymap.put("outputxml", outputxml + fileName + ".xml");
            /***========4.判断执行文件是否存在=========***/
            File f = new File(infileeast);
            if (!f.exists() || f.length() == 0) {
                result.setErrorMessage(infileeast + "不存在");
                return fileName;

            }
            File f1 = new File(infilewest);
            if (!f1.exists() || f1.length() == 0) {
                result.setErrorMessage(infilewest + "不存在");
                return fileName;
            }

        } catch (Exception e) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_108_ERROR.getValue());
            throw new RuntimeException();
        }
        return fileName;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void makeFile(String inputxml, String outputxml, Map dymap, ResultUtil<String> result) {
        try {
            /**======1.创建输入文件==========**/
            XxlJobFileAppender.makeLogFileNameByPath(inputxml);
            File inputxmlFile = new File(inputxml);
            StringBuffer xmlstr = new StringBuffer();
            xmlstr.append("<Inputcfg><prodinfo prodname=\"sugar_area\">");
            Iterator<Map.Entry<String, Object>> it = dymap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                xmlstr.append("<" + entry.getKey() + ">");
                xmlstr.append(entry.getValue() + "</" + entry.getKey() + ">");
            }
            xmlstr.append("</prodinfo></Inputcfg>");
            Document document = DocumentHelper.parseText(xmlstr.toString());
            OutputFormat format = new OutputFormat(" ", true);
            format.setEncoding("UTF-8");// 设置编码格式
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(inputxmlFile), format);
            xmlWriter.write(document);
            xmlWriter.close();
            /**======2.创建输出xml==========**/
            XxlJobFileAppender.makeLogFileNameByPath(outputxml);
        } catch (Exception e) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_109_ERROR.getValue());
            throw new RuntimeException();
        }

    }

    public ResultUtil<String> parsingXml(String outputxml, ResultUtil<String> result, List<Map> list) {
        try {
            File f = new File(outputxml);
            if (!f.exists() || f.length() == 0) {
                result.setErrorMessage("outxml文件不存在");
                return result;
            }
            //创建SAXReader对象
            SAXReader reader = new SAXReader();
            //读取文件 转换成Document
            Document document = reader.read(f);
            //获取根节点元素对象
            Element root = document.getRootElement();
            if (root == null) {
                result.setErrorMessage("outxml根节点获取错误");
                return result;
            }
            Element log = root.element("log");
            if (log == null) {
                result.setErrorMessage("outxml log节点获取错误");
                return result;
            }
            String loginfo = log.elementText("loginfo");
            if (!"success".equals(loginfo)) {
                result.setErrorMessage("outxml loginfo节点获取错误");
                return result;
            }

            Element outputfiles = root.element("outputfiles");
            if (outputfiles == null) {
                result.setErrorMessage("outxml outputfiles节点获取错误");
                return result;
            }
            Iterator<Element> file = outputfiles.elementIterator("file");
            while (file.hasNext()) {
                Element e = file.next();
                Map map = new HashMap();
                map.put("type", e.elementText("type"));
                map.put("path", e.elementText("path"));
                map.put("region", e.elementText("region"));
                list.add(map);

            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
        return result;
    }

    //遍历当前节点下的所有节点
    public void listNodes(Element node) {
        System.out.println("当前节点的名称：" + node.getName());
        //首先获取当前节点的所有属性节点
        List<Attribute> list = node.attributes();
        //遍历属性节点
        for (Attribute attribute : list) {
            System.out.println("属性" + attribute.getName() + ":" + attribute.getValue());
        }
        //如果当前节点内容不为空，则输出
        if (!(node.getTextTrim().equals(""))) {
            System.out.println(node.getName() + "：" + node.getText());
        }
        //同时迭代当前节点下面的所有子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }

    public boolean checkXmlExists(String outputxml) {
        boolean flag = false;
        File f = new File(outputxml);
        if (f.exists() && f.length() != 0) {
            flag = this.parsingXmlExists(f.getPath());
        }
        return flag;

    }

    public boolean parsingXmlExists(String xmlfilePath) {
        boolean flag = false;
        try {
            //创建SAXReader对象
            SAXReader reader = new SAXReader();
            //读取文件 转换成Document
            Document document = reader.read(new File(xmlfilePath));
            //获取根节点元素对象
            Element root = document.getRootElement();
            if (null != root) {
                Element log = root.element("log");
                if (null != log) {
                    String loginfo = log.elementText("loginfo");
                    if ("success".equals(loginfo)) {
                        flag = true;
                    }
                }
            }


        } catch (Exception e) {
            flag = false;
            throw new RuntimeException();
        }
        return flag;
    }

    public Map<String, List<Map>> orderByRegion(List<Map> list) {
        Map<String, List<Map>> map = new HashMap<String, List<Map>>();
        for (Map map1 : list) {
            List<Map> nList = map.get(map1.get("region"));
            if (nList == null) {
                nList = new ArrayList<Map>();
            }
            Map nmap = new HashMap();
            nList.add(map1);
            map.put((String) map1.get("region"), nList);
        }
        return map;
    }

    public void batchSaveProductFileInfo(String productId, Map dymap, List<Map> list, ResultUtil<String> result) {
        if (null == productId) {
            result.setErrorMessage("产品id为空");
            return;
        }
    }

}
