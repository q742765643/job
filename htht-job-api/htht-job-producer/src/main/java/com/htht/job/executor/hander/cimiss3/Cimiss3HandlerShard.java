package com.htht.job.executor.hander.cimiss3;/**
 * Created by zzj on 2018/12/6.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.cimiss2.module.DownloadInfo;
import com.htht.job.executor.model.downupload.CimissDownInfoDTO;
import com.htht.job.executor.model.paramtemplate.CimissDownParam;
import com.htht.job.executor.service.downupload.DownResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: htht-job
 * @description:
 * @author: zzj
 * @create: 2018-12-06 17:06
 **/
@Service("cimiss3HandlerShard")
public class Cimiss3HandlerShard implements SharingHandler {
    @Autowired
    private DownResultService downResultService;

    @Override
    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
        ResultUtil<List<String>> result = new ResultUtil<>();
        /**===========1.获取参数================**/
        CimissDownParam downParam = JSON.parseObject(params, CimissDownParam.class);
        String filePath = downParam.getFilePath().replaceAll("\\\\", "/");
        String fileName = downParam.getFilename();
        /**===========2.http参数拼装url================**/
        String url = this.preHttpParam(downParam);
        /*JSONObject obj1 = new JSONObject();
        obj1.put("returnCode","0");
        JSONArray array1=new JSONArray();
        JSONObject obj2 = new JSONObject();
        obj2.put("FILE_NAME","Z_NAFP_C_BABJ_20180123153026_P_CLDAS_NRT_ASI_0P0625_DAY-SHU-2018012000.nc");
        obj2.put("FILE_SIZE","20");
        obj2.put("FORMAT","20");
        obj2.put("FILE_URL","20");

        array1.add(0,obj2);
        array1.add(1,obj2);

        obj1.put("DS",array1);

        String data=JSON.toJSONString(obj1);*/
        /**===========3.http获取返回值================**/
        RestTemplate restTemplate = new RestTemplate();
        String data = restTemplate.getForObject(url, String.class);
        JSONObject json = JSON.parseObject(data);
        String returnCode = (String) json.get("returnCode");
        if (returnCode.equals("0")) {
            List<String> stringList = new ArrayList<>();
            /**===========4.组装返回list================**/
            this.getReturnList(json, filePath, fileName, stringList);
            result.setResult(stringList);

        } else {
            String returnMessage = (String) json.get("returnMessage");
            result.setErrorMessage(returnMessage);
            return result;
        }

        return result;
    }
    /** 
    * @Description: http url组装 
    * @Param: [downParam] 
    * @return: java.lang.String 
    * @Author: zzj
    * @Date: 2018/12/11 
    */ 
    public String preHttpParam(CimissDownParam downParam) {
        Map map = new LinkedHashMap();
        map.put("interfaceId", downParam.getInterfaceId());
        map.put("dataCode", downParam.getDataCode());
        String time = downParam.getTimes();
        String isopen = downParam.getIsopen();
        SimpleDateFormat fromat2 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat fromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (!Boolean.parseBoolean(isopen)) {
                List<String> list = Arrays.asList(time.split(" - "));
                map.put("timeRange", "(" + fromat2.format(fromat.parse(list.get(0))) + "," + fromat2.format(fromat.parse(list.get(1))) + ")");
            } else {
                map.put("times", fromat2.format(fromat.parse(time)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("elements", downParam.getElements());
        map.put("eleValueRanges", downParam.getEleValueRanges());
        map.put("dataFormat", "json");
        CimissDownInfoDTO info = downResultService.getCimissInfo("cimiss");
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        StringBuilder url = new StringBuilder();
        String cimissUrl = "http://" + info.getIpAddr() + ":" + info.getPort() + "/cimiss-web/api?userId=" + info.getUserName() + "&pwd=" + info.getPwd();
        url.append(cimissUrl);
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }

        return url.toString();

    }
    /** 
    * @Description: 获取文件名中的日期 
    * @Param: [fileName] 
    * @return: java.lang.String 
    * @Author: zzj
    * @Date: 2018/12/11 
    */ 
    public String getFileTime(String fileName) {
        Pattern pattern = Pattern.compile("[0-9]{14}");
        Matcher matcher = pattern.matcher(fileName);
        String temp = "";
        if (matcher.find()) {
            temp = matcher.group();
        }
        return temp;
    }
    /** 
    * @Description: 格式化日期
    * @Param: [name, type, temp] 
    * @return: java.lang.String 
    * @Author: zzj
    * @Date: 2018/12/11 
    */ 
    public String rename(String name, String type, String temp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String newName = "";
        Date date = null;
        try {
            date = dateFormat.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat(type);
        newName = name.replace("{" + type + "}", newDateFormat.format(date));
        return newName;

    }


    public void getReturnList(JSONObject json, String filePath, String fileName, List<String> stringList) {

        JSONArray array = json.getJSONArray("DS");
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            DownloadInfo info = new DownloadInfo();
            info.setFileName(obj.getString("FILE_NAME"));
            info.setFileSize(obj.getString("FILE_SIZE"));
            info.setFormat(obj.getString("FORMAT"));
            info.setFileURL(obj.getString("FILE_URL"));
            info.setFilePath(filePath);
            this.replaceValue(filePath, fileName, info);

            File file = new File(info.getFilePath() + '/' + info.getFileName());
            if (!file.exists()) {
                stringList.add(JSON.toJSONString(info));
            }

        }
    }
    /** 
    * @Description: 替换filepath，filename 
    * @Param: [filePath, fileName, info] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/12/11 
    */ 
    public void replaceValue(String filePath, String fileName, DownloadInfo info) {
        String filePathDataType = "";
        if (filePath.indexOf('{') != -1) {
            filePathDataType = filePath.split("\\{")[1].split("\\}")[0];
        }
        String fileNameDataType = "";
        if (fileName.indexOf('{') != -1) {
            fileNameDataType = fileName.split("\\{")[1].split("\\}")[0];
        }
        String tempDate = this.getFileTime(info.getFileName());
        if (!filePathDataType.isEmpty()) {
            String newFilePath = this.rename(filePath, filePathDataType, tempDate);
            info.setFilePath(newFilePath);
            File toPathFile = new File(newFilePath);
            if (!toPathFile.exists()) {
                toPathFile.mkdirs();
            }
        }
        if (!fileNameDataType.isEmpty()) {
            String newFileName = this.rename(fileName, fileNameDataType, tempDate);
            info.setFileName(newFileName);
        }

    }
}

