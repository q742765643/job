package com.htht.job.executor.dao.exceldata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.util.JdbcTemplate;


/**
 * DAO工具类,实现常用的操作
 * 
 * @author LY
 */

@Repository(value = "baseDao")
public class BaseDao {

	@PersistenceContext
	protected EntityManager em;

	@SuppressWarnings("unchecked")
	public <T> List<T> getAll(Class<T> clazz) {
		String className = clazz.getSimpleName();
		StringBuffer jpql = new StringBuffer("select o from ");
		jpql.append(className).append(" o ");
		return em.createQuery(jpql.toString()).getResultList();
	}

	public <T> T getById(Class<T> clazz, Object id) {
		return em.find(clazz, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> getByJpql(String jpql, Object... objects) {
		Session session = null;
		List list = null;
		try {
			session = (org.hibernate.Session) em.getDelegate();
			session = session.getSessionFactory().openSession();
			SQLQuery squery = session.createSQLQuery(jpql);
			if (objects != null) {
				for (int i = 0; i < objects.length; i++) {
					squery.setParameter(i, objects[i]);
				}
			}
			list = squery.list();
		} catch (Exception e) {
			if (session != null) {
				session.close();
			}
			System.err.println(e.getMessage());
			// throw new RuntimeException(e) ;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list;
	}
	
	public int selectCount(String sql) throws Exception{
		
		Connection connection = JdbcTemplate.getConnection();
		PreparedStatement psQuery = connection.prepareStatement(sql);
		
		ResultSet rs = psQuery.executeQuery();
		int count = 0;
		while(rs.next()){
			count = rs.getInt(1);
		}
		
		psQuery.close();
		return count;
		
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	public List<?> queryForObjectType(String sql, List<Object> values, Class T) {

		Query query = em.createNativeQuery(sql, T);
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				query.setParameter(i + 1, values.get(i));
			}
		}
		List<?> resultList = query.getResultList();
		
		return resultList;

	}

}