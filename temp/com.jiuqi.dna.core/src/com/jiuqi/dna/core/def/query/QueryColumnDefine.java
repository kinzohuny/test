package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.obja.StructFieldDefine;

/**
 * ��ѯ��䶨�������ж���
 * 
 * @author gaojingxin
 */
public interface QueryColumnDefine extends SelectColumnDefine {

	/**
	 * ��ȡ�����Ĳ�ѯ��䶨��
	 * 
	 * @return
	 */
	public QueryStatementDefine getOwner();

	/**
	 * ��ȡӳ�䵽���ֶ�,�������Զ�ȡMO,ORMEntity,RO�Ķ�Ӧֵ
	 * 
	 * @return ����ӳ�䵽���ֶ�
	 */
	public StructFieldDefine getMapingField();
}
