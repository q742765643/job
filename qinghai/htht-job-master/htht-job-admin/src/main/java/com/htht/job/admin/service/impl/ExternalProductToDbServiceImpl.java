package com.htht.job.admin.service.impl;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.htht.job.admin.service.ProductToDbService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.MatchTime;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.productfileinfo.ProductFileInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;

@Service
public class ExternalProductToDbServiceImpl implements ProductToDbService {
	@Resource
	protected DubboService dubboService;
	private static final String ISSUE_STR = "yyyyMMddHHmm";
	private static final SimpleDateFormat ISSUE_FORMAT = new SimpleDateFormat(ISSUE_STR);

	@Override
	public ReturnT<String> toDb(String startIssue, String endIssue, String productType) {
		ReturnT<String> result = new ReturnT<String>();
		/** 1.校验参数 **/
		String templetPath = findDictCode("产品路径配置", productType);
		if ("".equals(templetPath)) {
			result.setCode(ReturnT.FAIL_CODE);
			result.setMsg("未配置产品路径的产品类型" + productType);
			return result;
		}
		Product product = findProductByType(productType);
		if (null == product) {
			result.setCode(ReturnT.FAIL_CODE);
			result.setMsg("未知的产品类型" + productType);
			return result;
		}
		String fileNameRegular = findDictCode("产品入库正则", productType);
		/** 2.获取时间区间内的时间列表 **/
		List<String> issueList = new ArrayList<String>();
		try {
			issueList = matchIssueList(startIssue, endIssue, product.getCycle());
		} catch (ParseException e) {
			result.setCode(ReturnT.FAIL_CODE);
			result.setMsg("请输入正确的时间格式：" + ISSUE_STR);
			return result;
		}
		/** 3.根据日期列表获取文件路径 **/
		Map<String, String> pathIssueMap = matchPathList(issueList, templetPath);
		batchToDB(pathIssueMap, fileNameRegular, startIssue, endIssue, product, result);
		return result;
	}

