package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.DerivedQueryDefine;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableRelationDefine;

/**
 * 更新语句的关系引用
 * 
 * @author houchunlei
 * 
 */
public interface MoRelationRef extends NodableRelationRef {

	ModifyStatementImpl getOwner();

	MoRelationRef next();

	MoRelationRef last();

	MoJoinedRelationRef getJoins();

	MoJoinedTableRef newJoin(TableDefine target);

	MoJoinedTableRef newJoin(TableDefine target, String name);

	MoJoinedTableRef newJoin(TableDeclarator target);

	MoJoinedTableRef newJoin(TableDeclarator target, String name);

	MoJoinedTableRef newJoin(TableRelationDefine sample);

	MoJoinedTableRef newJoin(TableRelationDefine sample, String name);

	MoJoinedQueryRef newJoin(DerivedQueryDefine query);

	MoJoinedQueryRef newJoin(DerivedQueryDefine query, String name);
}