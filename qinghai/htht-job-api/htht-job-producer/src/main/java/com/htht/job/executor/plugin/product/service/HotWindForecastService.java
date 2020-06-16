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
 * 干热风预测
 * @author liuconghui
 * 2019年1月21日
 */
@Transactional
@Service("hotWindForecastService")
public class HotWindForecastService extends BasePlugin {

	private static Logger logger = LoggerFactory.getLogger(HotWindForecastService.class.getName());

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
				
				File file = new File(inputPath + issu);
				if (!file.exists()) {
					logger.info("当前路径不存在" + file.getAbsolutePath());
					continue;
				}
				issueesList.add(issu);
			}
		}
		dealIssuees.addAll(issueesList);

		return dealIssuees;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) {

		LinkedHashMap dynamicParameter = triggerParam.getDynamicParameter();
		Product product = productService.findById(triggerParam.getProductId());
		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String inputPath = ((String) dynamicParameter.get("inputPath")).replace("\\", "/");
		String areaID = (String) dynamicParameter.get("areaID");
        String mubanPath=(String) dynamicParameter.get("mubanPath");
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputFile", inputPath+issue);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", changeIssueToIssue12(issue));
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

//	@Autowired
//	private ProductFileInfoService productFileInfoService;
//
////	@Autowired
////	private ProductStorageService productStorageService;
//
//	@Autowired
//	private ProductInfoService productInfoService;
//
//	@SuppressWarnings("rawtypes")
//	@Override
//	protected Map<String, Object> initCallParamterMap(TriggerParam triggerParam, String issue) {
//
//		Map dynamicParameter = triggerParam.getDynamicParameter();
//		String outputPath = (String) dynamicParameter.get("output_path");
//		String cycle = (String) dynamicParameter.get("cycle");
//		for (int i = issue.length(); i < 12; i++) {
//			issue = issue + "0";
//		}
//
//		Map<String, Object> callParamterMap = new HashMap<String, Object>();
//		callParamterMap.put("output_path", outputPath);
//		callParamterMap.put("cycle", cycle);
//		callParamterMap.put("issue", issue);
//
//		return callParamterMap;
//	}
//
//	@SuppressWarnings("rawtypes")
//	@Override
//	protected Map<String, Object> initBusinessParamterMap(TriggerParam triggerParam) {
//
//		Map<String, Object> businessParamterMap = new HashMap<String, Object>();
//		Map dymap = triggerParam.getDynamicParameter();// 动态参数
//		ProductParam productParam = null;// 页面模板参数
//		try {
//			productParam = JSON.parseObject(triggerParam.getModelParameters(), ProductParam.class);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		String exePath = productParam.getExePath();
//		String inputxmlPath = productParam.getInputxml();
//		String outputlogPath = (String) dymap.get("outputlog");// 动态参数：建议写到页面模板参数中
//		String issue = (String) dymap.get("issue");
//		String filePath = ((String) dymap.get("input_path")).replace("\\", "/");
//		String outfilePath = (String) dymap.get("output_path");
//		if (outputlogPath == null) {
//			outputlogPath = BusinessConst.BASE_LOG_PATH + "hotWindYC_log";
//		}
//
//		businessParamterMap.put("exePath", exePath);
//		businessParamterMap.put("inputxmlPath", inputxmlPath);
//		businessParamterMap.put("outputlogPath", outputlogPath);
//		businessParamterMap.put("issue", issue);
//		businessParamterMap.put("filePath", filePath);
//		businessParamterMap.put("outfilePath", outfilePath);
//
//		return businessParamterMap;
//	}
//
//	@Override
//	public List<String> getIssuees(String filePath, Date doStartTime, Date doEndTime, String issue) {
//
//		List<String> dealIssuees = new ArrayList<String>();
//		Set<String> issueesList = new HashSet<String>();
//		if (doStartTime == null || doEndTime == null) {
//			return null;
//		}
//		if (issue.contains("{")) {
//			String dateFormat = issue.replace("{", "").replace("}", "").replace("-", "").replaceAll("\\d", "");
//			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
//
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTime(doStartTime);
//			Date tempTime = calendar.getTime();
//
//			while (!tempTime.after(doEndTime)) {
//				String issueDate = formatter.format(tempTime);
//				issueesList.add(issueDate);
//				calendar.add(Calendar.DATE, 1);
//				tempTime = calendar.getTime();
//			}
//
//		} else {
//			String[] issuees = issue.split(",");
//			for (String issu : issuees) {
//				issueesList.add(issu);
//			}
//		}
//		dealIssuees.addAll(issueesList);
//
//		return dealIssuees;
//	}
//
//	@Override
//	public ResultEntity getDbFile(AtomicAlgorithm at, String productId, String issue, String filePath, Map<String, Object> param) {
//
//		ResultEntity result = new ResultEntity();
//
//		List<ProductFileInfo> productFileInfos = new ArrayList<>();
//		List<ProductInfo> productInfos = new ArrayList<>();
//		if (null == filePath || "".equals(filePath)) {
//			filePath = "E:/QH_NQdata/";
//		}
//		issue = changeIssueToIssue12(issue);
//		String productDirPath = filePath;
//		File productDir = new File(productDirPath);
//		if (!productDir.exists() || !productDir.isDirectory()) {
//			result.setMessage("文件夹" + productDirPath + "不存在");
//			result.setCode(BusinessConst.RESULT_CODE_FAIL);
//			return result;
//		}
//
//		StringBuilder sb = new StringBuilder();
////		ProductStorage productStorage = productStorageService.findProductStorageById(productId);
////		String issueCyclePath = productDirPath + issue + productStorage.getCycle() + "/";
////		File issueDir = new File(issueCyclePath);
////		if (!issueDir.exists() || !issueDir.isDirectory()) {
////			sb.append(" 没有" + issue + "期的产品，不予处理").append("\n");
////			result.setCode(BusinessConst.RESULT_CODE_FAIL);
////			result.setMessage(sb.toString());
////			return result;
////		}
////		File[] regionDirs = issueDir.listFiles();
////		for (File regionDir : regionDirs) {
////			String regionId = regionDir.getName();
////
////			String[] types = productStorage.getKey().split(",");
////			String productType = types[0];
////			boolean checkProductFileInfosExists = productFileInfoService.isProductFileInfosExists(productType,
////					regionId, issue, productStorage.getCycle());
////			if (checkProductFileInfosExists) {
////				System.out.println("产品 HotWind ~~~~期号" + issue + " ~~~区域编号：" + regionId + " :已存在");
////				sb.append("该期产品已存在，不予处理").append("\n");
////				continue;
////			}
////
////			boolean productInfoExists = productInfoService.isProductInfoExists(productStorage.getId(), regionId, issue,
////					productStorage.getCycle());
////			if (!productInfoExists) {
////				ProductInfo productInfo = new ProductInfo();
////				productInfo.setStorageId(productStorage.getId());
////				productInfo.setCycle(productStorage.getCycle());
////				productInfo.setIssue(issue);
////				productInfo.setRegionId(regionId);
////				productInfo.setStatus(ProductInfo.STATUS_PASS);
////				productInfos.add(productInfo);
////			}
////
////			File[] productFiles = regionDir.listFiles();
////			productFileInfos.addAll(getProductFileInfos(productFiles, regionId, issue, productStorage, null));
////		}
////		result.setCode(BusinessConst.RESULT_CODE_SUCCESS);
////		result.setMessage("success");
////		result.setProductInfos(productInfos);
////		result.setProductFileInfos(productFileInfos);
//
//		return result;
//	}
//
//	@Override
//	public boolean checkProductFileExists(String productId, String issue, String cycle) {
//
//		boolean flag = true;
//		if ("".equals(cycle) || cycle == null) {
//			cycle = "COOD";
//		}
//
////		ProductStorage productStorage = productStorageService.findProductStorageById(productId);
////		if (productStorage == null
////				|| ("".equals(productStorage.getParentId()) || null == productStorage.getParentId())) {
////			System.err.println("someThing was wrong with productId = " + productId);
////			return false;
////		}
////		
////		String[] types = productStorage.getKey().split(",");
////		String productType = types[0];
////		boolean flagQHS = productFileInfoService.isProductFileInfosExists(productType, "QHS", issue,
////				productStorage.getCycle());
////		boolean flagDBYNQ = productFileInfoService.isProductFileInfosExists(productType, "EAST", issue,
////				productStorage.getCycle());
////		if (!flagQHS | !flagDBYNQ) {
////			flag = false;
////		}
//
//		return flag;
//	}

}
