package com.htht.job.executor.hander.predatahandler;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.DataTimeHelper;
import org.htht.util.ServerImpUtil;
import org.htht.util.XmlMakeUtil;
import org.htht.util.XmlParseUtil;
import org.htht.util.XmlProjectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.DateUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.UUIDTool;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.paramtemplate.PreDataParam;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dms.MetaImgService;
import com.htht.job.executor.service.dms.MetaInfoService;

/**
 * 投影数据
 *
 */
@Service("projectionService")
public class ProjectionService {
	@Autowired
	private DataMataInfoService dataMataInfoService;
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private MetaInfoService metaInfoService;
	@Autowired
	private MetaImgService metaImgService;
	@Autowired
	private RedisService redisService;

	/**
	 * inputXml 在inupPath中，文件名：处理的数据+.xml outputXml 在outputPath中， 文件名：处理的数据+.xml
	 * 
	 * @param triggerParam
	 * @param result
	 * @return
	 */
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		PreDataParam preDataParam = null;
		try {
			preDataParam = JSON.parseObject(triggerParam.getModelParameters(), PreDataParam.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		File dataFile = new File(triggerParam.getExecutorParams());
		/** =======2.创建日志文件=========== **/
		String outputlogpath = triggerParam.getLogFileName();
		XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
		XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "预处理开始执行");
		// 回调需要用到
		triggerParam.setLogFileName(outputlogpath);
		// 判断文件是否存在
		if (dataFile.exists()) {
			String argXmlDir = preDataParam.getProjectionInputArgXml();
			String exePath = preDataParam.getProjectionExeLocaiton();
			String inputFilePath = dataFile.getAbsolutePath();
			String fileName = dataFile.getName();
			String redisKeyName = fileName;
			String[] ssr = preDataParam.getPreDataTaskName().split("_");
			String satellite = "";
			String sensor = "";
			String resolutionx = "";
			if (ssr.length == 3) {
				satellite = ssr[0];
				sensor = ssr[1];
				resolutionx = ssr[2];
			}
			if ((preDataParam.getPreDataTaskName().contains("FY4A")
					|| (preDataParam.getPreDataTaskName().contains("FY3D")
					&& preDataParam.getPreDataTaskName().contains("MERSI")))
					&& !StringUtils.isEmpty(preDataParam.getResolutionX())) {
				if ("GLL".equals(preDataParam.getProjectionIdentify())) {
					resolutionx = Integer.toString((int) (Double.parseDouble(preDataParam.getResolutionX()) * 100000));
				} else {
					resolutionx = preDataParam.getResolutionX();
				}
			}
			Date fileDate = dataMataInfoService.findDataByFileName(fileName);
			//特殊处理，H8通过文件名查找文件的时间
			if(fileDate==null && fileName.indexOf("H08") > 0){
				fileDate = new Date(DataTimeHelper.getDataTimeFromFileNameByPattern(fileName,"yyyyMMdd_HHmm"));
			}
			// 输入xml
			String inputXml = argXmlDir + File.separator + satellite + File.separator + sensor + File.separator
					+ DateUtil.formatDateTime(fileDate, "yyyyMMdd") + File.separator + fileName + ".xml";
			inputXml = getXmlName(resolutionx, inputXml);
			ServerImpUtil.touchFile(inputXml);

			// 图片输出路径
			String imgBasePath = satellite + File.separator + sensor + File.separator
					+ DateUtil.formatDateTime(fileDate, "yyyyMMdd");
			// 目录为主目录+卫星标识+传感器+数据时间yyyyMMdd
			String outputDir = preDataParam.getOutputDataFilePath() + File.separator + satellite + File.separator
					+ sensor + File.separator + DateUtil.formatDateTime(fileDate, "yyyyMMdd");

			String outputXml = outputDir + File.separator + fileName + ".xml";
			outputXml = getXmlName(resolutionx, outputXml);
			ServerImpUtil.touchFile(outputXml);

			Map<String, Object> argMap = paramProjection(inputFilePath, outputDir, preDataParam);
			try {
				XmlMakeUtil.makeXml(argMap, inputXml);
				XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "xml生成");
			} catch (Exception e) {
				e.printStackTrace();
			}

			doProjection(exePath, inputXml, outputXml, result);
			XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "预处理完成");

