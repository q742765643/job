package com.htht.job.executor.plugin.product.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.htht.util.DateUtil;
import org.htht.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.plugin.common.BasePlugin;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
/**
 * 
 * 积雪监测（Terra、Aqua）
 * @author chensi
 * 2018年12月11日
 */
@Transactional
@Service("snowMonitorService")
public class SnowMonitorService extends BasePlugin {
	
	private static Logger logger = LoggerFactory.getLogger(SnowMonitorService.class.getName());
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private DictCodeService dictCodeService;

	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat , String fileFormat) throws IOException {

		List<String> issueList = new ArrayList<>();
		Set<String> issueesList = new HashSet<String>();
		if (doStartTime == null || doEndTime == null) {
			return null;
		}
		if (!inputPath.endsWith("/")) {
			inputPath += "/";
		}
		if (issueFormat.contains("{")) {
			String dateFormat = issueFormat.replace("{", "").replace("}", "").replace("-", "").replaceAll("\\d", "");
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(doStartTime);
			Date tempTime = calendar.getTime();

			while (!tempTime.after(doEndTime)) {
				String issueDate = formatter.format(tempTime);
				issueesList.add(issueDate);
				calendar.add(Calendar.DATE, 1);
				tempTime = calendar.getTime();
			}

		} else {
			String[] issuees = issueFormat.split(",");
			for (String issu : issuees) {
				issueesList.add(issu);
			}
		}
		Set<String> dealIssuees = new HashSet<String>();
		
		DictCode start = dictCodeService.findOneself("startTime");
		DictCode end = dictCodeService.findOneself("endTime");
		int startTime = Integer.parseInt(start.getDictCode());
		int endTime = Integer.parseInt(end.getDictCode());
		String inputPathTemp = inputPath; 
		for (String issue : issueesList) {

			logger.info("issue：" + issue);
			issueFormat = "_" + issue.substring(0,8) + "_";
			logger.info("issueFormat：" + issueFormat);
			
			if (inputPathTemp.indexOf("{") > -1 && inputPathTemp.indexOf("}") > -1 ) {
				Date date = DateUtil.strToDate(issue.substring(0,8), "yyyyMMdd");
				inputPath = DateUtil.getPathByDate(inputPathTemp, date);
			}
			
			File[] dataFileList = FileUtil.getDataFileList(inputPath, fileFormat.replace("issue", issueFormat));
			if (null == dataFileList || 0 ==dataFileList.length) {
				logger.info("can't find file with fileFormat = " + fileFormat);
				continue;
			}
			for (File dataFile : dataFileList) {
				String[] split = dataFile.getName().split("_");
				String tempTime = split[6];
				String issueName = split[5]+split[6];
				//判断当前时间是否在有效范围内
				//数据时间 10点前，20点之后（世界时间） ，北京时间为：早10点  -晚18点
				if (Integer.parseInt(tempTime) < endTime && Integer.parseInt(tempTime) > startTime) {
					dealIssuees.add(issueName);
				}else{
					logger.info("期号 :" + issueName + ",不在有效范围内，舍弃" );
				}
			}
		}
		
		issueList.addAll(dealIssuees);

		return issueList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) throws IOException {

		LinkedHashMap dynamicParameter = triggerParam.getDynamicParameter();
		Product product = productService.findById(triggerParam.getProductId());
		String issueFormat = "_" + issue.substring(0, 8) + "_" + issue.substring(8, 12) + "_";
		
		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String inputPath = ((String) dynamicParameter.get("inputPath"));		
		if (inputPath.indexOf("{") > -1 && inputPath.indexOf("}") > -1 ) {
			Date date = DateUtil.strToDate(issue.substring(0,8), "yyyyMMdd");
			inputPath = DateUtil.getPathByDate(inputPath, date);
		}
		String areaID = (String) dynamicParameter.get("areaID");
		String fileFormat = (String) dynamicParameter.get("fileFormat");
		String sensorType = (String) dynamicParameter.get("sensorType");
//		fileFormat.replace("issue", issueFormat);
		File[] dataFileList = FileUtil.getDataFileList(inputPath, fileFormat.replace("issue", issueFormat));
		logger.info("文件有 :" + dataFileList.length +"个"  );
		String inputFile = "";
		if (null != dataFileList && 0 != dataFileList.length) {
			inputFile = dataFileList[dataFileList.length-1].getAbsolutePath();
		}

		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputFile", inputFile);
		InputParamMap.put("sensorType", sensorType);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", issue);

		return InputParamMap;
	}

	@Override
	public List<Product> getProducts(Product product) {

		List<Product> products = new ArrayList<>();
		products.add(product);
		return products;
	}

	@Override
	public boolean checkProductExists(List<Product> products, String issue12, String cycle, String modelIdentify,
			String fileName, String regionId) {
		
		fileName = null;
		regionId = null;
		boolean flag = true;
		for (Product product : products) {
			List<ProductInfo> productFile = productInfoService.findProductExits(product.getId(), issue12, cycle,
					modelIdentify, fileName, regionId);
			if (productFile.size() <= 0) {
				flag = false;
				break;
			}
		}
		
		return flag;
	}

}
