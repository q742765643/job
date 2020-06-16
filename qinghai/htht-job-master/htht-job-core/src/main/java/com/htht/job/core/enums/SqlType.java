package com.htht.job.core.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhd
 * 数据库类型枚举
 */
public enum SqlType {
	
	TINYINT("tinyint"),
	
	SMALLINT("smallint"),
	
	MEDIUMINT("mediumint"),
	
	INT("int"),
	
	INTEGER("integer"),
	
	BIGINT("bigint"),
	
	DOUBLE("double"),
	
	FLOAT("float"),
	
	CHAR("char"),
	
	VARCHAR("varchar"),
	
	DATE("date"),
	
	TIME("time"),
	
	TIMESTAMP("timestamp"),
	
	DATETIME("datetime"),
	
	NULL("null");
	
	private static final Map<String, SqlType> stringToEnum = new HashMap<String, SqlType>();
	
	static {
		for (SqlType staType : values()) {
			stringToEnum.put(staType.toString(), staType);
		}
	}
	
	private String sqlType;

	public String getValue() {
		return sqlType;
	}
	
	SqlType (String sqlType){
		this.sqlType = sqlType;
	}
	
	/**
	 * 字符串转换为枚举
	 * 
	 * @param symbol
	 *            枚举字符串
	 * @return 失败返回Null
	 */
	public static SqlType fromString(String symbol) {

		SqlType de = SqlType.NULL;
		if (symbol != null && !symbol.isEmpty()) {
			symbol = symbol.trim().toUpperCase();
			if (stringToEnum.containsKey(symbol)) {
				de = stringToEnum.get(symbol);
			}
		}
		return de;
	}

	
}
