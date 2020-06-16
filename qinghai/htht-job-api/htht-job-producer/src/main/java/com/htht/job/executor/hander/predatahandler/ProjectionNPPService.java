package com.htht.job.executor.hander.predatahandler;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.UUIDTool;
import com.htht.job.executor.model.dms.module.MetaImg;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.paramtemplate.PreDataParam;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dms.MetaImgService;
import com.htht.job.executor.service.dms.MetaInfoService;
import org.apache.commons.lang3.StringUtils;
import org.htht.util.*;
import org.jeesys.common.jpa.search.Specifications;
import org.jeesys.common.jpa.search.Specifications.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.util.*;

/**
 * @author HHui
 * 投影数据
 */
@Service("projectionNPPService")
public class ProjectionNPPService {
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
        String inputF = triggerParam.getExecutorParams();
        String[] inputFs = inputF.split("#HTHT#");
        String issue = inputFs[0];
        String inputStr = inputFs[1];
        File dataFile = new File(inputStr);
        /** =======2.创建日志文件=========== **/
        String outputlogpath = triggerParam.getLogFileName();
        XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
        XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "投影开始执行");
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
            if ("GLL".equals(preDataParam.getProjectionIdentify())) {
                resolutionx = Integer.toString((int) (Double.parseDouble(preDataParam.getResolutionX()) * 100000));
            } else {
                resolutionx = preDataParam.getResolutionX();
            }
            Date fileDate = DateUtil.strToDate(issue, Consts.DateForMat.yyMMddHHmmFormat);
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
            XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "投影完成");
            Map<String, String> projectionResultMap = XmlProjectionUtil.getProjection(outputXml);
            String successInfo = projectionResultMap.get("loginfo");
            // 入库方式
            if (true) {
                if (successInfo != null && successInfo.indexOf("成功") > -1) {
                    // 查询是否有最近一小时的入库数据，如果有获取OrbitIdentify
                    synchronized (this) {
                        String orbitIdentify = getOrbitIdentify(fileDate, satellite, sensor, resolutionx);
                        if (!StringUtils.isBlank(orbitIdentify)) {
                            projectionResultMap.put("OrbitIdentify", orbitIdentify);
                        }
                        // 投影数据入库 meta_img表 meta_info表
                        saveProject2DB(outputDir, projectionResultMap, fileDate, satellite, sensor, resolutionx,
                                imgBasePath);
                    }
                    XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "投影结果入库完成");
                    // 释放redis
                    redisService.remove("projection_" + redisKeyName);
                } else if (successInfo != null && successInfo.indexOf("数据不在范围内") > -1) {
                    String inputFileName = dataFile.getAbsoluteFile().getName();
                    File inputFileDir = dataFile.getParentFile();
                    String pattern = inputFileName.substring(inputFileName.indexOf("_"));
                    inputFileDir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            if (pathname.getName().contains(pattern)) {
                                try {
                                    pathname.delete();
                                } catch (Exception e) {
                                }
                            }
                            return false;
                        }
                    });

                    redisService.remove("projection_" + redisKeyName);
                } else {
                    XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "投影数据不在范围内");
                    redisService.remove("projection_" + redisKeyName);
                }
            }

        } else {
            XxlJobLogger.logByfile(outputlogpath, dataFile.getAbsolutePath() + "文件不存在");
        }

        return result;
    }

    private String getOrbitIdentify(Date fileDate, String satellite, String sensor, String resolutionx) {
        Builder<MetaImg> builder = Specifications.builder();
        try {
            builder.ge("fProducetime", DateUtil.addDate(fileDate, Calendar.MINUTE, -30));
            builder.le("fProducetime", DateUtil.addDate(fileDate, Calendar.MINUTE, 30));
            builder.eq("fSatelliteid", satellite);
            builder.eq("fSensorid", sensor);
            builder.eq("fResolutionx", resolutionx);
            builder.eq("fDatatype", "projection");
            List<MetaImg> metaImg = metaImgService.getAll(builder.build());
            if (metaImg.isEmpty()) {
                return null;
            }
            MetaInfo metaInfo = metaInfoService.getById(metaImg.get(0).getfDataid());
            if (null == metaInfo) {
                return null;
            }
            return metaImg.get(0).getfOrbitIdentify();

        } catch (ParseException e) {
            return null;
        }
    }

    private void saveProject2DB(String outPutDir, Map<String, String> projectionMap, Date fileDate, String satellite,
                                String sensor, String resolutionx, String imgBasePath) {
        String outPutFileName = projectionMap.get("OutputFilename");

        String thumbnail = imgBasePath + File.separator + projectionMap.get("Thumbnail");
        String minx = projectionMap.get("minx");
        String maxx = projectionMap.get("maxx");
        String miny = projectionMap.get("miny");
        String maxy = projectionMap.get("maxy");
        String orbitIdentify = projectionMap.get("OrbitIdentify");
        String level = projectionMap.get("Level");
        String dayOrNight = projectionMap.get("DayOrNight");
        String projectionIdentify = projectionMap.get("ProjectionIdentify");
        if ("MODIS".equalsIgnoreCase(sensor) && DateUtil.getDateOnlyHour(fileDate) >= 7
                && DateUtil.getDateOnlyHour(fileDate) <= 19) {
            dayOrNight = "D";
        }
        if ("H08".equalsIgnoreCase(satellite)) {
            dayOrNight = "D";
        }
        if ("FY4A".equalsIgnoreCase(satellite) && "AGRI".equalsIgnoreCase(sensor) && DateUtil.getDateOnlyHour(fileDate) >= 0
                && DateUtil.getDateOnlyHour(fileDate) <= 10) {
            dayOrNight = "D";
        }
        String fileDate1 = DateUtil.formateDate(fileDate, Consts.DateForMat.yyMMddHHmmssFormatSplited);
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
        valSql.append(",'" + fileDate1 + "'");
        colSql.append(",F_PRODUCETIME");
        valSql.append(",'" + fileDate1 + "'");
        colSql.append(",F_CENTERTIME");
        valSql.append(",'" + fileDate1 + "'");
        colSql.append(",f_level");
        valSql.append(",'" + level + "'");// 数据级别
        colSql.append(",f_resolutionx");
        valSql.append(", '" + resolutionx + "'");
        colSql.append(",f_orbitidentify");
        valSql.append(", '" + orbitIdentify + "'");// 观测标识
        colSql.append(",F_DATATYPE");
        valSql.append(", '" + Consts.PreDateOutType.PROJECTION + "'");// 投影处理标识
        colSql.append(",F_DAYORNIGHT");
        valSql.append(", '" + dayOrNight + "'");// 投影处理标识
        colSql.append(",f_projectiontype");
        valSql.append(", '" + projectionIdentify + "'");// 投影类型

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
        argMap.put("ExtArgs", preDataParam.getExtArgs());
        argMap.put("OutputDir", outputDir);
        argMap.put("Bands", preDataParam.getBands());
        argMap.put("ProjectionIdentify",
                preDataParam.getProjectionIdentify() == null || "".equals(preDataParam.getProjectionIdentify())
                        || "none".equals(preDataParam.getProjectionIdentify()) ? "GLL"
                        : preDataParam.getProjectionIdentify());

        argMap.put("ValidEnvelopes", new String[]{preDataParam.getValidEnvelopes()});
        argMap.put("Envelopes", new String[]{preDataParam.getEnvelopes()});
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
        result.setMessage("投影成功");
        return result;
    }

    // 获取输出xml名称
    private String getXmlName(String resolutionx, String outputXml) {
        if (resolutionx.length() == 3) {
            resolutionx = "0" + resolutionx;
        }
        resolutionx = "_" + resolutionx + "M";
        return outputXml.replaceAll("_\\d{4}M", resolutionx);
    }

}
