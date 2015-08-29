package com.jiuqi.dna.core.spi.sql;

/**
 * �����ظ��쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLAliasDuplicateException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String alias;

	public SQLAliasDuplicateException(int line, int col, String alias) {
		super(line, col, "�����ظ����� '" + alias + "'");
		this.alias = alias;
	}

	public SQLAliasDuplicateException(String alias) {
		this(0, 0, alias);
	}

	/**
	 * ��ȡ�ظ��ı���
	 * 
	 * @return
	 */
	public String getAlias() {
		return this.alias;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ALIAS_DUPLICATE;
	}
}
