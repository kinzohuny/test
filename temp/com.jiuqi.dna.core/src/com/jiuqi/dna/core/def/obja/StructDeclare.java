package com.jiuqi.dna.core.def.obja;

import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.type.DataTypable;
import com.jiuqi.dna.core.type.DataType;

/**
 * �ṹ����
 * 
 * @author gaojingxin
 * 
 */
public interface StructDeclare extends StructDefine, NamedDeclare {
	/**
	 * ����ֶζ����б�
	 * 
	 * @return �����ֶζ����б�
	 */
	public ModifiableNamedElementContainer<? extends StructFieldDeclare> getFields();

	public StructFieldDeclare newField(String name, DataType type);

	public StructFieldDeclare newField(FieldDefine sample);

	public StructFieldDeclare newField(String name, DataTypable typable);
}
