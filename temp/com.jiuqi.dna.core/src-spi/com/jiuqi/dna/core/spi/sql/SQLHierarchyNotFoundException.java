package com.jiuqi.dna.core.spi.sql;

/**
 * 找不到级次异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLHierarchyNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String hierarchyName;

	public SQLHierarchyNotFoundException(int line, int col, String hier) {
		super(line, col, "找不到级次定义 '" + hier + "'");
		this.hierarchyName = hier;
	}

	public SQLHierarchyNotFoundException(String hier) {
		this(0, 0, hier);
	}

	/**
	 * 获取级次的名称
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
