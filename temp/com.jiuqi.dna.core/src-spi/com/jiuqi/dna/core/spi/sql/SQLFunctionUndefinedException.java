package com.jiuqi.dna.core.spi.sql;

/**
 * 函数未定义异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLFunctionUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String funcName;

	public SQLFunctionUndefinedException(int line, int col, String name) {
		super(line, col, "找不到函数 '" + name + "'");
		this.funcName = name;
	}

	public SQLFunctionUndefinedException(String name) {
		this(0, 0, name);
	}

	/**
	 * 获取函数名称
	 * 
	 * @return
	 */
	public String getFuncName() {
		return funcName;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.FUNC_UNDEFINED;
	}
}
