package com.jiuqi.dna.core.spi.sql;

/**
 * 变量名重复异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLVariableDuplicateException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String param;

	public SQLVariableDuplicateException(int line, int col, String name) {
		super(line, col, "参数名重复定义 '" + name + "'");
		this.param = name;
	}

	public SQLVariableDuplicateException(String name) {
		this(0, 0, name);
	}

	/**
	 * 获取参数名称
	 * 
	 * @return
	 */
	public String getParamName() {
		return this.param;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VAR_DUPLICATE;
	}
}
