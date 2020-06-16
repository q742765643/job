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
 * 多服务直通车
 * @author chensi
 * 2019年5月20日
 */
@Transactional
@Service("trainTwoService")
public class TrainTwoService extends BasePlugin {
	
	private static Logger logger = LoggerFactory.getLogger(TrainTwoService.class.getName());
	
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
		Set<String> dealIssuee = new HashSet<String>();
		File inputDir = new File(inputPath);
		if (inputDir.exists()) {
			logger.info("当前路径：" + inputPath +" ,已存在");
			for (String issue : issueesList) {
				String Path=inputPath + issue.substring(0, 6) + "/";
				File file1=new File(Path + issue.substring(0, 8) + "0800");
				File file2=new File(Path + issue.substring(0, 8) + "2000");
				File[] dataFileList08 = FileUtil.getDataFileList(file1.getAbsolutePath(), fileFormat.replace("issue", issue));
				File[] dataFileList20 = FileUtil.getDataFileList(file2.getAbsolutePath(), fileFormat.replace("issue", issue));
				if(null != dataFileList08 && 0 != dataFileList08.length) {
					dealIssuee.add(issue);
				}else if(null != dataFileList20 && 0 != dataFileList20.length){
					dealIssuee.add(issue);
				}
			}
		}
		issueList.addAll(dealIssuee);

		return issueList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) throws IOException {

		LinkedHashMap dynamicParameter = triggerParam.getDynamicParameter();
		Product product = productService.findById(triggerParam.getProductId());
		
		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String inputPath = ((String) dynamicParameter.get("inputPath")).replace("\\", "/");
		String preFilePath = ((String) dynamicParameter.get("preFilePath")).replace("\\", "/");	
		String inputFile = inputPath + "/";
		String areaID = (String) dynamicParameter.get("areaID");
		String fileFormat = (String) dynamicParameter.get("fileFormat");
		String preFileFormat = (String) dynamicParameter.get("preFileFormat");
		logger.info("preFileFormat：" + preFileFormat );
		File file1=new File(inputFile + issue.substring(0, 6) + "/" + issue.substring(0, 8) + "0800");
		File file2=new File(inputFile + issue.substring(0, 6) + "/" + issue.substring(0, 8) + "2000");
		if(file1.exists()) {
			inputPath = file1.getAbsolutePath();
		}else if(file2.exists()){
			inputPath = file2.getAbsolutePath();
		}

		File[] dataFiles = FileUtil.getDataFileList(inputPath, fileFormat.replace("issue", issue.substring(0, 8)));
		File[] preFiles = FileUtil.getDataFileList(preFilePath + "/" + issue.substring(0, 8), preFileFormat.replace("issue", issue.substring(0, 8)));
		String[] fileArrays = new String[dataFiles.length];
		String[] preFileArrays = new String[preFiles.length];
		for (int i = 0; i < dataFiles.length; i++) {
			File file = dataFiles[i];
			fileArrays[i] = file.getAbsolutePath();
		}
		for (int i = 0; i < preFiles.length; i++) {
			File file = preFiles[i];
			preFileArrays[i] = file.getAbsolutePath();
		}
		
		Arrays.sort(fileArrays);
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("inputFile",fileArrays[fileArrays.length-1]);
		InputParamMap.put("PreFile",preFileArrays[preFileArrays.length-1]);
		InputParamMap.put("issue", changeIssueToIssue12(issue));

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
