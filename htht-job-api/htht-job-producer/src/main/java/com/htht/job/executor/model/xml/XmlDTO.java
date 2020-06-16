package com.htht.job.executor.model.xml;

/**
 * @program: htht-job-api
 * @description: xml格式
 * @author: dingjiancheng
 * @create: 2018-09-28 12:47
 */
public class XmlDTO {

    private String identify;        //标识

    private String type;        //数据类型

    private String description;     //描述

    private String value;       //值
    
    public XmlDTO(String identify, String type, String description, String value) {
		super();
		this.identify = identify;
		this.type = type;
		this.description = description;
		this.value = value;
	}
    public static XmlDTO newStringXmlDTO(String identify, String description, String value) {
    	XmlDTO dto = new XmlDTO();
    	dto.identify = identify;
    	dto.type = "String";
    	dto.description = description;
    	dto.value = value;
    	return dto;
    }

	public XmlDTO() {
		super();
	}


	public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
