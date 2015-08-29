package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * 在逻辑表发布过程中,字段类型的转换不支持.
 * 
 * @author houchunlei
 * 
 */
public class UnsupportedTypeConversionException extends TableSyncException {

	private static final long serialVersionUID = 620953752493528558L;

	/**
	 * 要求的字段定义
	 */
	public final TableFieldDefine field;

	/**
	 * 字段当前的数据类型
	 */
	public final String before;

	public UnsupportedTypeConversionException(TableFieldDefine field,
			String type) {
		super(field.getOwner(), message(field, type));
		this.field = field;
		this.before = type;
	}

	private static final String message(TableFieldDefine field, String type) {
		return "以发布模式，尝试将字段" + intro(field) + "从数据库类型[" + type + "]更改为["
				+ field.getType().toString() + "]，该类型转换不支持。";
	}
}