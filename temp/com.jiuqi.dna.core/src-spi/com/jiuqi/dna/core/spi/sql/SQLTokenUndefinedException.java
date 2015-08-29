package com.jiuqi.dna.core.spi.sql;

/**
 * 未定义符号异常 (说明源SQL中包含非法标识符)
 * 
 * @author niuhaifeng
 * 
 */
public class SQLTokenUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String token;

	public SQLTokenUndefinedException(int line, int col, String token) {
		super(line, col, "无法识别符号 '" + token + "'");
		this.token = token;
	}

	/**
	 * 获取无法识别的符号内容
	 * 
	 * @return
	 */
	public String getToken() {
		return token;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.TOKEN_UNDEFINED;
	}
}
