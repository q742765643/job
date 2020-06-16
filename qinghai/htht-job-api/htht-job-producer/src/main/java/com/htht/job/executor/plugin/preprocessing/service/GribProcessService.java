package com.htht.job.executor.plugin.preprocessing.service;

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

import org.htht.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.plugin.common.BasePlugin;
import com.htht.job.executor.service.product.ProductService;

@Transactional
@Service("gribProcessService")
public class GribProcessService extends BasePlugin{
	
	private static Logger logger = LoggerFactory.getLogger(GribProcessService.class.getName());
	
	@Autowired
	private ProductService productService;
	
	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat,
			String fileFormat) throws IOException {
		List<String> issueList = new ArrayList<>();
		Set<String> issueesList = new HashSet<String>();
		if (doStartTime == null || doEndTime == null) {
			return null;
		}
		if (!inputPath.endsWith("/")) {
			inputPath += "/";
		}
		if (issueFormat.contains("{")) {//yyyyMMddHH
			String dateFormat = issueFormat.replace("{", "").replace("}", "").replace("-", "").replace(":", "").replaceAll("\\d", "");
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(doStartTime);
			//设置time时间点 去执行任务
			if (issueFormat.contains(":")) {
				String[] split = issueFormat.split(":");
				String time = split[split.length-1].substring(0, 2);
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time));
			}
			
			Date tempTime = calendar.getTime();

			while (!tempTime.after(doEndTime)) {
				String issueDate = formatter.format(tempTime);
				issueesList.add(issueDate);
				calendar.add(Calendar.HOUR_OF_DAY, 1);
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
			logger.info("current path：" + inputPath +" ,exists");
			for (String issue : issueesList) {
				
				issueFormat = "_" + issue +"00_";
				logger.info("current directory：" + inputPath + issue.substring(0, 8) + ",format：" + issueFormat);
				File[] dataFileList = FileUtil.getDataFileList(inputPath + issue.substring(0, 8), issueFormat);
				logger.info("dataFileList.size: " +dataFileList.length);
				if (0 == dataFileList.length) {
					logger.info(" no matching files ");
					continue;
				}else {
					dealIssuees.add(changeIssueToIssue12(issue));
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
		String inputPath = ((String) dynamicParameter.get("inputPath")).replace("\\", "/");		
		String areaID = (String) dynamicParameter.get("areaID");
		String wgrib = (String) dynamicParameter.get("wgrib");
		String gdalTranslate = (String) dynamicParameter.get("gdal_translate");

		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputFile", inputPath);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", changeIssueToIssue12(issue));
		InputParamMap.put("wgrib", wgrib);
		InputParamMap.put("gdal_translate", gdalTranslate);
		
		return InputParamMap;
	}

	@Override
	public List<Product> getProducts(Product product) {
		List<Product> products = new ArrayList<>();
		products.add(product);
		return products;
	}

	@Override
	public boolean checkProductExists(List<Product> productList, String issue12, String cycle, String modelIdentify,
			String fileName, String regionId) {
		return false;
	}

}
