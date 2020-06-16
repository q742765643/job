package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.img.PointBean;
import com.htht.job.executor.hander.dataarchiving.util.img.TransferProcess;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.service.dms.DiskService;

@Transactional
@Service("imageDelBlackEdgeHandlerService")
public class ImageDelBlackEdgeHandlerService {
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private DiskService diskService;
	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			HandlerParam handlerParam = JSON.parseObject(jsonString,
					HandlerParam.class);
			List<Disk> imgDisks = diskService.getImgDisk();
			if(imgDisks.size() > 0) {
				Disk imgDisk = imgDisks.get(0);
				handlerParam.setImgDiskInfo(imgDisk);
				boolean flag = ComputerConnUtil.login(imgDisk.getLoginurl(), imgDisk.getLoginname(), imgDisk.getLoginpwd());
				if (flag || new File(imgDisk.getLoginurl()).exists()) {
					// 数据解压路径
					String workPath = handlerParam.getWorkSpacePath();
					File workSpace = new File(workPath);
					if (workSpace.exists()) {
						// 取出所有JPG图片
						String[] extensions = new String[] { ".jpg",".png" };
						Iterator<File> jpgs = fileUtil.listFiles(workPath, extensions);
						File j = null;
						// 入库规则
						ArchiveRules archiveRules = handlerParam.getArchiveRules();
						
						// 默认先过滤配准图片
						j = fileUtil.getFileByEndWith(jpgs, "_raster.jpg");
						
						jpgs = fileUtil.listFiles(workPath, extensions);
						
						// 如果设置了jpg过滤条件
						if (null == j && null != archiveRules.getRegexpjpg() && !archiveRules.getRegexpjpg().equals("")) {
							// 通过正则匹配文件
							j = fileUtil.getFileByRegexp(jpgs, archiveRules.getRegexpjpg());
						} else if (null == j && jpgs.hasNext()) {
							// 未设置过滤条件,默认只读取第一个
							j = (File) jpgs.next();
						}
						// 判断是否有匹配的文件
						if (j != null) {
							// 卫星元数据
							Map<String, String> imgInfo = handlerParam.getArchiveMap();
							// 图片去黑边后存储地址
							String imgDelBlackPath = handlerParam.getDelBlackPath();
//							if(archiveRules.getFiletype()==0) {
//								imgDelBlackPath = handlerParam.getDelBlackDirPath();
//							}
							// 去除影像黑边
							PointBean pointBean = new PointBean();
							if(null == imgInfo.get("F_DATAUPPERLEFTLAT")) {
								result.setErrorMessage("JPG去黑边-经纬度信息不存在！");
								return result;
							}
							if(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLAT")) > Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLAT"))) {
								// 图像向右倾斜
								pointBean.setTopLeftY(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLAT")));
								pointBean.setTopLeftX(Double.parseDouble(imgInfo.get("F_DATALOWERLEFTLONG")));
								pointBean.setTopRightY(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLAT")));
								pointBean.setTopRightX(Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLONG")));
								pointBean.setBottomLeftY(Double.parseDouble(imgInfo.get("F_DATALOWERRIGHTLAT")));
								pointBean.setBottomLeftX(Double.parseDouble(imgInfo.get("F_DATALOWERLEFTLONG")));
								pointBean.setBottomRightY(Double.parseDouble(imgInfo.get("F_DATALOWERRIGHTLAT")));
								pointBean.setBottomRightX(Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLONG")));
								
								pointBean.setDATAUPPERLEFTLAT(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLAT")));
								pointBean.setDATAUPPERLEFTLONT(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLONG")));
								
								pointBean.setDATAUPPERRIGHTLAT(Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLAT")));
								pointBean.setDATAUPPERRIGHTLONG(Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLONG")));
								
								pointBean.setDATALOWERLEFTLAT(Double.parseDouble(imgInfo.get("F_DATALOWERLEFTLAT")));
								pointBean.setDATALOWERLEFTLONG(Double.parseDouble(imgInfo.get("F_DATALOWERLEFTLONG")));
								
								pointBean.setDATALOWERRIGHTLAT(Double.parseDouble(imgInfo.get("F_DATALOWERRIGHTLAT")));
								pointBean.setDATALOWERRIGHTLONG(Double.parseDouble(imgInfo.get("F_DATALOWERRIGHTLONG")));
							} else {
								// 图像向左倾斜
								pointBean.setTopLeftY(Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLAT")));
								pointBean.setTopLeftX(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLONG")));
								pointBean.setTopRightY(Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLAT")));
								pointBean.setTopRightX(Double.parseDouble(imgInfo.get("F_DATALOWERRIGHTLONG")));
								pointBean.setBottomLeftY(Double.parseDouble(imgInfo.get("F_DATALOWERLEFTLAT")));
								pointBean.setBottomLeftX(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLONG")));
								pointBean.setBottomRightY(Double.parseDouble(imgInfo.get("F_DATALOWERLEFTLAT")));
								pointBean.setBottomRightX(Double.parseDouble(imgInfo.get("F_DATALOWERRIGHTLONG")));
								
								pointBean.setDATAUPPERLEFTLAT(Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLAT")));
								pointBean.setDATAUPPERLEFTLONT(Double.parseDouble(imgInfo.get("F_DATAUPPERRIGHTLONG")));
								
								pointBean.setDATAUPPERRIGHTLAT(Double.parseDouble(imgInfo.get("F_DATALOWERRIGHTLAT")));
								pointBean.setDATAUPPERRIGHTLONG(Double.parseDouble(imgInfo.get("F_DATALOWERRIGHTLONG")));
								
								pointBean.setDATALOWERLEFTLAT(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLAT")));
								pointBean.setDATALOWERLEFTLONG(Double.parseDouble(imgInfo.get("F_DATAUPPERLEFTLONG")));
								
								pointBean.setDATALOWERRIGHTLAT(Double.parseDouble(imgInfo.get("F_DATALOWERLEFTLAT")));
								pointBean.setDATALOWERRIGHTLONG(Double.parseDouble(imgInfo.get("F_DATALOWERLEFTLONG")));
								
							}
							
							
							// 处理去除图片黑边并保存到指定目录
							new TransferProcess().disImageBackgroundByPath(j.getAbsolutePath(), j.getName(), imgDelBlackPath, pointBean);
							
							String resultMessage = JSON.toJSONString(handlerParam);
							List<String> resultList = new ArrayList<>();
							resultList.add(resultMessage);
							triggerParam.setOutput(resultList);
						} else {
							result.setErrorMessage("JPG去黑边-实体文件不存在！");
						}
					} else {
						result.setErrorMessage("JPG去黑边-文件夹不存在！");
					}
				} else {
					result.setErrorMessage("JPG去黑边-图片服务磁盘未连接！");
				}
			} else {
				result.setErrorMessage("JPG去黑边-没有可用图片磁盘！");
			}

		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}
}
