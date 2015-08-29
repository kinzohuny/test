package com.jiuqi.dna.core.spi.sql;

/**
 * ����û���ҵ�
 * 
 * @author niuhaifeng
 * 
 */
public class SQLTableNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String tableName;

	public SQLTableNotFoundException(int line, int col, String table) {
		super(line, col, "�Ҳ������� '" + table + "'");
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
