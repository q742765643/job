package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.model.dms.module.ArchiveFiledManage;
import com.htht.job.executor.model.dms.module.ArchiveFiledMap;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.service.dms.ArchiveFiledManageService;
import com.htht.job.executor.service.dms.ArchiveFiledMapService;

@Transactional
@Service("fileNameParsingHandlerService")
public class FileNameParsingHandlerService {
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private ArchiveFiledMapService archiveFiledMapService;
	@Autowired
	private ArchiveFiledManageService archiveFiledManageService;

	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String splitParam = (String) triggerParam.getDynamicParameter().get("splitParam");
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			
			HandlerParam handlerParam = JSON.parseObject(jsonString, HandlerParam.class);
			
			File file = new File(handlerParam.getBaseUrl());
			
			if (!file.isFile() || !file.exists()) {
				result.setErrorMessage("文件名解析-实体文件不存在！");
				return result;
			}
			
			if("".equals(splitParam) || null == splitParam) {
				result.setErrorMessage("文件名解析-splitParam参数错误！");
				return result;
			}
			
			String fileName = fileUtil.getFileNameWithoutSuffix(file);
			String[] split = fileName.split(splitParam);

			// 入库规则
			ArchiveRules archiveRules = handlerParam.getArchiveRules();
			ArchiveFiledManage archiveFiledManage;
			String nodeVal = "";
			// 存储需要入库的影像信息
			Map<String, String> archiveMap = new HashMap<>();
			
			// 根据catalogcode获取需要入库的名称规则
			List<ArchiveFiledMap> archiveFiledMap = archiveFiledMapService
					.getArchiveFiledMap(archiveRules.getCatalogcode());
			for (int i = 0; i < archiveFiledMap.size(); i++) {
				// 根据配置的名称顺序从名称中取值
				try {
					nodeVal = "";
					if(archiveFiledMap.get(i).getF_archivefield().indexOf("_") != -1) {
						String[]newArchiveFieldList = archiveFiledMap.get(i).getF_archivefield().split("_");
						for (int j = 0; j < newArchiveFieldList.length; j++) {
							nodeVal += split[Integer.parseInt(newArchiveFieldList[j])];
						}
					} else if (archiveFiledMap.get(i).getF_archivefield().indexOf("(") != -1 && archiveFiledMap.get(i).getF_archivefield().indexOf(")") != -1) {
						String substrIndex = archiveFiledMap.get(i).getF_archivefield().substring(archiveFiledMap.get(i).getF_archivefield().indexOf("(")+1,archiveFiledMap.get(i).getF_archivefield().indexOf(")"));
						if (substrIndex.startsWith("-")) {
							nodeVal = split[Integer.parseInt(archiveFiledMap.get(i).getF_archivefield().replace("("+substrIndex+")", ""))];
							substrIndex = substrIndex.replace("-", "");
							nodeVal = nodeVal.substring(0, nodeVal.length()-Integer.parseInt(substrIndex));
						}
					} else {
						nodeVal = split[Integer.parseInt(archiveFiledMap.get(i).getF_archivefield())];
					}
				} catch (Exception e) {
					nodeVal = "";
				}
				if (!nodeVal.equals("")) {
					
					archiveFiledManage = archiveFiledManageService.getById(archiveFiledMap.get(i).getF_fieldmanageid());
					if(archiveFiledManage.getF_valuetype().indexOf("VARCHAR2") != -1 ) {
						nodeVal = "'"+nodeVal+"'";
					}
					if(archiveFiledManage.getF_valuetype().indexOf("DATE") != -1) {
						if(nodeVal.indexOf(",") != -1) {//高分5数据全谱段时间有多个，以逗号拼接。这里只取第一个
							nodeVal = nodeVal.split(",")[0];
						}
						if(nodeVal.length()>8 && nodeVal.length()<14) {
							int size = 14-nodeVal.length();
							for (int j = 0; j < size; j++) {
								nodeVal+="0";
							}
						}
						nodeVal = "'"+nodeVal+"'";
					}
					//MODIS数据分辨率重新赋值
					nodeVal = getModisResolution(nodeVal);

					// 如果值不是空值则根据字段ID取出影像表中对应的字段名称,放到map中.
					archiveMap.put(archiveFiledManage.getF_name(), nodeVal);
				}
			}
			// 添加到影像信息集合中,最后在入库插件中统一入库
			handlerParam.setArchiveMap(archiveMap);
			String resultMessage = JSON.toJSONString(handlerParam);
			List<String> resultList = new ArrayList<>();
			resultList.add(resultMessage);
			triggerParam.setOutput(resultList);
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}
	private String getModisResolution(String nodeVal) {
		if ("'MOD021K'".equalsIgnoreCase(nodeVal)) {
			nodeVal = "'1000'";
		}else if ("'MOD02HK'".equalsIgnoreCase(nodeVal)) {
			nodeVal = "'500'";
		}else if ("'MOD02QK'".equalsIgnoreCase(nodeVal)) {
			nodeVal = "'250'";
		}
		return nodeVal;
	}

	public static void main(String[] args) {
		String aa = "6(-1)";
		System.out.println(aa.replace("(-1)", ""));
			
	}
}
