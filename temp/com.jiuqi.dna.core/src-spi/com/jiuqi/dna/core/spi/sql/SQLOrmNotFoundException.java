package com.jiuqi.dna.core.spi.sql;

/**
 * �Ҳ���ORM�쳣
 * 
 * @author niuhaifeng
 * 
 */
public class SQLOrmNotFoundException extends SQLParseException {
	private static final long serialVersionUID = 1L;
	private final String ormName;

	public SQLOrmNotFoundException(int line, int col, String orm) {
		super(line, col, "�Ҳ���ORM���� '" + orm + "'");
		this.ormName = orm;
	}

	public SQLOrmNotFoundException(String orm) {
		this(0, 0, orm);
	}

	/**
	 * ��ȡorm����
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
