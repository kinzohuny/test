/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.ORMDeclarator;

/**
 * @author yangduanxue
 * 
 */
public final class ORM_CoreResourceLog extends
		ORMDeclarator<QuirkCacheResourceLog> {

	protected ORM_CoreResourceLog(TD_CoreResourceLog tableDefine) {
		super("ORM_CoreResourceLog");
		this.orm.newReference(tableDefine);
		this.orm.newColumn(tableDefine.f_RECID, "id");
		this.orm.newColumn(tableDefine.f_facade, "facade");
		this.orm.newColumn(tableDefine.f_modifyTimes, "modifyTimes");
		this.orm.newColumn(tableDefine.f_quirk, "quirk");
		this.orm.newOrderBy(tableDefine.f_modifyTimes, true);
	}
}