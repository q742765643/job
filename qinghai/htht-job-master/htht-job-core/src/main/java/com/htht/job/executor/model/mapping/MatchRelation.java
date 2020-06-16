package com.htht.job.executor.model.mapping;/**
 * Created by zzj on 2018/7/5.
 */

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @program: htht-job-api
 * @description: 匹配关系
 * @author: zzj
 * @create: 2018-07-05 10:30
 **/
@Entity
@Table(name="htht_cluster_schedule_match_relation")
public class MatchRelation extends BaseEntity{

    private String dataId;
    private int jobId;
    @Column(columnDefinition="TEXT",name = "match_data")
    private String matchData;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getMatchData() {
        return matchData;
    }

    public void setMatchData(String matchData) {
        this.matchData = matchData;
    }
}

