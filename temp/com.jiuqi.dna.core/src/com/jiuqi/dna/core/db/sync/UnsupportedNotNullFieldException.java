package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * 尝试增加非空字段,或修改字段为非空的数据库同步操作不支持.
 * 
 * @author houchunlei
 * 
 */
public class UnsupportedNotNullFieldException extends TableSyncException {

	/**
	 * 异常产生的字段定义
	 */
	public final TableFieldDefine field;

	/**
	 * 指示字段是新增或修改.true表示为新增字段.
	 */
	public final boolean addOrModify;

	private static final long serialVersionUID = -9054776625593298664L;

	public UnsupportedNotNullFieldException(TableFieldDefine field,
			boolean addOrModify) {
		super(field.getOwner(), message(field, addOrModify));
		this.field = field;
		this.addOrModify = addOrModify;
	}

	public static final String message(TableFieldDefine field,
			boolean addOrModify) {
		return addOrModify ? "不能增加非空字段定义" + intro(field) + ",表不为空且字段未定义默认值."
				: "不能修改字段" + intro(field) + "为非空,指定字段包含空值.";
	}
}