package com.jiuqi.dna.core.spi.sql;

/**
 * ������������ʽ����ȷ�쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLNumberFormatException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String token;

	public SQLNumberFormatException(int line, int col, String token) {
		super(line, col, "��ֵ��ʽ����ȷ '" + token + "'");
		this.token = token;
	}

	/**
	 * ��ȡ����ķ�������
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
