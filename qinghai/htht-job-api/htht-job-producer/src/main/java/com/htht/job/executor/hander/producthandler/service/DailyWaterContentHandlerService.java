package com.htht.job.executor.hander.producthandler.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
@Service("dailyWaterContentHandlerService")
public class DailyWaterContentHandlerService  extends BasePlugin{
	
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
		
		String inputFileMOD09GA = ((String) dynamicParameter.get("inputFileMOD09GA")).replace("\\", "/");
		inputFileMOD09GA = replaceDate(issue, inputFileMOD09GA);
		
		File tmpFile = new File(inputFileMOD09GA);
		
		if(tmpFile.exists() && tmpFile.isFile()){
			InputParamMap.put("inputFileMOD09GA", inputFileMOD09GA);
		}else{
			String inputFileMOD09GAPattern = ((String) dynamicParameter.get("inputFileMOD09GAPattern"));
			inputFileMOD09GAPattern = replaceModisDate(issue, inputFileMOD09GAPattern);
			dealInputFiles(InputParamMap, inputFileMOD09GAPattern, tmpFile, "inputFileMOD09GA");
		}
		
		String inputFileMOD11A1 = ((String) dynamicParameter.get("inputFileMOD11A1")).replace("\\", "/");
		inputFileMOD11A1 = replaceDate(issue, inputFileMOD11A1);
		
		File tmpFile1 = new File(inputFileMOD11A1);
		
		if(tmpFile1.exists() && tmpFile1.isFile()){
			InputParamMap.put("inputFileMOD11A1", inputFileMOD11A1);
		}else{
			String inputFileMOD11A1Pattern = ((String) dynamicParameter.get("inputFileMOD11A1Pattern"));
			inputFileMOD11A1Pattern = replaceModisDate(issue, inputFileMOD11A1Pattern);
			dealInputFiles(InputParamMap, inputFileMOD11A1Pattern, tmpFile1, "inputFileMOD11A1");
		}
		
		if(null==InputParamMap.get("inputFileMOD09GA") || null==InputParamMap.get("inputFileMOD11A1")){
			InputParamMap.put("inputFile", "");
		}
		InputParamMap.put("inputFile", inputFileMOD09GA);
		
		return InputParamMap;
	}


	private void dealInputFiles(Map<String, Object> InputParamMap,
			String inputFilePattern, File tmpFile,String mapKey) {
		List<File> files = FileUtil.iteratorFileAndDirectory(tmpFile, inputFilePattern);
		StringBuffer filePaths = new StringBuffer();
		if(null!=files && !files.isEmpty()){
			Collections.sort(files, new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {
					
					return o1.getName().contains("h25v05")?-1:1;
				}
				
			});
			
			for(File f:files){
				filePaths.append(f.getPath());
				filePaths.append(",");
				
			}
		}
		if(filePaths.length()>1){
			filePaths = filePaths.replace(filePaths.lastIndexOf(","), filePaths.length(), "");
			InputParamMap.put(mapKey, filePaths);
		}
	}
	
	private static String replaceModisDate(String issue, String inputFilePattern) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date currentDate = new Date();
		try {
			currentDate = sdf.parse(issue.substring(0,8));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		
		int day = c.get(Calendar.DAY_OF_YEAR);
		inputFilePattern = inputFilePattern.replace("{yyyy}", issue.substring(0, 4));
		inputFilePattern = inputFilePattern.replace("{day}", String.valueOf(day));
		
		return inputFilePattern;
	}


	private static String replaceDate(String issue, String inputFile) {
		inputFile = inputFile.replace("{yyyy}", issue.substring(0, 4));
		return inputFile;
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