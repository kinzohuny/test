package com.jiuqi.dna.core.spi.sql;

/**
 * 名称重复定义异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLNameRedefineException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String name;

	public SQLNameRedefineException(int line, int col, String name) {
		super(line, col, "名称重复定义 '" + name + "'");
		this.name = name;
	}

	public SQLNameRedefineException(String name) {
		this(0, 0, name);
	}

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.NAMED_REDEFINED;
	}
}
