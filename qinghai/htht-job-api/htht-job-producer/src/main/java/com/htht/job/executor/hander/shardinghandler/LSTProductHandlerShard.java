package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * H8 LST产品生产
 * @author yuguoqing
 * @Date 2018年10月23日 上午10:36:04
 *
 *
 */
@Service("lSTProductHandlerShard")
public class LSTProductHandlerShard implements SharingHandler {

	@Autowired
	private DataMataInfoService dataMataInfoService;

	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {

		List<String> FileList = new ArrayList<String>();
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		String startTime  = (String) fixmap.get("startTime");
		String endTime  = (String) fixmap.get("endTime");
		//输入文件路径
		String inputdataPath = (String)dymap.get("inputdata");
//		List<String>  lists = Arrays.asList(inputdataPath.split("\\\\"));
//		String fileName = Arrays.asList(lists.get(lists.size()-1).split("\\.")).get(0);
		String fileName = new File(inputdataPath).getName();
		List<String>  lista = Arrays.asList(fileName.split("_"));
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String resolutions = "";
		if(lista.size()>2){
			resolutions = lista.get(lista.size()-1);
			for(int i=0;i<resolutions.length();i++){
				if(!Character.isDigit(resolutions.charAt(i))){
					resolutions = resolutions.substring(0,resolutions.length()-1);
				}
			}
		}
		paramMap.put("satellite", lista.get(0));
		paramMap.put("sensor", lista.get(1));
		paramMap.put("resolution",resolutions);
		paramMap.put("startDate", startTime);
		paramMap.put("endDate", endTime);
		paramMap.put("dataLevel", "L2");
		if(startTime.contains("{")&&startTime.contains("yyyy") || !(startTime.length()>0) || endTime.isEmpty()){

			SimpleDateFormat simple = new SimpleDateFormat("yyyyMMddHH");
			String time = simple.format(new Date());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(simple.parse(time));
			calendar.add(Calendar.HOUR,-1);
			String startDate = simple.format(calendar.getTime());
			String endDate = time;

			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
		}

		FileList = dataMataInfoService.findDataToProject(paramMap);

		if (FileList.isEmpty()) {
			return result;
		}
		if (!result.isSuccess()) {
			return result;
		}
		result.setResult(FileList);
		if(result.getResult().isEmpty()){
			System.out.println("EndShard");
		}
		return result;
	}
}


