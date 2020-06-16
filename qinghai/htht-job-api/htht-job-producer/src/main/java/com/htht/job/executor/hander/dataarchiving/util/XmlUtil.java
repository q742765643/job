package com.htht.job.executor.hander.dataarchiving.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * dom4j读取并解析xml
 * 
 * 
 */
public class XmlUtil {
	private Document document;

	public XmlUtil(String xmlPath) {
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read(new File(xmlPath));
		} catch (DocumentException e) {
			document = null;
		}
	}

	/**
	 * 获取名字为指定名称的第一个子元素值
	 * 
	 * @param nodeName
	 * @return
	 */
	public String getValByNode(String nodeName) {
		try {
			Element root = document.getRootElement();
			Element firstWorldElement = root.element(nodeName);
			return firstWorldElement.getTextTrim();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	
	/**
	 * 根据名称获取节点
	 *
	 * @param nodeName
	 * @return
	 */
	public Element getNodeByName(Element root, String nodeName) {
		try {
			Element elementInner = null;
			for ( Iterator iterInner = root.elementIterator(); iterInner.hasNext(); ) {   
				elementInner = (Element) iterInner.next();
				if(elementInner.getName().equals(nodeName)){
					break;
				}
			}
			return elementInner;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * 获取特定名称的子元素值
	 * 
	 * @param nodeName
	 * @return
	 */
	public List<String> getMoreValByNode(String nodeName) {
		List<String> list = new ArrayList<String>();
		Element root = document.getRootElement();
		List<Element> childList2 = root.elements(nodeName);
		for (int i = 0; i < childList2.size(); i++) {
			list.add(childList2.get(i).getText());
		}
		return list;
	}
	
	/**
	 * 获取多级节点下的值
	 * @param param root_package_package2
	 * @return
	 */
	public String getChiledVal(String param) {
		try {
			String [] params = param.split("_");
			Element root = document.getRootElement();
			String val = "";
			for (int i = 0; i < params.length; i++) {
				root = getNodeByName(root , params[i]);
				val = root.getTextTrim();
			}
			return val;
		} catch (Exception e) {
			return "";
		}
		
	}
	public static void main(String[] args) {
		XmlUtil xmlUtil = new XmlUtil("E:/09-work/Share/scan/环境星（甘肃）/HJ1A-CCD1-11-72-20160428-L20002842093/2842093/HJ1A-CCD1-11-72-20160428-L20002842093.XML");
		System.out.println(xmlUtil.getChiledVal("data_satelliteId"));
	}
}