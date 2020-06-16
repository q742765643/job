package com.htht.job.executor.plugin.job;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.htht.util.DateUtil;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;

@JobHandler("deleteFileHandler")
@Service
public class DeleteFileHandler extends IJobHandler {
	
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {

		
		LinkedHashMap fixedParameter = triggerParam.getFixedParameter();
		String inputPath = (String) fixedParameter.get("inputPath");
		String fileNamePattern = (String) fixedParameter.get("fileNamePattern");
		String days = (String) fixedParameter.get("days");
		if(StringUtils.isEmpty(days) || !StringUtils.isNumeric(days)){
			return new ReturnT<String>(ReturnT.FAIL_CODE, days + " 格式错误");
		}
		int day = Integer.parseInt(days);
		if (inputPath.indexOf("{") > -1 && inputPath.indexOf("}") > -1 ) {
			inputPath = DateUtil.getPathByDate(inputPath, new Date());
		}
		File file = new File(inputPath);
		if (!file.exists() && file.isFile()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, file + " is't exist");
		}
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -day);
		Pattern pattern = Pattern.compile(fileNamePattern);
		if (file.isFile()) {
			Matcher mat = pattern.matcher(file.getName());
			if (mat.find() && file.lastModified() <= cal.getTimeInMillis()) {
				file.delete();
			}
		}else if (file.isDirectory()) {
			RegexFileFilter regexFileFilter  = new RegexFileFilter(fileNamePattern);
			List<File> listFiles = (List<File>)FileUtils.listFiles(file, regexFileFilter, DirectoryFileFilter.INSTANCE);
			for(File f : listFiles){
				
				if ( f.lastModified() <= cal.getTimeInMillis()) {
					Matcher mat = pattern.matcher(f.getName());
					if(mat.find()){
						f.delete();
					}
				}
			}
		}
        return ReturnT.SUCCESS;
		
	}

}
