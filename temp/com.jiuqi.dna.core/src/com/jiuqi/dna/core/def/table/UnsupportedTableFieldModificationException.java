package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;

/**
 * 不支持的对逻辑表字段的修改操作
 * 
 * @author houchunlei
 * 
 */
public final class UnsupportedTableFieldModificationException extends
		UnsupportedTableModificationException {

	private static final long serialVersionUID = -435716537558819809L;

	/**
	 * 不支持修改的字段
	 */
	public final transient TableFieldDefine field;

	/**
	 * 不支持的操作定义
	 */
	public final int action;

	/**
	 * 不支持的原因
	 */
	public final int condition;

	/**
	 * 为系统字段
	 */
	public static final int SYSTEM_FIELD = 0;
	/**
	 * 为逻辑主键索引字段
	 */
	public static final int KEY_FIELD = 1;
	/**
	 * 为被索引字段
	 */
	public static final int INDEX_FIELD = 2;
	/**
	 * 为分区字段
	 */
	public static final int PARTITION_FIELD = 3;

	public UnsupportedTableFieldModificationException(TableDefineImpl table,
			TableFieldDefineImpl field, int action, int condition) {
		super(table, message(field, action, condition));
		this.field = field;
		this.action = action;
		this.condition = condition;
	}

	public static final String message(TableFieldDefineImpl field, int action,
			int condition) {
		return "不允许" + action(action) + condition(condition) + "字段定义"
				+ field.desc() + ".";
	}

	private static final String condition(int condition) {
		switch (condition) {
		case SYSTEM_FIELD:
			return "系统";
		case KEY_FIELD:
			return "主键";
		case INDEX_FIELD:
			return "被索引";
		case PARTITION_FIELD:
			return "分区";
		default:
			throw new IllegalArgumentException();
		}
	}
}