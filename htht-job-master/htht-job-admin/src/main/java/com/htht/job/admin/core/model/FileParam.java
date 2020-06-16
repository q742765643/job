package com.htht.job.admin.core.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fileParam")
public class FileParam {
    private String idAbsolutePath;
    private String name;
    private String pId;
    private String path;
    private boolean isFile;
    private boolean isParent;
    private int dataSource;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDataSource() {
        return dataSource;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }

    public String getIdAbsolutePath() {
        return idAbsolutePath;
    }

    public void setIdAbsolutePath(String idAbsolutePath) {
        this.idAbsolutePath = idAbsolutePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean isParent) {
        this.isParent = isParent;
    }
}
