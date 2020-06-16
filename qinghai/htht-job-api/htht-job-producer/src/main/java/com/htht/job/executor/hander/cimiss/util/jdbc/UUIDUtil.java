package com.htht.job.executor.hander.cimiss.util.jdbc;

import java.util.UUID;

public class UUIDUtil {
	public static String getUUID32(){
	    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
	    long nowTime = System.currentTimeMillis();
	    return nowTime+uuid;
	}
}
