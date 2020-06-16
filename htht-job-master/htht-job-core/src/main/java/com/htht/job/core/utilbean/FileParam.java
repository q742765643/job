package com.htht.job.core.utilbean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 文件属性
 *
 * @author guanxl
 */
@XmlRootElement(name = "fileParam")
public class FileParam {
    private String name;
    private String showPath;//显示目录
    private String rootPath;//真实根目录
    private String path;//真实目录
    private String lastUpdateDate;//最后修改日期
    private boolean isFile;//是否为普通文件
    private long size;//文件大小，文件夹忽略该属性
    private int dataSource;//来源，共享、私有或第三方；共享目录/文件不能删除

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getShowPath() {
        return showPath;
    }

    public void setShowPath(String showPath) {
        this.showPath = showPath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDataSource() {
        return dataSource;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }


}
