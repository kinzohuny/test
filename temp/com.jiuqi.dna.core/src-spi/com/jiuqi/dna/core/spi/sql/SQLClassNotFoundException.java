package com.jiuqi.dna.core.spi.sql;

/**
 * 类型找不到异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLClassNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String className;

	public SQLClassNotFoundException(int line, int col, String className) {
		super(line, col, "找不到类型定义 '" + className + "'");
		this.className = className;
	}

	public SQLClassNotFoundException(String className) {
		this(0, 0, className);
	}

	/**
	 * 获取类型名称
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.CLASS_NOT_FOUND;
	}
}
