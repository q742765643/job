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
 * 洪涝预测
 * @author liuconghui
 * 2019年1月10日
 */
@Transactional
@Service("floodForecastService")
public class FloodForecastService extends BasePlugin {

	private static Logger logger = LoggerFactory.getLogger(FloodForecastService.class.getName());
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat, String fileFormat) throws IOException {

		logger.info("doStartTime : " + doStartTime);
		logger.info("doEndTime : " + doEndTime);
		List<String> dealIssuees = new ArrayList<String>();
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
		Set<String> dealIssuee = new HashSet<String>();
		File inputDir=new File(inputPath);
		if(inputDir.exists()) {
			logger.info("当前路径：" + inputPath +" ,已存在");
			for(String issue:issueesList) {
				File file=new File(inputDir+"\\"+issue.substring(0,6) + "\\" + issue.substring(0,8) + "0800");
				if(file.exists()) {
					dealIssuee.add(issue.substring(0,8) + "0800");
				}
			}
		}
		dealIssuees.addAll(dealIssuee);
		return dealIssuees;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) throws IOException {

		LinkedHashMap dynamicParameter = triggerParam.getDynamicParameter();
		Product product = productService.findById(triggerParam.getProductId());
		
		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String inputPath = ((String) dynamicParameter.get("inputPath")).replace("\\", "/");
		String areaID = (String) dynamicParameter.get("areaID");
		String format12 = (String) dynamicParameter.get("format12");
		String format24 = (String) dynamicParameter.get("format24");
		String mubanPath = (String) dynamicParameter.get("mubanPath");
		if (!inputPath.endsWith("/")) {
			inputPath = inputPath + "/";
		}
		File inputDir = new File(inputPath + issue.substring(0, 6) + "\\" + issue.substring(0,8) + "0800");
		File[] dataFile12 = FileUtil.getDataFileList(inputDir.getAbsolutePath(), format12.replace("issue", issue));
		File[] dataFile24 = FileUtil.getDataFileList(inputDir.getAbsolutePath(), format24.replace("issue", issue));
		
		//获取ER12格式的文件
		String[] file12Arrays = new String[dataFile12.length];
		for (int i = 0; i < dataFile12.length; i++) {
			File file = dataFile12[i];
			file12Arrays[i] = file.getAbsolutePath();
		}
		Arrays.sort(file12Arrays);
		//获取ER24格式的文件
		String[] file24Arrays = new String[dataFile24.length];
		for (int i = 0; i < dataFile24.length; i++) {
			File file = dataFile24[i];
			file24Arrays[i] = file.getAbsolutePath();
		}
		Arrays.sort(file12Arrays);
		
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputFile", file12Arrays[file12Arrays.length-1]);
		InputParamMap.put("inputFile2", file24Arrays[file24Arrays.length-1]);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", issue);
		InputParamMap.put("mubanPath", mubanPath);
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
