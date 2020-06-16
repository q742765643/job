package com.htht.job.executor.service.ftp;

import com.htht.job.executor.dao.ftp.FtpDao;
import com.htht.job.executor.model.ftp.FtpDTO;
import com.htht.job.executor.util.ApacheFtpUtil;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zzj on 2018/1/16.
 */
@Transactional
@Service("ftpService")
public class FtpService extends BaseService<FtpDTO> {
    @Autowired
    private FtpDao ftpDao;

    @Override
    public BaseDao<FtpDTO> getBaseDao() {
        return ftpDao;
    }

    public List<FtpDTO> findAll() {
        return this.getAll();
    }

    /**
     * 按照id查询数据
     */
    public FtpDTO getById(String id) {
        return ftpDao.findOne(id);
    }

    /**
     * 修改FTP实体信息
     *
     * @param ftpDTO 实体对象
     * @return 返回1 修改成功 0 修改失败
     * @author miaowei 2018-01-24
     */
    public int updeat(FtpDTO ftpDTO) {
        int num = 1;
        try {
            ftpDao.save(ftpDTO);
        } catch (Exception e) {
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

    /**
     * 删除FTP实体信息
     *
     * @param id 需要删除的实体主键
     * @return 1 删除成功 0删除失败
     * @author miaowei 2018-01-24
     */
    public int del(String id) {
        int num = 1;
        try {
            ftpDao.delete(id);
        } catch (Exception e) {
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

    public Page<FtpDTO> getFtpsByPage(Pageable pageable) {
        return ftpDao.findAll(pageable);
    }

    public boolean testConnectFtp(String ip, int port, String userName, String pwd) {
        ApacheFtpUtil af = new ApacheFtpUtil(ip, port, userName, pwd);
        return af.connectServer();
    }

}
