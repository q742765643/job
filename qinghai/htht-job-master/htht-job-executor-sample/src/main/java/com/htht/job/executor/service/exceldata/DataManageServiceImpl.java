package com.htht.job.executor.service.exceldata;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.executor.dao.exceldata.BaseDao;
import com.htht.job.executor.model.exceldata.ExcelDataPageInfo;
import com.htht.job.executor.model.exceldata.ExcelElementInfo;

@Service("dataManageService")
public class DataManageServiceImpl implements DataManageService {

	@Autowired
	private BaseDao baseDao;
	
	@SuppressWarnings({ "finally", "rawtypes" })
	@Override
	public ExcelDataPageInfo findData(String shortName, int pageNum, int pageSize,String[] params) {

		ExcelDataPageInfo dataInfo = new ExcelDataPageInfo();
		ExcelElementInfo elementInfo = baseDao.getById(ExcelElementInfo.class, shortName);
		if (null == elementInfo) {
			System.err.println(" can't find table by " + shortName);
			return null;
		}
		
		int startNum = -1;
		if (pageSize != -1 && pageNum != -1) {
			startNum = (pageNum - 1) * pageSize;
		}

		String tableName = elementInfo.getTableName();
		StringBuffer sql = new StringBuffer("select * from " + tableName);
		StringBuffer countSql = new StringBuffer("select count(*) from " + tableName);
		

		if (null != params && 0 != params.length) {
			sql.append(" where 1=1 ");
			countSql.append(" where 1=1 ");
			for (String param : params) {
				String[] split = param.split(":");
				String andStr  = "";
				if (1 == split.length ||"-1".equals(split[1])) {
					andStr ="";
				}else {
					andStr =" and " + split[0] + " like '%" +split[1] +"%'";
				}
				
				sql.append(andStr);
				countSql.append(andStr);
			}
		}
		if (startNum != -1 ) {
			sql.append(" limit " + startNum + "," + pageSize);
		}
		
		

		String entityName = elementInfo.getClassName();
		List<?> dataList = null;
		int totalNum = 0;
		try {
			Class clazz=Class.forName(entityName);
			dataList = baseDao.queryForObjectType(sql.toString(), null, clazz);
			totalNum = baseDao.selectCount(countSql.toString());
			dataInfo.setRecordsTotal(totalNum);
			dataInfo.setRecordsFiltered(totalNum);
			dataInfo.setDataList(dataList);
			dataInfo.setExcelName(elementInfo.getExcelTitle());
		}catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			return dataInfo;
		}

	}

	@Override
	public List<ExcelElementInfo> findAllElement() {
		
		List<ExcelElementInfo> excelElementInfos = baseDao.getAll(ExcelElementInfo.class);
		return excelElementInfos;
	}

	@Override
	public List<String> findStationByName(String name) {
		
		//根据主键ID查询 要素表相关信息
		ExcelElementInfo element = baseDao.getById(ExcelElementInfo.class, name);
		List<String> areaStationList  = new ArrayList<>();
		String sql = new String("select distinct(area_station) from " + element.getTableName() + " order by area_station asc");
		areaStationList = baseDao.getByJpql(sql);
		
		return areaStationList;
		
	}

}
