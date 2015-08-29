package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.DerivedQueryDefine;
import com.jiuqi.dna.core.def.query.RelationJoinable;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableRelationDefine;

@SuppressWarnings("deprecation")
interface RelationJoinableIntrl extends RelationJoinable {

	public JoinedTableRef newJoin(TableDefine table);

	public JoinedTableRef newJoin(TableDefine table, String name);

	public JoinedTableRef newJoin(TableDeclarator table);

	public JoinedTableRef newJoin(TableDeclarator table, String name);

	public JoinedTableRef newJoin(TableRelationDefine sample);

	public JoinedTableRef newJoin(TableRelationDefine sample, String name);

	public JoinedQueryRef newJoin(DerivedQueryDefine query);

	public JoinedQueryRef newJoin(DerivedQueryDefine query, String name);
}