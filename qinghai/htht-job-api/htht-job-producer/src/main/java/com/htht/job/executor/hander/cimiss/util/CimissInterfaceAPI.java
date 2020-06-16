package com.htht.job.executor.hander.cimiss.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.htht.job.executor.hander.cimiss.module.DownloadFileInfo;
import com.htht.job.executor.hander.cimiss.module.ElemRectBean;
import com.htht.job.executor.hander.cimiss.module.ResultBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CimissInterfaceAPI {

	private String cimissUrlHead;

	public CimissInterfaceAPI(String ip, String userid, String password) {
		this.cimissUrlHead = ("http://" + ip + "/cimiss-web/api?userId=" + userid + "&pwd=" + password);
	}

	public ResultBean getGridData(String interfaceId,String dataCode,String times ,String adminCodes ,String elements ,String dataFormat,String limitCnt ) {
		String methodParams ="";
		if(limitCnt.isEmpty()||limitCnt==null) {
		methodParams = "&interfaceId=" + interfaceId + "&dataCode=" + dataCode + "&times=" + times
				+ "&adminCodes=" + adminCodes + "&elements=" + elements + "&dataFormat=" + dataFormat;
		}else {
			methodParams = "&interfaceId=" + interfaceId + "&dataCode=" + dataCode + "&times=" + times
					+ "&adminCodes=" + adminCodes + "&elements=" + elements + "&dataFormat=" + dataFormat
					+"&limitCnt="+limitCnt;
		}
//		if (dataFormat.contains("json")) {
//			return getFileOrStationData(methodParams, 0);
//		} else {
			return getFileOrStationData(methodParams, 1);
//		}

	}

	public ElemRectBean getElemRectData(String methodName, String dataCode, String timeRange) {
		String methodParams = "&interfaceId=" + methodName + "&dataCode=" + dataCode + "&timeRange=" + timeRange
				+ "&dataFormat=json";
		String url = this.cimissUrlHead + methodParams;

		ElemRectBean elemRectBean = new ElemRectBean();

		RestUtil restUtil = new RestUtil();
		String rstData = restUtil.getRestData(url);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(rstData);
		String formatJson = gson.toJson(je);
		if (("".equals(formatJson)) || (formatJson == null)) {
			elemRectBean.setReturnCode("-1");
			elemRectBean.setReturnMessage("No result for request");
			elemRectBean.setRowCount("0");
			elemRectBean.setColCount("0");
		} else {
			JSONObject jsonObject = JSONObject.fromObject(formatJson);

			String returnCode = (String) jsonObject.get("returnCode");
			elemRectBean.setReturnCode(returnCode);
			elemRectBean.setReturnMessage((String) jsonObject.get("returnMessage"));
			elemRectBean.setRequestParams((String) jsonObject.get("requestParams"));
			elemRectBean.setRequestTime((String) jsonObject.get("requestTime"));
			elemRectBean.setResponseTime((String) jsonObject.get("responseTime"));
			elemRectBean.setTakeTime((String) jsonObject.get("takeTime"));
			if ((returnCode != null) && (returnCode.equals("0"))) {
				setElemRectResult(elemRectBean, jsonObject);
			} else {
				elemRectBean.setRowCount((String) jsonObject.get("rowCount"));
				elemRectBean.setColCount((String) jsonObject.get("colCount"));
			}
		}
		return elemRectBean;
	}

	public static String getURLParamsByMap(Map<String, String> paramsMap) {
		Set<String> set = paramsMap.keySet();

		String paramStr = "";
		for (String param : set) {
			String paramValue = (String) paramsMap.get(param);
			paramStr = paramStr + "&" + param + "=" + paramValue;
		}
		return paramStr;
	}

	private ResultBean getFileOrStationData(String URLParamsPart, int type) {
		try {
			String url = this.cimissUrlHead + URLParamsPart;

			ResultBean resultBean = new ResultBean();

			RestUtil restUtil = new RestUtil();
			String rstData = restUtil.getRestData(url);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(rstData);
			String formatJson = gson.toJson(je);
			if (("".equals(formatJson)) || (formatJson == null)) {
				resultBean.setReturnCode("-1");
				resultBean.setReturnMessage("No result for request");
				resultBean.setRowCount("0");
				resultBean.setColCount("0");
			} else {
				JSONObject jsonObject = JSONObject.fromObject(formatJson);

				String returnCode = (String) jsonObject.get("returnCode");
				resultBean.setReturnCode(returnCode);
				resultBean.setReturnMessage((String) jsonObject.get("returnMessage"));
				if (type == 0) {
					resultBean.setRowCount((String) jsonObject.get("fileCount"));
				} else {
					resultBean.setRowCount((String) jsonObject.get("rowCount"));
				}
				resultBean.setColCount((String) jsonObject.get("colCount"));
				resultBean.setRequestParams((String) jsonObject.get("requestParams"));
				resultBean.setRequestTime((String) jsonObject.get("requestTime"));
				resultBean.setResponseTime((String) jsonObject.get("responseTime"));
				resultBean.setTakeTime((String) jsonObject.get("takeTime"));
				if ((String) jsonObject.get("fieldNames") != null) {
					String fileName = (String) jsonObject.get("fieldNames");
					String[] fileNames = fileName.split(" ");
					resultBean.setFieldNames(fileNames);
				}
				if ((String) jsonObject.get("fieldUnits") != null) {
					String fieldUnit = (String) jsonObject.get("fieldUnits");
					String[] fieldUnits = fieldUnit.split(" ");
					resultBean.setFieldUnits(fieldUnits);
				}
				if ((returnCode != null) && (returnCode.equals("0"))) {
					JSONArray array = jsonObject.getJSONArray("DS");
					if (type == 0) {
						setFileResult(resultBean, array);
					} else {
						setGridResult(resultBean, array);
					}
				}
			}
			return resultBean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void setFileResult(ResultBean resultBean, JSONArray array) {
		List<DownloadFileInfo> data = new ArrayList();
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			DownloadFileInfo info = new DownloadFileInfo();
			info.setFileName(obj.getString("FILE_NAME"));
			info.setFileSize(obj.getString("FORMAT"));
			info.setFormat(obj.getString("FILE_SIZE"));
			info.setFileURL(obj.getString("FILE_URL"));
			data.add(info);
		}
		resultBean.setData(data);
	}

	private void setGridResult(ResultBean resultBean, JSONArray array) {
		List<Map<String, String>> dataList = new ArrayList();

		String requestParams = resultBean.getRequestParams();
		String[] params = requestParams.split("&");
		String element = null;
		for (String param : params) {
			if (param.indexOf("elements") > -1) {
				element = param;
				break;
			}
		}
		String elementValue = element.substring(element.indexOf("=") + 1);
		String[] elements = elementValue.split(",");
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			Map<String, String> dataMap = new HashMap();
			for (String field : elements) {
				dataMap.put(field, obj.getString(field));
			}
			dataList.add(dataMap);
		}
		resultBean.setData(dataList);
	}

	private void setElemRectResult(ElemRectBean elemRectBean, JSONObject jsonObject) {
		elemRectBean.setStartLat((String) jsonObject.get("startLat"));
		elemRectBean.setStartLon((String) jsonObject.get("startLon"));
		elemRectBean.setEndLat((String) jsonObject.get("endLat"));
		elemRectBean.setEndLon((String) jsonObject.get("endLon"));

		String latCount = (String) jsonObject.get("latCount");
		int row = Integer.parseInt(latCount);
		elemRectBean.setLatCount(latCount);
		String lonCount = (String) jsonObject.get("lonCount");
		int col = Integer.parseInt(lonCount);
		elemRectBean.setLonCount(lonCount);

		elemRectBean.setLonStep((String) jsonObject.get("lonStep"));
		elemRectBean.setLatStep((String) jsonObject.get("latStep"));

		String[][] lonAndLat = new String[row][col];
		JSONArray array = jsonObject.getJSONArray("DS");
		for (int i = 0; i < array.size(); i++) {
			JSONArray array1 = (JSONArray) array.get(i);
			String[] data = new String[col];
			for (int j = 0; j < array1.size(); j++) {
				data[j] = ((String) array1.get(j));
			}
			lonAndLat[i] = data;
		}
		elemRectBean.setLonAndLat(lonAndLat);
	}

	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("returnCode", "0");
		String[][] DSStr = { { "299.2126", "299.31018", "299.53098", "299.7158" },
				{ "299.3878", "299.34216", "299.0894", "298.96857" } };
		jsonObject.put("DS", DSStr);
		System.out.println(jsonObject);

		String returnCode = (String) jsonObject.get("returnCode");
		System.out.println(returnCode);

		JSONArray array = jsonObject.getJSONArray("DS");
		String[][] data = new String[2][4];
		for (int i = 0; i < array.size(); i++) {
			JSONArray array1 = (JSONArray) array.get(i);
			String[] data2 = new String[4];
			for (int j = 0; j < array1.size(); j++) {
				data2[j] = ((String) array1.get(j));
			}
			data[i] = data2;
		}
		System.out.println(data);
	}


}
