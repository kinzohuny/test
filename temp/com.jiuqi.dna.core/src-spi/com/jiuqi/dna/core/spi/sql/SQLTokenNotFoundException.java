package com.jiuqi.dna.core.spi.sql;

/**
 * 符号未找到
 * 
 * @author niuhaifeng
 * 
 */
public class SQLTokenNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String token;

	public SQLTokenNotFoundException(int line, int col, String token) {
		super(line, col, "缺少符号 '" + token + "'");
		this.token = token;
	}

	public SQLTokenNotFoundException(String token) {
		this(0, 0, token);
	}

	/**
	 * 获取缺少的符号内容或名称
	 * 
	 * @return
	 */
	public String getToken() {
		return token;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.TOKEN_NOT_FOUND;
	}
}
