package com.htht.job.executor.model.productinfo;

import com.htht.job.core.enums.SqlType;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhd
 *         产品统计入库辅助类
 */
public class ProductAnalysisTableInfo {


    //表名
    private String tableName;

    //字段列表
    private List<Field> fields;

    public ProductAnalysisTableInfo(String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            throw new RuntimeException("sqlException:表明不能为空!");
        }
        this.tableName = tableName.replace("'", "").trim();
        this.fields = new ArrayList<Field>();
    }


    public Field addFieldAndValue(String fieldName, String value) {
        return addFieldAndValue(fieldName, false, value);
    }

    public Field addFieldAndValue(String fieldName, boolean isJoinKey, String value) {
        return addFieldAndValue("varchar", 60, fieldName, value);
    }

    public Field addFieldAndValue(String fieldType, int fieldLength, String fieldName, String value) {
        return addFieldAndValue(fieldType, fieldLength, fieldName, false, value);
    }

    public Field addFieldAndValue(String fieldType, int fieldLength, String fieldName, boolean isJoinKey, String value) {
        Field fd = null;
        for (Field field : fields) {
            if (field.fieldName.equalsIgnoreCase(fieldName)) {
                fd = field;
                break;
            }
        }
        if (fd == null) {
            fd = new Field(fieldType, fieldLength, fieldName);
            fields.add(fd);
        }
        fd.setValue(value).setJoinKey(isJoinKey);
        return fd;
    }


    public Field addFieldAndValues(String fieldName, List<String> values) {

        return addFieldAndValues(fieldName, false, values);
    }

    public Field addFieldAndValues(String fieldName, boolean isJoinKey, List<String> values) {
        return addFieldAndValues("varchar", 60, fieldName, isJoinKey, values);
    }

    public Field addFieldAndValues(String fieldType, int fieldLength, String fieldName, List<String> values) {
        return addFieldAndValues(fieldType, fieldLength, fieldName, false, values);
    }

    public Field addFieldAndValues(String fieldType, int fieldLength, String fieldName, boolean isJoinKey, List<String> values) {
        Field field = null;
        if (values != null && values.size() > 0) {
            for (String value : values) {
                field = addFieldAndValue(fieldType, fieldLength, fieldName, isJoinKey, value);
            }
        }
        return field;
    }


    //生成新建表sql
    public String generateCreateTableSql() {
        StringBuilder createTableSql = new StringBuilder();
        createTableSql.append("CREATE TABLE IF NOT EXISTS " + tableName
                + " (\n");

        boolean isHasJoinKey = false;
        StringBuilder joinKeySql = new StringBuilder("PRIMARY KEY(");
        for (int i = 0; i < fields.size(); i++) {
            createTableSql.append(fields.get(i).fieldName + " " + fields.get(i).fieldType + (fields.get(i).fieldLength > 0 ? "(" + fields.get(i).fieldLength + ")" : ""));
            if (i != fields.size() - 1) {
                createTableSql.append(",\n");
            }
            if (fields.get(i).isJoinKey) {
                isHasJoinKey = true;
                joinKeySql.append(fields.get(i).fieldName + ",");
            }
        }

        if (isHasJoinKey) {
            createTableSql.append(",\n");
            createTableSql.append(joinKeySql.substring(0, joinKeySql.lastIndexOf(",")) + ")");
        }
        createTableSql.append(")");
        return createTableSql.toString();
    }

    public String generateReplaceDataSql() {
        StringBuilder replaceDataSql = new StringBuilder();
        replaceDataSql.append("replace into " + tableName + "(");
        for (int i = 0; i < fields.size(); i++) {
            replaceDataSql.append(fields.get(i).fieldName.replace("'", "").trim());
            if (i != fields.size() - 1) {
                replaceDataSql.append(",");
            }
        }
        replaceDataSql.append(") values ");
        int minFieldValueNums = Integer.MAX_VALUE;
        for (Field field : fields) {
            if (field.fieldValue.size() < minFieldValueNums) {
                minFieldValueNums = field.fieldValue.size();
            }
        }
        for (int j = 0; j < minFieldValueNums; j++) {
            replaceDataSql.append("(");
            for (int f = 0; f < fields.size(); f++) {
                replaceDataSql.append(fields.get(f).getSqlValue(j));
                if (f != fields.size() - 1) {
                    replaceDataSql.append(",");
                }
            }
            if (j != minFieldValueNums - 1) {
                replaceDataSql.append("),");
            } else {
                replaceDataSql.append(")");
            }

        }
        return replaceDataSql.toString();
    }


    //字段
    private class Field {
        private final String NULL_VALUE = "NULL";
        //字段类型
        private SqlType fieldType;
        //字段长度
        private int fieldLength;
        //字段名称
        private String fieldName;
        //字段值
        private List<String> fieldValue;
        //是否主键
        private boolean isJoinKey = false;

        public Field(String fieldType, int fieldLength, String fieldName) {
            if (StringUtils.isEmpty(fieldName)) {
                throw new RuntimeException("sqlException:字段名称不能为空!");
            }
            if (StringUtils.isEmpty(fieldType)) {
                throw new RuntimeException("sqlException:" + fieldName + "字段类型不能为空!");
            }

            SqlType sqlFieldTypeEnum = SqlType.fromString(fieldType);

            if (sqlFieldTypeEnum == SqlType.NULL) {
                throw new RuntimeException("sqlException:暂不支持该字段类型!");
            }

            this.fieldType = sqlFieldTypeEnum;
            this.fieldLength = fieldLength;
            this.fieldName = fieldName;
            fieldValue = new ArrayList<String>();
        }

        private Field setValue(String value) {
            if (!StringUtils.isEmpty(value)) {
                fieldValue.add(value);
            } else {
                fieldValue.add(NULL_VALUE);
            }
            return this;
        }

        private Field setJoinKey(boolean isJoinKey) {
            this.isJoinKey = isJoinKey;
            return this;
        }

        private String getSqlValue(int i) {
            String value = fieldValue.get(i).replace("'", "").trim();
            if (value.equals(NULL_VALUE)) {
                return NULL_VALUE;
            }
            switch (fieldType) {
                case CHAR:
                case VARCHAR:
                    return "'" + value + "'";
                case DATE:
                case TIME:
                case TIMESTAMP:
                case DATETIME:
                    return "'" + value + "'";
                case TINYINT:
                case SMALLINT:
                case MEDIUMINT:
                case INT:
                case INTEGER:
                case BIGINT:
                case DOUBLE:
                case FLOAT:
                    return value;
                default:
                    return value;
            }
        }

    }
}
