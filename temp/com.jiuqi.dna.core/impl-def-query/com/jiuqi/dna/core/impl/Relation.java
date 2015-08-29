package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationDeclare;
import com.jiuqi.dna.core.misc.SXRenderable;

/**
 * ��ϵԪ������ڲ��ӿ�
 * 
 * @author houchunlei
 */
interface Relation extends RelationDeclare, SXRenderable {

	RelationColumn getColumn(String columnName);

	RelationColumn findColumn(String columnName);
}
