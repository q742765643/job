package com.htht.job.executor.service.fileinfo;

import com.htht.job.executor.dao.fileinfo.FileInfoDao;
import com.htht.job.executor.model.fileinfo.FileInfo;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zzj on 2018/2/5.
 */
@Transactional
@Service("fileInfoService")
public class FileInfoService extends BaseService<FileInfo> {
    @Autowired
    private FileInfoDao fileInfoDao;
    @Override
    public BaseDao<FileInfo> getBaseDao() {
        return fileInfoDao;
    }

    public FileInfo saveFileInfo(FileInfo fileInfo){
       return fileInfoDao.save(fileInfo);
    }

    public List<FileInfo> findByWhere(String id){
        SimpleSpecificationBuilder<FileInfo> specification=new SimpleSpecificationBuilder();
        specification.add("productFileInfoId","eq",id);
        List<FileInfo> list=fileInfoDao.findAll(specification.generateSpecification());
        return list;
    }
    public void deleteByissue(String productId,String issue){
        fileInfoDao.deleteByissue(productId,issue);
    }
}
