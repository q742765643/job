package com.htht.job.executor.hander.producthandler.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
@Service("drtSumFY3DHandlerService")
public class DrtSumFY3DHandlerService extends BasePlugin{
	
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
		inputFile = inputFile.replace("{yyyy}", issue.substring(0, 4)).replace("{MM}", issue.substring(4, 6)).replace("{dd}", issue.substring(6, 8));
		String inputFilePattern = ((String) dynamicParameter.get("inputFilePattern"));
		
		File tmpFile = new File(inputFile);
		
		if(tmpFile.exists() && tmpFile.isFile()){
			InputParamMap.put("inputFile", inputFile);
			return InputParamMap;
		}
		
		List<File> files = FileUtil.iteratorFileAndDirectory(tmpFile, inputFilePattern);
		if(null!=files && !files.isEmpty()){
			for(File f:files){
				// FY3D_MERSI_QH_PRJ_L1_20200511_0353_1000M.ldf
				String date = f.getName().split("_")[6];
				//  0800 - 1800
				if(Integer.valueOf(date)>=800 && Integer.valueOf(date)<=1800){
					inputFile = f.getPath();
					InputParamMap.put("inputFile", inputFile);
					return InputParamMap;
				}
			}
		}
		
		// 替换取 AEA_QH
		inputFile = inputFile.replace("GLL", "AEA_QH");
		List<File> files2 = FileUtil.iteratorFileAndDirectory(new File(inputFile), inputFilePattern);
		if(null!=files2 && !files2.isEmpty()){
			for(File f:files2){
				// FY3D_MERSI_QH_PRJ_L1_20200511_0353_1000M.ldf
				// FY3D_MERSI_QH_GLL_L1_20200511_0353_1000M.ldf
				
				String date = f.getName().split("_")[6];
				if(Integer.valueOf(date)>=800 && Integer.valueOf(date)<=1800){
					inputFile = f.getPath();
					InputParamMap.put("inputFile", inputFile);
					return InputParamMap;
				}
			}
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
