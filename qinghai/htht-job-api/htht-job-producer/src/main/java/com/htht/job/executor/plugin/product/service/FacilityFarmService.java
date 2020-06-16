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
 * 设施农业农用天气预报格点数据
 * @author liuconghui
 * 2019年1月09日
 */
@Transactional
@Service("facilityFarmService")
public class FacilityFarmService extends BasePlugin {
	
	private static Logger logger = LoggerFactory.getLogger(FacilityFarmService.class.getName());
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat, String fileFormat) throws IOException {

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
		File inputDir = new File(inputPath);
		if (inputDir.exists()) {
			logger.info("当前路径：" + inputPath +" ,已存在");
			for (String issue : issueesList) {
				String Path=null;
				File file1=new File(inputPath+issue.substring(0,6)+"/"+issue.substring(0,8)+"0800");
				File file2=new File(inputPath+issue.substring(0,6)+"/"+issue.substring(0,8)+"2000");
				if(file1.exists()) {
					Path=file1.getAbsolutePath();
				}else if(file2.exists()) {
					Path=file2.getAbsolutePath();
				}else{
					logger.info("期号：" + issue + ",没有匹配的数据");
					continue;
				}
				File[] dataFileList = FileUtil.getDataFileList(Path, fileFormat.replace("issue", issue));	
				for (File dataFile : dataFileList) {
					String[] split = dataFile.getName().split("_");
					String issueName = split[4].substring(0, 8);
					dealIssuees.add(issueName);
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
		//String issueFormat = "_" + issue.substring(0, 4) + "_" + issue.substring(4, 6)+"_"+issue.substring(6, 8)+"_"+issue.substring(8, 10)+"_"+issue.substring(10, 12)+"_";
		
		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String inputPath = ((String) dynamicParameter.get("inputPath")).replace("\\", "/");		
		String areaID = (String) dynamicParameter.get("areaID");
		String fileFormat = (String) dynamicParameter.get("fileFormat");
		String Path=null;
		File file1=new File(inputPath+issue.substring(0,6)+"/"+issue.substring(0,8)+"0800");
		File file2=new File(inputPath+issue.substring(0,6)+"/"+issue.substring(0,8)+"2000");
		if(file1.exists()) {
			Path=file1.getAbsolutePath();
		}else {
			Path=file2.getAbsolutePath();
		}
		
			File[] dataFileList = FileUtil.getDataFileList(Path, fileFormat.replace("issue", issue));	
		String inputFile = "";
		if (null != dataFileList && 0 != dataFileList.length) {
			inputFile = dataFileList[dataFileList.length-1].getAbsolutePath();
		}
		if (issue.length() < 12) {
			for (int i = issue.length(); i < 12; i++) {
				issue = issue + "0";
			}
		}
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputFile", inputFile);
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
