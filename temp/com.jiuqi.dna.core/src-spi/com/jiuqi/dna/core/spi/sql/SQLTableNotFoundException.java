package com.jiuqi.dna.core.spi.sql;

/**
 * 表定义没有找到
 * 
 * @author niuhaifeng
 * 
 */
public class SQLTableNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String tableName;

	public SQLTableNotFoundException(int line, int col, String table) {
		super(line, col, "找不到表定义 '" + table + "'");
		this.tableName = table;
	}

	public String getTableName() {
		return this.tableName;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.TABLE_NOT_FOUND;
	}
}
