package com.htht.job.executor.plugin.preprocessing.service;

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

import org.htht.util.DateUtil;
import org.htht.util.MatchTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import com.htht.job.executor.model.preprocess.PreProcess;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.plugin.common.BasePreProcess;
import com.htht.job.executor.plugin.common.BusinessConst;
import com.htht.job.executor.service.preprocess.PreProcessService;
import com.htht.job.executor.service.product.ProductService;

@Transactional
@Service("sunFlower8ProcessService")
public class SunFlower8ProcessService extends BasePreProcess{
	
	private static Logger logger = LoggerFactory.getLogger(SunFlower8ProcessService.class.getName());
	@Autowired
	private ProductService productService;
	@Autowired
	private PreProcessService preProcessService;
	
	@Override
	public HashMap<String, Date> getRangeDate(ProductParam productParam,
			Map dymap, String cycle) throws Exception {

		HashMap<String, Date> rangeDate = new HashMap<String, Date>();
		Date doStartTime = null;
		Date doEndTime = null;
		
		Calendar c = Calendar.getInstance();
		if (String.valueOf(BusinessConst.PROCESSTASKPRODUCT_DATETYPE_REAL_TIME).equals(productParam.getDateType())) {
			// 实时
			String issueStr = (String) dymap.get("issue");
			String issue = MatchTime.matchIssue(issueStr,cycle);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			Date issueDate = sdf.parse(issue);
			c.setTime(issueDate);
			doEndTime = c.getTime();
			if (null != productParam.getProductRangeDay() && !"".equals(productParam.getProductRangeDay())) {
				c.add(Calendar.DATE, -(Integer.parseInt(productParam.getProductRangeDay())));
			}
			doStartTime = c.getTime();
		} else if (String.valueOf(BusinessConst.PROCESSTASKPRODUCT_DATETYPE_HISTORICAL_TIME)
				.equals(productParam.getDateType())) {
			// 历史
			String[] temp = productParam.getProductRangeDate().split(" - ");
			String stratTime = temp[0];
			String endTime = temp[1];
			
			String stratTimePattern = this.timeToPattern(stratTime);
			String endTimePattern = this.timeToPattern(endTime);
			doStartTime = DateUtil.getDate(stratTime.replace("-", "").replace(":", "").replace(" ", ""),
					stratTimePattern);
			doEndTime = DateUtil.getDate(endTime.replace("-", "").replace(":", "").replace(" ", ""), endTimePattern);
			
			//北京时间减8为世界时间即数据时间
			Calendar cal = Calendar.getInstance();
			cal.setTime(doStartTime);
			cal.add(Calendar.HOUR_OF_DAY, -8);
			doStartTime = cal.getTime();
			cal.setTime(doEndTime);
			cal.add(Calendar.HOUR_OF_DAY, -8);
			doEndTime = cal.getTime();
			dymap.put("fileRangeFormat","0");
		}
		rangeDate.put("doStartTime", doStartTime);
		rangeDate.put("doEndTime", doEndTime);

		return rangeDate;
	
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<String> getIssuees(String productId, String inputPath, Date doStartTime, Date doEndTime, String issueFormat,
			String fileFormat) throws IOException {
		logger.info("enter method getIssues() with doStartTime : " + doStartTime + ",doEndTime : " + doEndTime + ",inputPath : " + inputPath);
		List<String> issueList = new ArrayList<>();
		Set<String> issueesList = new HashSet<String>();
		if (doStartTime == null || doEndTime == null) {
			return null;
		}
		if (!inputPath.endsWith("/")) {
			inputPath += "/";
		}
		
		if (issueFormat.contains("{")) {//yyyyMMddHH
			String dateFormat = issueFormat.replace("{", "").replace("}", "").replace("-", "").replaceAll("\\d", "");
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

			Calendar calendar = Calendar.getInstance();
			
			calendar.setTime(doStartTime); 
			if (doStartTime.getHours()<2) {
				calendar.set(Calendar.HOUR_OF_DAY, 2);
			}
			int startMinute = calendar.get(Calendar.MINUTE);
			int startNum = (int) Math.round((double) startMinute/10);
			calendar.set(Calendar.MINUTE,startNum*10);
			doStartTime = calendar.getTime();
			
			calendar.setTime(doEndTime); 
			if (doEndTime.getHours()>9) {
				calendar.set(Calendar.HOUR_OF_DAY, 9);
			}
			int endMinute = calendar.get(Calendar.MINUTE);
			int endNum = (int) Math.round((double)endMinute/10);
			calendar.set(Calendar.MINUTE,endNum*10);
			doEndTime = calendar.getTime();
			
			logger.info("doStartTime : " + doStartTime);
			logger.info("doEndTime : " + doEndTime);
			calendar.setTime(doStartTime);
			logger.info("fileFormat : " + fileFormat);
			int timeRange = Integer.parseInt(fileFormat);
			calendar.add(Calendar.MINUTE, timeRange);
			Date tempTime = calendar.getTime();

			while (tempTime.before(doEndTime)) {
				String issueDate = formatter.format(tempTime);
				issueesList.add(issueDate);
				calendar.add(Calendar.MINUTE, 10);
				tempTime = calendar.getTime();
			}

		} else {
				String[] issuees = issueFormat.split(",");
				for (String issu : issuees) {
					issueesList.add(issu);
			}
		}
		
		for (String issue : issueesList) {
			// 只处理数据时间段为 0200 - 1150 之间的，即北京时间10点-19点
			if(Integer.valueOf(issue.substring(8,12)) >=200 && Integer.valueOf(issue.substring(8,12)) <= 1150){
				PreProcess preProcess = preProcessService.findProcessByProductIdAndIssue(productId, issue);
				boolean status = checkPreProcessStatus(preProcess);
				if (status) {
					issueList.add(issue);
				}
			}
			
		}
		logger.info("issueesList : " + issueesList);
		logger.info("issueList : " + issueList);
		
		return issueList;
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) throws IOException {

		LinkedHashMap dynamicParameter = triggerParam.getDynamicParameter();
		Product product = productService.findById(triggerParam.getProductId());
		
		Map<String, Object> InputParamMap = new HashMap<String, Object>();
		String inputPath = ((String) dynamicParameter.get("inputPath")).replace("\\", "/");		
//		String areaID = (String) dynamicParameter.get("areaID");
		
//		String fileTime = (String) dynamicParameter.get("fileTime");
		String channals = (String) dynamicParameter.get("channals");
		String segMents = (String) dynamicParameter.get("segMents");
		String extend = (String) dynamicParameter.get("extend");
		String pixSize = (String) dynamicParameter.get("pixSize");
		
//		InputParamMap.put("areaID", areaID);
		InputParamMap.put("inputPath", inputPath);
		InputParamMap.put("cycle", product.getCycle());
		InputParamMap.put("fileTime", changeIssueToIssue12(issue));
		InputParamMap.put("channals", channals);
		InputParamMap.put("segMents", segMents);
		InputParamMap.put("extend", extend);
		InputParamMap.put("pixSize", pixSize);
		
		return InputParamMap;
	}

}
