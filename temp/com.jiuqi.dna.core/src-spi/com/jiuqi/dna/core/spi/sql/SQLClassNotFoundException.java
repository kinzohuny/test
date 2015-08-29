package com.jiuqi.dna.core.spi.sql;

/**
 * �����Ҳ����쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLClassNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String className;

	public SQLClassNotFoundException(int line, int col, String className) {
		super(line, col, "�Ҳ������Ͷ��� '" + className + "'");
		this.className = className;
	}

	public SQLClassNotFoundException(String className) {
		this(0, 0, className);
	}

	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.CLASS_NOT_FOUND;
	}
}
