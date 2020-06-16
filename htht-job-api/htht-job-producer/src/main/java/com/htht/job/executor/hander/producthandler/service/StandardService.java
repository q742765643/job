package com.htht.job.executor.hander.producthandler.service;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;
import org.dom4j.Element;
import org.htht.util.DataTimeHelper;
import com.htht.job.core.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;


public abstract class StandardService {
    @Autowired
    protected ProductInfoService productInfoService;
    @Autowired
    protected ProductUtil productUtil;
    @Autowired
    protected AtomicAlgorithmService atomicAlgorithmService;
    @Autowired
    protected DataMataInfoService dataMataInfoService;

    public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result)
    {
        try
        {
            String algorId = triggerParam.getAlgorId();
            //算法标识
            AtomicAlgorithmDTO at = atomicAlgorithmService.findModelIdentifyById(algorId);
            LinkedHashMap dymap = triggerParam.getDynamicParameter();
            LinkedHashMap fixmap = triggerParam.getFixedParameter();
            //-------
            ResultUtil<String> res=makeXml(triggerParam,at,result);
            if (!res.isSuccess()){
                result.setErrorMessage(res.getMessage());
                return result;
            }
            //---
            String issue= (String) dymap.get("issue");
            String exePath = (String) fixmap.get("exePath");
            String fileName=(String) fixmap.get("fileName");
            String inputxmlpath= (String) fixmap.get("inputxmlpath");
            String outputxmlpath= (String) dymap.get("outXMLPath");
            String outputlogpath= (String) dymap.get("outLogPath");


            /*if (!result.isSuccess())
            {
                return result;
            }*/
            /** =======4.执行脚本=========== **/
            XxlJobLogger.logByfile(outputlogpath, "正在执行_运行");
            triggerParam.setLogFileName(outputlogpath);
            ServerImpUtil.executeCmd(exePath, inputxmlpath);
            boolean b = XmlUtils.isSuccessByXml(outputxmlpath);
            if (!b) {
                result.setErrorMessage("产品生产失败");
                XxlJobLogger.logByfile(outputlogpath, "输出xml为空，生产失败");
                return result;
            }
            /** ========5.脚本 结束入库======= **/
            File filelist = new File(outputxmlpath);
            if(!filelist.exists() || filelist.length() == 0){
                result.setErrorMessage("输出xml为空，入库失败");
                XxlJobLogger.logByfile(outputlogpath, "输出xml为空，入库失败");
                return result;
            }
            XxlJobLogger.logByfile(outputlogpath, "正在执行_文件及信息入库");
            statistics(triggerParam,outputxmlpath,outputlogpath,at,fileName,issue);
            XxlJobLogger.logByfile(outputlogpath, "执行成功");
            result.setResult("成功");
            if (!result.isSuccess()) {
                result.setErrorMessage("入库出错");
                return result;
            }
        } catch (Exception e)
        {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
            throw new RuntimeException();
        }
        return result;
    }

    void  statistics(TriggerParam triggerParam,String outputxmlpath,String outputlogpath,
                     AtomicAlgorithmDTO at,String fileName,String issue){
        LinkedHashMap dymap=triggerParam.getDynamicParameter();
        List<String> regionIdList = XmlUtils.getXmlAttrVal(outputxmlpath, "region",
                "identify");

        List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(
                outputxmlpath, "mosaicFile");
        String mosaicFile = "";
        if (mosaicFiles.size() > 0) {
            mosaicFile = mosaicFiles.get(0);
        }
        // 产品信息及文件信息入库
        ProductInfoDTO productInfoDTO = new ProductInfoDTO();
        if(regionIdList!=null&&regionIdList.size()>0) {
            for(String regionId:regionIdList) {
                productInfoDTO = productUtil.saveProductInfo(
                        triggerParam.getProductId(), regionId, issue,
                        (String) dymap.get("cycle"), mosaicFile,
                        at.getModelIdentify(), fileName);


                List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(
                        outputxmlpath, "region","identify",regionId);
                for (String file : lFiles) {
                    productUtil.saveProductInfoFile(productInfoDTO.getId(), file,
                            (String) dymap.get("outFolder"), regionId, issue,
                            (String) dymap.get("cycle"));
                }
            }
        }

        //产品结果信息入库
        XxlJobLogger.logByfile(outputlogpath, "正在执行_统计入库");


        List<Element> xmllists = XmlUtils.getTablenameElements(outputxmlpath,"table");
        for(Element e : xmllists){
            //获取数据集
            List<Element> list2 = e.elements();
            List<String> cc = new ArrayList<>();
            for(Element e2 : list2){
                if("values".equals(e2.getName())){
                    cc.add(e2.getText());
                }
            }
            //封数据执行入库
            ProductAnalysisTableInfo productAnalysisTableInfo = new ProductAnalysisTableInfo(e.attribute("identify").getValue());
            String[] fields = (e.element("field").getText()+",product_info_id").replace("'","").trim().split(",");
            String otherValue = ","+ productInfoDTO.getId();

            for(int i=0;i<fields.length;i++){
                for(String s : cc){
                    String[] values = (s+otherValue).replace("'","").replace(","," ").trim().split("\\s+");
                    productAnalysisTableInfo.addFieldAndValue(fields[i].trim(), values[i].trim());
                }
            }
            productUtil.saveProductDetail(productAnalysisTableInfo);
        }
    }

    ResultUtil<String> makeXml(TriggerParam triggerParam,AtomicAlgorithmDTO at,ResultUtil<String> result){
        LinkedHashMap dymap = triggerParam.getDynamicParameter();
        LinkedHashMap fixmap = triggerParam.getFixedParameter();

        String proMark = (String) fixmap.get("proMark");

        //取出每次分片文件名
        File f=new File(triggerParam.getExecutorParams());
        String fileName = new File(triggerParam.getExecutorParams()).getName();

        /** =======1.获取期次信息=========== **/
        String cycle = (String) dymap.get("cycle");
        proMark = StringUtils.trimAllWhitespace(proMark).toLowerCase();
        Date fileDate = null;
        String inputFolder="";
        if(proMark.equals("scanfolder")){
            String timePattern = (String) fixmap.get("timePattern");
            fileDate = new Date(DataTimeHelper.getDataTimeFromFileNameByPattern(fileName, timePattern));
            inputFolder=triggerParam.getExecutorParams();
        }else if(proMark.equals("scandatabase")){
        	if(f.isDirectory()) {
        		String dataSuffix = (String) fixmap.get("dataSuffix");
        		fileName = fileName+dataSuffix;
        		inputFolder=triggerParam.getExecutorParams()+File.separator+fileName;
        	}else {
        		inputFolder=triggerParam.getExecutorParams();
        	}
            String dataLevel = (String)fixmap.get("dataLevel");
            fileDate = dataMataInfoService.findDataByFileNameAndLevel(fileName,StringUtils.isEmpty(dataLevel)? "L2":dataLevel);
        }

        if(fileDate==null){
            result.setCode(1);
            result.setMessage("获取当前期次时间信息失败");
            return result;
        }

        String outFolder = (String)dymap.get("outFolder");
        // 获取期号
        String issue = MatchTime.matchIssue(fileDate,cycle);
        String outputlogpath = outFolder + File.separator + "outputXml" + File.separator + at.getModelIdentify()+ File.separator + fileName + ".log";
        XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
        /** =======2.判断是否以生产=========== **/
        List<ProductInfoDTO> productFile =
                productInfoService.findProductExits(triggerParam.getProductId(),issue,"",at.getModelIdentify(),fileName,(String)dymap.get("areaID"));

        if (productFile.size() > 0){
            XxlJobLogger.logByfile(outputlogpath, " 产品已生产");
            result.setCode(1);
            result.setMessage("already done");
            return result;
        }
        String inputxmlpath = outFolder + File.separator + "inputXml" + File.separator + at.getModelIdentify()
                + File.separator + fileName + ".xml";

        String outputxmlpath = outFolder + File.separator + "outputXml" + File.separator + at.getModelIdentify()
                + File.separator + fileName + ".xml";
        
        XxlJobFileAppender.makeLogFileNameByPath(inputxmlpath);
        XxlJobFileAppender.makeLogFileNameByPath(outputxmlpath);
        XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
        // 回调需要用到
        triggerParam.setLogFileName(outputlogpath);

        /** =======3.生成输入xml=========== **/
        XxlJobLogger.logByfile(outputlogpath, "开始执行_xml");
        String type = (String)fixmap.get("identify");
        List<XmlDTO>  inputList = new ArrayList<>();
        List<XmlDTO>  outputList = new ArrayList<>();
        Iterator iterator = dymap.entrySet().iterator();
        while(iterator.hasNext()){
            XmlDTO to = new XmlDTO();
            Map.Entry entry = (Map.Entry) iterator.next();
            to.setIdentify(entry.getKey().toString());
            to.setValue(entry.getValue().toString());
            to.setDescription(" ");
            to.setType("string");
            if(entry.getKey().toString().equalsIgnoreCase("outFolder")){
                outputList.add(to);
            }else{
                inputList.add(to);
            }
        }

        XmlDTO inputFileXmlDTO = new XmlDTO();
        inputFileXmlDTO.setIdentify("inputFile");
        inputFileXmlDTO.setValue(inputFolder);
        inputFileXmlDTO.setDescription(" ");
        inputFileXmlDTO.setType("string");
        inputList.add(inputFileXmlDTO);
        XmlDTO issueXmlDTO = new XmlDTO();
        issueXmlDTO.setIdentify("issue");
        issueXmlDTO.setValue(issue);
        issueXmlDTO.setDescription(" ");
        issueXmlDTO.setType("string");
        inputList.add(issueXmlDTO);
        XmlDTO outXMLPathXmlDTO = new XmlDTO();
        outXMLPathXmlDTO.setIdentify("outXMLPath");
        outXMLPathXmlDTO.setValue(outputxmlpath);
        outXMLPathXmlDTO.setDescription(" ");
        outXMLPathXmlDTO.setType("string");
        outputList.add(outXMLPathXmlDTO);
        XmlDTO outLogPathXmlDTO = new XmlDTO();
        outLogPathXmlDTO.setIdentify("outLogPath");
        outLogPathXmlDTO.setValue(outputlogpath);
        outLogPathXmlDTO.setDescription(" ");
        outLogPathXmlDTO.setType("string");
        outputList.add(outLogPathXmlDTO);

        XmlUtils.createAlgorithmXml(type,inputList,outputList,inputxmlpath);
        fixmap.put("inputxmlpath",inputxmlpath);
        dymap.put("inputFile",triggerParam.getExecutorParams());
        dymap.put("outXMLPath",outputxmlpath);
        dymap.put("outLogPath",outputlogpath);
        dymap.put("issue",issue);
        result.setCode(0);
        return result;
    }
}
