package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.DynamicObject;
import com.jiuqi.dna.core.internal.da.sql.render.ESql;

/**
 * 数据库访问语句实现类
 * 
 * @author houchunlei
 * 
 */
abstract class StatementImpl extends ArgumentableImpl implements IStatement {

	@Override
	protected final boolean isNameCaseSensitive() {
		return false;
	}

	public final boolean ignorePrepareIfDBInvalid() {
		return true;
	}

	StatementImpl(String name) {
		super(name, DynamicObject.class);
	}

	StatementImpl(String name, StructDefineImpl arguments) {
		super(name, arguments);
	}

	public final StructDefineImpl getArgumentsDefine() {
		return this.arguments;
	}

	public abstract ESql getSql(DBAdapterImpl dbAdapter);

	public void ensurePrepared() {
		this.ensurePrepared(null, false);
	}
}