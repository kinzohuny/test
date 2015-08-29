package com.jiuqi.dna.core.def.arg;

import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.type.DataTypable;
import com.jiuqi.dna.core.type.DataType;

/**
 * �������ģ���������
 * 
 * @see com.jiuqi.dna.core.def.arg.ArgumentableDefine
 * 
 * @author gaojingxin
 * 
 */
public interface ArgumentableDeclare extends ArgumentableDefine {

	/**
	 * ��ò�������
	 * 
	 * @return ���ز�������
	 */
	public ModifiableNamedElementContainer<? extends ArgumentDeclare> getArguments();

	/**
	 * ����һ������
	 */
	public ArgumentDeclare newArgument(String name, DataType type);

	/**
	 * ����һ������
	 */
	public ArgumentDeclare newArgument(String name, DataTypable typable);

	/**
	 * ����һ������
	 * 
	 * @param sample
	 *            ���ݸ�ֵ�����ƺ����ʹ�����������
	 */
	public ArgumentDeclare newArgument(FieldDefine sample);
}