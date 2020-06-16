package com.htht.job.admin.dao;

import com.htht.job.admin.core.model.XxlJobBadNode;
import org.apache.ibatis.annotations.Param;

public interface XxlJobBadNodeDao {
    int save(@Param("badNodeIp") String badNodeIp);

    int remove(@Param("badNodeIp") String badNodeIp);

    XxlJobBadNode get(@Param("badNodeIp") String badNodeIp);
}
