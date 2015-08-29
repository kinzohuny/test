package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.spi.sql.SQLVariableDuplicateException;
import com.jiuqi.dna.core.type.DataType;

class SQLVisitorContext {
	static final class SQLArgumentStructImpl extends StructDefineImpl {

		public SQLArgumentStructImpl() {
			super("SQLArguments", DynObj.class);
		}

		@Override
		protected String structTypeNamePrefix() {
			throw new UnsupportedOperationException();
		}
	}

	private StructDefineImpl struct;

	public ObjectQuerier querier;
	/**
	 * 是否限制使用字面量
	 */
	public boolean restrict;
	public Object rootStmt;

	public SQLVisitorContext(SQLVisitorContext root) {
		this.querier = root.querier;
		this.restrict = root.restrict;
		this.rootStmt = root.rootStmt;
	}

	public SQLVisitorContext(ObjectQuerier querier, boolean restrict) {
		this.querier = querier;
		this.restrict = restrict;
	}

	public final StructFieldDefineImpl newArgument(String name, DataType type) {
		if (this.struct == null) {
			this.struct = new SQLArgumentStructImpl();
		} else if (this.struct.getFields().find(name) != null) {
			throw new SQLVariableDuplicateException(name);
		}
		return this.struct.newField(name, type);
	}

	public final StructFieldDefineImpl findArgument(String name) {
		StructFieldDefineImpl argument = null;
		if (this.struct != null) {
			argument = this.struct.getFields().find(name);
		}
		return argument;
	}

	public final NamedDefineContainerImpl<StructFieldDefineImpl> getArguments() {
		if (this.struct != null) {
			return this.struct.fields;
		}
		return null;
	}
}
