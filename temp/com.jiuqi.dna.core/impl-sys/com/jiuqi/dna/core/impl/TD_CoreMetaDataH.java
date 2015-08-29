package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.type.TypeFactory;

final class TD_CoreMetaDataH extends TableDeclarator {

	TD_CoreMetaDataH() {
		super("core_metadata_hist");
		this.table.newField("kind", TypeFactory.NVARCHAR(32));
		this.table.newField("name", TypeFactory.NVARCHAR(64));
		this.table.newField("finish_time", TypeFactory.DATE);
		this.table.newField("operation", TypeFactory.NVARCHAR(20));
		this.table.newField("xml", TypeFactory.TEXT);
	}
}