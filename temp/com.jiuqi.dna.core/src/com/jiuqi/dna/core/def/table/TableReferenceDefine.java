package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.query.RelationRefDefine;

/**
 * �����ýӿ�
 * 
 * <p>
 * �̳�����ϵ���ö��壬��ʾĿ������Ϊ����Ĺ�ϵ���á�
 * 
 * @see com.jiuqi.dna.core.def.query.RelationRefDefine
 * 
 * @author gaojingxin
 * 
 */
public interface TableReferenceDefine extends RelationRefDefine {

	/**
	 * ��ȡĿ���߼���
	 */
	public TableDefine getTarget();
}