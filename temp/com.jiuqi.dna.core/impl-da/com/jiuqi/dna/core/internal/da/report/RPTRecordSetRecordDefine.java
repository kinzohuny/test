package com.jiuqi.dna.core.internal.da.report;

import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.StructDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.type.DataType;

final class RPTRecordSetRecordDefine extends StructDefineImpl {

	final static class RPTRecord extends DynObj {
		int hash;
		RPTRecord nextSameHash;
		long mask;
	}

	RPTRecordSetRecordDefine() {
		super("rpt-record", RPTRecord.class);
	}

	@Override
	protected String structTypeNamePrefix() {
		throw new UnsupportedOperationException();
	}

	final void reset() {
		this.fields.clear();
		this.clearAccessInfo();
	}

	final StructFieldDefineImpl newField(DataType type) {
		return super.newField(Integer.toString(this.fields.size()), type);
	}

	final RPTRecord newRecord(int recordState) {
		RPTRecord record = new RPTRecord();
		record.masks = recordState;
		this.prepareSONoCheck(record);
		this.initBinFieldsNullMask(record);
		return record;
	}
}