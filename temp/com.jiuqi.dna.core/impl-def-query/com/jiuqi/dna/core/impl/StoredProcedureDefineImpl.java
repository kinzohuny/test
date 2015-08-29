package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.nio.charset.Charset;

import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.query.ArgumentOutput;
import com.jiuqi.dna.core.def.query.StoredProcedureDeclarator;
import com.jiuqi.dna.core.def.query.StoredProcedureDeclare;
import com.jiuqi.dna.core.internal.common.Strings;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;
import com.jiuqi.dna.core.internal.da.statement.IllegalArgumentOutputOrderException;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.type.DataType;

public final class StoredProcedureDefineImpl extends StatementImpl implements
		StoredProcedureDeclare, Declarative<StoredProcedureDeclarator> {

	public final StoredProcedureDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.STORED_PROC;
	}

	public final int getResultSets() {
		return this.resultSets;
	}

	public final void setResultSets(int resultSets) {
		this.resultSets = resultSets;
	}

	public final StructFieldDefineImpl newArgument(String name, DataType type,
			ArgumentOutput output) {
		if (output == null) {
			throw new IllegalArgumentException("参数的传出类型为空.");
		}
		StructFieldDefineImpl arg = this.newArgument(name, type);
		arg.output = output;
		return arg;
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_stored_proc;
	}

	static final String xml_element_stored_proc = "stored_proc";

	public final StoredProcedureDeclarator declarator;

	public StoredProcedureDefineImpl(String name,
			StoredProcedureDeclarator declarator) {
		super(name);
		this.declarator = declarator;
	}

	private int resultSets;

	public final String loadDdl(DbMetadata dbMetadata) {
		final Class<?> clz = this.declarator.getClass();
		try {
			final String ddl = Strings.readString(clz, clz.getSimpleName(), dbMetadata.getModifiers(), CHARSET_UTF8);
			if (ddl == null || ddl.length() == 0) {
				throw new IllegalStateException("本地存储过程[" + this.name + "]针对数据库[" + dbMetadata.dbProductName + "]的构建脚本不存在。");
			}
			return ddl;
		} catch (IOException e) {
			throw new IllegalStateException("读取声明器名为[" + clz.getName() + "]的存储过程构建脚本错误。", e);
		}
	}

	private static final Charset CHARSET_UTF8 = Charset.forName("UTF8");

	@Override
	public final SpCallSql getSql(DBAdapterImpl adapter) {
		this.ensurePrepared();
		if (this.sql == null) {
			synchronized (this) {
				if (this.sql == null) {
					this.sql = adapter.dbMetadata.spCallSqlFor(this);
				}
			}
		}
		return this.sql;
	}

	private volatile SpCallSql sql;

	@Override
	public final void doPrepare(ContextImpl<?, ?, ?> context) throws Throwable {
		super.doPrepare(context);
		this.sql = null;
		this.checkArgumentOutputOrder();
	}

	private final void checkArgumentOutputOrder() {
		StructFieldDefineImpl prev = null;
		for (StructFieldDefineImpl sf : this.arguments.fields) {
			if (prev == null) {
				prev = sf;
			} else if (!sf.output.checkPrevOutput(prev.output)) {
				throw new IllegalArgumentOutputOrderException(this, prev, sf);
			}
		}
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitStoredProcedure(this, context);
	}

	public final boolean isInvalid() {
		return !this.valid;
	}

	public final void setValid(boolean valid) {
		this.valid = valid;
	}

	private boolean valid;
}