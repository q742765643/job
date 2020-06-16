package com.htht.job.executor.service.downupload;

import com.htht.job.executor.dao.downupload.CimissDataInfoDao;
import com.htht.job.executor.dao.downupload.CimissDownResultDao;
import com.htht.job.executor.dao.downupload.DownResultDao;
import com.htht.job.executor.model.downupload.CimissDataInfoDTO;
import com.htht.job.executor.model.downupload.CimissDownInfoDTO;
import com.htht.job.executor.model.downupload.DownResultDTO;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.search.Specifications;
import org.jeesys.common.jpa.search.Specifications.Builder;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zzj on 2018/1/15.
 */

@Transactional
@Service("downResultService")
public class DownResultService extends BaseService<DownResultDTO> {
    @Autowired
    private DownResultDao downResultDao;
    @Autowired
    private CimissDownResultDao cimissDownResultDao;
    @Autowired
    private CimissDataInfoDao cimissDataInfoDao;

    @Override
    public BaseDao<DownResultDTO> getBaseDao() {
        return downResultDao;
    }

    /*
    public List<String> findList(String startDate,String endDate,String targetFtp,String nfilepath,String targetType){
        List<String> list=new ArrayList<String>();
        SimpleSpecificationBuilder<DownResult> specification=new SimpleSpecificationBuilder();
        if(!StringUtils.isEmpty(startDate)){
            specification.add("fileDate","ge",startDate);
        }
        if(!StringUtils.isEmpty(endDate)){
            specification.add("fileDate","le",endDate);
        }
        if(!StringUtils.isEmpty(targetFtp)&&"ftp".equals(targetType)){
            specification.add("targetFtp","eq",targetType);
        }
        if(!StringUtils.isEmpty(nfilepath)){
            specification.add("nfilepath","eq",nfilepath);
        }
        List<DownResult> downResultList=this.getAll(specification.generateSpecification());
        for(DownResult downResult:downResultList){
            list.add(downResult.getYfileName()+","+downResult.getFileDate()+","+downResult.getYfilepath());
        }
        return list;
    }
    */
    public List<String> findFilesByTime(Date beginTime, Date endTime) {
        List<DownResultDTO> list = new ArrayList<DownResultDTO>();
        Builder<DownResultDTO> s = Specifications.builder();
        s.ge("dataTime", beginTime);
        s.le("dataTime", endTime);
        s.eq("zt", "1");
        list = this.getAll(s.build());
        List<String> realFileNameAndFilePath = new ArrayList<String>();
        for (DownResultDTO dr : list) {
            realFileNameAndFilePath.add(dr.getFilePath() + "#" + dr.getFileName());
        }
        return realFileNameAndFilePath;
    }

    public DownResultDTO saveDownResult(DownResultDTO downResultDTO) {
        if (!StringUtils.isEmpty(downResultDTO.getId())) {
            downResultDTO.setUpdateTime(new Date());
        } else {
            downResultDTO.setCreateTime(new Date());
        }
        downResultDTO = downResultDao.save(downResultDTO);
        return downResultDTO;
    }

    public void uodateDownResult(DownResultDTO downResultDTO) {
        if (!StringUtils.isEmpty(downResultDTO.getId())) {
            DownResultDTO downloadnew = downResultDao.findById(downResultDTO.getId());
            downloadnew.setZt(downResultDTO.getZt());
            downloadnew.setFileName(downResultDTO.getFileName());
            downloadnew.setFilePath(downResultDTO.getFilePath());
            downloadnew.setUpdateTime(downResultDTO.getUpdateTime());
            this.save(downloadnew);
        }
    }

    public List<DownResultDTO> findDownloadFiles(String fileName, String filePath, long fileSize, String zt) {
        return downResultDao.findByFileNameAndFilePathAndFileSizeAndZt(fileName, filePath, fileSize, zt);
    }
    public List<String> findH8DataToProject(String startTime, String endTime) {
    	return downResultDao.findH8DataToProject(startTime, endTime);
    }

    public CimissDownInfoDTO getCimissInfo(String id) {
        return cimissDownResultDao.findByName(id);
    }

    public List<CimissDataInfoDTO> getCimissData(String type) {
        return cimissDataInfoDao.findByType(type);
    }

    public List<DownResultDTO> findByFileName(String reg, String startTime, String endTime) {
        return downResultDao.findByFileName(reg, startTime, endTime);
    }

    public List<DownResultDTO> findFilePath(String reg, String Time) {
        return downResultDao.findFilePath(reg, Time);
    }

}
