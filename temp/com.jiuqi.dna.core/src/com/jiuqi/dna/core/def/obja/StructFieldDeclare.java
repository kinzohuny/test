package com.jiuqi.dna.core.def.obja;

import com.jiuqi.dna.core.def.FieldDeclare;

/**
 * 结构字段定义
 * 
 * @author gaojingxin
 * 
 */
public interface StructFieldDeclare extends StructFieldDefine, FieldDeclare {
	/**
	 * 设置是否是状态字段，参与序列化克隆比较等
	 */
	public void setStateField(boolean value);

}
