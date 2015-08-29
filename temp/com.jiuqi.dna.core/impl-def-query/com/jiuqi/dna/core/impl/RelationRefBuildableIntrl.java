package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.DerivedQueryDefine;
import com.jiuqi.dna.core.def.query.RelationRefBuildable;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;

interface RelationRefBuildableIntrl extends RelationRefBuildable {

	public TableRef newReference(TableDefine table);

	public TableRef newReference(TableDefine table, String name);

	public TableRef newReference(TableDeclarator table);

	public TableRef newReference(TableDeclarator table, String name);

	public QueryRef newReference(DerivedQueryDefine query);

	public QueryRef newReference(DerivedQueryDefine query, String name);

}
