package com.htht.job.executor.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.BaseShardService;
import com.htht.job.executor.service.LYYProcessService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.htht.util.DateUtil;
import org.htht.util.FileOperate;
import org.htht.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Transactional
@Service("lyyProcessService")
public class LYYProcessServiceImpl implements LYYProcessService {
	
	private static Logger logger = LoggerFactory.getLogger(LYYProcessServiceImpl.class.getName());

    @Autowired
    private RedisService redisService;

    @Autowired
    protected AtomicAlgorithmService atomicAlgorithmService;

    @Autowired
    private DictCodeService dictCodeService;

    @Autowired
    protected ProductUtil productUtil;

    @Autowired
    protected ProductInfoService productInfoService;

    @Autowired
    protected ProductFileInfoService productFileInfoService;

    /**
     * 执行业务
     *
     * @param triggerParam
     * @param result
     * @return
     */
    @Override
    public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
        /** 1.获取参数列表 **/
        LinkedHashMap<?, ?> fixmap = triggerParam.getFixedParameter();

        /** 2.解析产品参数 **/
        String exePath = (String) fixmap.get("exePath");
        String outputlogpath = triggerParam.getLogFileName();
        String projectKey = (String) fixmap.get("projectKey");
        XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);

        // 算法标识
        String algorId = triggerParam.getAlgorId();
        AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(algorId);
        DictCode productPath = dictCodeService.findOneself("productPath");

        String issue = "";
        String cycle = "";

        try {
            // 制作输入xml
            String paramStr = triggerParam.getExecutorParams();
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> inputxmlParam = gson.fromJson(paramStr, type);
            String outputXml = inputxmlParam.get("outXMLPath");
            String inputXml = outputXml.replace("outputXml", "inputXml");

            issue = inputxmlParam.get("issue");
            cycle = inputxmlParam.get("cycle");

            List<XmlDTO> inputList = FormatXmlParam(inputxmlParam);
            XmlUtils XmlUtils = new XmlUtils();
            if (XmlUtils.createAlgorithmXml(projectKey, inputList,
                    new ArrayList<XmlDTO>(), inputXml)) {
                /** =======4.执行脚本=========== **/
                XxlJobLogger.logByfile(outputlogpath, inputXml + "正在执行算法");
                ServerImpUtil.executeCmd(exePath, inputXml);
                XxlJobLogger.logByfile(outputlogpath, inputXml + "算法运行完毕");
            }
            /** ========5.脚本 结束======= **/
            File outputXmlFile = new File(outputXml);
            if (!outputXmlFile.exists()) {

                result.setErrorMessage("outputXmlFile文件不存在，入库失败");
                XxlJobLogger.logByfile(outputlogpath, "outputXmlFile文件不存在，路径为："
                        + outputXml);

                // 释放redis
                if (redisService.exists(projectKey + issue)) {
                    redisService.remove(projectKey + issue);
                }

                return result;
            }
            XxlJobLogger.logByfile(outputlogpath, "开始读取输出xml文件" + outputXml);

            if (!XmlUtils.isSuccessByXml(outputXml)) {
                result.setErrorMessage("outputxml显示算法失败" + outputXml);
                XxlJobLogger.logByfile(outputlogpath, "算法执行失败，执行的算法为："
                        + exePath + "参数为：" + inputXml);

                // 释放redis
                if (redisService.exists(projectKey + issue)) {
                    redisService.remove(projectKey + issue);
                }
                return result;
            }

            XxlJobLogger.logByfile(outputlogpath, "算法执行成功，准备入库");
            //读取xml，把xml转换成map对象
            Map<String,List<Element>> map = XmlUtils.outputFilesXmlToMap(outputXml);

            List<String> regionIdList = XmlUtils.getXmlAttrVal(map,"region", "identify");

            List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(map, "mosaicFile");
            String mosaicFile = "";
            if (mosaicFiles.size() > 0) {
                mosaicFile = mosaicFiles.get(0);
            }
            if (regionIdList != null && regionIdList.size() > 0) {
                for (String regionId : regionIdList) {
                    //查重 并删除
                    List<ProductInfo> pis = productInfoService.findProductExits(triggerParam.getProductId(), issue, cycle, at.getModelIdentify(), null, regionId);
                    for(ProductInfo pi : pis){
                        productInfoService.deleteProductInfo(pi.getId());
                    }
                    // 产品信息及文件信息入库
                    ProductInfo productInfo = productUtil.saveProductInfo(
                            triggerParam.getProductId(), regionId, issue,
                            cycle, mosaicFile, at.getModelIdentify(), inputXml);

                    //查重
                    productFileInfoService.deleteByproductInfoId(productInfo.getId());

                    List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(map, "region","identify",regionId);
                    for (String file : lFiles) {
                        productUtil.saveProductInfoFile(productInfo.getId(),
                                file.replace("\\", "/"),
                                productPath.getDictCode(), regionId, issue,
                                cycle);
                    }

                }
            }
            
            // 产品结果信息入库
			tableElementDeal(triggerParam.getProductId(), at.getModelIdentify(), cycle, outputXml, inputXml,XmlUtils);

            if (!result.isSuccess()) {
                result.setErrorMessage("入库出错");
                XxlJobLogger.logByfile(outputlogpath, "入库失败");
                // 释放redis
                if (redisService.exists(projectKey + issue)) {
                    redisService.remove(projectKey + issue);
                }
                return result;
            }

            XxlJobLogger.logByfile(outputlogpath, "算法执行成功，开始入库");
            XxlJobLogger.logByfile(outputlogpath, "执行成功");
            result.setResult("成功");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.setErrorMessage("出现异常");
            XxlJobLogger.logByfile(outputlogpath, issue + "期次出现异常");
            // 释放redis
            if (redisService.exists(projectKey + issue)) {
                redisService.remove(projectKey + issue);
            }
            throw new RuntimeException();
        }
        result.setMessage("执行完毕");
        // 释放redis
        if (redisService.exists(projectKey + issue)) {
            redisService.remove(projectKey + issue);
        }
        return result;
    }


	private void tableElementDeal(String productId,
			String modelIdentify, String cycle, String outputXml, String inputXml,XmlUtils XmlUtils) {
		List<Element> xmllists = XmlUtils.getTablenameElements(outputXml, "table");

		
		if (null == xmllists || xmllists.size() == 0) {
			 logger.info("LYY统计表信息为空，不进行统计入库 ;");
		} else {
			for (Element e : xmllists) {
				// 获取数据集
				List<Element> list2 = e.elements();
				List<String> cc = new ArrayList<>();
				for (Element e2 : list2) {
					if ("values".equals(e2.getName())) {
						cc.add(e2.getText());
					}
				}
				// 封数据执行入库
				ProductAnalysisTableInfo productAnalysisTableInfo = new ProductAnalysisTableInfo(
						e.attribute("identify").getValue());
				String[] fields = (e.element("field").getText() + ",product_info_id,Cycle,model_identify,file_name")
						.replace("'", "").trim().split(",");
				String otherValue = "," + productId + "," + cycle
						+ "," + modelIdentify + "," + inputXml;

				for (int i = 0; i < fields.length; i++) {

					for (String s : cc) {

						String[] values = (s + otherValue).replace("'", "")
								.replace(",", " ").trim().split("\\s+");
						productAnalysisTableInfo.addFieldAndValue(
								fields[i].trim(), values[i]);
					}
				}
				productUtil.saveProductDetail(productAnalysisTableInfo);
			}
		}
	}


    /**
     * 执行业务2
     *
     * @param params 任务参数
     * @param fixmap 固定参数
     * @param dymap  输入参数
     * @return
     */
    @Override
    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) {
        ResultUtil<List<String>> result = new ResultUtil<List<String>>();
        List<String> list = new ArrayList<String>();
        String projectKey = (String) fixmap.get("projectKey");
        String issue = "";

        try {
            /** 解析产品参数 **/
            String startTime = (String) fixmap.get("startTime");
            String endTime = (String) fixmap.get("endTime");
            String xmlPath = (String) fixmap.get("xmlPath");
            String logPath = (String) fixmap.get("logPath");

            String inputPath = (String) dymap.get("inputPath");
            String areaID = (String) dymap.get("areaID");
            String assignDaysNum = (String) dymap.get("assignDaysNum");
            String outFolder = (String) dymap.get("outputPath");

            String cycle = "COOD";
            if (dymap.containsKey("cycle")) {
                cycle = (String) dymap.get("cycle");
            }

            // 默认设置要处理的数据时间个数
            String dataTime = "{yyyy}{MM}{dd}{HH}{mm}";
            if (dymap.containsKey("dataTime")) {
                dataTime = (String) dymap.get("dataTime");
            }

            // 要处理的数据时间段
            Calendar calendar = Calendar.getInstance();
            Date doEndTime = calendar.getTime();
            if (StringUtils.isNotEmpty(endTime)) {
                doEndTime = DateUtil.strToDate(endTime, "yyyy-MM-dd");
            }
            Date doStartTime = null;
            if (StringUtils.isNotEmpty(startTime)) {
                doStartTime = DateUtil.strToDate(startTime, "yyyy-MM-dd");
                // 获取需要处理的数据
                calendar.setTime(doStartTime);
            }
            calendar = MatchTime.getCalendar(calendar.getTime(), dataTime);

            // 确保同一数据只执行一次
            while (calendar.getTimeInMillis() <= doEndTime.getTime()) {
                issue = MatchTime.matchIssue(calendar.getTime(), cycle);
                // 缓存中已经存在，就进行下一个期次
                if (redisService.exists(projectKey + issue)) {
                    calendar = MatchTime.getCalendarByCycle(calendar, cycle);
                    continue;
                }

                // 放入缓存 防止同一时间执行同一期次,不然输入xml会覆盖
                redisService.add(projectKey + issue, issue);

                String outLogPath = logPath + File.separator + projectKey+File.separator
                        + issue + ".log";
                FileOperate.newParentFolder(outLogPath);

                String outputXml = xmlPath + File.separator + "outputXml"
                        + File.separator + issue + File.separator + issue
                        + ".xml";
                FileOperate.newParentFolder(outputXml);

                // 制作xml参数
                Map<String, Object> inputxmlParam = new HashMap<>();

                inputxmlParam.put("inputFile", inputPath);
                inputxmlParam.put("areaID", areaID);
                inputxmlParam.put("issue", issue);
                inputxmlParam.put("cycle", cycle);
                inputxmlParam.put("assign_days_num", assignDaysNum);
                inputxmlParam.put("outFolder", outFolder);
                inputxmlParam.put("outXMLPath", outputXml);
                inputxmlParam.put("outLogPath", outLogPath);
                Gson gson = new Gson();
                String inputParam = gson.toJson(inputxmlParam);
                if (!list.contains(inputParam)) {
                    list.add(inputParam);
                }
                calendar = MatchTime.getCalendarByCycle(calendar, cycle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 释放redis
            if (redisService.exists(projectKey + issue)) {
                redisService.remove(projectKey + issue);
            }
        }

        if (!result.isSuccess()) {
            return result;
        }

        result.setResult(list);
        if (list.size() < 1) {
            result.setMessage("本次调度没有需要处理的数据，期次是" + issue);
        }
        /** 返回结果 **/
        return result;
    }

    /**
     * map集合转化为list集合
     *
     * @param map
     * @return
     */
    private List<XmlDTO> FormatXmlParam(Map<String, String> map) {
        List<XmlDTO> inputList = new ArrayList<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            XmlDTO inputFileXmlDTO = new XmlDTO();
            inputFileXmlDTO.setIdentify(key);
            inputFileXmlDTO.setValue(map.get(key));
            inputFileXmlDTO.setDescription(" ");
            inputFileXmlDTO.setType("string");
            inputList.add(inputFileXmlDTO);
        }
        return inputList;
    }
}
