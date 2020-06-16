package com.htht.job.admin.service;

import com.htht.job.admin.core.model.FileParam;

import java.util.List;

public interface FileResourceManager {
    /**
     * 创建目录
     *
     * @param parent  父目录
     * @param newName 目录名称
     * @return
     */
    public FileParam createNewDirectory(String parent, Integer dataSource);

    /**
     * 创建目录
     *
     * @param parent     上级目录
     * @param newDirName 新目录名称
     * @return
     */
    public FileParam createNewDirectory(String parent, String newDirName, Integer dataSource);

    /**
     * 重命名目录
     *
     * @param parent  父目录
     * @param newName 目录名称
     * @return
     */
    public FileParam renameDirectory(String oldPath, String newName, Integer dataSource);

    /**
     * 删除目录或文件
     *
     * @param path
     * @return
     */
    public boolean deleteFile(String path, Integer dataSource);

    /**
     * 获取根目录列表
     *
     * @return
     */
    public List<FileParam> getRootFiles();

    /**
     * 获取子目录列表
     *
     * @param pid
     * @return
     */
    public List<FileParam> getChildrenFiles(FileParam param);

    public List<FileParam> getShareRootFiles();

    /**
     * 获取pie创建工程文件 pan子目录
     *
     * @param param
     * @return
     */
    public List<FileParam> getChildrenFiles2(FileParam param);

    /**
     * 获取pie创建工程文件 mss 子目录
     *
     * @param param
     * @return
     */
    public List<FileParam> getChildrenFiles3(FileParam param);

    /**
     * 获取pie创建工程文件 ref 子目录
     *
     * @param param
     * @return
     */
    public List<FileParam> getChildrenFiles4(FileParam param);
}
