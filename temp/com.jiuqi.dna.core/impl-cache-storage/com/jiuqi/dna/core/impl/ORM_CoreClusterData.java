package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.ORMDeclarator;
import com.jiuqi.dna.core.def.query.QuTableRefDeclare;

public class ORM_CoreClusterData extends ORMDeclarator<QuirkCacheClusterData> {

	protected ORM_CoreClusterData(TD_CoreClusterData tableDefine) {
		super("ORM_CoreClusterData");
		QuTableRefDeclare tableReference = this.orm.newReference(tableDefine);
		this.orm.newColumn(tableDefine.f_id, "id");
		this.orm.newColumn(tableDefine.f_index, "index");
		this.orm.newColumn(tableDefine.f_from, "from");
		this.orm.newColumn(tableDefine.f_time, "time");
		this.orm.newColumn(tableDefine.f_data, "data");
		this.orm.setCondition(tableReference.expOf(tableDefine.f_index).xEq(this.orm.newArgument(tableDefine.f_index)));
		this.orm.newOrderBy(tableDefine.f_time);
	}
}