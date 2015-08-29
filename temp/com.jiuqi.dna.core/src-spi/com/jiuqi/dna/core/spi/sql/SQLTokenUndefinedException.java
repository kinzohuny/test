package com.jiuqi.dna.core.spi.sql;

/**
 * δ��������쳣 (˵��ԴSQL�а����Ƿ���ʶ��)
 * 
 * @author niuhaifeng
 * 
 */
public class SQLTokenUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String token;

	public SQLTokenUndefinedException(int line, int col, String token) {
		super(line, col, "�޷�ʶ����� '" + token + "'");
		this.token = token;
	}

	/**
	 * ��ȡ�޷�ʶ��ķ�������
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
