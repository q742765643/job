package com.htht.job.executor.model.datacatalog;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the htht_orbit_info database table.
 * 
 */
@Entity
@Table(name = "htht_meta_catalog")
public class MetaCatalog extends BaseEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4411348395053421491L;

	private String satellite;// 卫星
	private String sensor;// 传感器
	private String code;// 编码
	private String level;

	public String getLevel()
	{
		return level;
	}

	public void setLevel(String level)
	{
		this.level = level;
	}

	public String getSatellite()
	{
		return satellite;
	}

	public void setSatellite(String satellite)
	{
		this.satellite = satellite;
	}

	public String getSensor()
	{
		return sensor;
	}

	public void setSensor(String sensor)
	{
		this.sensor = sensor;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public MetaCatalog()
	{
		super();
	}
}