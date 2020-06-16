package com.htht.job.core.utilbean;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 请求消息
 * 
 * @author guanxl
 * 
 */
@XmlRootElement(name = "requestMessage")
public class RequestMessage {
	private List<String> pathList;//一组文件路径
	private String operation;    //操作
    private String newName;//新的文件名
    
    public static String CREATE="create";
    public static String DELETE="delete";
    public static String RENAME="rename";
	public RequestMessage() {

	}
	
	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}

	
}
