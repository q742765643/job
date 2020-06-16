package com.htht.job.executor.hander.producthandler.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.htht.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.plugin.common.BasePlugin;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.productinfo.ProductInfoService;

@Transactional
@Service("fireForecastHandlerService")
public class FireForecastHandlerService extends BasePlugin{
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductService productService;

	@Override
	public List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat,
			String fileFormat) throws IOException {
		return null;
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) throws IOException {


		LinkedHashMap dynamicParameter = triggerParam.getDynamicParameter();
		Product product = productService.findById(triggerParam.getProductId());
		
		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String areaID = (String) dynamicParameter.get("areaID");
		InputParamMap.put("areaID", areaID);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("issue", issue);
		
		String inputFile = ((String) dynamicParameter.get("inputFile")).replace("\\", "/");
		String inputPRE = ((String) dynamicParameter.get("inputPRE")).replace("\\", "/");
		
		inputFile = inputFile.replace("{yyyy}", issue.substring(0, 4)).replace("{MM}", issue.substring(4, 6)).replace("{dd}", issue.substring(6, 8));
		inputPRE = inputPRE.replace("{yyyy}", issue.substring(0, 4)).replace("{MM}", issue.substring(4, 6)).replace("{dd}", issue.substring(6, 8));
		
		String inputFilePattern = ((String) dynamicParameter.get("inputFilePattern"));
		String inputPREPattern = ((String) dynamicParameter.get("inputPREPattern"));
		
		File tmpFile = new File(inputFile);
		File preFile = new File(inputPRE);
		if(!tmpFile.exists() || !preFile.exists()){
			return InputParamMap;
		}
		
		if(tmpFile.isDirectory()){
			List<File> files = FileUtil.iteratorFileAndDirectory(tmpFile, inputFilePattern);
			if(null!=files && !files.isEmpty()){
				inputFile = files.get(0).getPath();
			}
		}
		
		if(preFile.isDirectory()){
			List<File> files = FileUtil.iteratorFileAndDirectory(preFile, inputPREPattern);
			if(null!=files && !files.isEmpty()){
				inputPRE = files.get(0).getPath();
			}
		}
		
		if(new File(inputFile).exists() && new File(inputPRE).exists()){
			InputParamMap.put("inputFile", inputFile);
			InputParamMap.put("inputPRE", inputPRE);
		}

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
