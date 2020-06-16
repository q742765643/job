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
 *  春耕春播
 * @author 陈思
 * 2019年2月15日
 */
@Transactional
@Service("springSowingService")
public class SpringSowingService extends BasePlugin {
	
	private static Logger logger = LoggerFactory.getLogger(SpringSowingService.class.getName());
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;
	//最高温度路径
	private String inputFile;
	
	//降水量路径
	private String preFile;
	
	
	@SuppressWarnings("deprecation")
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

		if (issueFormat.contains("{")) {
			String dateFormat = issueFormat.replace("{", "").replace("}", "").replace("-", "").replaceAll("\\d", "");
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(doStartTime);
			Date tempTime = calendar.getTime();

			while (!tempTime.after(doEndTime)) {
				String issueDate = formatter.format(tempTime);
				/* 限制条件变化了，由算法来控制
				//若当前时间不是3~4月份，则不执行算法
				
				Calendar tempCalendar = Calendar.getInstance();
				tempCalendar.setTime(tempTime);
//				tempCalendar.add(Calendar.DATE, 7);
				int month = tempCalendar.getTime().getMonth();
				if ((month+1)<3 ||(month+1)>4) {
					logger.info("当前时间为"+ (month+1) +"月份，不在产品执行范围内");
				}else {
					issueesList.add(issueDate);
				}
				*/
				issueesList.add(issueDate);
				calendar.add(Calendar.DATE, 1);
				tempTime = calendar.getTime();
			}

		} else {
			String[] issuees = issueFormat.split(",");
			for (String issu : issuees) 
			{
				issueesList.add(issu);
			}
		}
		//根据最高温度文件格式和降水量文件格式分别查找路径
		String[] filePaths = fileFormat.split(":");
		
		String inputFileFormat = filePaths[0];
		String preFileFormat = filePaths[1];
		logger.info("inputFileFormat：" + inputFileFormat);
		logger.info("preFileFormat：" + preFileFormat);
		Set<String> dealIssuees = new HashSet<String>();
		File inputDir = new File(inputPath);
		inputFile = null;
		preFile = null;
		logger.info("当前路径：" + inputPath );
		if (inputDir.exists()) {
			logger.info("当前路径：" + inputPath +" ,已存在");
			for (String issue : issueesList) {
				
				String Path=inputPath+issue;
				//获取最高温度路径inputFile
				File[] inputFileList = FileUtil.getDataFileList(Path, inputFileFormat);	
				String issue08 = issue + "0800";
				String issue20 = issue + "2000";
				for (File file : inputFileList) {
					
					if (file.getName().contains(issue08)) {
						inputFile = file.getAbsolutePath();
						break;
					} else if (file.getName().contains(issue20)) {
						inputFile = file.getAbsolutePath();
						break;
					} else {
						inputFile = null;
					}
				}
				
				//获取降水量路径preFile
				File[] preFileList = FileUtil.getDataFileList(Path, preFileFormat);	
				
				String[] fileArrays = new String[preFileList.length];
				for (int i = 0; i < preFileList.length; i++) {
					File file = preFileList[i];
					fileArrays[i] = file.getAbsolutePath();
				}
				Arrays.sort(fileArrays);
				preFile = fileArrays[fileArrays.length-1];
				
				logger.info("inputFile：" + inputFile );
				logger.info("preFile：" + preFile );
				
				if (null != inputFile && null != preFile) {
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
		String demFile = ((String) dynamicParameter.get("demFile")).replace("\\", "/");	
		String areaID = (String) dynamicParameter.get("areaID");
		String inputPath = (String) dynamicParameter.get("inputPath");
		String inputFileFormat = (String) dynamicParameter.get("inputFileFormat");
		String preFileFormat = (String) dynamicParameter.get("preFileFormat");
		String inputFile = "";
		String preFile = "";
		File inputDir = new File(inputPath);
		if (inputDir.exists()) {
			logger.info("当前路径：" + inputPath +" ,已存在");
			String issue8 = issue.substring(0, 8);
			String path=inputPath + issue8;
			//获取最高温度路径inputFile
			File[] inputFileList = FileUtil.getDataFileList(path, inputFileFormat);	
			String issue08 = issue8 + "0800";
			String issue20 = issue8 + "2000";
			for (File file : inputFileList) {
				
				if (file.getName().contains(issue08)) {
					inputFile = file.getAbsolutePath();
					break;
				} else if (file.getName().contains(issue20)) {
					inputFile = file.getAbsolutePath();
					break;
				} else {
					inputFile = null;
				}
			}
			
			//获取降水量路径preFile
			File[] preFileList = FileUtil.getDataFileList(path, preFileFormat);	
			
			String[] fileArrays = new String[preFileList.length];
			for (int i = 0; i < preFileList.length; i++) {
				File file = preFileList[i];
				fileArrays[i] = file.getAbsolutePath();
			}
			Arrays.sort(fileArrays);
			preFile = fileArrays[fileArrays.length-1];
		}
		
		logger.info("inputFile：" + inputFile );
		logger.info("preFile：" + preFile );
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputFile", inputFile);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", changeIssueToIssue12(issue));
		InputParamMap.put("preFile", preFile);
		InputParamMap.put("demFile", demFile);

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
