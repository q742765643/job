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

import org.apache.commons.lang3.StringUtils;
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
 * 农区干土层干旱监测
 * @author chensi
 * 2018年12月11日
 */
@Transactional
@Service("drySoilService")
public class DrySoilService extends BasePlugin {
	
	private static Logger logger = LoggerFactory.getLogger(DrySoilService.class.getName());
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;

	@Autowired
	private DictCodeService dictCodeService;
	
	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat, String fileFormat) throws IOException {

		List<String> issueList = new ArrayList<>();
		Set<String> issueesList = new HashSet<String>();
		if (doStartTime == null || doEndTime == null) {
			return null;
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
		for (String issue : issueesList) {
			dealIssuees.add(issue);
		}
		
		issueList.addAll(dealIssuees);

		return issueList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) throws IOException {

		LinkedHashMap dynamicParameter = triggerParam.getDynamicParameter();
		Product product = productService.findById(triggerParam.getProductId());
//		String issueFormat = "_" + issue.substring(0, 4) + "_" + issue.substring(4, 6)+"_"+issue.substring(6, 8)+"_";
		String issueFormat = "_" + issue.substring(0, 8) ;

		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String inputPath = ((String) dynamicParameter.get("inputPath"));		
		if (inputPath.indexOf("{") > -1 && inputPath.indexOf("}") > -1 ) {
			Date date = DateUtil.strToDate(issue.substring(0,8), "yyyyMMdd");
			inputPath = DateUtil.getPathByDate(inputPath, date);
		}
		inputPath = inputPath.replace("\\", "/");
		String areaID = (String) dynamicParameter.get("areaID");
		String satellite = (String) dynamicParameter.get("satellite");
		String inputFormat250 = (String) dynamicParameter.get("input250");
		String inputFormat500 = (String) dynamicParameter.get("input500");
		String inputPath250 = inputPath + File.separator + "250/";
		String inputPath500 = inputPath + File.separator + "500/";
		File[] dataFile250 = FileUtil.getDataFileList(inputPath250, inputFormat250.replace("issue", issueFormat));
		File[] dataFile500 = FileUtil.getDataFileList(inputPath500, inputFormat500.replace("issue", issueFormat));
		
		DictCode start = dictCodeService.findOneself("startTime");
		DictCode end = dictCodeService.findOneself("endTime");
		int startTime = Integer.parseInt(start.getDictCode());
		int endTime = Integer.parseInt(end.getDictCode());
		
		StringBuffer input250 = new StringBuffer();
		StringBuffer input500 = new StringBuffer();
		
		for (int i = 0; i < dataFile250.length; i++) {
			String fileName = dataFile250[i].getName();
			if(fileName.contains("(")){
				continue;
			}
			String[] split = fileName.split("_");
			String tempTime = split[6];
			//判断当前时间是否在有效范围内
			if (Integer.parseInt(tempTime) < endTime && Integer.parseInt(tempTime) > startTime) {
				input250 = input250.append(fileName);
				if ((dataFile250.length - 1) != i) {
					input250 = input250.append(",");
				}
			}
		}
		for (int i = 0; i < dataFile500.length; i++) {
			String fileName = dataFile500[i].getName();
			if(fileName.contains("(")){
				continue;
			}
			String[] split = fileName.split("_");
			String tempTime = split[6];
			//判断当前时间是否在有效范围内
			if (Integer.parseInt(tempTime) < endTime && Integer.parseInt(tempTime) > startTime) {
				input500 = input500.append(fileName);
				if ((dataFile500.length - 1) != i) {
					input500 = input500.append(",");
				}
			}
		}
		
		InputParamMap.put("inputFile",inputPath);
		if(StringUtils.isEmpty(input500) || StringUtils.isEmpty(input250)){
			InputParamMap.put("inputFile","");
		}
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("input250", input250.toString());
		InputParamMap.put("input500", input500.toString());
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", changeIssueToIssue12(issue));
		InputParamMap.put("satellite", satellite);

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
