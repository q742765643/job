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
 * h8_fire
 * @author liuconghui
 * 2018年12月26日
 */
@Transactional
@Service("h8FireService")
public class H8FireService extends BasePlugin {
	
	private static Logger logger = LoggerFactory.getLogger(H8FireService.class.getName());
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;

	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat,String fileFormat) throws IOException {

		List<String> issueList = new ArrayList<>();
		Set<String> issueesList = new HashSet<String>();
		if (doStartTime == null || doEndTime == null) {
			return null;
		}
		if (!inputPath.endsWith("/")) {
			inputPath += "/";
		}
		if (issueFormat.contains("{")) {//yyyyMMddHHmm
			String dateFormat = issueFormat.replace("{", "").replace("}", "").replace("-", "").replaceAll("\\d", "");
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(doStartTime); 
			
			Date tempTime = calendar.getTime();
			while (!tempTime.after(doEndTime)) {
				String issueDate = formatter.format(tempTime);
				issueesList.add(issueDate);
				calendar.add(Calendar.MINUTE, 10);
				tempTime = calendar.getTime();
			}
		   }else{
				String[] issuees = issueFormat.split(",");
				for (String issu : issuees) {
					String newIssue=issu;
					issueesList.add(newIssue);
			}
		}
		Set<String> dealIssuees = new HashSet<String>();
		File inputDir = new File(inputPath);
		if (inputDir.exists()) {
			logger.info("当前路径：" + inputPath +" ,已存在");
			for (String issue : issueesList) {
				String issueForm= "_" + issue.substring(0,8) + "_" + issue.substring(8,12) + "_";
				 File[] fileList=FileUtil.getDataFileList(inputPath+issue.substring(0,8),issueForm);
				 logger.info("there have ( " + fileList.length + " ) files with issueFormat = " + issueForm);
				 if (fileList != null && fileList.length != 0) {
					 dealIssuees.add(issue);
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
		
		File datafile=new File(inputPath + "/" + issue.substring(0, 8) + "/");
		String inputFile=datafile.getAbsolutePath();
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputFile", inputFile);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", issue.substring(0, 8) + "_" + issue.substring(8, 12));

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
