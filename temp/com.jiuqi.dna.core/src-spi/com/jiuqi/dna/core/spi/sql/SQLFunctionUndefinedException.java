package com.jiuqi.dna.core.spi.sql;

/**
 * ����δ�����쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLFunctionUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String funcName;

	public SQLFunctionUndefinedException(int line, int col, String name) {
		super(line, col, "�Ҳ������� '" + name + "'");
		this.funcName = name;
	}

	public SQLFunctionUndefinedException(String name) {
		this(0, 0, name);
	}

	/**
	 * ��ȡ��������
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
