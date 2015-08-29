package com.jiuqi.dna.core.spi.sql;

/**
 * ����δ�ҵ�
 * 
 * @author niuhaifeng
 * 
 */
public class SQLTokenNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String token;

	public SQLTokenNotFoundException(int line, int col, String token) {
		super(line, col, "ȱ�ٷ��� '" + token + "'");
		this.token = token;
	}

	public SQLTokenNotFoundException(String token) {
		this(0, 0, token);
	}

	/**
	 * ��ȡȱ�ٵķ������ݻ�����
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
