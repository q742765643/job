package com.htht.job.executor.hander.dataarchiving.util.webservice.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientUtil {

	 /**
     * 调用对方接口方法
     * @param path 对方或第三方提供的路径
     * @param data 向对方或第三方发送的数据，大多数情况下给对方发送JSON数据让对方解析
     */
    public static void interfaceUtil(String path,String data) {
        try {
            URL url = new URL(path);
            //打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PrintWriter out = null;
            //请求方式
//          conn.setRequestMethod("POST");
//           //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"); 
            conn.setRequestProperty("contentType", "utf-8"); 
            //设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
            //最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
            //post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //发送请求参数即数据
            out.print(data);
            //缓冲数据
            out.flush();
            //获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            //构造一个字符流缓存
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = "";
            while ((str = br.readLine()) != null) {
                System.out.println(str);
            }
            //关闭流
            is.close();
            //断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
            //固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
            conn.disconnect();
            System.out.println("完整结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        interfaceUtil("http://127.0.0.1:8080/api/achive/cp?jobId=17&json={\"archiveDiskInfo\":{\"createTime\":1523855935000,\"diskdesc\":\"归档磁盘\",\"diskfreesize\":68738740224,\"diskstatus\":0,\"disktotlesize\":199378333696,\"disktype\":\"1\",\"diskusesize\":130639593472,\"id\":\"2\",\"loginname\":\"LY\",\"loginpwd\":\"109413\",\"loginurl\":\"\\\\\\\\127.0.0.1\\\\zzj\\\\data\",\"updateTime\":1524030278000,\"usagerate\":34,\"version\":5},\"archivePath\":\"\",\"archiveRules\":{\"archivdisk\":\"2\",\"catalogcode\":\"A0302020101\",\"createTime\":1524037892000,\"id\":\"3\",\"regexpjpg\":\"(GF)(2)(_)(PMS)(\\\\d+)(_)(W)([+-]?\\\\d*\\\\.\\\\d+)(?![-+0-9\\\\.])(_)(N)([+-]?\\\\d*\\\\.\\\\d+)(?![-+0-9\\\\.])(_)((?:(?:[1]{1}\\\\d{1}\\\\d{1}\\\\d{1})|(?:[2]{1}\\\\d{3}))(?:[0]?[1-9]|[1][012])(?:(?:[0-2]?\\\\d{1})|(?:[3][01]{1})))(?![\\\\d])(_)(L)(1)(A)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(-)(MSS2)(\\\\.)(jpg)\",\"regexpstr\":\"(GF)(2)(_)(PMS)(\\\\d+)(_)(W)([+-]?\\\\d*\\\\.\\\\d+)(?![-+0-9\\\\.])(_)(N)([+-]?\\\\d*\\\\.\\\\d+)(?![-+0-9\\\\.])(_)((?:(?:[1]{1}\\\\d{1}\\\\d{1}\\\\d{1})|(?:[2]{1}\\\\d{3}))(?:[0]?[1-9]|[1][012])(?:(?:[0-2]?\\\\d{1})|(?:[3][01]{1})))(?![\\\\d])(_)(L)(1)(A)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\.)(tar)(\\\\.)(gz)\",\"regexpxml\":\"(GF)(2)(_)(PMS)(\\\\d+)(_)(W)([+-]?\\\\d*\\\\.\\\\d+)(?![-+0-9\\\\.])(_)(N)([+-]?\\\\d*\\\\.\\\\d+)(?![-+0-9\\\\.])(_)((?:(?:[1]{1}\\\\d{1}\\\\d{1}\\\\d{1})|(?:[2]{1}\\\\d{3}))(?:[0]?[1-9]|[1][012])(?:(?:[0-2]?\\\\d{1})|(?:[3][01]{1})))(?![\\\\d])(_)(L)(1)(A)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(\\\\d)(-)(MSS2)(\\\\.)(xml)\",\"rulestatus\":0,\"updateTime\":1524037894000,\"version\":1},\"baseDiskInfo\":{\"createTime\":1523855836000,\"diskdesc\":\"扫描磁盘\",\"diskfreesize\":68738740224,\"diskstatus\":0,\"disktotlesize\":199378333696,\"disktype\":\"0\",\"diskusesize\":130639593472,\"id\":\"1\",\"loginname\":\"LY\",\"loginpwd\":\"109413\",\"loginurl\":\"\\\\\\\\127.0.0.1\\\\zzj\\\\scan\",\"updateTime\":1524030274000,\"usagerate\":34,\"version\":5},\"baseUrl\":\"\\\\\\\\127.0.0.1\\\\zzj\\\\scan\\\\GF2_PMS2_W56.4_N51.5_20150731_L1A0000953221.tar.gz\",\"delBlackPath\":\"\",\"delBlackPathNoIP\":\"\",\"workDiskInfo\":{\"createTime\":1524642215000,\"diskdesc\":\"工作磁盘\",\"diskstatus\":0,\"disktype\":\"3\",\"id\":\"4\",\"loginname\":\"LY\",\"loginpwd\":\"109413\",\"loginurl\":\"\\\\\\\\127.0.0.1\\\\zzj\\\\work\",\"updateTime\":1524642218000,\"usagerate\":34,\"version\":1},\"workSpacePath\":\"\\\\\\\\127.0.0.1\\\\zzj\\\\work/GF2_PMS2_W56.4_N51.5_20150731_L1A0000953211\"}", "");
//        interfaceUtil("http://192.168.10.89:8080/eoffice-restful/resources/sys/oadata", "usercode=10012");
//        interfaceUtil("http://192.168.10.89:8080/eoffice-restful/resources/sys/oaholiday",
//                    "floor=first&year=2017&month=9&isLeader=N");
    }
}