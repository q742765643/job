package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import org.htht.util.Consts;
import org.htht.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * FY3B LST产品生产
 * @author yuguoqing
 * @Date 2018年10月23日 上午10:41:00
 *
 *
 */
@Service("productHandlerNewShard")
public class ProductHandlerNewShard implements SharingHandler {

	@Autowired
	private DataMataInfoService dataMataInfoService;

	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {

		List<String> FileList = new ArrayList<String>();
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		String startTime  = (String) fixmap.get("startTime");
		String endTime  = (String) fixmap.get("endTime");
		//输入文件路径
		String inputdataPath = (String)dymap.get("inputdataPath");
		List<String>  lists = Arrays.asList(inputdataPath.split("\\\\"));
		String fileName = Arrays.asList(lists.get(lists.size()-1).split("\\.")).get(0);
		List<String>  lista = Arrays.asList(fileName.split("_"));
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String resolution = lista.get(lista.size()-1);
		paramMap.put("satellite", lista.get(0));
		paramMap.put("sensor", lista.get(1));
		paramMap.put("resolution", resolution.substring(0,resolution.length()-1));
		paramMap.put("startDate", startTime);
		paramMap.put("endDate", endTime);
		paramMap.put("dataLevel", "L2");
		if(startTime.contains("{")&&startTime.contains("yyyy")|| !(startTime.length()>0) || endTime.isEmpty()){
			String startDate = DateUtil.getCurrentDateStringWithOffset(
					Consts.DateForMat.yyMMddFormat,-1);
			String endDate = DateUtil
					.getCurrentDateString(Consts.DateForMat.yyMMddFormat);
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
		}

		FileList = dataMataInfoService.findDataToProject(paramMap);
		File f = null;
		for (int i = 0; i < FileList.size(); i++) {
			f = new File(FileList.get(i));
			if(f.isDirectory()) {
				FileList.set(i, f.getAbsolutePath()+"/"+f.getName()+".ldf");
			}
		}

		if (FileList.isEmpty()) {
			return result;
		}
		if (!result.isSuccess()) {
			return result;
		}
		result.setResult(FileList);
		if(result.getResult().isEmpty()){
			System.out.println("ShardEnd");
		}
		return result;
	}
}


