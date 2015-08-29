package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.def.table.TableDefine;

/**
 * 逻辑表数据库结构同步异常
 * 
 * @author houchunlei
 * 
 */
public class TableSyncException extends RuntimeException {

	private static final long serialVersionUID = -7594164829136723261L;

	public final TableDefine table;

	public TableSyncException(TableDefine table, Throwable cause) {
		this(table, "", cause);
	}

	public TableSyncException(TableDefine table, String message) {
		this(table, message, null);
	}

	public TableSyncException(TableDefine table, String message, Throwable cause) {
		super(message(table) + message, cause);
		this.table = table;
	}

	public static final String message(TableDefine table) {
		return "逻辑表" + intro(table) + "数据库结构同步异常.";
	}

	static final boolean notEmpty(String s) {
		return s != null && s.length() > 0;
	}

	static final String intro(NamedDefine define) {
		if (notEmpty(define.getTitle())) {
			return "[" + define.getName() + ", " + define.getTitle() + "]";
		}
		return "[" + define.getName() + "]";
	}
}