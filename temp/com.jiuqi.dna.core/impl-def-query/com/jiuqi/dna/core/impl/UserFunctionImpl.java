package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.nio.charset.Charset;

import com.jiuqi.dna.core.da.IFuncSpec;
import com.jiuqi.dna.core.da.SQLFuncSpec.ArgumentSpec;
import com.jiuqi.dna.core.da.SQLFuncSpec.SQLFuncPattern;
import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.def.query.UserFunctionDeclarator;
import com.jiuqi.dna.core.def.query.UserFunctionDeclare;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.common.Strings;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.type.DataType;

/**
 * �û��Զ��庯��
 * 
 * @author houchunlei
 * 
 */
public final class UserFunctionImpl extends NamedDefineImpl implements
		UserFunctionDeclare, Declarative<UserFunctionDeclarator>, IFuncSpec,
		OperatorIntrl {

	@Override
	protected final boolean isNameCaseSensitive() {
		return false;
	}

	/**
	 * �û����庯���Ĺ��췽��
	 * 
	 * @param name
	 * @param returns
	 * @param declarator
	 */
	public UserFunctionImpl(String name, DataType returns,
			boolean isNonDeterministic, UserFunctionDeclarator declarator) {
		super(name);
		if (returns == null) {
			throw new NullPointerException("�û����庯���ķ�������Ϊ�ա�");
		}
		this.returns = (DataTypeInternal) returns;
		this.isNonDeterministic = isNonDeterministic;
		this.declarator = declarator;
	}

	final DataTypeInternal returns;

	final boolean isNonDeterministic;

	final NamedDefineContainerImpl<FunctionArgumentImpl> arguments = new NamedDefineContainerImpl<FunctionArgumentImpl>();

	final UserFunctionDeclarator declarator;

	@Override
	public final String getXMLTagName() {
		return null;
	}

	public final UserFunctionDeclarator getDeclarator() {
		return this.declarator;
	}

	public final SQLFuncPattern accept(DataType[] types) {
		if (this.pattern.accept(types)) {
			return this.pattern;
		}
		return null;
	}

	public final DataType getReturnType() {
		return this.returns;
	}

	public final FunctionArgumentImpl newArgument(DataType type) {
		return this.newArgument("arg" + Integer.valueOf(this.arguments.size()), type);
	}

	public final FunctionArgumentImpl newArgument(String name, DataType type) {
		if (this.inited) {
			throw new UnsupportedOperationException("�������޸��Ѿ���ɳ�ʼ�����û��������塣");
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("��������");
		}
		if (type == null) {
			throw new NullArgumentException("��������");
		}
		name = name.toLowerCase();
		if (this.arguments.find(name) != null) {
			throw new IllegalArgumentException("����Ϊ[" + name + "]�Ĳ����Ѿ����ڡ�");
		}
		FunctionArgumentImpl arg = new FunctionArgumentImpl(name, (DataTypeInternal) type);
		this.arguments.add(arg);
		return arg;
	}

	public final NamedDefineContainerImpl<FunctionArgumentImpl> getArguments() {
		return this.arguments;
	}

	/**
	 * ��ʼ��������վ������ʱ����һ�Ρ�
	 */
	final void initPatterns() {
		final ArgumentSpec[] args;
		if (this.arguments.size() == 0) {
			args = ArgumentSpec.EMPTY_ARRAY;
		} else {
			args = new ArgumentSpec[this.arguments.size()];
			for (int i = 0; i < this.arguments.size(); i++) {
				FunctionArgumentImpl fa = this.arguments.get(i);
				args[i] = new ArgumentSpec(fa.name, fa.description, fa.type);
			}
		}
		this.pattern = new SQLFuncPattern(this.description, this.returns, args) {
			@Override
			public OperateExpression expOf(Object[] values) {
				return UserFunctionImpl.this.expOf(values);
			}
		};
		this.inited = true;
	}

	boolean inited;

	private SQLFuncPattern pattern;

	public final OperateExpression expOf(Object... values) {
		final ValueExpr[] exprs = ValueExpr.expArrayOf(values);
		if (this.pattern.accept(exprs)) {
			return new OperateExpr(this, exprs);
		}
		throw new UnsupportedRoutineArgumentValuesException();
	}

	public final DataTypeInternal checkValues(ValueExpr[] values) {
		if (this.pattern.accept(values)) {
			return this.returns;
		}
		throw new UnsupportedRoutineArgumentValuesException();
	}

	public final boolean isNonDeterministic() {
		return this.isNonDeterministic;
	}

	public final void render(ISqlExprBuffer buffer, TableUsages usages,
			OperateExpr expr) {
		for (ValueExpr value : expr.values) {
			value.render(buffer, usages);
		}
		buffer.userfunction(this.name, this.pattern.args.length);
	}

	private static final Charset CHARSET_UTF8 = Charset.forName("UTF8");

	public final String loaddDdl(final DbMetadata dbMetadata) {
		final Class<?> clz = this.declarator.getClass();
		try {
			final String ddl = Strings.readString(clz, clz.getSimpleName(), dbMetadata.getModifiers(), CHARSET_UTF8);
			if (ddl == null || ddl.length() == 0) {
				throw new IllegalStateException("�û����庯��[" + this.name + "]������ݿ�[" + dbMetadata.dbProductName + "]�Ĺ����ű������ڡ�");
			}
			return ddl;
		} catch (IOException e) {
			throw new IllegalStateException("��ȡ��������Ϊ[" + clz.getName() + "]���û����庯���Ĺ����ű���", e);
		}
	}

	boolean valid;

	public final String functionName() {
		return this.name;
	}
}