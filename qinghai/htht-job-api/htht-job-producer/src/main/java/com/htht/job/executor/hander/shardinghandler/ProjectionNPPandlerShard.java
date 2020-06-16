package com.htht.job.executor.hander.shardinghandler;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.paramtemplate.PreDataParam;
import com.htht.job.executor.redis.RedisService;
import org.htht.util.Consts;
import org.htht.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author HHui
 * 扫描到所有待处理文件
 */
@Service("projectionNPPHandlerShard")
public class ProjectionNPPandlerShard implements SharingHandler {

    @Autowired
    private DataMataInfoService dataMataInfoService;
    @Autowired
    private RedisService redisService;

    /**
     * 查询待预处理的文件 将待预处理的文件提交至调度中心
     */
    @Override
    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
        ResultUtil<List<String>> result = new ResultUtil<List<String>>();

        PreDataParam preDataParam = null;
        try {
            preDataParam = JSON.parseObject(params, PreDataParam.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        String inputPathParam = preDataParam.getInputDataFilePath();
        String timePattern = preDataParam.getFileNamePattern();
        String projectionIdentify = preDataParam.getProjectionIdentify();
        String[] ssr = preDataParam.getPreDataTaskName().split("_");
        List<String> machedFileList = new ArrayList<String>();
        String satellite = null;
        String inputDataFilePath = null;
        if (ssr.length == 3) {
            satellite = ssr[0];
            String sensor = ssr[1];
            String resolutionX = ssr[2];
            if ("GLL".equals(preDataParam.getProjectionIdentify())) {
                resolutionX = Integer.toString((int) (Double.parseDouble(preDataParam.getResolutionX()) * 100000));
            } else {
                resolutionX = preDataParam.getResolutionX();
            }

            paramMap.put("satellite", satellite);
            paramMap.put("sensor", sensor);
            paramMap.put("resolution", resolutionX);
            paramMap.put("datatype", Consts.PreDateOutType.PROJECTION);
            paramMap.put("projectionIdentify", projectionIdentify);

            String beginTime = null;
            String endTime = null;
            String beginTimeNoS = null;
            if (Consts.PreDateType.DAY_RANGE_INT.equals(preDataParam.getDateType())) {
                beginTime = DateUtil.getCurrentDateStringWithHourOffset(Consts.DateForMat.yyMMddHHmmssFormatSplited,
                        -6);
                beginTimeNoS = beginTime.replaceAll("-", "");
                endTime = DateUtil.getCurrentDateStringWithHourOffset(Consts.DateForMat.yyMMddHHmmssFormatSplited, 0);
                // 世界时间转换
                paramMap.put("startDate", beginTime);
                paramMap.put("endDate", endTime);

            } else if (Consts.PreDateType.DAY_RANGE_STR.equals(preDataParam.getDateType())) {
                String[] temp = preDataParam.getProjectioDate().split(" - ");
                beginTime = temp[0] + " 00:00:00";
                beginTimeNoS = temp[0].replaceAll("-", "");
                endTime = temp[1] + " 23:59:00";

                preDataParam.setStartDate(beginTime);
                preDataParam.setEndDate(endTime);


                paramMap.put("startDate", preDataParam.getStartDate());
                paramMap.put("endDate", preDataParam.getEndDate());
            }
            // 获取目录下的数据
            Date date = DateUtil.strToDate(endTime, Consts.DateForMat.yyMMddHHmmssFormatSplited);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            while (DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")
                    .compareTo(beginTimeNoS.substring(0, 6)) >= 0) {
                // 获取所有的数据
//				String folderTime = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM");

                if (inputPathParam.contains("{")) {
                    inputDataFilePath = DateUtil.getPathByDate(inputPathParam, calendar.getTime());
                }
                File inputDir = new File(inputDataFilePath);
                if (!inputDir.exists()) {
                    calendar.add(Calendar.MONTH, -1);
                    continue;
                }
                List<String> findNppDataToFire = findNppDataToFire(beginTime, endTime, inputDir);

                if (findNppDataToFire.isEmpty()) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    continue;
                }
                // 查询预处理结果
                List<String> existIssues = dataMataInfoService.findProjectedData(paramMap);
                // 查询需要处理的文件，并过滤已处理时次文件
                for (String inputF : findNppDataToFire) {
                    String[] inputFs = inputF.split("#HTHT#");
                    File inputFile = new File(inputFs[1]);
                    if (!existIssues.contains(inputFs[0])
                            && !redisService.exists("projection_" + inputFile.getName())) {
                        machedFileList.add(inputF);
                        redisService.set("projection_" + inputFile.getName(), inputFile.getName());
                    }
                }
                calendar.add(Calendar.MONTH, -1);
            }
        }
        if (!result.isSuccess()) {
            return result;
        }
        if (machedFileList.isEmpty()) {
            result.setResult(new ArrayList<>());
            return result;
        }
        result.setResult(machedFileList);
        return result;

    }

    private List<String> findNppDataToFire(String doStartStr, String doEndStr, File inputDir) {
        doStartStr = doStartStr.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
        doEndStr = doEndStr.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
        List<String> list = new ArrayList<>();
        String[] fileNames = inputDir.list();
        if (null == fileNames || fileNames.length == 0) {
            return list;
        }

        String[] datas = {
                "SVI01", "SVI02", "SVI03", "SVI04", "SVI05",
                "SVDNB", "IVOBC", "IVCDB", "ICDBG", "GMTCO", "GMODO", "GITCO", "GIMGO", "GDNBO",
                "SVM01", "SVM02", "SVM03", "SVM04", "SVM05", "SVM06", "SVM07", "SVM08", "SVM09", "SVM10", "SVM11", "SVM12", "SVM13", "SVM14", "SVM15", "SVM16"};
        List<String> fileNamesList = Arrays.asList(fileNames);
        // 去除重复文件后缀
        List<String> filterNames = fileNamesList.stream().filter(name -> !name.contains("xml")).map(name -> name.substring(0, name.lastIndexOf("_"))).distinct()
                .collect(Collectors.toList());
        // 时间匹配
        Pattern pattern = Pattern.compile("_d\\d{8}_t\\d{4}");
        for (String filterName : filterNames) {
            int flag = 0;
            for (String data : datas) {
                String name = filterName + "_" + data.toLowerCase() + ".h5";
                File file = new File(inputDir, name);
                if (fileNamesList.contains(name) && file.renameTo(file)) {
                    flag++;
                }
            }
            if (flag < 30) {
                continue;
            }
            Matcher matcher = pattern.matcher(filterName);
            if (matcher.find()) {
                String issue = matcher.group().replaceAll("_d", "").replaceAll("_t", "");
                if (issue.compareTo(doStartStr) >= 0 && issue.compareTo(doEndStr) <= 0) {
                    list.add(issue + "#HTHT#" + inputDir.getAbsolutePath() + "/" + filterName + "_svi01.h5");
                }
            }
        }
        return list;
    }

}