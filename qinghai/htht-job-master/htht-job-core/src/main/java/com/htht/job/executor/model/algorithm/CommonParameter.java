package com.htht.job.executor.model.algorithm;

import java.io.Serializable;

public class CommonParameter implements Serializable {
    /**
     * 参数
     */
    private String id;
    /**
     * 参数英文名称
     */
    private String parameterName;
    /**
     * 参数中文名称
     */
    private String parameterDesc;
    /**
     * 参数类型
     */
    private String parameterType;
    /**
     * parameterType 为select url为controller路径返回select框格式数据，或者select直接json数据格式
     */
    private String url;
    /**
     * 默认值
     */
    private String value;
    private String operate;
    /**
     * 流程cellid
     */
    private String cellId;
    /**
     * 流程节点id
     */
    private String dataID;
    /**
     * 流程节点中文名称
     */
    private String group;
    /**
     * 是否为空
     */
    private String isNull;
    /**
     * 后缀名
     */
    private String expandedname;
    /**
     *
     */
    private String dataType;
    /**
     * 弹窗类型
     */
    private String dialogType;
    /**
     * 是否显示 true false
     */
    private String display;
    /**
     * 模型标识
     * 
     */
    private String modelIdentification;

	public String getModelIdentification() {
		return modelIdentification;
	}

	public void setModelIdentification(String modelIdentification) {
		this.modelIdentification = modelIdentification;
	}

    /**
     * 流程节点id
     */
    private String uuid;

	public String getDialogType() {
		return dialogType;
	}

	public void setDialogType(String dialogType) {
		this.dialogType = dialogType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getIsNull() {
		return isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getExpandedname() {
		return expandedname;
	}

	public void setExpandedname(String expandedname) {
		this.expandedname = expandedname;
	}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterDesc() {
        return parameterDesc;
    }

    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getDataID() {
        return dataID;
    }

    public void setDataID(String dataID) {
        this.dataID = dataID;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
