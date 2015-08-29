package com.jiuqi.dna.core.spi.sql;

/**
 * 找不指定的列异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLColumnNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String columnName;

	public SQLColumnNotFoundException(int line, int col, String column) {
		super(line, col, "找不到列 '" + column + "'");
		this.columnName = column;
	}

	public SQLColumnNotFoundException(String column) {
		this(0, 0, column);
	}

	/**
	 * 获取列名称
	 * 
	 * @return
	 */
	public String getColumnName() {
		return columnName;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.COLUMN_NOT_FOUND;
	}
}
