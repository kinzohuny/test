package com.jiuqi.dna.core.spi.sql;

/**
 * 使用了没有定义的变量名
 * 
 * @author niuhaifeng
 * 
 */
public class SQLVariableUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String param;

	public SQLVariableUndefinedException(int line, int col, String name) {
		super(line, col, "参数未定义 '" + name + "'");
		this.param = name;
	}

	/**
	 * 获取参数名称
	 * 
	 * @return
	 */
	public String getParamName() {
		return param;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VAR_UNDEFINED;
	}
}
