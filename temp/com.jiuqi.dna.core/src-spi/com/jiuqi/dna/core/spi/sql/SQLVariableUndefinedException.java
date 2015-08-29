package com.jiuqi.dna.core.spi.sql;

/**
 * ʹ����û�ж���ı�����
 * 
 * @author niuhaifeng
 * 
 */
public class SQLVariableUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String param;

	public SQLVariableUndefinedException(int line, int col, String name) {
		super(line, col, "����δ���� '" + name + "'");
		this.param = name;
	}

	/**
	 * ��ȡ��������
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
