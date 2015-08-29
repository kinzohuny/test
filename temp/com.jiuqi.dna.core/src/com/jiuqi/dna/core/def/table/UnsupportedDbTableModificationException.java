package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;

/**
 * 不支持的对物理表定义的修改操作
 * 
 * @author houchunlei
 * 
 */
public final class UnsupportedDbTableModificationException extends
		UnsupportedTableModificationException {

	private static final long serialVersionUID = -6629961493091261989L;

	public final transient DBTableDefine dbTable;

	/**
	 * 为主物理表
	 */
	public static final int PRIMARY_TABLE = 0;
	/**
	 * 物理表仍包含字段
	 */
	public static final int NOT_EMPTY_TABLE = 1;

	public final int action;
	public final int condition;

	public UnsupportedDbTableModificationException(TableDefineImpl table,
			DBTableDefineImpl dbTable, int action, int condition) {
		super(table, message(dbTable, action, condition));
		this.dbTable = dbTable;
		this.action = action;
		this.condition = condition;
	}

	private static final String message(DBTableDefineImpl dbTable, int action,
			int condition) {
		return "不允许" + action(action) + condition(condition) + "物理表定义"
				+ dbTable.desc() + ".";
	}

	private static final String condition(int condition) {
		switch (condition) {
		case PRIMARY_TABLE:
			return "主";
		case NOT_EMPTY_TABLE:
			return "非空的";
		default:
			throw new IllegalStateException();
		}
	}
}