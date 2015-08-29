package com.jiuqi.dna.core.internal.da.report;

import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.StructDefineImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.type.DataType;

/**
 * ¼üÖµ»º´æ¶¨Òå
 * 
 * @author gaojingxin
 * 
 */
final class RPTRecordSetKeyValuesCacheDefine extends StructDefineImpl {

	static class RPTKeyValuesCache extends DynObj {

	}

	RPTRecordSetKeyValuesCacheDefine() {
		super("rpt-kvc", RPTKeyValuesCache.class);
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

	final RPTKeyValuesCache newKeyValuesCache() {
		RPTKeyValuesCache keyValues = new RPTKeyValuesCache();
		this.prepareSONoCheck(keyValues);
		return keyValues;
	}
}