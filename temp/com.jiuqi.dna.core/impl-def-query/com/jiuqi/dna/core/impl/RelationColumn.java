package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationColumnDeclare;

/**
 * ��ϵ�ж�����ڲ��ӿ�
 * 
 * <p>
 * ֻ�Ǹ���ǽӿ�
 * 
 * @author houchunlei
 */
interface RelationColumn extends RelationColumnDeclare {

	Relation getOwner();
}
