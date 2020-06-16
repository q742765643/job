package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.ProcCmd;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.util.DubboIpUtil;

@Transactional
@Service("imageCorrectionHandlerService")
public class ImageCorrectionHandlerService {
	@Autowired
	private FileUtil fileUtil;
//	@Value("${xxl.job.executePath}")
//	private String executePath;
	@Resource
	DubboService dubboService;
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			HandlerParam handlerParam = JSON.parseObject(jsonString, HandlerParam.class);
			Map<String, String> metaImg = handlerParam.getArchiveMap();
			StringBuffer param = new StringBuffer();
			String p1 = "", p2 = "", p3 = "", p4 = "", p5 = "", p6 = "", p7 = "", p8 = "";
			
			for (Map.Entry<String, String> entry : metaImg.entrySet()) {
				if (entry.getKey().equals("F_DATAUPPERLEFTLONG")) {
					p1 =  entry.getValue();
				} else if (entry.getKey().equals("F_DATAUPPERLEFTLAT")) {
					p2 = "|" + entry.getValue();
				} else if (entry.getKey().equals("F_DATAUPPERRIGHTLONG")) {
					p3 = "|" + entry.getValue();
				} else if (entry.getKey().equals("F_DATAUPPERRIGHTLAT")) {
					p4 = "|" + entry.getValue();
				} else if (entry.getKey().equals("F_DATALOWERLEFTLONG")) {
					p5 = "|" + entry.getValue();
				} else if (entry.getKey().equals("F_DATALOWERLEFTLAT")) {
					p6 = "|" + entry.getValue();
				} else if (entry.getKey().equals("F_DATALOWERRIGHTLONG")) {
					p7 = "|" + entry.getValue();
				} else if (entry.getKey().equals("F_DATALOWERRIGHTLAT")) {
					p8 = "|" + entry.getValue();
				}
			}
			// 四角坐标
			param.append(p1);
			param.append(p2);
			param.append(p3);
			param.append(p4);
			param.append(p5);
			param.append(p6);
			param.append(p7);
			param.append(p8);
			// 取出所有JPG图片
			String[] extensions = new String[] { ".jpg" };
			String workPath = handlerParam.getWorkSpacePath();
//			if(handlerParam.getArchiveRules().getFiletype()==0) {
//				workPath = handlerParam.getWorkSpaceDir();
//			}
			Iterator<File> jpgs = fileUtil.listFiles(workPath, extensions);
			File j = null;
			// 入库规则
			ArchiveRules archiveRules = handlerParam.getArchiveRules();

			// 如果设置了jpg过滤条件
			if (null == j && null != archiveRules.getRegexpjpg() && !archiveRules.getRegexpjpg().equals("")) {
				// 通过正则匹配文件
				j = fileUtil.getFileByRegexp(jpgs, archiveRules.getRegexpjpg());
			} else if (null == j && jpgs.hasNext()) {
				// 未设置过滤条件,默认只读取第一个
				j = (File) jpgs.next();
			}
			if (j == null) {
				result.setErrorMessage("图片配准-匹配图片未找到！");
				return result;
			}
			// 图片路径
			param.append("|"+workPath);
			// 图片名称
			param.append("|"+j.getName());
			
			String[] args = param.toString().split("[|]");
			if(args.length < 10 ) {
				result.setErrorMessage("图片配准-参数个数错误！");
				return result;
			}
			ReturnT<String> os= new ReturnT<String>(DubboIpUtil.getOsName());
			String executePath = dubboService.getExePath(os);
			new ProcCmd().exec(executePath + "/ImgToRaster/ImgToRaster.exe " + param.toString());
			List<String> resultList = new ArrayList<>();
			resultList.add(jsonString);
			triggerParam.setOutput(resultList);

		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}
