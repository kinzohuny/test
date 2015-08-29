package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.impl.TableDefineImpl;

public abstract class UnsupportedTableModificationException extends
		RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * 修改操作
	 */
	public static final int ACTION_MODIFY = 0;
	/**
	 * 移除操作
	 */
	public static final int ACTION_REMOVE = 1;
	/**
	 * 移动操作
	 */
	public static final int ACTION_MOVE = 2;

	public final TableDefine table;

	public UnsupportedTableModificationException(TableDefineImpl table,
			String cause) {
		super(message0(table) + cause);
		this.table = table;
	}

	public static final String message0(TableDefineImpl table) {
		return "不支持的针对逻辑表" + table.desc() + "的修改操作: ";
	}

	protected static final String action(int action) {
		switch (action) {
		case ACTION_MODIFY:
			return "修改";
		case ACTION_REMOVE:
			return "移除";
		case ACTION_MOVE:
			return "移动";
		default:
			throw new IllegalArgumentException();
		}
	}
}