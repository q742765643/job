package com.htht.job.executor.hander.webservicehandler.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.htht.util.HttpThread;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

/**
 * 2018-5-25
 * @author Administrator
 *
 */
public class ModisUtil {
	private static final String ADDRESS = "https://modwebsrv.modaps.eosdis.nasa.gov/axis2/services/MODAPSservices?wsdl";
	private static final String ACTION = "http://laads.modapsws.gsfc.nasa.gov/listProducts";
	private static final String NAMESPACE = "http://laads.modapsws.gsfc.nasa.gov/";
	private static final String GETIDSMETHOD = "searchForFiles";
	private static final String GETURLSMETHOD = "getFileUrls";
	private static final String[] IDS_PARAMS = new String[]{"product", "start", "stop", "north", "south", "west", "east", "coordsOrTiles", "dayNightBoth"};
	private static final String URL_PARAMS = "fileIds";
	private static final EndpointReference endpointReference = new EndpointReference(ADDRESS);
	private static ServiceClient SENDER = null;
	private volatile static ModisUtil modisUtil = null;
	private static OMFactory fac = OMAbstractFactory.getOMFactory();
	private static OMNamespace omNs = fac.createOMNamespace(NAMESPACE,GETURLSMETHOD);

	private ModisUtil() throws AxisFault {
		SENDER = new ServiceClient();
		Options options = new Options();
		options.setAction(ACTION);
		options.setTo(endpointReference);
		SENDER.setOptions(options);
	}
	public static ModisUtil getSingleton() throws AxisFault{

		if(null == modisUtil){
			synchronized(ModisUtil.class) {
				if(modisUtil == null) {
					modisUtil = new ModisUtil();
				}
			}
		}
		return modisUtil;
	}
	/**
	 * 获取所需URLs
	 * @param bDate	开始时间
	 * @param eDate	结束时间
	 * @return
	 */
	public List<String> getURLs(String bDate, String eDate, String dataType){
		List<String> urlList = new ArrayList<>();
		try {
			String ids = getIds(SENDER, bDate, eDate, dataType);
			urlList = getUrlList(SENDER, ids);
		} catch (AxisFault e1) {
			System.out.println("获取文件失败：bDate:"+bDate+",eDate:"+eDate+",dataType:"+dataType);
		}

		return urlList;
	}
	//根据IDS 获取文件URL列表
	private List<String> getUrlList(ServiceClient sender, String ids) throws AxisFault {
		List<String> urlList = new ArrayList<>();
		OMElement data = fac.createOMElement(GETURLSMETHOD, omNs);
		OMElement inner = fac.createOMElement(URL_PARAMS, omNs);
		inner.setText(ids);
		data.addChild(inner);
		OMElement result = sender.sendReceive(data);
		@SuppressWarnings("unchecked")
		Iterator<OMElement> iter = result.getChildElements();
		while (iter.hasNext()) {
			OMElement temp = iter.next();
			urlList.add(temp.getText());
		}
		return urlList;
	}
	/**
	 * 获取文件IDS
	 * @param sender
	 * @param bDate
	 * @param eDate
	 * @param dataType
	 * @return
	 * @throws AxisFault
	 */
	private String getIds(ServiceClient sender, String bDate, String eDate, String dataType) throws AxisFault {
		StringBuffer buffer = new StringBuffer();
		OMElement data = fac.createOMElement(GETIDSMETHOD, omNs);

		//数据类型 开始时间 结束时间 北 南 西 东 坐标 白天黑夜
		String[] vals = new String[]{dataType, bDate, eDate, "54", "3", "73", "136", "coords", "DNB"};
		for(int i =0; i < vals.length; i++){
			OMElement inner = fac.createOMElement(IDS_PARAMS[i], omNs);
			inner.setText(vals[i]);
			data.addChild(inner);
		}
		OMElement result = sender.sendReceive(data);
		@SuppressWarnings("unchecked")
		Iterator<OMElement> iter = result.getChildElements();
		while (iter.hasNext()) {
			OMElement temp = iter.next();
			buffer.append(temp.getText()).append(",");
		}
		String idsStr = buffer.toString();
		return idsStr.substring(0, idsStr.length()-1);
	}

	public Map<String, Long> getNeedUrl(List<String> urls, String downloadPath, List<String> existFiles, String fileRegex){
		Map<String, Long> needMap = new HashMap<>();
		File[] files = new File(downloadPath).listFiles();
		String urlFileName = "";
		Collections.shuffle(urls);
		for(String urlStr : urls){
			if(needMap.size()>=4){					//每次只下4景
				return needMap;
			}
			urlFileName = urlStr.substring(urlStr.lastIndexOf("/")+1);
			if(existFiles.contains(urlFileName) || !urlFileName.matches(fileRegex)){
				continue;
			}
			URL url = null;
			try {
				url = new URL(urlStr);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			long remoteSize = 0;
			try {
				remoteSize = HttpThread.getRemoteSize(url);
				if(remoteSize<=0){
					System.out.println(urlStr+"连接失败！");
					continue;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!existUrl(urlStr, files, remoteSize)){
				needMap.put(urlStr, remoteSize);
			}
		}
		return needMap;
	}
	private boolean existUrl(String url, File[] files, long remoteSize) {
		for(File file : files){
			if(file.isFile()){
				if(!file.getName().endsWith(".temp") && url.indexOf(file.getName())>=0){
					if(remoteSize == file.length()){
						return true;
					}
					file.delete();
				}
			}
		}
		return false;
	}
	public static void main(String[] args) throws IOException {
		List<String> urlList = ModisUtil.getSingleton().getURLs("2018-01-01", "2018-02-01", "MOD09A1");
		for(String url : urlList){
			System.out.println(url);
			URL temp = new URL(url);
			System.out.println(temp.getAuthority());
			System.out.println(temp.getContent());
			System.out.println(temp.getDefaultPort());
			System.out.println(temp.getFile());
			System.out.println(temp.getPath());
			System.out.println(temp.getHost());
			System.out.println(temp.getProtocol());
			System.out.println(temp.getQuery());
			System.out.println(temp.getUserInfo());
		}
	}
}
