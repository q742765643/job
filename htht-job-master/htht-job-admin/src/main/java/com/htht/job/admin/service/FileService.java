package com.htht.job.admin.service;

import com.htht.job.core.utilbean.FileParam;
import com.htht.job.core.utilbean.RequestMessage;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;


public interface FileService {
    /**
     * 获取根目录
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @GET
    @Path("/getRootFolderList")
    @Produces(MediaType.APPLICATION_XML)
    public List<FileParam> getRootFolderList();

    /**
     * 根据目录列出当前目录下文件
     *
     * @param folderPath
     * @return
     */
    @POST
    @Path("/listFilesByFolder")
    @Produces(MediaType.APPLICATION_XML)
    public List<FileParam> listFilesByFolder(RequestMessage request);
}


