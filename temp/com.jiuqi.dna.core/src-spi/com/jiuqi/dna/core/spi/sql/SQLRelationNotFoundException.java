package com.jiuqi.dna.core.spi.sql;

/**
 * ���ϵ�Ҳ����쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLRelationNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String relationName;

	public SQLRelationNotFoundException(int line, int col, String name) {
		super(line, col, "�Ҳ������ϵ'" + name + "'");
		this.relationName = name;
	}

	public SQLRelationNotFoundException(String name) {
		this(0, 0, name);
	}

	/**
	 * ��ȡ���ϵ����
	 * 
	 * @return
	 */
	public String getRelationName() {
		return relationName;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.RELATION_NOT_FOUND;
	}
}
