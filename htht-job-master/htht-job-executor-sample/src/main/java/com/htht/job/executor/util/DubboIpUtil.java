package com.htht.job.executor.util;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.htht.job.core.util.PropertiesUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

/**
 * get ip
 *
 * @author xuxueli 2016-5-22 11:38:05
 */
public class DubboIpUtil {
    private DubboIpUtil() {
    }

    static String sysType = null;
    private static String localAddress = null;

    /**
     * get ip
     *
     * @return
     */
    public static String getIp() {
        if (localAddress != null) {
            return localAddress;
        }
        Set<String> addressList = getNetworkAddress();
        String adressUse = "";
        for (String address : addressList) {
            adressUse = address;
            if(null!=adressUse){
                break;
            }
        }
        if (StringUtils.isEmpty(adressUse)) {
            adressUse = NetUtils.getLocalHost();
        }
        String port = PropertiesUtil.getString("cluster.dubbo.port");
        localAddress = adressUse + ":" + port;

        return localAddress;
    }

    public static String getOsName() {
        if (sysType != null) {
            return sysType;
        }
        String sysTypeLocal = System.getProperties().getProperty("os.name");
        if (sysTypeLocal.toLowerCase().startsWith("win")) {
            sysType = "windows";
            return "windows";
        } else {
            sysType = "linux";
            return "linux";
        }
    }


    public static Set<String> getNetworkAddress() {
        Set<String> set = new TreeSet<>();
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1 && ip.getHostAddress().indexOf("192.168") == -1) {
                        set.add(ip.getHostAddress());
                    }
                }
            }
            return set;
        } catch (Exception e) {
            return set;
        }
    }


}
