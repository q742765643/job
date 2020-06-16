package com.htht.job.executor.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * @author HG
 *         <p>
 *         2018年11月15日 上午9:20:09
 */
public class XmlTools {

    /**
     * 获取文档根元素
     *
     * @param path
     * @return 如果获取失败，则返回null
     */
    public static Element getRootElement(String path) {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(path));
            return document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析xml是否执行成功
     *
     * @param rootElement
     * @return 是否执行成功
     */
    public static boolean isSuccess(Element rootElement) throws Exception {
        if ("1".equals(rootElement.element("log").element("status").getText())) {
            return true;
        }
        return false;
    }
}
