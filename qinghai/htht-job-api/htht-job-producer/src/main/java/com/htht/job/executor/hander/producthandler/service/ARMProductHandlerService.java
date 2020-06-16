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

import java.util.*;

/**
 * Created by atom on 2018/11/9.
 */


@Transactional
@Service("ARMProductHandlerService")
public class ARMProductHandlerService {

    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private ProductUtil productUtil;



    public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
        try{
        String algorId = triggerParam.getAlgorId();
        LinkedHashMap dymap = triggerParam.getDynamicParameter();
        LinkedHashMap fixmap = triggerParam.getFixedParameter();
        String exePath = (String) fixmap.get("exePath");
        String issue= triggerParam.getExecutorParams();
        String inputdataPath=(String) dymap.get("inputFile");
        if (!inputdataPath.endsWith("\\")){
            inputdataPath=inputdataPath+"\\";
        }





        //输出xml目录
         String outXMLPath = (String) dymap.get("outXMLPath");

        // 周期
        String cycle = (String) dymap.get("cycle");




        //算法标识
        AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(algorId);

        /** =======1.拼装dymap=========== **/


        String fileName = issue;

        if(!outXMLPath.endsWith("\\")){
            outXMLPath=outXMLPath+"\\";
        }

        String outputxmlpath = outXMLPath +"outPutXml\\ARM_SO\\AnHui_ARM_SO_"+ issue+"000000.xml";
        String inputxmlpath = outXMLPath +"inPutXml\\ARM_SO\\AnHui_ARM_SO_"+ issue+"000000.xml";
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

        //String inputdataPath = new File(triggerParam.getExecutorParams()).getParentFile().getParent();
        //String inputdataPath=(String) dymap.get("inputFile");
        String type = "";
        List<XmlDTO>  inputList = new ArrayList<>();
        Map<String,Object> map  = new HashMap<String,Object>();

        type = "ARM_SO";
        map.put("areaID",dymap.get("areaID"));
        map.put("issue",issue);
        map.put("inputFile",inputdataPath);
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
        map2.put("outFolder", dymap.get("outFolder"));
        map2.put("outXMLPath",outputxmlpath);
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
        XmlUtils.createAlgorithmXml(type,inputList,outputList,inputxmlpath);

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
            String[] fields = (e.element("field").getText()+",product_info_id,cycle,model_identify,file_name").replace("'","").trim().split(",");
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
    } catch (Exception e)
    {
        result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
        throw new RuntimeException();
    }
		return result;
    }
}
