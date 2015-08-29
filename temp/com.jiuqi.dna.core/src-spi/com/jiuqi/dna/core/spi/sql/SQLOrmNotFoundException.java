package com.jiuqi.dna.core.spi.sql;

/**
 * 找不到ORM异常
 * 
 * @author niuhaifeng
 * 
 */
public class SQLOrmNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String ormName;

	public SQLOrmNotFoundException(int line, int col, String orm) {
		super(line, col, "找不到ORM定义 '" + orm + "'");
		this.ormName = orm;
	}

	public SQLOrmNotFoundException(String orm) {
		this(0, 0, orm);
	}

	/**
	 * 获取orm名称
	 * 
	 * @return
	 */
	public String getOrmName() {
		return ormName;
	}

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.ORM_NOT_FOUND;
	}
}