			Map<String, String> projectionResultMap = XmlProjectionUtil.getProjection(outputXml);
			String successInfo = projectionResultMap.get("loginfo");
			String thumbnailName = projectionResultMap.get("Thumbnail");
			// 入库方式
			if (true) {
				if (successInfo != null && successInfo.indexOf("成功") > 0) {

					// L2数据入库 meta_img表 meta_info表
					saveProject2DB(outputDir, projectionResultMap, fileDate, satellite, sensor, resolutionx,
							imgBasePath);
					//扩大基于FY4A、FY3D二级数据对一级数据的更新范围
					fileName = getFileName(fileName);
					// 更新L1数据，把拇指图更新
					String sql = "SELECT i.F_DATAID FROM htht_dms_meta_img i,htht_dms_meta_info f"
							+ " WHERE i.F_DATAID = f.f_dataid " + " AND i.F_LEVEL = 'L1' "
							+ " AND f.f_datasourcename in ('" + fileName + "')";

					List<String> online = baseDaoUtil.getByJpql(sql, null);
					for (int i = 0; i < online.size(); i++) {
						MetaInfo metaInfo = metaInfoService.getById(online.get(i));
						metaInfo.setF_viewdatapath(imgBasePath + File.separator + thumbnailName);
						metaInfoService.save(metaInfo);
						saveToMetaImage(online.get(i), projectionResultMap);
					}

					XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "预处理结果入库完成");
					// 释放redis
					redisService.remove("projection_" + redisKeyName);
				} else {
					XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "预处理数据不在范围内");
					redisService.remove("projection_" + redisKeyName);
				}
			} else {
				// TODO
			}

		} else {
			XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "文件不存在");
		}

		return result;
	}

	private String getFileName(String fileName) {
		//扩大基于FY4A二级数据对一级数据的更新范围
		if (fileName.contains("FY4A") && fileName.contains("AGRI")) {
			fileName = fileName + "','" + fileName.replace("4000", "2000") + "','"
					+ fileName.replace("4000", "1000") + "','" + fileName.replace("4000", "0500");
		}
		//扩大基于FY3D二级数据对一级数据的更新范围
		if (fileName.contains("FY3D") && fileName.contains("MERSI")) {
			fileName = fileName + "','" + fileName.replace("1000", "0250");
		}
		return fileName;
	}

	private void saveToMetaImage(String fDataId, Map<String, String> projectionResultMap) {
		if (StringUtils.isEmpty(fDataId)) {
			return;
		}

		String minx = projectionResultMap.get("minx");
		String maxx = projectionResultMap.get("maxx");
		String miny = projectionResultMap.get("miny");
		String maxy = projectionResultMap.get("maxy");

		String sql = "update htht_dms_meta_img";
		StringBuffer sqlStr = new StringBuffer(" set ");
		sqlStr.append("F_DATAUPPERLEFTLAT = ");// 图像左上角纬度
		sqlStr.append("'" + maxy + "',");
		sqlStr.append(" F_DATAUPPERLEFTLONG = ");// 图像左上角经度
		sqlStr.append("'" + minx + "',");

		sqlStr.append("F_DATAUPPERRIGHTLAT = ");// 图像右上角纬度
		sqlStr.append("'" + maxy + "',");
		sqlStr.append("F_DATAUPPERRIGHTLONG = ");// 图像右上角经度
		sqlStr.append("'" + maxx + "',");

		sqlStr.append("F_DATALOWERLEFTLAT = ");// 左下角纬度
		sqlStr.append("'" + miny + "',");
		sqlStr.append("F_DATALOWERLEFTLONG = ");// 左下角经度
		sqlStr.append("'" + minx + "',");

		sqlStr.append("F_DATALOWERRIGHTLAT = ");// 右下角纬度
		sqlStr.append("'" + miny + "',");
		sqlStr.append("F_DATALOWERRIGHTLONG = ");// 右下角经度
		sqlStr.append("'" + maxx + "'");

		sqlStr.append(" where F_DATAID = ");
		sqlStr.append("'" + fDataId + "'");
		System.out.println("=========>" + sql + sqlStr.toString());

		baseDaoUtil.executeSql(sql + sqlStr);
	}

	private void saveProject2DB(String outPutDir, Map<String, String> projectionMap, Date fileDate, String satellite,
			String sensor, String resolutionx, String imgBasePath) {
		String outPutFileName = projectionMap.get("OutputFilename");

		String thumbnail = imgBasePath + File.separator + projectionMap.get("Thumbnail");
		// String satellite = projectionMap.get("Satellite");
		// String sensor = projectionMap.get("Sensor");
		// String resolutionx = projectionMap.get("ResolutionX");
		//
		String minx = projectionMap.get("minx");
		String maxx = projectionMap.get("maxx");
		String miny = projectionMap.get("miny");
		String maxy = projectionMap.get("maxy");

		File file = new File(outPutDir + File.separator + outPutFileName);
		// 获取需要入库的主表名称
		String uuid = UUIDTool.getUUID();
		// 物理信息
		MetaInfo metaInfo = new MetaInfo();
		metaInfo.setId(uuid);
		metaInfo.setCreateTime(new Date());
		metaInfo.setF_dataid(uuid);
		metaInfo.setF_dataname(fileUtil.getFileNameWithoutSuffix(file));
		metaInfo.setF_importdate(new Date());
		metaInfo.setF_datasize(fileUtil.getDirSize(file));
		metaInfo.setF_dataunit("B");
		metaInfo.setF_dataextname(fileUtil.getFileNameSuffix(file));
		metaInfo.setF_isfile(0);
		metaInfo.setF_location(file.getAbsolutePath());
		metaInfo.setF_flag(0);
		metaInfo.setF_recycleflag(0);
		metaInfo.setF_viewdatapath(thumbnail);

		metaInfo.setF_datasourcename(file.getName());
		metaInfoService.save(metaInfo);

		// 影像信息
		String sql = "insert into htht_dms_meta_img";
		StringBuffer colSql = new StringBuffer("(f_dataid");
		StringBuffer valSql = new StringBuffer("('" + uuid + "'");
		colSql.append(",F_SATELLITEID");
		valSql.append(",'" + satellite + "'");
		colSql.append(",F_SENSORID");
		valSql.append(",'" + sensor + "'");
		colSql.append(",F_RECEIVETIME");
		valSql.append(",'" + fileDate + "'");
		colSql.append(",F_PRODUCETIME");
		valSql.append(",'" + fileDate + "'");
		colSql.append(",F_CENTERTIME");
		valSql.append(",'" + fileDate + "'");
		colSql.append(",f_level");
		valSql.append(",'L2'");
		colSql.append(",f_resolutionx");
		valSql.append(", '" + resolutionx + "'");

		colSql.append(",F_DATAUPPERLEFTLAT");// 图像左上角纬度
		valSql.append(", '" + maxy + "'");
		colSql.append(",F_DATAUPPERLEFTLONG");// 图像左上角经度
		valSql.append(", '" + minx + "'");

		colSql.append(",F_DATAUPPERRIGHTLAT");// 图像右上角纬度
		valSql.append(", '" + maxy + "'");
		colSql.append(",F_DATAUPPERRIGHTLONG");// 图像右上角经度
		valSql.append(", '" + maxx + "'");

		colSql.append(",F_DATALOWERLEFTLAT");// 左下角纬度
		valSql.append(", '" + miny + "'");
		colSql.append(",F_DATALOWERLEFTLONG");// 左下角经度
		valSql.append(", '" + minx + "'");

		colSql.append(",F_DATALOWERRIGHTLAT");// 右下角纬度
		valSql.append(", '" + miny + "'");
		colSql.append(",F_DATALOWERRIGHTLONG");// 右下角经度
		valSql.append(", '" + maxx + "'");

		colSql.append(")");
		valSql.append(")");
		Object[] obj = {};

		System.out.println("=========>" + sql + colSql.toString() + " values " + valSql.toString());

		baseDaoUtil.executeSql(sql + colSql.toString() + " values " + valSql.toString(), obj);
	}

	private Map<String, Object> paramProjection(String inputFilePath, String outputDir, PreDataParam preDataParam) {
		Map<String, Object> argMap = new HashMap<String, Object>();
		argMap.put("InputFilename", inputFilePath);
		argMap.put("ExtArgs",preDataParam.getExtArgs());
		argMap.put("OutputDir", outputDir);
		argMap.put("Bands", preDataParam.getBands());
		argMap.put("ProjectionIdentify",
				preDataParam.getProjectionIdentify() == null || "".equals(preDataParam.getProjectionIdentify())
						|| "none".equals(preDataParam.getProjectionIdentify()) ? "GLL"
								: preDataParam.getProjectionIdentify());

		argMap.put("ValidEnvelopes", new String[] { preDataParam.getValidEnvelopes() });
		argMap.put("Envelopes", new String[] { preDataParam.getEnvelopes() });
		argMap.put("Formate", preDataParam.getFormate());
		argMap.put("ResolutionX", preDataParam.getResolutionX());
		argMap.put("ResolutionY", preDataParam.getResolutionY());
		argMap.put("PervObservationDate", "");
		argMap.put("PervObservationTime", "");
		argMap.put("OrbitIdentify", "");
		return argMap;
	}

	public ResultUtil<String> doProjection(String exePath, String inputXml, String outputXml,
			ResultUtil<String> result) {
		ServerImpUtil.executeCmd(exePath, inputXml);
		result.setMessage("预处理成功");
		return result;
	}

	/**
	 * 投影处理
	 * 
	 * @param dataFile
	 * @param preDataParam
	 * @return
	 */
	private String callProjectionService(File dataFile, PreDataParam preDataParam, ResultUtil<String> result) {
		String outputXml = "";
		String preDataTaskName = preDataParam.getPreDataTaskName();
		if (StringUtils.isNotBlank(preDataTaskName) && preDataTaskName.contains("FY4A_AGRI_")) {
			outputXml = agriProjection(dataFile, preDataParam);
		} else {
			// outputXml = doProjection(dataFile, preDataParam, result);
		}
		return outputXml;
	}

	private boolean makeXmlAndOkFile2DB(String outputXml) {
		Map<String, String> map = XmlParseUtil.getProjection(outputXml);
		/**
		 * 生成xml
		 * 
		 * Map<String , Object > argMap = new HashMap<String, Object>();
		 * argMap.put("SatelliteID", map.get("")); argMap.put("SensorID", value);
		 * argMap.put("ProduceTime", value); argMap.put("CloudPercent", value);
		 * argMap.put("TopLeftLatitude", value); argMap.put("TopLeftLongitude", value);
		 * argMap.put("TopRightLatitude", value); argMap.put("TopRightLongitude",
		 * value); argMap.put("BottomRightLatitude", value);
		 * argMap.put("BottomRightLongitude", value); argMap.put("BottomLeftLatitude",
		 * value); argMap.put("BottomLeftLongitude", value);
		 * 
		 * try { boolean b = XmlMakeUtil.makeXml(argMap, outputXml); } catch (Exception
		 * e) { e.printStackTrace(); }
		 */
		// 生成ok
		return false;
	}
	//获取输出xml名称
	private String getXmlName(String resolutionx,String outputXml) {
		if (resolutionx.length() == 3) {
			resolutionx = "0" + resolutionx;
		}
		resolutionx = "_" + resolutionx + "M";
		return outputXml.replaceAll("_\\d{4}M", resolutionx);
	}
	// FY4A预处理
	public String agriProjection(File dataFile, PreDataParam preDataParam) {
		/*
		 * String argXmlDir = preDataParam.getProjectionInputArgXml();
		 * 
		 * String exePath = preDataParam.getProjectionExeLocaiton(); String
		 * createTif_exePath = exePath.split(",")[0]; //生成Tif的exe的路径 String
		 * createPng_exePath = exePath.split(",")[1]; //生成Png的exe的路径 String
		 * inputFilePath = dataFile.getAbsolutePath(); String fileName =
		 * dataFile.getName();
		 * 
		 * String outputDir = preDataParam.getOutputDataFilePath() +
		 * PreDataOutputFileUtil .getSubDirByDay(preDataParam.getPreDataTaskName(),
		 * fileName); File infile = new File(outputDir); if (!infile.exists()) {
		 * infile.mkdirs(); } String createTif_outputFilePath = outputDir + "\\" +
		 * fileName.replace(".HDF", ".tif"); //生成Tif的路径 String createPng_outputFilePath
		 * = outputDir + "\\" + fileName.replace(".HDF", ".png"); //生成Png的路径 String
		 * outputXmlPath = createTif_outputFilePath + ".xml"; //生成xml的路径 //调用生成Tif的exe
		 * StringBuffer createTif_commandstrbuff = new StringBuffer();
		 * createTif_commandstrbuff.append(createPngexelocation).append(" ")
		 * .append(inputFilePath).append(" ") .append(createTif_outputFilePath);
		 * ServerImpUtil.executeCmdByCommandstr(createTif_exePath,
		 * createTif_commandstrbuff.toString()); try { File createTif_outputFile = new
		 * File(createTif_outputFilePath); if (createTif_outputFile.exists()) {
		 * //调用生成Png的exe StringBuffer createPng_commandstrbuff = new StringBuffer();
		 * createPng_commandstrbuff.append(createTif_outputFilePath).append(" ")
		 * .append(createPng_outputFilePath).append(" ") .append("1024").append(" ")
		 * .append("1024").append(" ") .append("0").append(" ") .append("1").append(" ")
		 * .append("2"); ServerImpUtil.executeCmdByCommandstr(createPng_exePath,
		 * createPng_commandstrbuff.toString()); File createPng_outputFile = new
		 * File(createPng_outputFilePath); File outputXml = new File(outputXmlPath); if
		 * (createPng_outputFile.exists() && outputXml.exists()) { String fcontent =
		 * FileUtils.readFileToString(outputXml,"UTF-8"); String loginfo = fcontent
		 * .substring(fcontent.indexOf("<ret>")+5,fcontent.indexOf("</ret>"));
		 * //生成所需的xml内容 String xmlPath = createTif_outputFilePath.replace(".tif",
		 * ".xml"); String tempxmlPath = createPng_outputFilePath + ".xml"; Map<String,
		 * Object> argMap = new LinkedHashMap<String, Object>();
		 * argMap.put("OrbitFilename", fileName); String[] ssr =
		 * preDataParam.getPreDataTaskName().split("_"); String satellite = "",sensor =
		 * "",resolution = ""; if(3 == ssr.length) { satellite = ssr[0]; sensor =
		 * ssr[1]; resolution = ssr[2]; } argMap.put("Satellite", satellite);
		 * argMap.put("Sensor", sensor); argMap.put("Level", "L1");
		 * argMap.put("ProjectionIdentify", preDataParam.getProjectionIdentify() == null
		 * || "".equals(preDataParam.getProjectionIdentify()) ||
		 * "none".equals(preDataParam.getProjectionIdentify()) ? "GLL" :
		 * preDataParam.getProjectionIdentify()); SimpleDateFormat sdf = new
		 * SimpleDateFormat("yyyyMMdd"); SimpleDateFormat sdf1 = new
		 * SimpleDateFormat("HHmm"); long obsDateTime = 0; try{ obsDateTime =
		 * DataTimeHelper.getDataTimeFromFileName(fileName); } catch (ParseException e){
		 * e.printStackTrace(); } Date observTime = new Date(obsDateTime); String
		 * observDateStr = sdf.format(observTime); String observTimeStr =
		 * sdf1.format(observTime); argMap.put("ObservationDate", observDateStr);
		 * argMap.put("ObservationTime", observTimeStr); argMap.put("Station",
		 * fileName.split("_")[3]); argMap.put("DayOrNight", ""); argMap.put("Length",
		 * String.valueOf(dataFile.length())); argMap.put("OrbitIdentify",
		 * observTimeStr); Map<String, Object> argMapSecond = new LinkedHashMap<String,
		 * Object>(); argMapSecond.put("OutputFilename", fileName.replace(".HDF",
		 * ".tif")); argMapSecond.put("Thumbnail", fileName.replace(".HDF", ".png"));
		 * argMapSecond.put("ExtendFiles", ""); argMapSecond.put("Envelope", new
		 * String[] {
		 * "name:GBAL,minx:104.1022,maxx:113.4411,miny:20.2582,maxy:27.7836"});
		 * argMapSecond.put("ResolutionX", preDataParam.getResolutionX());
		 * argMapSecond.put("ResolutionY", preDataParam.getResolutionY());
		 * argMapSecond.put("Length", String.valueOf(createTif_outputFile.length()));
		 * argMap.put("OutputFiles", argMapSecond); Map<String, Object> argMapThird =
		 * new LinkedHashMap<String, Object>(); if (StringUtils.isNotBlank(loginfo) &&
		 * "success".equals(loginfo)) { argMapThird.put("loglevel", "info");
		 * argMapThird.put("loginfo", "投影成功"); } else { argMapThird.put("loglevel",
		 * "error"); argMapThird.put("loginfo", loginfo); } argMap.put("log",
		 * argMapThird); XmlMakeUtil.makeXml(argMap, tempxmlPath); File xmlFile = new
		 * File(xmlPath); if (xmlFile.exists()) { xmlFile.delete(); }
		 * XmlMakeUtil.turnUTF8withBOM(tempxmlPath, xmlPath); String xmlContent =
		 * FileUtils.readFileToString(new File(xmlPath),"UTF-8"); return
		 * ServerImpUtil.encodeXMLContent(xmlContent); } } } catch (IOException e ) {
		 * e.printStackTrace(); } catch (Exception e) { e.printStackTrace(); }
		 */
		return "";

	}
}
