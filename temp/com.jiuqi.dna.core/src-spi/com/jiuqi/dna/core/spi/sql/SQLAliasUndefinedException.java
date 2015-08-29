package com.jiuqi.dna.core.spi.sql;

/**
 * ����Ϊ�����쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLAliasUndefinedException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String alias;

	public SQLAliasUndefinedException(int line, int col, String alias) {
		super(line, col, "����δ���� '" + alias + "'");
		this.alias = alias;
	}

	/**
	 * ��ȡδ����ı���
	 * 
	 * @return
	 */
	public String getAlias() {
		return this.alias;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ALIAS_UNDEFINED;
	}
}
