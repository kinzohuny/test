package com.jiuqi.dna.core.spi.sql;

/**
 * 别名重复异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLAliasDuplicateException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String alias;

	public SQLAliasDuplicateException(int line, int col, String alias) {
		super(line, col, "别名重复定义 '" + alias + "'");
		this.alias = alias;
	}

	public SQLAliasDuplicateException(String alias) {
		this(0, 0, alias);
	}

	/**
	 * 获取重复的别名
	 * 
	 * @return
	 */
	public String getAlias() {
		return this.alias;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ALIAS_DUPLICATE;
	}
}
