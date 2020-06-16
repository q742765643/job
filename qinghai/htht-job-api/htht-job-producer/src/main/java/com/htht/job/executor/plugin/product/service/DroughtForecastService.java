package com.htht.job.executor.plugin.product.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.htht.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.plugin.common.BasePlugin;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.productinfo.ProductInfoService;

/**
 * 
 * 干旱预测插件
 * @author liuconghui
 * 2019年1月10日
 */
@Transactional
@Service("droughtForecastService")
public class DroughtForecastService extends BasePlugin {
	
	private static Logger logger = LoggerFactory.getLogger(DroughtForecastService.class.getName());
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;

	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat , String fileFormat) throws IOException  {
		List<String> issueList = new ArrayList<String>();
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
		File inputDir = new File(inputPath);
		if (inputDir.exists()) {
			for(String issue:issueesList) {
				String issue08 = issue.substring(0, 8) + "0800";
				String issue20 = issue.substring(0, 8) + "2000";
				File[] dataFile08=FileUtil.getDataFileList(inputPath+issue.substring(0, 8), fileFormat.replace("issue", issue08));
				File[] dataFile20=FileUtil.getDataFileList(inputPath+issue.substring(0, 8), fileFormat.replace("issue", issue20));
				if (null != dataFile08 && 0 != dataFile08.length ) {
					dealIssuees.add(issue08);
				} else if (null != dataFile20 && 0 != dataFile20.length) {
					dealIssuees.add(issue20);
				}else {
					logger.info("当前期号：" + issue + "，没有找到匹配的文件");
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
		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String areaID = (String) dynamicParameter.get("areaID");
		String inputPath = (String) dynamicParameter.get("inputPath");
		String fileFormat = (String) dynamicParameter.get("fileFormat");
		String drtFactorA = (String) dynamicParameter.get("drt_factorA");
		String drtFactorB = (String) dynamicParameter.get("drt_factorB");
		String drtFactorC = (String) dynamicParameter.get("drt_factorC");
		String drtFactorD = (String) dynamicParameter.get("drt_factorD");
		String reclass1 = (String) dynamicParameter.get("reclass1");
		String reclass2 = (String) dynamicParameter.get("reclass2");
		
		File[] dataFiles=FileUtil.getDataFileList(inputPath+issue.substring(0, 8), fileFormat.replace("issue", issue));
		String[] fileArrays = new String[dataFiles.length];
		for (int i = 0; i < dataFiles.length; i++) {
			File file = dataFiles[i];
			fileArrays[i] = file.getAbsolutePath();
		}
		Arrays.sort(fileArrays);
		String inputFile = fileArrays[fileArrays.length-1];
		
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("issue", issue);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("inputFile", inputFile);
		InputParamMap.put("drt_factorA", drtFactorA);
		InputParamMap.put("drt_factorB", drtFactorB);
		InputParamMap.put("drt_factorC", drtFactorC);
		InputParamMap.put("drt_factorD", drtFactorD);
		InputParamMap.put("reclass1", reclass1);
		InputParamMap.put("reclass2", reclass2);
		return InputParamMap;
	}

	@Override
	public List<Product> getProducts(Product product) {
		List<Product> productList = new ArrayList<>();
		productList.add(product);
		return productList;
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
