package com.htht.job.executor.util.xmlUtil;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * param：参数
 * filepath：生成文件路径
 */
public class InputXmlUtil {

	public static void toInputXml(String filePath, Map<String, Object> param) {
		File file = new File(filePath);

		// 创建DocumentBuilderFactory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			// 创建DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 创建Document
			Document document = builder.newDocument();
			// 设置XML声明中standalone为yes，即没有dtd和schema作为该XML的说明文档，且不显示该属性
			// document.setXmlStandalone(true);
			// 创建根节点
			Element xml = document.createElement("XML");
			// 循环创建子节点
			Set<Entry<String, Object>> entrys = param.entrySet();
			for (Entry<String, Object> entry : entrys) {
				String key = entry.getKey();
				String value = (String) entry.getValue();
				// 创建子节点，并设置属性
				Element element = document.createElement(key);
				element.setTextContent(value);
				xml.appendChild(element);

			}
			// 将根节点添加到Document下
			document.appendChild(xml);

			/*
			 * 下面开始实现： 生成XML文件
			 */
			// 创建TransformerFactory对象
			TransformerFactory tff = TransformerFactory.newInstance();
			// 创建Transformer对象
			Transformer tf = tff.newTransformer();
			// 设置输出数据时换行
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			// 使用Transformer的transform()方法将DOM树转换成XML
			tf.transform(new DOMSource(document), new StreamResult(file));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
