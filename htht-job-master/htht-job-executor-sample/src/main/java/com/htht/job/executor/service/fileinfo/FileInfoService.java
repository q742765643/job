package com.htht.job.executor.service.fileinfo;

import com.htht.job.executor.dao.fileinfo.FileInfoDao;
import com.htht.job.executor.model.fileinfo.FileInfoDTO;
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
public class FileInfoService extends BaseService<FileInfoDTO> {
    @Autowired
    private FileInfoDao fileInfoDao;

    @Override
    public BaseDao<FileInfoDTO> getBaseDao() {
        return fileInfoDao;
    }

    public FileInfoDTO saveFileInfo(FileInfoDTO fileInfoDTO) {
        return fileInfoDao.save(fileInfoDTO);
    }

    public List<FileInfoDTO> findByWhere(String id) {
        SimpleSpecificationBuilder<FileInfoDTO> specification = new SimpleSpecificationBuilder();
        specification.add("productFileInfoId", "eq", id);
        List<FileInfoDTO> list = fileInfoDao.findAll(specification.generateSpecification());
        return list;
    }

    public void deleteByissue(String productId, String issue) {
        fileInfoDao.deleteByissue(productId, issue);
    }
}
