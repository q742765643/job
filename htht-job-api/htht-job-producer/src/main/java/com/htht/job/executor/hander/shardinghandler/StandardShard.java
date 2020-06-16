package com.htht.job.executor.hander.shardinghandler;


import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.DateUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ZipFileUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;

import org.htht.util.DataTimeHelper;
import org.htht.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

/**
 *
 * @author zhanghongda 常规流程：
 * (1).遍历文件夹找文件目录或者文件作为输入,获取时间,取指定时间内数据，执行【proMark、startTime、endTime、startHour、endHour、inputFolder、、filePattern、timePattern、outputFolder】
 * (2).查询数据库,获取文件,取指定时间内数据，执行使用【proMark、startTime、endTime、startHour、endHour、inputDataType】
 *  固定参数：proMark、startTime、endTime、startHour、endHour、exePath、cycle
 *  其他参数：inputFolder、outputFolder、filePattern、timePattern、timePattern、  dataLevel
 *  基础分片类
 *  基础标识
 *  处理标识：proMark 1.scanFolder 2.scanDataBase
 *  起始时间：startTime
 *  结束时间：endTime
 *  起始小时：startHour 0~24
 *  结束小时：endHour 0~24
 *  算法路径：exePath
 *  输入目录：inputFolder
 *  输出目录：outFolder
 *  文件正则：filePattern
 *  时间正则：timePattern
 *  输入数据源匹配类型：timePattern
 *  卫星_传感器_分辨率：inputDataType
 *  数据级别：dataLevel
 *  产品周期：cycle
 *  文件后缀：dataSuffix
 *  行政区划：areaID
 *  产品标识：identify
 */

public abstract class StandardShard implements SharingHandler{
    @Autowired
    private DataMataInfoService dataMataInfoService;

    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {


        ResultUtil<List<String>> result = new ResultUtil<List<String>>();

        /** =======1.获取处理标识【1.scanFolder 2.scanDataBase】参数校验=========== **/
        String proMark = (String) fixmap.get("proMark");
        String exePath = (String)fixmap.get("exePath");
        String startTime = (String) fixmap.get("startTime");
        String endTime = (String) fixmap.get("endTime");
        String startHourInString = (String) fixmap.get("startHour");
        String endHourInString = (String) fixmap.get("endHour");
        String identify = (String)fixmap.get("identify");
        String cycle = (String) dymap.get("cycle");
        String areaID = (String)dymap.get("areaID");
        String outputFolder = (String)dymap.get("outFolder");
        if (StringUtils.isEmpty(proMark) || StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)
                || StringUtils.isEmpty(cycle)||StringUtils.isEmpty(exePath)||StringUtils.isEmpty(areaID)||
                StringUtils.isEmpty(identify)||StringUtils.isEmpty(outputFolder)) {
            return result;
        }
        proMark = StringUtils.trimAllWhitespace(proMark).toLowerCase();
        Date startDate = DateUtil.strToDate(startTime, "yyyy-MM-dd");
        Date endDate = DateUtil.strToDate(endTime, "yyyy-MM-dd");
        Calendar startCa = Calendar.getInstance();
        startCa.setTime(startDate);
        Calendar endCa = Calendar.getInstance();
        endCa.setTime(endDate);
        int startHour = -1;
        int endHour = 25;
        if(!StringUtils.isEmpty(startHourInString)&&!StringUtils.isEmpty(endHourInString)){
            try{
                int sHour = Integer.parseInt(startHourInString);
                int eHour = Integer.parseInt(endHourInString);
                if(sHour>=0&&sHour<=24&&eHour>=0&&eHour<=24){
                    startHour = sHour;
                    endHour = eHour;
                }
            }catch(NumberFormatException e){

            }
        }

        List<String> matchFiles = new ArrayList<String>();
        if (proMark.equals("scanfolder")) {
            String inputFolder = (String) fixmap.get("inputFolder");
            String filePattern = (String) fixmap.get("filePattern");
            String timePattern = (String) fixmap.get("timePattern");
            String scantype = (String) fixmap.get("scantype");
            if (StringUtils.isEmpty(inputFolder) || StringUtils.isEmpty(filePattern)
                    || StringUtils.isEmpty(timePattern)) {
                return result;
            }
            String BZPattern = (String) fixmap.get("BZPattern");
            if (!StringUtils.isEmpty(BZPattern)){
                /** =======1.扫描压缩文件=========== **/

                List<File> bz2Files = FileUtil.iteratorFile(new File(inputFolder),BZPattern);

                /** =======2.解压压缩数据=========== **/
                if(bz2Files!=null&&bz2Files.size()>0){
                    for(File file:bz2Files){
                        ZipFileUtil.decompressBz2File(file.getAbsolutePath(),true);
                    }
                }
            }
            List<File> fileList =null;
            if ("2".equals(scantype)) {
            	fileList = FileUtil.iteratorDirectory(new File(inputFolder), filePattern);
			}else {
				fileList = FileUtil.iteratorFileAndDirectory(new File(inputFolder), filePattern);
			}
            if (fileList == null || fileList.size() == 0) {
                return result;
            }
            Calendar ca = Calendar.getInstance();
            for (File file : fileList) {
                ca.setTimeInMillis(DataTimeHelper.getDataTimeFromFileNameByPattern(file.getName(), timePattern));
                if (ca.after(startCa)&&ca.before(endCa)&&ca.get(Calendar.HOUR_OF_DAY)>startHour&&ca.get(Calendar.HOUR_OF_DAY)<endHour) {
                    matchFiles.add(file.getAbsolutePath());
                }
            }
        } else if (proMark.equals("scandatabase")) {
            String inputDataType = (String) fixmap.get("inputDataType");
            inputDataType = StringUtils.trimAllWhitespace(inputDataType);
            if(StringUtils.isEmpty(inputDataType)){
                return result;
            }
            List<String> splitInputDataType = Arrays.asList(inputDataType.split("_"));
            if(splitInputDataType.size()<3){
                return result;
            }
            Map<String, Object> paramMap = new HashMap<String, Object>();


            String dataSuffix = (String)fixmap.get("dataSuffix");
            if(!StringUtils.isEmpty(dataSuffix)){
                paramMap.put("dataextname", dataSuffix);
            }else{
                paramMap.put("dataextname", ".ldf");
            }

            paramMap.put("satellite", splitInputDataType.get(0));
            paramMap.put("sensor", splitInputDataType.get(1));
            paramMap.put("resolution", splitInputDataType.get(2));
            paramMap.put("startDate", startTime);
            paramMap.put("endDate", endTime);
            paramMap.put("startHour",String.valueOf(startHour));
            paramMap.put("endHour",String.valueOf(endHour));
            String dataLevel = (String)fixmap.get("dataLevel");
            if(!StringUtils.isEmpty(dataLevel)){
                paramMap.put("dataLevel",dataLevel);
            }else{
                paramMap.put("dataLevel", "L2");
            }
            matchFiles = dataMataInfoService.findDataToProject(paramMap);
        } else {
            return result;
        }


        if (matchFiles.isEmpty()) {
            return result;
        }
        if (!result.isSuccess()) {
            return result;
        }
        result.setResult(matchFiles);
        if (result.getResult().isEmpty()) {
            System.out.println("EndShard");
        }
        return result;
    }
}
