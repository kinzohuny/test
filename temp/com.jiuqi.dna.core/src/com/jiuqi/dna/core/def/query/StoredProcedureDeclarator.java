package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;

public abstract class StoredProcedureDeclarator extends
		StatementDeclarator<StoredProcedureDefine> {

	protected StoredProcedureDeclare procedure;

	@Override
	public StoredProcedureDefine getDefine() {
		return this.procedure;
	}

	@Override
	protected final void declareUseRef(ObjectQuerier querier) {
	}

	public StoredProcedureDeclarator(String name) {
		super(false);
		this.procedure = new StoredProcedureDefineImpl(name, this);
	}

	private final static Class<?>[] intf_classes = { StoredProcedureDefine.class };

	@Override
	protected Class<?>[] getDefineIntfRegClasses() {
		return intf_classes;
	}

}
