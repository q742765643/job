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
 * 霜冻监测
 * @author liuconghui
 * 2019年1月10日
 */
@Transactional
@Service("frostMonitorService")
public class FrostMonitorService extends BasePlugin {

	private static Logger logger = LoggerFactory.getLogger(FrostMonitorService.class.getName());
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;

	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat, String fileFormat) throws IOException {

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
		File inputDir = new File(inputPath);
		if (inputDir.exists()) {
			logger.info("当前路径：" + inputPath +" ,已存在");
			for (String issue : issueesList) {
				String Path=inputPath + issue.substring(0, 6) + "/" + issue.substring(0, 8) + "/RT/HOR/";
				File file=new File(Path);
				File[] dataFiles = FileUtil.getDataFileList(file.getAbsolutePath(), fileFormat.replace("issue", issue.substring(0, 8)));
				if(null != dataFiles && 0 != dataFiles.length) {
					dealIssuee.add(issue);
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
		String mubanPath = (String) dynamicParameter.get("mubanPath");
		String configXml = (String) dynamicParameter.get("sd_config_xml");
		String fileFormat = (String) dynamicParameter.get("fileFormat");
		
		
		if (!inputPath.endsWith("/")) {
			inputPath = inputPath + "/";
		}
		inputPath = inputPath + issue.substring(0, 6) + "/" + issue + "/RT/HOR/";
		File[] dataFiles = FileUtil.getDataFileList(inputPath, fileFormat.replace("issue", issue.substring(0, 8)));
		String[] fileArrays = new String[dataFiles.length];
		for (int i = 0; i < dataFiles.length; i++) {
			File file = dataFiles[i];
			fileArrays[i] = file.getAbsolutePath();
		}
		Arrays.sort(fileArrays);
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputFile", fileArrays[fileArrays.length-1]);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", changeIssueToIssue12(issue));
		InputParamMap.put("mubanPath", mubanPath);
		InputParamMap.put("sd_config_xml", configXml);
		return InputParamMap;
	}

	@Override
	public List<Product> getProducts(Product product) {
		
		return productService.getProductsByParentId(product.getTreeId());
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
