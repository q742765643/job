package com.htht.job.executor.service.downupload;

import com.htht.job.executor.dao.downupload.CimissDataInfoDao;
import com.htht.job.executor.dao.downupload.CimissDownResultDao;
import com.htht.job.executor.dao.downupload.DownResultDao;
import com.htht.job.executor.model.downupload.CimissDataInfo;
import com.htht.job.executor.model.downupload.CimissDownInfo;
import com.htht.job.executor.model.downupload.DownResult;
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
public class DownResultService extends BaseService<DownResult>{
    @Autowired
    private DownResultDao downResultDao;
    @Autowired
    private CimissDownResultDao cimissDownResultDao;
    @Autowired
    private CimissDataInfoDao cimissDataInfoDao;
    @Override
    public BaseDao<DownResult> getBaseDao() {
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
    public List<String> findFilesByTime(Date beginTime,Date endTime){
    	List<DownResult> list = new ArrayList<DownResult>();
    	 Builder<DownResult> s=Specifications.builder();
    	 s.ge("dataTime", beginTime);
    	 s.le("dataTime", endTime);
    	 s.eq("zt", "1");
/*    	 SimpleSpecificationBuilder<DownResult> specification=new SimpleSpecificationBuilder();
         specification.add("fileTime","gt",beginTime);
         specification.add("fileTime","lt",endTime);*/
         list=this.getAll(s.build());
         List<String> realFileNameAndFilePath = new ArrayList<String>();
         for(DownResult dr:list){
        	 realFileNameAndFilePath.add(dr.getFilePath()+"#"+dr.getFileName());
         }
    	return realFileNameAndFilePath;
    }
    
    public DownResult saveDownResult(DownResult downResult){
        if(!StringUtils.isEmpty(downResult.getId())){
            downResult.setUpdateTime(new Date());
        }else{
            downResult.setCreateTime(new Date());
        }
        downResult=downResultDao.save(downResult);
        return downResult;
    }
    public void uodateDownResult(DownResult downResult){
        if(!StringUtils.isEmpty(downResult.getId())){
            DownResult downloadnew = downResultDao.findById(downResult.getId());
            downloadnew.setZt(downResult.getZt());
            downloadnew.setFileName(downResult.getFileName());
            downloadnew.setFilePath(downResult.getFilePath());
            downloadnew.setUpdateTime(downResult.getUpdateTime());
            this.save(downloadnew);
        }
//        downResultDao.update(downResult.getId(),downResult.getFilePath(),downResult.getFileName());
    }

    public List<DownResult> findDownloadFiles(String fileName, String filePath, long fileSize, String zt){
        return downResultDao.findByFileNameAndFilePathAndFileSizeAndZt(fileName, filePath, fileSize, zt);
    }
    public CimissDownInfo getCimissInfo(String id){
        return cimissDownResultDao.findByName(id);
    }
    public List<CimissDataInfo> getCimissData(String type){
        return cimissDataInfoDao.findByType(type);
    }

    public List<DownResult> findByFileName(String reg,String startTime,String endTime){
        return downResultDao.findByFileName(reg,startTime,endTime);
    }

    public List<DownResult> findFilePath(String reg,String Time){
        return downResultDao.findFilePath(reg,Time);
    }
    /**
     * 
     * @param format 文件的format信息，一般为后缀
     * @param regs	文件名中的某些字段
     * @param startTime	开始时间
     * @param endTime 结束时间
     * @return
     */
    public List<String> findFileByRegs(String format,String regs,String startTime,String endTime){
    	if(StringUtils.isNotEmpty(regs)){
    		if(regs.indexOf(",") > -1){
    			String[] rs = regs.split(",");
    			return downResultDao.findFileByFileNameAndTime(format, rs[0], rs[1], startTime, endTime);
    		}else{
    			return downResultDao.findFileByFileNameAndTime(format, regs, startTime, endTime);
    		}
    	}
        return new ArrayList<String>();
    }
}
