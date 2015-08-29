package com.jiuqi.dna.core.spi.sql;

/**
 * �Ҳ��������쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLHierarchyNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String hierarchyName;

	public SQLHierarchyNotFoundException(int line, int col, String hier) {
		super(line, col, "�Ҳ������ζ��� '" + hier + "'");
		this.hierarchyName = hier;
	}

	public SQLHierarchyNotFoundException(String hier) {
		this(0, 0, hier);
	}

	/**
	 * ��ȡ���ε�����
	 * 
	 * @return
	 */
	public String getHierarchyName() {
		return this.hierarchyName;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.HIERARCHY_NOT_FOUND;
	}
}
