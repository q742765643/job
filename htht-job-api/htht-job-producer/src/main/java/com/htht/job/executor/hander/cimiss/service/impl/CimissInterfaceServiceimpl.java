package com.htht.job.executor.hander.cimiss.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.hander.cimiss.module.ResultBean;
import com.htht.job.executor.hander.cimiss.service.CimissInterfaceService;
import com.htht.job.executor.hander.cimiss.util.CimissInterfaceAPI;
import com.htht.job.executor.hander.cimiss.util.CimissUser;
import com.htht.job.executor.hander.cimiss.util.jdbc.JdbcTemplate;
import com.htht.job.executor.hander.cimiss.util.jdbc.UUIDUtil;

/**
 * Cimiss 接口实现类
 * 
 * @author gqy
 *
 */
@Transactional
@Service("CimissInterfaceServiceimpl")
public class CimissInterfaceServiceimpl implements CimissInterfaceService {

	
	@Override
	public void getCimissData(String interfaceId, String dataCode, String times, String adminCodes, String elements,
			String dataFormat, String queryCondition,String limitCnt) throws Exception {

		CimissInterfaceAPI cimiss = new CimissInterfaceAPI(CimissUser.CIMISSIP, CimissUser.USERNAME, CimissUser.PWD);
		ResultBean resultBean = cimiss.getGridData(interfaceId, dataCode, times, adminCodes, elements, dataFormat,limitCnt);
		String tableName = "CIMISS_"+dataCode;// 表名
		String[] columns = elements.split(",");// 列名
		String[] queryConditions = queryCondition.split(",");// 查询是否存在的条件
		List<Map<String, String>> data = resultBean.getData();// 数据

		StringBuffer insertSql = new StringBuffer("insert into " + tableName + " ( id");
		StringBuffer valuesSql = new StringBuffer("values(?");
		for (String column : columns) {
			insertSql.append("," + column);
			valuesSql.append(",?");
		}
		insertSql.append(")");
		valuesSql.append(")");
		insertSql.append(valuesSql);

		StringBuffer querySql = new StringBuffer("select * from " + tableName + " where 1=1");
		for (String query : queryConditions) {
			querySql.append(" and " + query + " = ? ");
		}
		querySql.append("");

		Connection connection= JdbcTemplate.getConnection();
		try(
			PreparedStatement psQuery = connection.prepareStatement(querySql.toString());
			){
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet set = meta.getTables(null, null, tableName, null);
			if (set.next()) {
				System.out.println("此表存在");
				// 插入数据： 根据期号，站名查询记录是否存在，若不存在插入
				System.out.println("插入数据： 根据期号，站名查询记录是否存在，若不存在插入");
				PreparedStatement psInsert = connection.prepareStatement(insertSql.toString());
				try {
//					JdbcTemplate.beginTx(connection);
					List<String> queryList = Arrays.asList(queryConditions);
					int k = 0;
					for (Map<String, String> map : data) {
						String uuid = UUIDUtil.getUUID32();
						psInsert.setString(1, uuid);
						for (int i = 0; i < columns.length; i++) {
							String value = map.get(columns[i]);
							if (queryList.contains(columns[i])) {
								int index = queryList.indexOf(columns[i]);
								psQuery.setString(index + 1, value);
							}

							psInsert.setString(i + 2, value);
						}
						try(
							ResultSet rs = psQuery.executeQuery();
							){
							if (!rs.next()) {
								psInsert.addBatch();
								k++;
								if ((k + 1) % 100 == 0) {
									psInsert.executeBatch();
									psInsert.clearBatch();
								}
							}
						}
						
					}
					psInsert.executeBatch();
					psInsert.clearBatch();
//					JdbcTemplate.commit(connection);
				} catch (Exception e) {
					e.printStackTrace();
//					JdbcTemplate.rollback(connection);
				} finally {
					psInsert.close();
					psQuery.close();

				}

			} else {
				Statement statement = connection.createStatement();
				PreparedStatement psInsert = connection.prepareStatement(insertSql.toString());
				try {
					// 创建表
					System.out.println("此表不存在，创建");
					StringBuffer createSql = new StringBuffer("CREATE TABLE " + tableName + " (");
					createSql.append("id VARCHAR(64) NOT NULL PRIMARY KEY");
					for (String column : columns) {
						createSql.append(", " + column + " VARCHAR(128) ");
					}
					createSql.append(")");
					statement.execute(createSql.toString());

					// 插入数据：全部插入
					System.out.println("插入数据：全部插入");
//					JdbcTemplate.beginTx(connection);
					int k = 0;
					for (Map<String, String> map : data) {
						String uuid = UUIDUtil.getUUID32();
						psInsert.setString(1, uuid);
						for (int i = 0; i < columns.length; i++) {
							String value = map.get(columns[i]);
							psInsert.setString(i + 2, value);
						}
						psInsert.addBatch();
						k++;
						if ((k + 1) % 100 == 0) {
							psInsert.executeBatch();
							psInsert.clearBatch();
						}
					}
					psInsert.executeBatch();
					psInsert.clearBatch();
//					JdbcTemplate.commit(connection);
				} catch (Exception e) {
					e.printStackTrace();
//					JdbcTemplate.rollback(connection);
				} finally {
					statement.close();
					psInsert.close();
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally{
			connection.close();
		}
	}

}
