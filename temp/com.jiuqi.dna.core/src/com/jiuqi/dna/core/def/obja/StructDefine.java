package com.jiuqi.dna.core.def.obja;

import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.TupleType;

/**
 * �ṹ����
 * 
 * @author gaojingxin
 * 
 */
public interface StructDefine extends NamedDefine, ObjectDataType, TupleType {
	/**
	 * ����ֶζ����б�
	 * 
	 * @return �����ֶζ����б�
	 */
	public NamedElementContainer<? extends StructFieldDefine> getFields();

	/**
	 * ����ת��
	 * 
	 * @param obj
	 *            ��Ҫ��ת���Ķ��󣬲���Ϊ��
	 * @return ����null��ʾת��ʧ��
	 * @exception NullArgumentException
	 *                objΪnull
	 */
	public Object tryConvert(Object convertFrom) throws NullArgumentException;

	/**
	 * ����Ƿ��ǽṹ�����Ӧ�Ķ���ʵ��
	 */
	public boolean isInstance(Object obj);
}
