package com.htht.job.executor.model.uus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author mao_r
 * @ClassName: RegionInfo
 * @Description: 行政区域
 * @date 2018年10月26日
 */

@Entity
@Table(name = "htht_uus_region_info")
public class RegionInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "regionid")
    private String regionId;    //区域ID

    @Column(name = "t_m_regionid")
    private String parentRegionId;        //上一级区域ID

    @Column(name = "regionlevel")
    private Integer regionLevel;        //区域级别（省市县）

    @Column(name = "fullname")
    private String areaName;            //全名

    @Column(name = "districtname")
    private String districtName;        //区域名称

    @Transient
    private List<RegionInfo> subRegion;

    public RegionInfo() {
        super();
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getParentRegionId() {
        return parentRegionId;
    }

    public void setParentRegionId(String parentRegionId) {
        this.parentRegionId = parentRegionId;
    }

    public Integer getRegionLevel() {
        return regionLevel;
    }

    public void setRegionLevel(Integer regionLevel) {
        this.regionLevel = regionLevel;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public List<RegionInfo> getSubRegion() {
        return subRegion;
    }

    public void setSubRegion(List<RegionInfo> subRegion) {
        this.subRegion = subRegion;
    }

}
