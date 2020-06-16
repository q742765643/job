package com.htht.job.admin.core.jobbean;/**
 * Created by zzj on 2018/10/25.
 */

import com.htht.job.executor.model.algorithm.CommonParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-25 18:01
 **/
public class ParamClassifyBean {
    List<CommonParameter> inFile;
    List<CommonParameter> inFolder;
    List<CommonParameter> outFile;
    List<CommonParameter> paramFile;
    List<CommonParameter> fileList;
    List<CommonParameter> outFolder;
    int parallel=1;
    Map<Integer,String> flowMap;

    public List<CommonParameter> getInFile() {
        if(null==inFile){
            inFile=new ArrayList<>();
        }
        return inFile;
    }

    public void setInFile(List<CommonParameter> inFile) {
        this.inFile = inFile;
    }

    public List<CommonParameter> getInFolder() {
        if(null==inFolder){
            inFolder=new ArrayList<>();
        }
        return inFolder;
    }

    public void setInFolder(List<CommonParameter> inFolder) {
        this.inFolder = inFolder;
    }

    public List<CommonParameter> getOutFile() {
        if(null==outFile){
            outFile=new ArrayList<>();
        }
        return outFile;
    }

    public void setOutFile(List<CommonParameter> outFile) {
        this.outFile = outFile;
    }

    public List<CommonParameter> getParamFile() {
        if(null==paramFile){
            paramFile=new ArrayList<>();
        }
        return paramFile;
    }

    public void setParamFile(List<CommonParameter> paramFile) {
        this.paramFile = paramFile;
    }

    public List<CommonParameter> getFileList() {
        if(null==fileList){
            fileList=new ArrayList<>();
        }
        return fileList;
    }

    public void setFileList(List<CommonParameter> fileList) {
        this.fileList = fileList;
    }

    public List<CommonParameter> getOutFolder() {
        if(null==outFolder){
            outFolder=new ArrayList<>();
        }
        return outFolder;
    }

    public void setOutFolder(List<CommonParameter> outFolder) {
        this.outFolder = outFolder;
    }

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }

    public Map<Integer, String> getFlowMap() {
        if(null==flowMap){
            flowMap=new HashMap<>();
        }
        return flowMap;
    }

    public void setFlowMap(Map<Integer, String> flowMap) {
        this.flowMap = flowMap;
    }
}

