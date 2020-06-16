package com.htht.job.executor.hander.cimiss2.service.impl;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.executor.hander.cimiss2.module.DownloadInfo;
import com.htht.job.executor.hander.cimiss2.module.ResultBeanOne;
import com.htht.job.executor.hander.cimiss2.service.CimissFileDownLoadService;
import com.htht.job.executor.model.downupload.DownResult;
import com.htht.job.executor.service.downupload.DownResultService;
import org.htht.util.FileOperate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;

//import com.htht.job.executor.hander.cimiss.util.CimissInterfaceAPI;

/**
 * Cimiss 接口实现类
 *
 */
@Transactional
@Service("CimissFileDownLoadService")
public class CimissFileDownLoadServiceimpl implements CimissFileDownLoadService {
    @Autowired
    private DownResultService downResultService;

    @Override
    public void getCimissData(String ip, String port, String userid, String password,String filePath, String filename,Map<String,String> map) throws Exception {
        CimissDownLoad(ip, port, userid, password, filePath, filename, map);

    }


	private  void CimissDownLoad(String ip, String port, String userid, String password,String filePath, String filename,Map<String,String> map) throws Exception {
        //从参数获取文件名中定义的类型
//        String[] list = filePath.replace("}", "*").replace("{", "*").split("*");
        List<String> string = Arrays.asList(filePath.replace("}", "!").replace("{", "!").split("!"));
        String formatPathType = string.get(1);
        filePath = string.get(0);
        //从路径中获取路径中定义的类型
        List<String> stringsthr = Arrays.asList(filename.split("\\."));
        List<String> stringsec = Arrays.asList(filename.replace("}", "!").replace("{", "!").split("!"));
        String formatNameType = "";
        if(!filename.isEmpty()&&!filename.equals("")&&!stringsthr.get(0).isEmpty()){
            formatNameType = stringsec.get(1);
        }


            String cimissUrl = "http://" + ip + ":" + port + "/cimiss-web/api?userId=" + userid + "&pwd=" + password;
            String fullUrl = cimissUrl + getURLParamsByMap(map);
            System.out.println(fullUrl);

            //创建文件夹
            creatFile(filePath);
            String data = getData(fullUrl);
            if (!(data.length() > 0)) {
                FileOperate operate = new FileOperate();
                operate.delFolder(filePath);
                return;
            }
            //解析返回结果，下载数据文件
        ResultBeanOne resultBean = new ResultBeanOne();
              JSONObject jso= JSON.parseObject(data);
            if (("".equals(data))) {
                resultBean.setReturnCode("-1");
                resultBean.setReturnMessage("No result for request");
            } else {
                String returnCode = (String)jso.get("returnCode");
                String fieldUnits = (String)jso.get("fieldUnits");
                if(!"- - byt -".equals(fieldUnits)){
                    //下载数据 2 TXT文件
                    //入库
                    DownResult downResult = new DownResult();
                    downResult.setZt("1");
                    downResult.setRealFileName("test");
                    downResult.setBz("cimissData2file");
                    downResult.setFormat("TXT");
                    DownResult downResult2 = downResultService.saveDownResult(downResult);


//                    TextToFile
                    String savename = "";
                    String finalpath = "";

                    String name = "";
                    String nameb = "";
                    if(map.get("interfaceId").contains("TimeRange")){
                        List<String> list = Arrays.asList(map.get("timeRange").split(","));
                        name = list.get(1).substring(0,list.get(1).length()-1);
                        nameb = list.get(0).substring(1,list.get(1).length());

                    }else{
                        name = map.get("times");
                    }

                    //文件路径中是否有自定义类型
                    if (!formatPathType.isEmpty()){

                        try {
                            //创建文件夹
                            finalpath = filePath+"\\"+ getByType(formatPathType,name);
                            creatFile(filePath+"\\"+ getByType(formatPathType,name));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }else{
                        finalpath = filePath;
                        creatFile(filePath);
                    }
                    //文件名中是否有自定义类型
                    if(!formatNameType.isEmpty()){
                        try {
                            savename = filename.replace("{"+formatNameType+"}",getByType(formatNameType,name));
                            TextToFile(finalpath+"\\"+savename,data);
                        } catch (Throwable throwable) {
                            checkFileExists(finalpath,savename,downResult2.getId());
                            throwable.printStackTrace();
                        }
                    }else{
                        savename = filename;
                        if(filename.isEmpty() || filename.equals("") || stringsthr.get(0).isEmpty()){
                            savename = nameb+"_"+name+".txt";
                            if(nameb.isEmpty()){
                                savename = name+".txt";
                            }
                            if(1 != filename.length()){
                                savename = nameb+"_"+name+filename;
                            }
                        }
                        TextToFile(finalpath+"\\"+savename,data);
                    }
                    checkFileExists(finalpath,savename,downResult2.getId());
                    return;
                }
                //下载数据文件
                //获取文件信息list
                JSONArray array = jso.getJSONArray("DS");
                List<DownloadInfo> downinfo = new ArrayList();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    DownloadInfo info = new DownloadInfo();
                    info.setFileName(obj.getString("FILE_NAME"));
                    info.setFileSize(obj.getString("FILE_SIZE"));
                    info.setFormat(obj.getString("FORMAT"));
                    info.setFileURL(obj.getString("FILE_URL"));
                    downinfo.add(info);
                }
                if (!"0".equals(returnCode) || downinfo.size() < 1) {
                    resultBean.setReturnCode("-1");
                    resultBean.setReturnMessage("No result");
                }


                String finalpath = "";
                //下载前将需要下载的数据文件信息入库
                for(DownloadInfo info : downinfo){
                    DownResult downResult = new DownResult();
                    downResult.setZt("1");
                    downResult.setRealFileName(info.getFileName());
                    downResult.setFileSize(Long.parseLong(info.getFileSize()));
                    downResult.setBz("cimiss2file");
                    downResult.setFormat(info.getFormat());
                    DownResult downResult2 = downResultService.saveDownResult(downResult);
                    info.setId(downResult2.getId());
                }
                //从路径中截取的格式信息
                for (DownloadInfo info : downinfo) {
                    String savename = "";

                    String name =info.getFileName();
                    //文件路径中是否有自定义类型
                    if (!formatPathType.isEmpty()){
                        try {
                            //创建文件夹
                            finalpath = filePath+"\\"+ getNameByType(formatPathType,name);
                            creatFile(filePath+"\\"+ getNameByType(formatPathType,name));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }else{
                        finalpath = filePath;
                        creatFile(filePath);
                    }
//                    System.out.println(info.getFileURL());
                    //文件名中是否有自定义类型
                    if(!formatNameType.isEmpty()){
                        try {
                             savename = filename.replace("{"+formatNameType+"}",getNameByType(formatNameType,name));
                            fileDownload(info.getFileURL(),finalpath+"\\"+savename, Long.parseLong(info.getFileSize()));
                        } catch (Throwable throwable) {
                            checkFileExists(finalpath,savename,info.getId());
                            throwable.printStackTrace();
                        }
                    }else{
                        savename = filename;
                        if(filename.isEmpty() || filename.equals("") || stringsthr.get(0).isEmpty()){
                            savename = info.getFileName();
                        }
                        fileDownload(info.getFileURL(), finalpath+"\\"+savename, Long.parseLong(info.getFileSize()));
                    }
                    checkFileExists(finalpath,savename,info.getId());
                 }
            }

	}

    /**
     * 将下载后的信息入库
     * @param path
     * @param name
     */
    public void checkFileExists(String path,String name,String id){
//        			String path= "D:\\data\\appss\\fine\\20180829\\ac-ma-test-20180829.nc";
			File myPath = new File(path+"\\"+name);
            int type = 2;
	        if (myPath.exists()){
                type = 0;
	        }
        DownResult downResult = new DownResult();
        downResult.setId(id);
        downResult.setZt(type+"");
        downResult.setFileName(name);
        downResult.setFilePath(path+ "\\" + name);
        downResult.setUpdateTime(new Date());
        downResultService.uodateDownResult(downResult);
    }

    /**
     * 传入需要转换的类型和原文件名，返回修改的区域
     * @param type
     * @param filename
     * @return
     * @throws Throwable
     */
    public static String getNameByType(String type,String filename) throws Throwable {
        SimpleDateFormat format2 = null;
        SimpleDateFormat format = new SimpleDateFormat(type);
        Pattern pattern = Pattern.compile("(?<=\\D)\\d{8}(?!\\d)");
        Pattern pattern2 = Pattern.compile("(?<=\\D)\\d{4}(?!\\d)");
        Matcher matcher = pattern.matcher(filename);
        Matcher matcher2 = pattern2.matcher(filename);
        if(matcher.find()) {
            format2 = new SimpleDateFormat("yyyyMMdd");
            String date = matcher.group();
            if(matcher2.find()) {
                format2 = new SimpleDateFormat("yyyyMMddHHmm");
                date = date + matcher2.group();
            }
            return format.format(format2.parse(date));
        }

        return null;
    }

    public static String getByType(String type,String name) throws Throwable {
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");;
        SimpleDateFormat format = new SimpleDateFormat(type);
       return format.format(format2.parse(name));

    }
    
//    public static void main(String[] args){
////        String url = "http://10.181.89.55/cimiss-web/file?url=L3NwYWNlL2NpbWlzc19CRVhOL2RhdGEvbWV0ZGIvc2F0ZS9GWS0yRy9TQVRFX0wzX0YyR19WSVNT%0AUl9NV0JfT1RHX1NOVyAvMjAxOC9TQVRFX0wzX0YyR19WSVNTUl9NV0JfT1RHX1NOVy1QT0FELTIw%0AMTgwOTAxLkFXWA%3D%3D";
//        String url = "http://localhost:8080/htht-job-dq/brace/datatest";
//        fileDownload(url,"D:\\DataOutput\\test.xml",44826);
//    }

    public static  String getURLParamsByMap(Map<String, String> paramsMap) {
        Set<String> set = paramsMap.keySet();
        String paramStr = "";
        for (String param : set) {
            String paramValue = (String) paramsMap.get(param);
            if(!"".equals(paramValue)){
                paramStr = paramStr + "&" + param + "=" + paramValue;
            }
        }
        return paramStr;
    }

    public static void creatFile(String path){
        File myPath = new File(path);
        if ( !myPath.exists()){
            myPath.mkdirs();
            System.out.println("创建文件夹路径为："+ path);
        }
    }

    /**
     * 请求拼接好的url
     * @param fullurl
     * @return
     */
    public static String getData(String fullurl) {
        StringBuilder retStr = new StringBuilder();
        URL url = null;
        BufferedReader reader = null;
        try {
            url = new URL(fullurl);
//            url = new URL("http://localhost:8080/htht-job-dq/brace/datatest");
            URLConnection con = url.openConnection();
            con.setConnectTimeout(30000);
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = reader.readLine();
            int i = 0;
            while (line != null) {
                i++;
                System.out.println(i + " : " + line.length());
                retStr.append(line).append("\r\n");
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception ex1) {
            ex1.printStackTrace();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retStr.toString();
    }

    public static void TextToFile(final String Filename, final String strBuffer) {
        try {
            // 创建文件对象
            File fileText = new File(Filename);
            FileWriter fileWriter = new FileWriter(fileText);
            fileWriter.write(strBuffer);
            fileWriter.close();
        } catch (IOException e) {
            //
            e.printStackTrace();
        }
    }
    public static long getRemoteSize(URL url2) throws IOException {
        long size = 0L;
        HttpURLConnection httpConnection = null;
        httpConnection = (HttpURLConnection) url2.openConnection();
        size = httpConnection.getContentLengthLong();
        httpConnection.disconnect();
        return size;
    }

    /*
     * 去掉temp后缀
     */
    private static boolean renameFile(File file){
        String fileName = file.getAbsolutePath();
        int pos = fileName.indexOf(".temp");
        if(pos>0){
            if(file.renameTo(new File(fileName.substring(0, pos)))){
                file.delete();
                return true;
            }
        }
        return false;
    }
    /**
     * 格式化文件大小
     * @param fileSize
     * @return
     */
    public static String formatSize(Long fileSize) {
        String formatSize = "";
        DecimalFormat df = new DecimalFormat("0.##");
        if (fileSize / (1024 * 1024 * 1024) > 0) {
            formatSize = df.format(Float.parseFloat(fileSize.toString())/ (1024 * 1024 * 1024))+ " GB";
        } else if (fileSize / (1024 * 1024) > 0) {
            formatSize = df.format(Float.parseFloat(fileSize.toString())/ (1024 * 1024))+ " MB";
        } else if (fileSize / (1024) > 0) {
            formatSize = df.format(Float.parseFloat(fileSize.toString())/ (1024))+ " KB";
        } else {
            formatSize = fileSize + " 字节";
        }
        return formatSize;
    }

    /**
     * 文件下载
     * @param urlStr
     * @param destFile
     * @param remoteSize
     * @return
     */
    public static long fileDownload(String urlStr, String destFile, long remoteSize) {
        destFile = destFile.replaceAll("\\\\", "/");
        String tempFileName = destFile+".temp";
        File temp = new File(tempFileName);

        // 下载网络文件
        long bytesum = 0;
        int byteread = 0;
        InputStream is = null;
        RandomAccessFile raf = null;
        HttpURLConnection httpConnection = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long fileSize = 0;
        try {
            URL url = new URL(urlStr);
            String fileName = destFile.substring(destFile.lastIndexOf('/')+1);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("Accept-Encoding", "identity");
            httpConnection.setRequestProperty("User-Agent", "NetFox");
            httpConnection.setConnectTimeout(30000);  		//连接超时
            httpConnection.setReadTimeout(30000);  			//读取超时
            if(temp.exists() ){
                if (!temp.renameTo(temp)) {			//该文件正在被下载
                    System.out.println(destFile+"文件正在下载");
                    return 0;
                }
                fileSize = temp.length();
                if(temp.length() == remoteSize && renameFile(temp)){
                    System.out.println(destFile+"下载完成");
                    return remoteSize;						//下载完成，返回
                }else if(fileSize > remoteSize){		//如果temp文件大小大于远程文件，则说明temp文件错误，删除
                    temp.delete();
                    fileSize = 0;
                }
            }
            else if(!temp.getParentFile().exists()){
                temp.getParentFile().mkdirs();
            }
            httpConnection.setRequestProperty("RANGE", "bytes=" +  fileSize + "-");
            byte[] buffer = new byte[1204 * 10];


            raf = new RandomAccessFile(temp, "rw");

            if(remoteSize == 0 ){
                return fileSize;
            }else if(remoteSize == -1){
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread;
                    raf.write(buffer, 0, byteread);
                }
            }else {
                raf.seek(fileSize);
                is = httpConnection.getInputStream();
                if(is == null){
                    System.out.println(new Date()+"获取文件流失败："+destFile);
                    httpConnection.disconnect();
                    return 0;
                }

                long step = (remoteSize) /100;
                if(step == 0){
                    System.out.println(sdf.format(new Date())+"http download progress： 0% ("+fileName+",文件下载失败！");
                    return 0;
                }
                long process= (fileSize+bytesum) /step;
                System.out.println(formatSize((long)httpConnection.getContentLength()));
                System.out.println(sdf.format(new Date())+"http download progress： "+process +"% ("+fileName+","+formatSize(fileSize+bytesum)+")：");

                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread;
                    raf.write(buffer, 0, byteread);

                    long nowProcess = (fileSize+bytesum) /step;
                    if(nowProcess > process){
                        process = nowProcess;
                     //System.out.println("http download progress： "+process +"% ("+fileName+","+formatSize(fileSize+bytesum)+")");
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
                if (is != null) {
                    is.close();
                }
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        renameFile(temp);
        System.out.println(destFile+"下载完成");
        return bytesum;
    }

//    public static void main(String[] args) throws Exception {
//        /*http://10.181.89.55/cimiss-web/api?userId=BEXN_KYS_ygzx&pwd=6151653&interfaceId=getSurfEleInRectByTimeRange&dataCode=SURF_CHN_MUL_HOR&elements=Station_Name,Province,TEM,TEM_Max,DPT,RHU&timeRange=[20180904000000,20180904010000]&minLon=73.33&maxLon=135.05&minLat=3.51&maxLat=53.33&limitCnt=30&dataFormat=json*/
//        /*http://1:2/cimiss-web/api?userId=3&pwd=4&minLon=2.2&maxLat=3.3&dataFormat=json&minLat=1.1&elements=Station_Name,Province,City&maxLon=4.4&interfaceId=getSurfEleInRectByTimeRange&dataCode=SURF_CHN_MUL_HOR&timeRange=(20170526060600,20170527060600)*/
//        CimissFileDownLoadServiceimpl cimiss =  new CimissFileDownLoadServiceimpl();
//        String ip = "1";
//        String port = "2";
//        String userid = "3";
//        String password = "4";
//
//        //SATE_L3_F2G_VISSR_MWB_OTG_CFR-20180829-0121.hdf
//        String filePath = "D:\\data\\appss\\fine\\{yyyyMMdd}\\";
//        String filename = "ac-ma-test-{yyyyMMdd}.nc";
//        String interfaceId = "getSurfEleInRectByTime";
//        Map<String, String> map = new HashMap<String, String>();
//
//
//        //要素
//        String elements = "Station_Name,Province,City";
//        //资料代码
//        String dataCode = "SURF_CHN_MUL_HOR";
//        //时间
//        String time = "20170526060600,20170527060600";
//        String isopen = "false";
//        map.put("interfaceId", interfaceId);
//        map.put("dataCode", dataCode);
//        map.put("elements", elements);
//        map.put("dataFormat", "json");
//        if (!Boolean.parseBoolean(isopen)) {
//            map.put("timeRange", "[" + time + "]");
//            interfaceId = map.get("interfaceId").replace("Time", "TimeRange");
//            map.put("interfaceId", interfaceId);
//
//        } else {
//            map.put("times", time);
//        }
//        if (interfaceId.contains("InRegion")) {
//            //行政编码
//            String adminCodes = "111100";
//            map.put("adminCodes", adminCodes);
//            cimiss.CimissDownLoad(ip, port, userid, password, filePath, filename, map);
//
//        } else if (interfaceId.contains("InRect")) {
//            String minLat = "1.1";
//            String minLon = "2.2";
//            String maxLat = "3.3";
//            String maxLon = "4.4";
//            map.put("minLat", minLat);
//            map.put("minLon", minLon);
//            map.put("maxLat", maxLat);
//            map.put("maxLon", maxLon);
//            cimiss.CimissDownLoad(ip, port, userid, password, filePath, filename, map);
//        }
//    }



}
