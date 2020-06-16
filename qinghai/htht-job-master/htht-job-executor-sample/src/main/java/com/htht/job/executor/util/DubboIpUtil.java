package com.htht.job.executor.util;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.htht.job.core.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * get ip
 * @author xuxueli 2016-5-22 11:38:05
 */
public class DubboIpUtil {
	private static  String SYS_TYPE = null;
	private static  String LOCAL_ADDRESS = null;

	/**
	 * get ip
	 * @return
	 */
	public static String getIp(){
		if (LOCAL_ADDRESS != null) {
			return LOCAL_ADDRESS;
		}
		Set<String> addressList=getNetworkAddress();
		String adress_use="";
        for(String address:addressList){
				adress_use=address;
				break;
		}
		if(StringUtils.isEmpty(adress_use)){
			adress_use=NetUtils.getLocalHost();
		}
		String port=PropertiesUtil.getString("cluster.dubbo.port");
		LOCAL_ADDRESS=adress_use+":"+port;

		return LOCAL_ADDRESS;
	}

	public static String getOsName(){
		if (SYS_TYPE != null) {
			return SYS_TYPE;
		}
		String sysType = System.getProperties().getProperty("os.name");
		if (sysType.toLowerCase().startsWith("win")) {
			SYS_TYPE="windows";
			return "windows";
		}else{
			SYS_TYPE="linux";
			return "linux";
		}
	}


	public static  Set<String> getNetworkAddress() {
		Set<String> set = new TreeSet<String>();
		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> addresses=ni.getInetAddresses();
				while(addresses.hasMoreElements()){
					ip = addresses.nextElement();
					if (ip.isSiteLocalAddress()&&!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1&&ip.getHostAddress().indexOf("192.168")==-1) {
						set.add(ip.getHostAddress());
					}
				}
			}
			return set;
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		String aa=getIp();
		System.out.println(aa);
	}

	}
