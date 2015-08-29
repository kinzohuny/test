package com.jiuqi.dna.core.spi.sql;

/**
 * �����ظ������쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLNameRedefineException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String name;

	public SQLNameRedefineException(int line, int col, String name) {
		super(line, col, "�����ظ����� '" + name + "'");
		this.name = name;
	}

	public SQLNameRedefineException(String name) {
		this(0, 0, name);
	}

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.NAMED_REDEFINED;
	}
}
