package com.jiuqi.dna.core.spi.sql;

/**
 * �������ظ��쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLVariableDuplicateException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String param;

	public SQLVariableDuplicateException(int line, int col, String name) {
		super(line, col, "�������ظ����� '" + name + "'");
		this.param = name;
	}

	public SQLVariableDuplicateException(String name) {
		this(0, 0, name);
	}

	/**
	 * ��ȡ��������
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
