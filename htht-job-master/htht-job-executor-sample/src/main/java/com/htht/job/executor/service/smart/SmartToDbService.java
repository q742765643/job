package com.htht.job.executor.service.smart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.MatchTime;
import com.htht.job.core.util.ScriptUtil;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.product.ProductDTO;
import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.service.builder.ProductHandler;
import com.htht.job.executor.service.builder.ProductHandlerBuilder;
import com.htht.job.executor.service.builder.part.XmlInfo;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlTools;

@Transactional
@Service
public class SmartToDbService {
	private Logger logger = LoggerFactory.getLogger(SmartToDbService.class);
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
    private static final String IDENTIFY = "identify";
    private static final String CYCLE = "Cycle";
    private static final String NULLSTR = "NULL";
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private ProductUtil productUtil;
    @Autowired
    private ProductFileInfoService productFileInfoService;
    @Autowired
    private BaseDaoUtil baseDaoUtil;
    @Autowired
    private DictCodeService dictCodeService;

    public ReturnT<String> toDbByJson(String jsonObject, ReturnT<String> returnT) {
        JSONObject parseObject = JSONObject.parseObject(jsonObject);
        String areaID = findDictCode("smart", "areaId");
        String exePath = findDictCode("smart", "exePath");
        if (null == areaID || "".equals(areaID) || null == exePath || "".equals(exePath)) {
            returnT.setCode(ReturnT.FAIL_CODE);
            returnT.setMsg("产品入库失败。");
            returnT.setMsg("请在字典中配置正确的smart areaId与exePath");
            return returnT;
        }
        String productName = parseObject.getString("ProductName");
        ProductDTO productDTO = findProductByType(productName);
        if (null == productDTO) {
            returnT.setCode(ReturnT.FAIL_CODE);
            returnT.setMsg("产品入库失败。");
            returnT.setMsg("产品类型不存在" + parseObject.getString("ProductName"));
            return returnT;
        }
        ProductHandler handler = ProductHandlerBuilder.newBuilder().useJobDataMap(parseObject)
                .useIssueHandler((result, jobDataMap) -> Arrays.asList(
                        MatchTime.matchIssue(jobDataMap.get("SatTime").toString(), jobDataMap.get(CYCLE).toString())))
                .useIsExistPart(issue -> false).useCreateXmlPart((issue, result, jobDataMap) -> {
                    List<XmlDTO> inputList = new ArrayList<>();
                    List<XmlDTO> outputList = new ArrayList<>();
                    String productPath = productDTO.getProductPath();
                    String tempFileName = productName + "_" + issue + "(" + df.format(new Date()) + ")";
                    String inputxmlpath = productPath + "/inputxml/" + tempFileName + ".xml";
                    String outputxmlpath = productPath + "/outputxml/" + tempFileName + ".xml";
                    String algLog = productPath + "/outputLog/" + tempFileName + ".log";
                    inputList.add(newStringXmlDTO("区域ID", "areaID", areaID));
                    inputList.add(newStringXmlDTO("产品期次", "issue", issue));
                    inputList.add(newStringXmlDTO("卫星名称", "SatName", jobDataMap.getOrDefault("SatName", NULLSTR).toString()));
                    inputList.add(newStringXmlDTO("传感器名称", "SensorName", jobDataMap.getOrDefault("SensorName", NULLSTR).toString()));
                    inputList.add(newStringXmlDTO("分辨率 单位m", "Resolution", jobDataMap.getOrDefault("Resolution", NULLSTR).toString()));
                    inputList.add(newStringXmlDTO("产品周期", CYCLE, jobDataMap.getOrDefault(CYCLE, NULLSTR).toString()));
                    inputList.add(newStringXmlDTO("输入文件", "inputFile", productPath));
                    String productObj = jobDataMap.get("Products").toString();
                    if (productObj != null) {
                        try {
                            if (productObj.trim().startsWith("[")) {
                                JSONArray jsonArray = JSONArray.parseArray(productObj);
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JSONObject jso = jsonArray.getJSONObject(i);
                                    inputList.add(newStringXmlDTO(jso.getString("Type"), jso.getString("Type"),
                                            jso.getString("File").replace("[", "").replace("]", "")));
                                }
                            } else {
                                JSONObject jso = JSONObject.parseObject(productObj);
                                inputList.add(newStringXmlDTO(jso.getString("Type"), jso.getString("Type"),
                                        jso.getString("File").replace("[", "").replace("]", "").replace("\"", "").replaceAll("\'", "")));
                            }
                        } catch (Exception e) {
                            result.setCode(ReturnT.FAIL_CODE);
                            result.setMsg("解析Json产品信息出错" + jsonObject);
                        }
                    }
                    outputList.add(newStringXmlDTO("输出文件夹路径", "outFolder", productPath));
                    outputList.add(newStringXmlDTO("输出xml目录", "outXMLPath", outputxmlpath));
                    outputList.add(newStringXmlDTO("log目录", "outLogPath", algLog));
                    createParentDir(inputxmlpath, result);
                    createParentDir(outputxmlpath, result);
                    createParentDir(algLog, result);
                    jobDataMap.put("algLog", algLog);
                    if (!createAlgorithmXml(productName, inputList, outputList, inputxmlpath)) {
                        result.setCode(ReturnT.FAIL_CODE);
                        result.setMsg("创建输入文件失败" + inputxmlpath);
                    }
                    return new XmlInfo(inputxmlpath, outputxmlpath);
                }).useDoExcutePart((inputXml, result, jobDataMap) -> {
                    try {
                        int exitValue = ScriptUtil.execToFile(exePath, "", jobDataMap.get("algLog").toString(), inputXml);
                        if (exitValue != 0) {
                            result.setCode(ReturnT.FAIL_CODE);
                            result.setMsg("调用算法失败：" + exePath + "  " + inputXml);
                        }
                    } catch (IOException e) {
                        result.setCode(ReturnT.FAIL_CODE);
                        result.setMsg("调用算法异常：" + exePath + "  " + inputXml);
                    }
                }).usePraseXmlPart((outputXml, result, jobDataMap) -> {
                    try {
                        String productIssue = jobDataMap.get("productIssue").toString();
                        String productCycle = jobDataMap.get(CYCLE).toString();
                        Element rootElement = XmlTools.getRootElement(outputXml);
                        if (!XmlTools.isSuccess(rootElement)) {
                            result.setCode(ReturnT.FAIL_CODE);
                            result.setMsg("算法执行失败");
                            return;
                        }
                        String mosaicFile = "";
                        List<ProductInfoDTO> pfiList = new ArrayList<>();
                        Element outFiles = rootElement.element("outFiles");
                        if (outFiles != null) {
                            Element mosaic = outFiles.element("mosaicFile");
                            if (null != mosaic) {
                                Element file = mosaic.element("file");
                                if (null != file) {
                                    mosaicFile = file.getTextTrim();
                                }
                            }
                            for (Object obj : outFiles.elements("region")) {
                                Element regionElement = (Element) obj;
                                String region = regionElement.attributeValue(IDENTIFY);
                                /*** ============ 删除产品及文件信息 ================= **/
                                List<ProductInfoDTO> findProductExits = productInfoService
                                        .findProductExits(productDTO.getId(), productIssue, productCycle, "", "", region);
                                pfiList.addAll(findProductExits);
                                findProductExits.forEach(productInfo -> {
                                    productFileInfoService.deleteByproductInfoId(productInfo.getId());
                                    productInfoService.deleteProductInfo(productInfo.getId());
                                });
                                /*** ============ 产品信息入库 ================= **/
                                ProductInfoDTO pinfo = productUtil.saveProductInfo(productDTO.getId(), region, productIssue,
                                        productCycle, mosaicFile, "", "");
                                /*** ============ 产品文件信息入库 ================= **/
                                for (Object fileObj : regionElement.elements("file")) {
                                    Element fileElemnt = (Element) fileObj;
                                    File file = new File(fileElemnt.getText());
                                    String fileType = fileElemnt.attributeValue("type").replaceAll("\\.", "");

                                    if (file.exists()) {
                                        ProductFileInfoDTO pfInfo = new ProductFileInfoDTO(productDTO.getMark(), pinfo.getId(),
                                                file.getName(), file.length(), fileType, file.getPath(),
                                                file.getAbsolutePath(), productIssue, region, productCycle);
                                        productFileInfoService.save(pfInfo);
                                    }
                                }
                            }
                            /*** ============ 统计信息入库 ================= **/
                            Element tables = rootElement.element("tables");
                            for (Object tableObj : tables.elements("table")) {
                                Element tableElement = (Element) tableObj;
                                String tablename = tableElement.attributeValue(IDENTIFY);
                                if (null == tablename || "".equals(tablename)) {
                                    /////////////////////////////////////////
                                    // 待补充 //
                                    /////////////////////////////////////////
                                    continue;
                                }
                                List<String> valueList = new ArrayList<>();
                                for (Object valueElement : tableElement.elements("values")) {
                                    valueList.add(((Element) valueElement).getText());
                                }
                                /*** ============ 删除统计信息 ================= **/
                                String sql = "delete from " + tablename + " where product_info_id=?";
                                pfiList.forEach(productInfo -> 
                                    baseDaoUtil.executeSql(sql, productInfo.getId())
                                );
                                /***============ 封数据执行入库 =================**/
                                ProductAnalysisTableInfo productAnalysisTableInfo = new ProductAnalysisTableInfo(
                                        tablename);
                                String[] fields = (tableElement.element("field").getText()
                                        + ",product_info_id,Cycle,model_identify,file_name")
                                        .replace("'", "").trim().split(",");
                                String otherValue = "," + "0" + "," + productCycle + "," + "0" + "," + "0";
                                for (int i = 0; i < fields.length; i++) {
                                    for (String s : valueList) {
                                        String[] values = (s + otherValue).replace("'", "").replace(",", " ").trim()
                                                .split("\\s+");
                                        productAnalysisTableInfo.addFieldAndValue(fields[i].trim(), values[i].trim());
                                    }
                                }
                                productUtil.saveProductDetail(productAnalysisTableInfo);
                            }
                        }
                    } catch (Exception e) {
                        result.setCode(ReturnT.FAIL_CODE);
                        result.setMsg("算法执行失败");
                        return;
                    }

                }).builer();
        handler.excute(null, returnT);
        return returnT;
    }

    private String findDictCode(String pDictName, String dictName) {
        // 获取产品路径配置信息
        List<DictCodeDTO> pathConfs = dictCodeService.findChildren(pDictName);
        for (DictCodeDTO pathconf : pathConfs) {
            if (dictName.equals(pathconf.getDictName())) {
                return pathconf.getDictCode();
            }
        }
        return "";
    }

    private void createParentDir(String path, ReturnT<String> result) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            try {
                FileUtils.forceMkdirParent(file);
            } catch (IOException e) {
                result.setCode(ReturnT.FAIL_CODE);
                result.setContent("创建目录失败" + file.getParent());
            }
        }
    }

    private ProductDTO findProductByType(String productType) {
        List<ProductDTO> productDTOList = productService.findALlProduct();
        for (ProductDTO productDTO : productDTOList) {
            if (productType.equals(productDTO.getMark())) {
                return productDTO;
            }
        }
        return null;
    }

    private XmlDTO newStringXmlDTO(String description, String identify, String value) {
        XmlDTO dto = new XmlDTO();
        dto.identify = identify;
        dto.type = "String";
        dto.description = description;
        dto.value = value;
        return dto;
    }

    public boolean createAlgorithmXml(String rootTag, List<XmlDTO> inputList, List<XmlDTO> outputList, String path) {
        // 判断文件是否存在，不存在就创建
        File file = new File(path);

        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        if (!file.exists()) {
            try {
                if(!file.createNewFile()) {
                	return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // 创建文档对象
        Document doc = DocumentHelper.createDocument();
        // 创建根节点
        Element root = doc.addElement("xml");
        // 根节点添加identify属性
        if (!StringUtils.isEmpty(rootTag)) {
            root.addAttribute(IDENTIFY, rootTag);
        }
        // 从inputList添加input节点
        for (int i = 0; i < inputList.size(); i++) {
            // 创建input节点
            Element inputElement = root.addElement("input");
            // 赋值
            if (!StringUtils.isEmpty(inputList.get(i).getIdentify())) {
                inputElement.addAttribute(IDENTIFY, inputList.get(i).getIdentify());
            }
            if (!StringUtils.isEmpty(inputList.get(i).getType())) {
                inputElement.addAttribute("type", inputList.get(i).getType());
            }
            if (!StringUtils.isEmpty(inputList.get(i).getDescription())) {
                inputElement.addAttribute("des", inputList.get(i).getDescription());
            }
            if (!StringUtils.isEmpty(inputList.get(i).getValue())) {
                inputElement.setText(inputList.get(i).getValue());
            }
        }
        // 从outputList添加outputs节点
        for (int i = 0; i < outputList.size(); i++) {
            // 创建input节点
            Element inputElement = root.addElement("output");
            // 赋值
            if (!StringUtils.isEmpty(outputList.get(i).getIdentify())) {
                inputElement.addAttribute(IDENTIFY, outputList.get(i).getIdentify());
            }
            if (!StringUtils.isEmpty(outputList.get(i).getType())) {
                inputElement.addAttribute("type", outputList.get(i).getType());
            }
            if (!StringUtils.isEmpty(outputList.get(i).getDescription())) {
                inputElement.addAttribute("des", outputList.get(i).getDescription());
            }
            if (!StringUtils.isEmpty(outputList.get(i).getValue())) {
                inputElement.setText(outputList.get(i).getValue());
            }
        }
        // 设置XML文档格式
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        // 设置XML编码方式,即是用指定的编码方式保存XML文档到字符串(String),这里也可以指定为GBK或是ISO8859-1
        outputFormat.setEncoding("UTF-8");
        // outputFormat.setSuppressDeclaration(true); //是否生产xml头
        outputFormat.setIndent(true); // 设置是否缩进
        outputFormat.setIndent("    "); // 以四个空格方式实现缩进
        outputFormat.setNewlines(true); // 设置是否换行
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(outputFormat);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        try {
            FileOutputStream fos = new FileOutputStream(path);
            assert writer != null;
            writer.setOutputStream(fos);
            writer.write(doc);
            writer.close();
            logger.info("xml入库成功{}", path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private class XmlDTO {
        private String identify; // 标识
        private String type; // 数据类型
        private String description; // 描述
        private String value; // 值

        public XmlDTO() {
            super();
        }

        public String getIdentify() {
            return identify;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getValue() {
            return value;
        }
    }
}
