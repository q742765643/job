package com.htht.job.executor.plugin.product.service;

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
 * 作物低温冷害插件
 * @author liuconghui
 * 2019年1月10日
 */
@Transactional
@Service("chillingDamageService")
public class ChillingDamageService extends BasePlugin 
{

	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat, String fileFormat) throws IOException  {

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
		dealIssuees.addAll(issueesList);

		return dealIssuees;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) {
		Product product = productService.findById(triggerParam.getProductId());
		LinkedHashMap dynamicParameter = triggerParam.getDynamicParameter();
		Map<String, Object> InputParamMap = new HashMap<String, Object>();

		String inputPath = (String) dynamicParameter.get("inputPath");
		String areaID = (String) dynamicParameter.get("areaID");
		String mubanPath = (String) dynamicParameter.get("mubanPath");
		String configXml = (String) dynamicParameter.get("config_xml");

		
		InputParamMap.put("inputFile", inputPath);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", changeIssueToIssue12(issue));
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("mubanPath", mubanPath);
		InputParamMap.put("config_xml", configXml);
		
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
