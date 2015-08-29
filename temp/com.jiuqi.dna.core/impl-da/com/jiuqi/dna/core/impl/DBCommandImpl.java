package com.jiuqi.dna.core.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.obja.DynamicObject;

/**
 * ���ݿ�����ʵ����
 * 
 * @author houchunlei
 * 
 */
abstract class DBCommandImpl extends StatementHolder<DBCommandProxy> {

	abstract int executeUpdate();

	abstract RecordSet[] executeProcedure();

	abstract ResultSet executeQuery(Object argValueObj);

	abstract RecordSetImpl executeQuery();

	abstract RecordSetImpl executeQueryTop(long limit);

	abstract RecordSetImpl executeQueryLimit(long limit, long offset);

	abstract void iterateQuery(RecordIterateAction action);

	abstract void iterateQueryTop(RecordIterateAction action, long limit);

	abstract void iterateQueryLimit(RecordIterateAction action, long limit,
			long offset);

	abstract long rowCountOf();

	abstract Object executeScalar();

	abstract IStatement getStatement();

	final DynamicObject getArgumentsObj() {
		this.adapter.checkAccessible();
		return this.argValueObj;
	}

	final void setArgumentValues(Object... argValues) {
		this.adapter.checkAccessible();
		setArgumentValues(this.argValueObj, this.getStatement(), argValues);
	}

	final void setArgumentValue(int argumentIndex, Object argValue) {
		this.adapter.checkAccessible();
		this.getStatement().getArguments().get(argumentIndex).setFieldValueAsObject(this.argValueObj, argValue);
	}

	final void setArgumentValue(ArgumentDefine arg, Object argValue) {
		this.adapter.checkAccessible();
		if (arg.getOwner() != this.getStatement().getArgumentsDefine()) {
			throw new IllegalArgumentException();
		}
		((StructFieldDefineImpl) arg).setFieldValueAsObject(this.argValueObj, argValue);
	}

	/**
	 * ����ֵ����
	 */
	final DynamicObject argValueObj;

	DBCommandImpl(ContextImpl<?, ?, ?> context, IStatement statement,
			DBCommandProxy proxy) {
		super(context, proxy);
		statement.ensurePrepared(context, false);
		this.argValueObj = (DynamicObject) statement.getArgumentsDefine().newEmptyDynSO();
		this.initDefaultValues(statement);
	}

	final void initDefaultValues(IStatement statement) {
		final ArrayList<StructFieldDefineImpl> args = statement.getArguments();
		for (int i = 0, c = args.size(); i < c; i++) {
			StructFieldDefineImpl arg = args.get(i);
			if (arg.defaultValue != null && arg.defaultValue != NullExpr.NULL) {
				try {
					arg.setFieldValue(this.argValueObj, (ConstExpr) arg.defaultValue);
				} catch (ClassCastException e) {
					throw new IllegalArgumentException("��֧�ֵ�Ĭ��ֵ����", e);
				}
			}
		}
	}

	final UnsupportedOperationException notQueryStatement() {
		return new UnsupportedOperationException("��䶨��[" + this.getStatement().getName() + "]���ǲ�ѯ��䶨��.");
	}

	final UnsupportedOperationException notModifyStatement() {
		return new UnsupportedOperationException("��䶨��[" + this.getStatement().getName() + "]���Ǹ�����䶨��.");
	}

	final UnsupportedOperationException notProcedureStatement() {
		return new UnsupportedOperationException("��䶨��[" + this.getStatement().getName() + "]���Ǵ洢������䶨��.");
	}

	final Object getArgumentValue(int index) {
		return this.getStatement().getArguments().get(index).getFieldValueAsObject(this.argValueObj);
	}

	final Object getArgumentValue(ArgumentDefine arg) {
		if (arg.getOwner() != this.getStatement().getArgumentsDefine()) {
			throw new IllegalArgumentException("�������岻���ڵ�ǰ��䶨��.");
		}
		return ((StructFieldDefineImpl) arg).getFieldValueAsObject(this.argValueObj);
	}
}