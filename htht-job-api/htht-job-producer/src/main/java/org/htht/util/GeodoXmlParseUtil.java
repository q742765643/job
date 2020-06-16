package org.htht.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class GeodoXmlParseUtil {
	
	@SuppressWarnings("rawtypes")
	private static List getRootNodes(String xmlContent) throws JDOMException, IOException{
		if(xmlContent == null || xmlContent.isEmpty()){
			return new ArrayList();
		}
		try{
		 String tempXml = xmlContent.replaceAll("<!--.*-->", "");//删除注释内容
		 xmlContent = tempXml;
		}catch(Exception ex){
		}
		StringReader sr = new StringReader(xmlContent);
		System.out.println(sr);
		InputSource source = new InputSource(sr);
		SAXBuilder saxb = new SAXBuilder();
		Document doc = saxb.build(source);
		Element root = doc.getRootElement();
		List nodeChildren = root.getChildren();
		return nodeChildren;
	}
	public static String getOutputXMLContent(String xmlPath) throws IOException {
		String fcontent = FileUtils.readFileToString(new File(xmlPath),
				"UTF-8");
		return fcontent;
	}
	@SuppressWarnings("rawtypes")
	public static List<XMLElement> getXMLElementsFromXMLContent(String xmlPath,String name) throws JDOMException, IOException{
		String xmlCotent = getOutputXMLContent(xmlPath);
		List rootNodes = getRootNodes(xmlCotent);
		Element et = null;
		List<XMLElement> list = new ArrayList<XMLElement>();
		
		for (int i = 0; i < rootNodes.size(); i++) {
			et = (Element) rootNodes.get(i);// 循环依次得到子元素
			String ename = et.getName();
			if(ename.equals(name)){
				List subLogNode = et.getChildren(); // 得到内层子节点
				for(int j = 0; j < subLogNode.size(); j++){
					XMLElement xe = new XMLElement();
					xe.setElementName(((Element)subLogNode.get(j)).getName());
					xe.setElementVlaue(((Element)subLogNode.get(j)).getValue().toString());
					list.add(xe);
				}
			}  
		}
		return list;
	}  
}
