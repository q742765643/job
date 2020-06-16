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
 * 干热风监测
 * @author liuconghui
 * 2019年1月21日
 */
@Transactional
@Service("hotWindMonitorService")
public class HotWindMonitorService extends BasePlugin {

	private static Logger logger = LoggerFactory.getLogger(HotWindMonitorService.class.getName());
	
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductInfoService productInfoService;
	
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

}
