package com.jiuqi.dna.core.internal.da.report;

import com.jiuqi.dna.core.da.ext.RPTRecordSetKey;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.type.DataType;

final class RPTRecordSetKeyImpl extends RPTRecordSetColumnImpl implements
		RPTRecordSetKey {

	final RPTRecordSetKeyRestrictionImpl defaultKeyRestriction;

	RPTRecordSetKeyImpl(RPTRecordSetImpl owner, TableFieldDefineImpl tableField) {
		// StructFieldµÄnameÍ¬KeyName
		super(owner, owner.keys.size(), owner.recordStruct.newField(tableField));
		this.defaultKeyRestriction = new RPTRecordSetKeyRestrictionImpl(this);
	}

	RPTRecordSetKeyImpl(RPTRecordSetImpl owner, String name, DataType type) {
		super(owner, owner.keys.size(), owner.recordStruct.newField(name, type));
		this.defaultKeyRestriction = new RPTRecordSetKeyRestrictionImpl(this);
	}

	@Override
	public final String toString() {
		return "Key:".concat(this.structField.name);
	}

	public final String getName() {
		return this.structField.name;
	}

	public final RPTRecordSetKeyRestrictionImpl getDefaultKeyRestriction() {
		return this.defaultKeyRestriction;
	}

	public final int addMatchValue(Object keyValue) {
		return this.defaultKeyRestriction.addMatchValue(keyValue);
	}

	public final void clearMatchValues() {
		this.defaultKeyRestriction.clearMatchValues();
	}
}
