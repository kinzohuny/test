package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.def.query.ORMDeclarator;
import com.jiuqi.dna.core.def.table.TableReferenceDeclare;

public class VariationMonitorORM extends
		ORMDeclarator<VariationMonitorMetadata> {

	protected VariationMonitorORM(VariationMonitorTB t) {
		super("core/db/monitor/MonitorORM");
		TableReferenceDeclare tr = this.orm.newReference(t, "t");
		this.orm.newColumn(tr.expOf(t.f_RECID), "id");
		this.orm.newColumn(tr.expOf(t.f_RECVER), "version");
		this.orm.newColumn(tr.expOf(t.name), "name");
		this.orm.newColumn(tr.expOf(t.target), "target");
		this.orm.newColumn(tr.expOf(t.variation), "variation");
		this.orm.newColumn(tr.expOf(t.trigger), "trigger");
		this.orm.newColumn(tr.expOf(t.setting), "setting");
	}
}