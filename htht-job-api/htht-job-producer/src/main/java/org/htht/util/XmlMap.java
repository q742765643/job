package org.htht.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * 解释输出的XML文件 
 * @author heshibing
 * @data  2016-8-19
 */
public class XmlMap {
	public static  Map<String,String> getFileNodes(String  outpPutPathXML){
		Element element = null;
		File f = new File(outpPutPathXML);
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		Map<String,String>  map=new HashMap<String,String>();
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(f);
			element = dt.getDocumentElement();
			//System.out.println("根元素：" + element.getNodeName());
			NodeList childNodes = element.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node1 = childNodes.item(i);
				if ("file".equals(node1.getNodeName())) {
					NodeList nodeDetail = node1.getChildNodes();
					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);
						map.put(detail.getNodeName(),detail.getTextContent());
					    
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return  map;
  }
}