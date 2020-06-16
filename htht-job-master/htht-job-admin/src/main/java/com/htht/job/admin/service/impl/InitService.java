package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/8/15.
 */

import com.htht.job.admin.core.shiro.common.UpmsConstant;
import com.htht.job.admin.core.util.PropertiesFileUtil;
import com.htht.job.admin.dao.XxlJobRegistryDao;
import com.htht.job.core.constant.JobConstant;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-08-15 14:57
 **/
public class InitService {
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    public void init() {
        boolean flag = this.existTable();
        if (flag) {
            return;
        }
        this.initSql();
        this.insertLogSql();

    }

    public boolean existTable() {
        List<String> list = xxlJobRegistryDao.existTable();
        if(null!=list&&!list.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void initSql() {
        String url = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get("master.job.db.url");
        String username = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get("master.job.db.user");
        String password = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get("master.job.db.password");
        String driver = PropertiesFileUtil.getInstance(JobConstant.XXL_JOB_ADMIN).get("master.job.db.driverClass");

        SQLExec sqlExec = new SQLExec();
        sqlExec.setDriver(driver);
        sqlExec.setUrl(url);
        sqlExec.setUserid(username);
        sqlExec.setPassword(password);
        sqlExec.setEncoding("UTF8");

        String file = this.getClass().getClassLoader().getResource("htht_data.sql").getFile();

        sqlExec.setSrc(new File(file));
        sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
        sqlExec.setProject(new Project()); // 要指定这个属性，不然会出错
        sqlExec.execute();
    }

    public void insertLogSql() {
        String url = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get("master.job.db.url");
        String username = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get("master.job.db.user");
        String password = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get("master.job.db.password");
        String driver = PropertiesFileUtil.getInstance(JobConstant.XXL_JOB_ADMIN).get("master.job.db.driverClass");
        SQLExec sqlExec = new SQLExec();
        sqlExec.setDriver(driver);
        sqlExec.setUrl(url);
        sqlExec.setUserid(username);
        sqlExec.setPassword(password);
        sqlExec.setEncoding("UTF8");

        String file = this.getClass().getClassLoader().getResource("htht_xxl_executesql_log.sql").getFile();

        sqlExec.setSrc(new File(file));
        sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
        sqlExec.setProject(new Project()); // 要指定这个属性，不然会出错
        sqlExec.execute();
    }
}

