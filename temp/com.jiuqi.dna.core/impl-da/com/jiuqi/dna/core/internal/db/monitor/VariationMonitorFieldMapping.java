package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.db.monitor.VariationMonitorHelper;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.impl.NamedDefineImpl;

public final class VariationMonitorFieldMapping extends NamedDefineImpl {

	public final TableFieldDefine field;
	final String oldValueFN;
	final String newValueFN;

	public VariationMonitorFieldMapping(TableFieldDefine field,
			String oldValueFN, String newValueFN) {
		super(field.getName());
		this.field = field;
		this.oldValueFN = oldValueFN;
		this.newValueFN = newValueFN;
	}

	public VariationMonitorFieldMapping(TableFieldDefine field) {
		this(field, VariationMonitorHelper.defaultOldValueFN(field.getName()), VariationMonitorHelper.defaultNewValueFN(field.getName()));
	}

	@Override
	public String getXMLTagName() {
		throw new UnsupportedOperationException();
	}
}