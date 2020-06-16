package com.htht.job.executor.plugin.job.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.plugin.common.BasePlugin;

@Service("deleteFileService")
public class DeleteFileService extends BasePlugin{
	
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
		if (null == issueFormat || "".equals(issueFormat)) {
			return dealIssuees;
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

	@Override
	public Map<String, Object> getInputParam(TriggerParam triggerParam, String issue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getProducts(Product product) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkProductExists(List<Product> productList, String issue12, String cycle, String modelIdentify,
			String fileName, String regionId) {
		// TODO Auto-generated method stub
		return false;
	}

	public void deleteFile(String inputPath, String format) {
		// TODO Auto-generated method stub
		
	}


}
