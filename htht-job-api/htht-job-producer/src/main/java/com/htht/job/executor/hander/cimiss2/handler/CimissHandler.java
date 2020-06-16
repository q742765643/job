package com.htht.job.executor.hander.cimiss2.handler;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.executor.hander.cimiss2.service.CimissFileDownLoadService;
import com.htht.job.executor.model.downupload.CimissDownInfoDTO;
import com.htht.job.executor.model.paramtemplate.CimissDownParam;
import com.htht.job.executor.service.downupload.DownResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JobHandler(value = "CimissHandler")
@Service
public class CimissHandler extends IJobHandler {

	@Autowired
	private CimissFileDownLoadService cimissService;
	@Autowired
	private DownResultService downResultService;

	@Override
	public ReturnT<String> execute(TriggerParam arg0) throws Exception {

		CimissDownInfoDTO info = downResultService.getCimissInfo("cimiss");

		String ip = info.getIpAddr();
		String port = info.getPort();
		String userid = info.getUserName();
		String password = info.getPwd();

		CimissDownParam downParam = JSON.parseObject(arg0.getModelParameters(),CimissDownParam.class);

		String filePath = downParam.getFilePath();
		String filename = downParam.getFilename();
		String interfaceId = downParam.getInterfaceId();
		//要素
		String elements = downParam.getElements();
		//资料代码
		String dataCode = downParam.getDataCode();
		//时间
		String time = downParam.getTimes();
		String isopen = downParam.getIsopen();
		SimpleDateFormat fromat2 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat fromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if("SATE".equals(dataCode.substring(0,4))){
			Map<String, String> map = new HashMap<String, String>();
			interfaceId = "getSateFileByTime";
			map.put("interfaceId", interfaceId);
			map.put("dataCode", dataCode);
			map.put("dataFormat", "json");
			if (!Boolean.parseBoolean(isopen)) {
				List<String> list = Arrays.asList(time.split("\\*"));
				map.put("timeRange","("+ fromat2.format(fromat.parse(list.get(0)))+","+fromat2.format(fromat.parse(list.get(1))) +")");
				map.put("interfaceId",map.get("interfaceId").replace("Time","TimeRange"));

			} else {
				map.put("times",fromat2.format(fromat.parse(time)));
			}
			cimissService.getCimissData(ip,port,userid,password,filePath,filename,map);
		}
		if("SURF".equals(dataCode.substring(0,4))){
			Map<String, String> map = new HashMap<String, String>();
			interfaceId = "getSurfEleInRegionByTime";
			map.put("interfaceId", interfaceId);
			map.put("elements",elements);
			map.put("dataCode", dataCode);
			map.put("dataFormat", "json");
			if (!Boolean.parseBoolean(isopen)) {
				List<String> list = Arrays.asList(time.split("\\*"));
				map.put("timeRange","("+ fromat2.format(fromat.parse(list.get(0)))+","+fromat2.format(fromat.parse(list.get(1))) +")");
				map.put("interfaceId",map.get("interfaceId").replace("Time","TimeRange"));

			} else {
				map.put("times",fromat2.format(fromat.parse(time)));
			}
			cimissService.getCimissData(ip,port,userid,password,filePath,filename,map);
		}
		if("UPAR".equals(dataCode.substring(0,4))){
			Map<String, String> map = new HashMap<String, String>();
			interfaceId = "getSurfEleInRegionByTime";
			map.put("interfaceId", interfaceId);
			map.put("elements",elements);
			map.put("dataCode", dataCode);
			map.put("dataFormat", "json");
			if (!Boolean.parseBoolean(isopen)) {
				List<String> list = Arrays.asList(time.split("\\*"));
				map.put("timeRange","("+ fromat2.format(fromat.parse(list.get(0)))+","+fromat2.format(fromat.parse(list.get(1))) +")");
				map.put("interfaceId",map.get("interfaceId").replace("Time","TimeRange"));

			} else {
				map.put("times",fromat2.format(fromat.parse(time)));
			}
			cimissService.getCimissData(ip,port,userid,password,filePath,filename,map);
		}

/*
		String filePath = "D:\\data\\appss\\fine\\{yyyyMMdd}\\";
		String filename = "ac-ma-test-{yyyyMMddHHmmss}.nc";
		String interfaceId = "getSurfEleInRegionByTime";
		//要素
		String elements = "Station_Name,Province,City";
		//资料代码
		String dataCode = "SURF_CHN_MUL_HOR";
		//时间
		String time = "20170526060600,20170527060600";
		String isopen = "false";*/

		/*Map<String, String> map = new HashMap<String, String>();

			map.put("interfaceId", interfaceId);
			map.put("dataCode", dataCode);
			map.put("elements", elements);
			map.put("dataFormat", "json");
			if (!Boolean.parseBoolean(isopen)) {
				map.put("timeRange",time);
				map.put("interfaceId",map.get("interfaceId").replace("Time","TimeRange"));

			} else {
				map.put("times",time);
			}
		if(interfaceId.contains("InRegion")){
			//行政编码
//			String adminCodes = downParam.getAdminCodes();

			map.put("adminCodes", "test");

			cimissService.getCimissData(ip,port,userid,password,filePath,filename,map);

		} else if (interfaceId.contains("InRect")){
			String minLat = downParam.getMinLat();
			String minLon = downParam.getMinLon();
			String maxLat = downParam.getMaxLat();
			String maxLon = downParam.getMaxLon();
			map.put("minLat",minLat);
			map.put("minLon",minLon);
			map.put("maxLat",maxLat);
			map.put("maxLon",maxLon);
			cimissService.getCimissData(ip,port,userid,password,filePath,filename,map);
		}*/
		return null;
	}


}
