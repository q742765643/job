package org.htht.util;

/**
 * xml元数据实体对象，本对象实现xml元数据读取
 * @author XieQiang
 *
 */
public class XMLElement {
	/**
	 * 节点名称
	 */
	private String elementName;
	/*
	 * 节点值
	 */
	private String elementVlaue;
	public XMLElement(){
		
	}
	public XMLElement(String elementName,String elementVlaue){
		this.elementName=elementName;
		this.elementVlaue=elementVlaue;
	}
	public String getElementName() {
		return elementName;
	}
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	public String getElementVlaue() {
		return elementVlaue;
	}
	public void setElementVlaue(String elementVlaue) {
		this.elementVlaue = elementVlaue;
	}

}