	private void batchToDB(Map<String, String> pathIssueMap, String fileNameRegular, String startIssue, String endIssue,
			Product product, ReturnT<String> result) {
		/** 1.获取时间范围内的产品信息 **/
		StringBuffer resultContent = new StringBuffer("产品入库信息：");
		List<ProductInfo> existProductList = dubboService.findProductInfoListByIssueRange(product.getId(), startIssue,
				endIssue);
		Set<String> pathSet = pathIssueMap.keySet();
		Map<String, File> issueDirMap = new HashMap<String, File>();
		Map<String, ProductInfo> issueProductInfoMap = new HashMap<String, ProductInfo>();
		for (ProductInfo productInfo : existProductList) {
			issueProductInfoMap.put(productInfo.getIssue(), productInfo);
		}
		Set<String> dbIssueSet = issueProductInfoMap.keySet();
		for (String path : pathSet) {
			File dir = new File(path);
			if (dir.isDirectory()) {
				issueDirMap.put(pathIssueMap.get(path), dir);
			}
		}
		Set<String> fileIssueSet = issueDirMap.keySet();
		Map<String, String> issuePidMap = new HashMap<>();
		/** 2.取文件issueSet和数据库的issue Set的交集和补集，分别做跟新，插入和删除操作 **/
		/** 求交 更新 **/
		Set<String> resultSet = new HashSet<String>();
		resultSet.addAll(fileIssueSet);
		resultSet.retainAll(dbIssueSet);
		resultContent.append("更新：");
		for (String updateIssue : resultSet) {
			resultContent.append(updateIssue).append(",");
			ProductInfo productInfo = issueProductInfoMap.get(updateIssue);
			if (null != productInfo) {
				productInfo.setUpdateTime(new Date());
				issuePidMap.put(updateIssue, dubboService.saveProductInfo(productInfo).getId());
			}
		}
		
		/** 求差 新增 **/
		resultSet.clear();
		resultSet.addAll(fileIssueSet);
		resultSet.removeAll(dbIssueSet);
		resultContent.append("新增：");
		for (String insertIssue : resultSet) {
			resultContent.append(insertIssue).append(",");
			
			ProductInfo insertProductinfo = new ProductInfo();
			insertProductinfo.setCreateTime(new Date());
			insertProductinfo.setCycle(product.getCycle());
			insertProductinfo.setIssue(insertIssue);
			insertProductinfo.setMapUrl(product.getMapUrl());
			insertProductinfo.setMark(product.getMark());
			insertProductinfo.setName(product.getName());
			insertProductinfo.setProductId(product.getId());
			insertProductinfo.setProductPath(product.getProductPath());
			// 根据各省情况更新
			insertProductinfo.setRegionId("");
			issuePidMap.put(insertIssue, dubboService.saveProductInfo(insertProductinfo).getId());
		}
		/** 求差 删除 **/
		resultSet.clear();
		resultSet.addAll(dbIssueSet);
		resultSet.removeAll(fileIssueSet);
		resultContent.append("删除：");
		for (String deleteIssue : resultSet) {
			resultContent.append(deleteIssue).append(",");
			ProductInfo delInfo = issueProductInfoMap.get(deleteIssue);
			if (null != delInfo) {
				issuePidMap.put(deleteIssue, delInfo.getId());
				dubboService.deleteProductInfo(delInfo.getId());
			}
		}
		/** 3.更新ProductFileInfo **/
		/** 求并 删除文件信息 **/
		resultSet.clear();
		resultSet.addAll(dbIssueSet);
		resultSet.addAll(fileIssueSet);
		for (String deleteIssue : resultSet) {
			dubboService.deleteProductFileInfoByIssue(deleteIssue, issuePidMap.get(deleteIssue));
		}
		/** 新增产品文件信息 **/
		Set<File> set = null;
		if ("".equals(fileNameRegular.trim()) || null == fileNameRegular) {
			for (String insertIssue : fileIssueSet) {
				File Dir = issueDirMap.get(insertIssue);
				set = FileUtil.getAllFiles(Dir, new HashSet<File>());
				saveFileListToDb(set, insertIssue, product, issuePidMap.get(insertIssue));
			}
			
		}else {
			for (String insertIssue : fileIssueSet) {
				File Dir = issueDirMap.get(insertIssue);
				set = FileUtil.getAllFiles(Dir, new HashSet<File>(), fileNameRegular);
				saveFileListToDb(set, insertIssue, product, issuePidMap.get(insertIssue));
			}
		}
		result.setContent(resultContent.toString());
		result.setMsg("入库成功");
		result.setCode(ReturnT.SUCCESS_CODE);
	}
	private void saveFileListToDb(Set<File> list,String issue, Product product, String productInfoId) {
		ProductFileInfo productFileInfo = null;
		String fileName = "";
		for(File file : list) {
			if(file.isFile()) {
				fileName = file.getName();
				productFileInfo = new ProductFileInfo();
				productFileInfo.setCreateTime(new Date());
				productFileInfo.setCycle(product.getCycle());
				productFileInfo.setFileName(fileName);
				productFileInfo.setFilePath(file.getPath());
				productFileInfo.setFileSize(file.length());
				productFileInfo.setFileType(fileName.substring(fileName.lastIndexOf("."),fileName.length()).toUpperCase());
				productFileInfo.setIssue(issue);
				productFileInfo.setProductType(product.getMark());
				productFileInfo.setRegion("");
				productFileInfo.setRelativePath(file.getAbsolutePath());
				productFileInfo.setProductInfoId(productInfoId);
				dubboService.saveProductFileInfo(productFileInfo);
			}	
		}
	}

	private String findDictCode(String pDictName, String dictName) {
		// 获取产品路径配置信息
		List<DictCode> pathConfs = dubboService.findChildrenDictCode(pDictName);
		for (DictCode pathconf : pathConfs) {
			if (dictName.equals(pathconf.getDictName())) {
				return pathconf.getDictCode();
			}
		}
		return "";
	}

	private List<String> matchIssueList(String startIssue, String endIssue, String cycle) throws ParseException {
		List<String> issueList = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		String issue = "";
		Date doStartTime = ISSUE_FORMAT.parse(startIssue);
		Date doEndTime = ISSUE_FORMAT.parse(endIssue);
		calendar.setTime(doStartTime);
		while (doStartTime.compareTo(doEndTime) <= 0) {
			issue = MatchTime.matchIssue12(ISSUE_FORMAT.format(doStartTime), cycle);
			if (!"".equals(issue) && !issueList.contains(issue)) {
				issueList.add(issue);
			}
			calendar.add(Calendar.MINUTE, 10);
			doStartTime = calendar.getTime();
		}

		return issueList;
	}

	private Map<String, String> matchPathList(List<String> issueList, String templetPath) {
		HashMap<String, String> map = new HashMap<String, String>();
		Date date = new Date();
		String path = "";
		for (String issue : issueList) {
			try {
				date = ISSUE_FORMAT.parse(issue);
				path = FileUtil.getPathByDate(templetPath, date);
				if (!map.containsKey(path)) {
					map.put(path, issue);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	private Product findProductByType(String productType) {
		List<Product> productList = dubboService.findALlProduct();
		for (Product product : productList) {
			if (productType.equals(product.getMark())) {
				return product;
			}
		}
		return null;
	}
}
