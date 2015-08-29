package com.jiuqi.dna.core.spi.sql;

/**
 * 数字字面量格式不正确异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLNumberFormatException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String token;

	public SQLNumberFormatException(int line, int col, String token) {
		super(line, col, "数值格式不正确 '" + token + "'");
		this.token = token;
	}

	/**
	 * 获取出错的符号内容
	 * 
	 * @return
	 */
	public String getToken() {
		return token;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.VALUE_FORMAT;
	}
}
