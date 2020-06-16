package com.htht.job.executor.hander.producthandler.service;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;
import org.dom4j.Element;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

/**
 * Created by atom on 2018/11/14.
 */

@Transactional
@Service("GRIBProductHandlerService")
public class GRIBProductHandlerService {
    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;

    @Autowired
    private ProductUtil productUtil;

    public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
        try {
            String algorId = triggerParam.getAlgorId();
            LinkedHashMap dymap = triggerParam.getDynamicParameter();
            LinkedHashMap fixmap = triggerParam.getFixedParameter();
            String exePath = (String) fixmap.get("exePath");
            String areaID = (String) dymap.get("areaID");
            String cycle = (String) dymap.get("cycle");
            String outFolder = (String) dymap.get("outFolder");
            String params=triggerParam.getExecutorParams();
            String outXMLPath=(String) dymap.get("outXMLPath");
            String[] arrstr=params.split("@");
            String issue= arrstr[1];
            String inputfilepath=arrstr[0];
            String fileName=new File(inputfilepath).getName();

            //算法标识
            AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(algorId);

            /** =======1.拼装dymap=========== **/
            String xmlfileName = issue;

            if(!outXMLPath.endsWith("\\")){
                outXMLPath=outXMLPath+"\\";
            }

            String outputxmlpath = outXMLPath +"outPutXml\\SMDAS2_GRIB\\AnHui_SMDAS2_GRIB_"+ xmlfileName + ".xml";
            String inputxmlpath = outXMLPath +"inPutXml\\SMDAS2_GRIB\\AnHui_SMDAS2_GRIB_"+ xmlfileName + ".xml";

            String outputlogpath=triggerParam.getLogFileName();
            /** =======2.创建日志文件=========== **/
            XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
            // 回调需要用到
            triggerParam.setLogFileName(outputlogpath);

            /** =======3.判断是否存在文件=========== **/
            List<ProductInfo> productFile =
                    productInfoService.findProductExits(triggerParam.getProductId(),issue,cycle,at.getModelIdentify(),fileName,(String)dymap.get("areaID"));
            if (productFile.size() > 0){
                XxlJobLogger.logByfile(outputlogpath, " 产品已生产");
                result.setMessage("already done");
                return result;
            }


            /** =======4.生成文件=========== **/
            XxlJobLogger.logByfile(outputlogpath, "开始执行_xml");


            String type = "";
            List<XmlDTO>  inputList = new ArrayList<>();
            Map<String,Object> map  = new HashMap<String,Object>();

            type = "SMDAS2_GRIB";
            map.put("areaID",areaID);
            map.put("issue",issue);
            map.put("inputFile",inputfilepath);
            map.put("cycle",cycle);

            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                XmlDTO to = new XmlDTO();
                Map.Entry entry = (Map.Entry) iter.next();
                to.setIdentify(entry.getKey().toString());
                to.setValue(entry.getValue().toString());
                to.setDescription(" ");
                to.setType("string");
                inputList.add(to);
            }

            List<XmlDTO>  outputList = new ArrayList<>();
            Map<String,Object> map2  = new HashMap<String,Object>();
            map2.put("outFolder",outFolder);
            map2.put("outXMLPath","");
            map2.put("outLogPath",outputlogpath);
            Iterator iter2 = map2.entrySet().iterator();
            while (iter2.hasNext()) {
                XmlDTO to = new XmlDTO();
                Map.Entry entry = (Map.Entry) iter2.next();
                to.setIdentify(entry.getKey().toString());
                to.setValue(entry.getValue().toString());
                to.setDescription(" ");
                to.setType("string");
                outputList.add(to);
            }

            XmlUtils XmlUtils = new XmlUtils();
            XmlUtils.createAlgorithmXml(type,inputList,outputList,"inputxmlpath");

            if (!result.isSuccess())
            {
                return result;
            }

            /** =======5.执行脚本=========== **/
            XxlJobLogger.logByfile(outputlogpath, "正在执行_运行");
            ServerImpUtil.executeCmd(exePath, inputxmlpath);

            /** ========6.脚本 结束入库======= **/
            boolean b = XmlUtils.isSuccessByXml(outputxmlpath);
            if (!b) {
                result.setErrorMessage("产品生产失败");
                XxlJobLogger.logByfile(outputlogpath, "输出xml为空，生产失败");
                return result;
            }
            XxlJobLogger.logByfile(outputlogpath, "正在执行_入库");

            Map<String,List<Element>> outputFiles = XmlUtils
                    .outputFilesXmlToMap(outputxmlpath);
            List<String> regionIdList = XmlUtils.getXmlAttrVal(outputxmlpath, "region",
                    "identify");

            List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(
                    outputxmlpath, "mosaicFile");
            String mosaicFile = "";
            if (mosaicFiles.size() > 0) {
                mosaicFile = mosaicFiles.get(0);
            }

            // 产品信息及文件信息入库
            ProductInfo productInfo = new ProductInfo();
            if(regionIdList!=null&&regionIdList.size()>0) {
                for(String regionId:regionIdList) {
                     productInfo = productUtil.saveProductInfo(
                            triggerParam.getProductId(), regionId, issue,
                            (String) dymap.get("cycle"), mosaicFile,
                            at.getModelIdentify(), fileName);// TODO


                    List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(
                            outputxmlpath, "region","identify",regionId);
                    for (String file : lFiles) {
                        productUtil.saveProductInfoFile(productInfo.getId(), file,
                                (String) dymap.get("outFolder"), regionId, issue,
                                (String) dymap.get("cycle"));
                    }
                }
            }


            //产品结果信息入库
            XxlJobLogger.logByfile(outputlogpath, "正在执行_统计入库");

            List<Map<String,Object>> ls = new ArrayList<>();
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
                String[] fields = (e.element("field").getText()+",product_info_id,Cycle,model_identify,file_name").replace("'","").trim().split(",");
                String otherValue = ","+productInfo.getId()+","+(String)dymap.get("cycle")+","+at.getModelIdentify()+","+fileName;

                for(int i=0;i<fields.length;i++){
                    for(String s : cc){
                        String[] values = (s+otherValue).replace("'","").replace(","," ").trim().split("\\s+");
                        productAnalysisTableInfo.addFieldAndValue(fields[i].trim(), values[i].trim());
                    }
                }
                productUtil.saveProductDetail(productAnalysisTableInfo);
            }

            result.setResult("成功");
            if (!result.isSuccess()) {
                result.setErrorMessage("入库出错");
                return result;
            }
        }catch (Exception e){
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
            throw new RuntimeException();
        }
        return result;
    }
}
