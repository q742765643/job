package com.htht.job.executor.hander.predatahandler.service.impl;

import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
@Service("dataMataInfoService")
public class DataMataInfoServiceImpl implements DataMataInfoService  {
	@Autowired
	private BaseDaoUtil baseDaoUtil;

	@Override
	public List<String> findDataToProject(Map<String, Object> paramMap) {
		String satellite = (String) paramMap.get("satellite");
		String sensor = (String) paramMap.get("sensor");
		String resolution = (String) paramMap.get("resolution");
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		
		String dataLevel ="L1";
		if(StringUtils.isNotEmpty((String) paramMap.get("dataLevel"))){
			dataLevel = (String) paramMap.get("dataLevel");
		};
		StringBuffer sql = null;
		//通过排除已处理对应日期区间和分辨率的FY4A、FY3D二级数据，获取未生产FY4A、FY3D二级数据所需原始数据
		if ((("FY4A".equalsIgnoreCase(satellite) && "AGRI".equalsIgnoreCase(sensor))
				|| ("FY3D".equalsIgnoreCase(satellite) && "MERSI".equalsIgnoreCase(sensor)))
				&& StringUtils.isEmpty((String) paramMap.get("dataLevel"))) {
			String inputDataResolution = "4000";
			if ("FY3D".equalsIgnoreCase(satellite)) {
				inputDataResolution = "1000";
			}
			sql = new StringBuffer("SELECT f.f_location FROM	htht_dms_meta_img i,htht_dms_meta_info f"
					+ " WHERE	f.f_dataid = i.f_dataid "
					+ " AND i.f_satelliteid = '" + satellite + "'"
					+ " AND i.f_sensorid = '" + sensor + "' "
					+ " AND i.f_resolutionx = '" + inputDataResolution + "' "
					+ " AND i.f_producetime >= '" + startDate + "'"
					+ " AND i.f_producetime <= '" + endDate + "'"
					+ " AND i.F_LEVEL = '" + dataLevel + "'"
					+ " AND i.f_producetime NOT IN ("
					+ " SELECT i.f_producetime FROM htht_dms_meta_img i,htht_dms_meta_info f"
					+ " WHERE f.id = i.F_DATAID "
					+ " AND i.f_level = 'L2'"
					+ " AND i.F_SATELLITEID = '" + satellite + "'"
					+ " AND i.F_SENSORID = '" + sensor + "'"
					+ " AND i.F_RESOLUTIONX = '" + resolution + "')"); 

		} else {
			sql = new StringBuffer("SELECT f.f_location FROM	htht_dms_meta_img i,htht_dms_meta_info f"
					+ " WHERE	f.f_dataid = i.f_dataid "
					+ " AND i.f_satelliteid = '" + satellite + "'"
					+ " AND i.f_sensorid = '" + sensor + "' "
				    + " AND i.f_resolutionx = "+resolution
					+ " AND i.f_producetime > '" + startDate + "'"
					+ " AND i.f_producetime < '" + endDate + "'"
					+ " AND i.F_LEVEL = '" + dataLevel + "'");
			String startHourInString = (String)paramMap.get("startHour");
			String endHourInString = (String)paramMap.get("endHour");
			if(StringUtils.isNotEmpty(startHourInString)&&StringUtils.isNotEmpty(endHourInString)){
				try{
					int startHour = Integer.parseInt(startHourInString);
					int endHour = Integer.parseInt(endHourInString);
					if(startHour>=0&&startHour<=24&&endHour>=0&&endHour<=24){
						sql.append(" AND HOUR(i.f_producetime)>"+startHour+" AND HOUR(i.f_producetime)<"+endHour);
					}
				} catch(NumberFormatException ex){
					
				}
			}
			// MODIS 数据查库失败
			if (resolution.length() > 0 && !"MODIS".equals(sensor)) {
				sql.append(" AND i.f_resolutionx = " + resolution);
			} else {
				sql.append(" AND i.f_resolutionx = '" + resolution + "'");
			}
			/*
			 * if(resolution.length()>0){ sql.append(" AND i.f_resolutionx = "+ resolution
			 * ); }
			 */
			if (StringUtils.isNotEmpty((String) paramMap.get("isProjection"))) {
				sql.append(" AND (f.f_viewdatapath is NULL or f.f_viewdatapath='') ");
			}
		}
		
		sql.append(" ORDER BY	i.f_centertime");
		System.err.println(sql.toString());
		return baseDaoUtil.getByJpql(sql.toString(), null);
	}

	@Override
	public Date findDataByFileName(String fileName) {
		String sql = "SELECT i.f_producetime FROM htht_dms_meta_img i,htht_dms_meta_info f "
				+ "WHERE i.F_DATAID = f.f_dataid "
//				+ "AND i.F_LEVEL = 'L1' "
				+ "AND f.f_datasourcename = '"+fileName+"'";
		List<Date> list= baseDaoUtil.getByJpql(sql,null);
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<String> findDataToProjectNdvi(Map<String, Object> paramMap){
		String satellite = (String) paramMap.get("satellite");
		String sensor = (String) paramMap.get("sensor");
		String resolution = (String) paramMap.get("resolution");
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String dataextname = (String) paramMap.get("dataextname");
		String startHourInString = (String)paramMap.get("startHour");
		String endHourInString = (String)paramMap.get("endHour");
		 
		String dataLevel ="L1";
		boolean flag = true;
		if(StringUtils.isNotEmpty((String) paramMap.get("dataLevel"))){
			dataLevel = (String) paramMap.get("dataLevel");
			flag = false;
		};
		
		StringBuffer sql = new StringBuffer( "SELECT f.f_location FROM	htht_dms_meta_img i,htht_dms_meta_info f"
				+ " WHERE	f.f_dataid = i.f_dataid "
				+ " AND f.f_dataextname = '"+dataextname+"'"
				+ " AND i.f_satelliteid = '"+satellite+"'"
				+ " AND i.f_sensorid = '"+sensor+"' "
//				+ " AND i.f_resolutionx = "+resolution
				+ " AND i.f_producetime > '"+startDate+"'"
				+ " AND i.f_producetime < '"+endDate+"'"				
				+ " AND i.F_LEVEL = '"+dataLevel+"'");
		if(StringUtils.isNotEmpty(startHourInString)&&StringUtils.isNotEmpty(endHourInString)){
			try{
				int startHour = Integer.parseInt(startHourInString);
				int endHour = Integer.parseInt(endHourInString);
				if(startHour>=0&&startHour<=24&&endHour>=0&&endHour<=24){
					sql.append(" AND HOUR(i.f_producetime)>"+startHour+" AND HOUR(i.f_producetime)<"+endHour);
				}
			} catch(NumberFormatException ex){
				
			}
		}
		
		if(resolution.length()>0){
			sql.append(" AND i.f_resolutionx = "+ resolution );
		}
		if(flag){
			sql.append(" AND (f.f_viewdatapath is NULL or f.f_viewdatapath='') ");
		}
		sql.append(" ORDER BY	i.f_centertime");
		return baseDaoUtil.getByJpql(sql.toString(),null);
	}
	@Override
	public Date findDataByFileNameAndLevel(String fileName, String level) {
		String sql = "SELECT i.f_producetime FROM htht_dms_meta_img i,htht_dms_meta_info f "
				+ "WHERE i.F_DATAID = f.f_dataid "
				+ "AND i.F_LEVEL = '"+level+"' "
				+ "AND f.f_datasourcename = '"+fileName+"'";
		System.err.println(sql);
		List<Date> list= baseDaoUtil.getByJpql(sql,null);
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
}
