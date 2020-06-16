package com.htht.job.executor.hander.cimiss.util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class JdbcTemplate {
	private static String DRIVER_CLASS_NAME = null;
	private static String URL = null;
	private static String USERNAME = null;
	private static String PASSWORD = null;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		DRIVER_CLASS_NAME = bundle.getString("spring.datasource.driver");
		URL = bundle.getString("spring.datasource.url");
		USERNAME = bundle.getString("spring.datasource.username");
		PASSWORD = bundle.getString("spring.datasource.password");
	}

	/*
	 * 获取数据库连接
	 */
	public static Connection getConnection() throws Exception {
		Class.forName(DRIVER_CLASS_NAME);
		Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		return connection;
	}

	/*
	 * 提交事务
	 */
	public static void commit(Connection connection) {
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 开启事务
	 */
	public static void beginTx(Connection connection) {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 回滚
	 */
	public static void rollback(Connection connection) {
		try {
			connection.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void releaseDb(Statement statement, Connection connection) {
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
