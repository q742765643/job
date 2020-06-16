package com.htht.job.admin.core.util;

import com.htht.job.admin.core.shiro.common.UpmsConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by liunian on 6/7/17.
 */
public class PropertiesFileUtil {
    //cache time
    private static final Integer TIME_OUT = 60 * 1000;
    //cache resource
    private static HashMap<String, PropertiesFileUtil> configMap = new HashMap<>();
    //time for open file
    private Date loadTime = null;
    //resource
    private ResourceBundle resourceBundle = null;

    //private init method creat single
    private PropertiesFileUtil(String name) {
        this.loadTime = new Date();
        this.resourceBundle = ResourceBundle.getBundle(name);
    }

    public static synchronized PropertiesFileUtil getInstance() {
        return getInstance(UpmsConstant.CONFIG);
    }

    public static synchronized PropertiesFileUtil getInstance(String name) {
        PropertiesFileUtil propertiesFileUtil = configMap.get(name);
        if (null == propertiesFileUtil) {
            propertiesFileUtil = new PropertiesFileUtil(name);
            configMap.put(name, propertiesFileUtil);
        }
        if ((new Date().getTime() - propertiesFileUtil.getLoadTime().getTime()) > TIME_OUT) {
            propertiesFileUtil = new PropertiesFileUtil(name);
            configMap.put(name, propertiesFileUtil);
        }
        return propertiesFileUtil;
    }

    public String get(String key) {
        try {
            String value = resourceBundle.getString(key);
            return value;
        } catch (MissingResourceException e) {
            return "";
        }
    }

    public Integer getInt(String key) {
        try {
            String value = resourceBundle.getString(key);
            return Integer.parseInt(value);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    public boolean getBool(String key) {
        try {
            String value = resourceBundle.getString(key);
            if ("true".equals(value)) {
                return true;
            }
            return false;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    public Date getLoadTime() {
        return loadTime;
    }
}
