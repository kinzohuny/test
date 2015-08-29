package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.query.InsertStatementDeclarator;
import com.jiuqi.dna.core.def.query.InsertStatementDeclare;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.statement.UnsupportedAssignmentException;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.DataType;

/**
 * ������䶨��ʵ��
 * 
 * @author houchunlei
 * 
 */
public final class InsertStatementImpl extends ModifyStatementImpl implements
		InsertStatementDeclare, Declarative<InsertStatementDeclarator> {

	public final InsertStatementDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.INSERT;
	}

	public final void assignConst(TableFieldDefine field, Object value) {
		if (field == null) {
			throw new NullArgumentException("������");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		ValueExpr expr = value == null ? NullExpr.NULL : f.getType().detect(ConstExpr.parser, value);
		this.assign(f, expr);

	}

	public final void assignExpression(TableFieldDefine field,
			ValueExpression value) {
		if (value == null) {
			throw new NullArgumentException("����ֵ");
		}
		this.assign(field, (ValueExpr) value);
	}

	public final void assignArgument(TableFieldDefine field,
			ArgumentDefine argument) {
		StructFieldDefineImpl arg = (StructFieldDefineImpl) argument;
		if (arg.owner != this.arguments) {
			throw new IllegalStateException();
		}
		this.assign(field, new ArgumentRefExpr((StructFieldDefineImpl) argument));
	}

	public final StructFieldDefineImpl assignArgument(TableFieldDefine field,
			String name, DataType type) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("��������");
		}
		if (type == null) {
			throw new NullArgumentException("��������");
		}
		StructFieldDefineImpl arg = this.newArgument(name, type);
		this.assign(field, new ArgumentRefExpr(arg));
		return arg;
	}

	public final StructFieldDefineImpl assignArgument(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("������");
		}
		StructFieldDefineImpl arg = this.newArgument(field);
		this.assign(field, new ArgumentRefExpr(arg));
		return arg;
	}

	public final DerivedQueryImpl getInsertValues() {
		return this.values;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_insert;
	}

	static final String xml_name_insert = "insert-statement";

	/**
	 * ��������Ŀ���ֶμ���Ӧֵ
	 * 
	 * <p>
	 * insert���ʵ�����ǲ���һ����ϵ,ʹ��query�ṹ.<br>
	 * ע�⵽values�Ӿ���ʵ��select�Ӿ��˻������й�ϵʱ������.
	 * 
	 * <p>
	 * query��ÿһ����е�name��expr�ֱ��ӦĿ���ֶμ�ֵ
	 */
	public final DerivedQueryImpl values = new DerivedQueryImpl(null);

	final InsertStatementDeclarator declarator;

	public InsertStatementImpl(String name, TableDefineImpl table) {
		super(name, table.name, table);
		this.declarator = null;
	}

	public InsertStatementImpl(String name, TableDefineImpl table,
			InsertStatementDeclarator declarator) {
		super(name, table.name, table);
		this.declarator = declarator;
	}

	InsertStatementImpl(String name, StructDefineImpl argumentsRef,
			TableDefineImpl table) {
		super(name, table.name, table, argumentsRef);
		this.declarator = null;
	}

	final void assign(TableFieldDefine field, ValueExpr value) {
		if (field == null) {
			throw new NullArgumentException("�����ж���");
		}
		if (value == null) {
			throw new NullArgumentException("����ֵ");
		}
		TableFieldDefineImpl fi = (TableFieldDefineImpl) field;
		if (this.moTableRef.target != fi.owner) {
			throw new IllegalStatementDefineException(this, "�ڲ�������У��ֶ�[" + fi.name + "]������Ŀ���[" + this.moTableRef.target.name + "]��");
		}
		AssignCapability ac = fi.getType().isAssignableFrom(value.getType());
		if (ac == AssignCapability.NO || ac == AssignCapability.CONVERT) {
			switch (ContextVariableIntl.isStrictAssignType()) {
			case 1:
				System.err.println(UnsupportedAssignmentException.message(this, field, value.getType()));
				break;
			case 2:
				new UnsupportedAssignmentException(this, field, value.getType()).printStackTrace();
				break;
			case 3:
				throw new UnsupportedAssignmentException(this, field, value.getType());
			}
		}
		DerivedQueryColumnImpl column = this.values.columns.find(fi.name);
		if (column == null) {
			this.values.newColumn(value, fi.name);
		} else {
			column.setExpression(value);
		}
	}

	public final boolean isSubqueried() {
		return this.values.rootRelationRef() != null;
	}

	private volatile ModifySql sql;

	@Override
	public final ModifySql getSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ModifySql sql = this.sql;
		if (sql == null) {
			synchronized (this) {
				sql = this.sql;
				if (sql == null) {
					this.sql = sql = dbAdapter.dbMetadata.insertSqlFor(this);
				}
			}
		}
		return sql;
	}

	@Override
	public final void doPrepare(ContextImpl<?, ?, ?> context) throws Throwable {
		super.doPrepare(context);
		this.sql = null;
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitInsertStatement(this, context);
	}

	public final void checkValid() {
		if (this.values.columns.size() == 0) {
			throw new IllegalStatementDefineException(this, "������䶨��[" + this.name + "]δ�������ֵ��");
		}
		if (this.values.columns.find(TableDefineImpl.FIELD_NAME_RECID) == null) {
			throw new IllegalStatementDefineException(this, "������䶨��[" + this.name + "]δ����RECID�ֶεĲ���ֵ��");
		}
	}
}