package com.jiuqi.dna.core.def.obja;

import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.type.DataTypable;
import com.jiuqi.dna.core.type.DataType;

/**
 * 结构定义
 * 
 * @author gaojingxin
 * 
 */
public interface StructDeclare extends StructDefine, NamedDeclare {
	/**
	 * 获得字段定义列表
	 * 
	 * @return 返回字段定义列表
	 */
	public ModifiableNamedElementContainer<? extends StructFieldDeclare> getFields();

	public StructFieldDeclare newField(String name, DataType type);

	public StructFieldDeclare newField(FieldDefine sample);

	public StructFieldDeclare newField(String name, DataTypable typable);
}
