package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.GUID;

final class CoreMetadataMergeEntity {

	final GUID id;
	GUID appid;
	long timestamp;

	CoreMetadataMergeEntity(ContextImpl<?, ?, ?> context) {
		this.id = context.newRECID();
		this.appid = context.occorAt.site.application.localNodeID;
		this.timestamp = System.currentTimeMillis();
	}

	String kind;
	String name;

	final void setTable(String name) {
		this.kind = "TABLE";
		this.name = name;
	}

	String staticDef;
	String dynamicDef;
	String merged;

	final void setStatic(TableDefineImpl t) {
		final SXElement e = SXElement.newDoc();
		t.renderInto(e);
		this.staticDef = e.toString();
	}

	final void setDynamic(SXElement e) {
		this.dynamicDef = e.toString();
	}

	final void setMerged(TableDefineImpl t) {
		final SXElement e = SXElement.newDoc();
		t.renderInto(e);
		this.merged = e.toString();
	}
}