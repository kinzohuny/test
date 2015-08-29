package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * ��ϵ��Ԫ����.������ά����ʽ�����ݽṹ��Ԫ���ݶ���.
 * 
 * @see com.jiuqi.dna.core.def.query.RelationDefine
 * 
 * @author houchunlei
 * 
 */
public interface RelationDeclare extends RelationDefine, NamedDeclare {

	public RelationColumnDeclare findColumn(String columnName);

	public RelationColumnDeclare getColumn(String columnName);
}
