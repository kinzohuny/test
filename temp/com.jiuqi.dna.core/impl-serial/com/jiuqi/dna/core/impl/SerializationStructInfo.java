package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.type.GUID;

/**
 * ���л��ṹ��Ϣ������֧�����ڵ����ڰ汾��ͬ�Ľṹ���ͼ����������
 * 
 * @author gaojingxin
 * 
 */
@StructClass
public class SerializationStructInfo {
	String name;
	String[] fieldNames;
	GUID[] fieldTypeIDs;
}
