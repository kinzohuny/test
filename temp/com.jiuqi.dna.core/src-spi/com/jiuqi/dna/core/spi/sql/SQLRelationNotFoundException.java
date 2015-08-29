package com.jiuqi.dna.core.spi.sql;

/**
 * 表关系找不到异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLRelationNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String relationName;

	public SQLRelationNotFoundException(int line, int col, String name) {
		super(line, col, "找不到表关系'" + name + "'");
		this.relationName = name;
	}

	public SQLRelationNotFoundException(String name) {
		this(0, 0, name);
	}

	/**
	 * 获取表关系名称
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
