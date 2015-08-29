package com.jiuqi.dna.core.da.ext;

import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * 字段
 * 
 * @deprecated
 * 
 */
public interface RPTRecordSetField extends RPTRecordSetColumn {
	/**
	 * 对应字段
	 */
	public TableFieldDefine getTableField();

	/**
	 * 返回该字段的约束
	 */
	public RPTRecordSetRestriction getRestriction();
}
