package com.jiuqi.dna.core.spi.sql;

/**
 * 别名为定义异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLAliasUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String alias;

	public SQLAliasUndefinedException(int line, int col, String alias) {
		super(line, col, "别名未定义 '" + alias + "'");
		this.alias = alias;
	}

	/**
	 * 获取未定义的别名
	 * 
	 * @return
	 */
	public String getAlias() {
		return this.alias;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ALIAS_UNDEFINED;
	}
}
