package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.htht.util.UtilDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.XmlUtil;
import com.htht.job.executor.model.dms.module.ArchiveFiledManage;
import com.htht.job.executor.model.dms.module.ArchiveFiledMap;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.service.dms.ArchiveFiledManageService;
import com.htht.job.executor.service.dms.ArchiveFiledMapService;

@Transactional
@Service("xMLParsingHandlerService")
public class XMLParsingHandlerService {
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private ArchiveFiledMapService archiveFiledMapService;
	@Autowired
	private ArchiveFiledManageService archiveFiledManageService;

	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			HandlerParam handlerParam = JSON.parseObject(jsonString,
					HandlerParam.class);
			// 获取数据解压路径
			String workPath = handlerParam.getWorkSpacePath();
			File workSpace = new File(workPath);
			if (workSpace.exists()) {
				// 文件过滤条件
				String[] extensions = new String[] { ".xml" };
				Iterator<File> xmls = fileUtil.listFiles(workPath, extensions);
				File j = null;
				// 入库规则
				ArchiveRules archiveRules = handlerParam.getArchiveRules();
				// 如果设置了xml过滤条件
				if (null != archiveRules.getRegexpxml() && !archiveRules.getRegexpxml().equals("")) {
					// 通过正则匹配文件
					j = fileUtil.getFileByRegexp(xmls, archiveRules.getRegexpxml());
				} else if (xmls.hasNext()) {
					// 未设置过滤条件,默认只读取第一个
					j = (File) xmls.next();
				}
				// 判断是否有匹配的文件
				if (j != null && j.exists()) {
					
					ArchiveFiledManage archiveFiledManage;
					String nodeVal = "";
					// 存储需要入库的影像信息
					Map<String, String> archiveMap = new HashMap<>();
					// 初始化XML影像信息对象
					XmlUtil xmlObj = new XmlUtil(j.getAbsolutePath());
					
					// 根据catalogcode获取需要入库的xml节点名称
					List<ArchiveFiledMap> archiveFiledMap = archiveFiledMapService
							.getArchiveFiledMap(archiveRules.getId());
					for (int i = 0; i < archiveFiledMap.size(); i++) {
						nodeVal = "";
						if(null != archiveFiledMap.get(i).getDefault_val() && !archiveFiledMap.get(i).getDefault_val().trim().equals("")) {
							nodeVal = archiveFiledMap.get(i).getDefault_val();
						} else {
							// 根据配置的节点名称从xml取值
							if(archiveFiledMap.get(i).getF_archivefield().indexOf(">") != -1) {
								nodeVal = xmlObj.getChiledVal(archiveFiledMap.get(i).getF_archivefield());
							} else {
								nodeVal = xmlObj.getValByNode(archiveFiledMap.get(i).getF_archivefield());
							}
						}
						if (!nodeVal.equals("")) {
							
							archiveFiledManage = archiveFiledManageService.getById(archiveFiledMap.get(i).getF_fieldmanageid());
							if(null != archiveFiledManage) {
								if(archiveFiledManage.getF_valuetype().indexOf("VARCHAR2") != -1 ) {
									nodeVal = "'"+nodeVal+"'";
								}
								if(archiveFiledManage.getF_valuetype().indexOf("DATE") != -1) {
									if(nodeVal.indexOf(",") != -1) {//高分5数据全谱段时间有多个，以逗号拼接。这里只取第一个
										nodeVal = nodeVal.split(",")[0];
									}
									nodeVal = "'"+nodeVal+"'";
								}
								// 如果值不是空值则根据字段ID取出影像表中对应的字段名称,放到map中.
								archiveMap.put(archiveFiledManage.getF_name(), nodeVal);
							}
						}
					}
					// 添加到影像信息集合中,最后在入库插件中统一入库
					handlerParam.setArchiveMap(archiveMap);
					String resultMessage = JSON.toJSONString(handlerParam);
					List<String> resultList = new ArrayList<>();
					resultList.add(resultMessage);
					triggerParam.setOutput(resultList);
				} else {
					result.setErrorMessage("XML解析-实体文件不存在！");
				}
			} else {
				result.setErrorMessage("XML解析-文件夹不存在！");
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}
