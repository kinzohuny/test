package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.obja.DynamicObject;
import com.jiuqi.dna.core.def.query.QueryStatementDeclare;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.def.query.SelectDefine;
import com.jiuqi.dna.core.exception.NamedDefineExistingException;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.QueryLimitSql;
import com.jiuqi.dna.core.internal.da.sql.render.QueryRowCountSql;
import com.jiuqi.dna.core.internal.da.sql.render.QuerySql;
import com.jiuqi.dna.core.internal.da.sql.render.QueryTopSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.type.DataTypable;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.Type;
import com.jiuqi.dna.core.type.TypeDetector;

public abstract class QueryStatementBase extends
		SelectImpl<QueryStatementBase, QueryColumnImpl> implements
		QueryStatementDeclare, IStatement, Withable, Cloneable {

	public final boolean ignorePrepareIfDBInvalid() {
		return true;
	}

	public final Type getRootType() {
		return this;
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.QUERY_H);
		this.digestAuthAndName(digester);
		short c = (short) this.columns.size();
		digester.update(c);
		for (int i = 0; i < c; i++) {
			this.columns.get(i).digestType(digester);
		}
	}

	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> detector, TUserData userData)
			throws UnsupportedOperationException {
		try {
			return detector.inQuery(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final NamedDefineContainerImpl<DerivedQueryImpl> getWiths() {
		return this.withs;
	}

	public final DerivedQueryImpl getWith(String name) {
		return this.withs.get(name);
	}

	public final DerivedQueryImpl newWith(String name) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("With定义名称");
		}
		if (this.withs.find(name) != null) {
			throw new NamedDefineExistingException("名称为[" + name + "]的With定义已经存在。");
		}
		DerivedQueryImpl query = new DerivedQueryImpl(this, name, true);
		this.withs.add(query);
		return query;
	}

	public final DerivedQueryImpl newDerivedQuery(SelectDefine sample) {
		DerivedQueryImpl query = new DerivedQueryImpl(this);
		SelectImpl<?, ?> from = (SelectImpl<?, ?>) sample;
		if (from instanceof Withable) {
			Withable withable = (Withable) from;
			for (DerivedQueryImpl with : withable.getWiths()) {
				DerivedQueryImpl withClone = this.newWith(with.getName());
				with.cloneSelectTo(withClone, this);
			}
		}
		from.cloneSelectTo(query, this);
		return query;
	}

	@Override
	public final MetaBaseContainerImpl<OrderByItemImpl> getOrderBys() {
		return this.orderbys;
	}

	@Override
	public final OrderByItemImpl newOrderBy(ValueExpression expr) {
		return this.newOrderBy(expr, false);
	}

	@Override
	public final OrderByItemImpl newOrderBy(ValueExpression expr, boolean isDesc) {
		if (expr == null) {
			throw new NullArgumentException("排序表达式");
		}
		ValueExpr value = (ValueExpr) expr;
		if (ContextVariableIntl.isStrictExprDomain()) {
			value.checkDomain(this);
		}
		OrderByItemImpl orderby = new OrderByItemImpl(this, value);
		orderby.setDesc(isDesc);
		return this.addOrderBy(orderby);
	}

	@Override
	public final OrderByItemImpl newOrderBy(RelationColumnDefine column) {
		return this.newOrderBy(column, false);
	}

	@Override
	public final OrderByItemImpl newOrderBy(RelationColumnDefine column,
			boolean isDesc) {
		if (column == null) {
			throw new NullArgumentException("排序的关系列定义");
		}
		OrderByItemImpl orderby;
		if (column instanceof QueryColumnImpl) {
			orderby = new OrderByItemImpl(this, new QueryColumnRefExpr((QueryColumnImpl) column));
		} else {
			orderby = new OrderByItemImpl(this, this.exprOf(column));
		}
		orderby.setDesc(isDesc);
		return this.addOrderBy(orderby);
	}

	public final NamedDefineContainerImpl<StructFieldDefineImpl> getArguments() {
		return this.args.fields;
	}

	public final StructFieldDefineImpl newArgument(String name, DataType type) {
		return this.args.newField(name, type);
	}

	public final StructFieldDefineImpl newArgument(String name,
			DataTypable typable) {
		return this.args.newField(name, typable);
	}

	public final StructFieldDefineImpl newArgument(FieldDefine sample) {
		return this.args.newField(sample);
	}

	public final Class<?> getAOClass() {
		return this.args.soClass;
	}

	public final Object newAO() {
		return this.args.newInitedSO();
	}

	public final Object newAO(Object... args) {
		return this.args.valuesAsSo(args);
	}

	public final StructFieldDefineImpl getArgument(String name) {
		return this.args.fields.get(name);
	}

	public final StructDefineImpl getArgumentsDefine() {
		return this.args;
	}

	public final StructDefineImpl args;

	final NamedDefineContainerImpl<DerivedQueryImpl> withs = new NamedDefineContainerImpl<DerivedQueryImpl>(false);

	MetaBaseContainerImpl<OrderByItemImpl> orderbys;

	StructDefineImpl mapping;

	QueryStatementBase(String name) {
		super(name);
		this.args = new ArgumentsDefine(DynamicObject.class);
	}

	QueryStatementBase(String name, StructDefineImpl args) {
		super(name);
		if (args == null) {
			throw new NullPointerException();
		}
		this.args = args;
	}

	public final RelationRefDomain getDomain() {
		return null;
	}

	final QueryColumnImpl newColumn(String name, ValueExpr expr,
			StructFieldDefineImpl sf) {
		QueryColumnImpl c = super.newColumn(expr, name);
		if (sf != null && sf.owner == this.mapping) {
			c.field = sf;
		}
		return c;
	}

	private final OrderByItemImpl addOrderBy(OrderByItemImpl orderby) {
		if (this.orderbys == null) {
			this.orderbys = new MetaBaseContainerImpl<OrderByItemImpl>();
		}
		this.orderbys.add(orderby);
		return orderby;
	}

	private final HashMap<QuRelationRef, Boolean> forUpdates = new HashMap<QuRelationRef, Boolean>();

	final void setForUpdate(QuRelationRef relationRef, boolean forUpdate) {
		if (relationRef.getOwner() != this) {
			throw new IllegalArgumentException();
		}
		this.forUpdates.put(relationRef, forUpdate);

	}

	final boolean isForUpdate(QuRelationRef relationRef) {
		if (relationRef.getOwner() != this) {
			throw new IllegalArgumentException();
		}
		Boolean forUpdate = this.forUpdates.get(relationRef);
		return forUpdate == null ? false : forUpdate.booleanValue();
	}

	final void cloneTo(QueryStatementBase target) {
		this.args.cloneFieldsTo(target.args);
		this.cloneWithsTo(target);
		super.cloneSelectTo(target, target);
		this.cloneOrderbysTo(target);
	}

	private final void cloneWithsTo(QueryStatementBase target) {
		for (DerivedQueryImpl with : this.getWiths()) {
			DerivedQueryImpl clone = target.newWith(with.getName());
			with.cloneSelectTo(clone, target);
		}
	}

	public final void cloneOrderbysTo(QueryStatementBase target) {
		if (this.orderbys != null) {
			for (int i = 0, c = this.orderbys.size(); i < c; i++) {
				this.orderbys.get(i).cloneTo(target, target);
			}
		}
	}

	@Override
	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		if (this.withs != null && this.withs.size() > 0) {
			for (int i = 0, c = this.withs.size(); i < c; i++) {
				this.withs.get(i).visit(visitor, context);
			}
		}
		super.visit(visitor, context);
	}

	public final void renderWiths(ISqlQueryBuffer buffer, TableUsages usages) {
		if (this.withs != null) {
			for (int i = 0, c = this.withs.size(); i < c; i++) {
				DerivedQueryImpl with = this.withs.get(i);
				ISqlSelectBuffer wb = buffer.newWith(with.name);
				with.renderFullSelect(wb, usages);
			}
		}
	}

	public final void renderOrderbys(ISqlQueryBuffer buffer, TableUsages usages) {
		if (this.orderbys != null && this.orderbys.size() > 0) {
			for (int i = 0, c = this.orderbys.size(); i < c; i++) {
				this.orderbys.get(i).render(buffer, usages);
			}
		}
	}

	public final void setRootKeys(TableFieldDefineImpl[] fields,
			ArgumentPlaceholder[] args) {
		if (this.rootRelationRef() instanceof QuRootTableRef) {
			QuRootTableRef tableRef = (QuRootTableRef) this.rootRelationRef();
			TableDefineImpl table = tableRef.getTarget();
			final ArrayList<IndexItemImpl> items = table.logicalKey.items;
			final int c = items.size();
			for (int i = 0; i < c; i++) {
				IndexItemImpl item = items.get(i);
				QueryColumnImpl column = this.findColumn(tableRef, item.getField());
				if (column == null) {
					throw new IllegalStatementDefineException(this, "没有输出逻辑表字段[" + items.get(i).getField().name + "]");
				}
				TableFieldDefineImpl field = item.field;
				fields[i] = field;
				args[i] = new ArgumentPlaceholder(column.field, field.getType());
			}
			return;
		}
		throw new IllegalStatementDefineException(this, "查询定义的根关系引用不是表引用。");
	}

	final QueryColumnImpl[] getRootKeyColumns() {
		if (this.rootRelationRef() instanceof QuRootTableRef) {
			QuRootTableRef tableRef = (QuRootTableRef) this.rootRelationRef();
			TableDefineImpl table = tableRef.getTarget();
			table.checkLogicalKeyAvaiable();
			final ArrayList<IndexItemImpl> items = table.logicalKey.items;
			final int c = items.size();
			QueryColumnImpl[] columns = new QueryColumnImpl[c];
			for (int i = 0; i < c; i++) {
				QueryColumnImpl column = this.findColumn(tableRef, items.get(i).getField());
				if (column == null) {
					throw new IllegalStatementDefineException(this, "查询语句定义[" + this.name + "]没有输出逻辑主键[" + items.get(i).getField().name + "]。");
				}
				columns[i] = column;
			}
			return columns;
		}
		throw new IllegalStatementDefineException(this, "查询定义的根关系引用不是表引用。");
	}

	public final void checkModifyRootOnly() {
		QuRootRelationRef root = this.rootRelationRef();
		for (QuRelationRef relationRef : root) {
			boolean isRoot = relationRef == root;
			boolean isUpdate = this.isForUpdate(relationRef);
			if (isRoot && !isUpdate || !isRoot && isUpdate) {
				throw new IllegalStatementDefineException(this, "查询定义不是只更新根表引用。");
			}
		}
	}

	private volatile boolean prepared;

	public final boolean isPrepared() {
		return this.prepared;
	}

	public final void ensurePrepared(ContextImpl<?, ?, ?> context,
			boolean rePrepared) {
		if (rePrepared || !this.prepared) {
			try {
				synchronized (this) {
					if (rePrepared || !this.prepared) {
						this.doPrepare();
						this.prepared = true;
					}
				}
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	public final void ensurePrepared() {
		this.ensurePrepared(null, false);
	}

	public final boolean supportModify(QuRelationRef relationRef) {
		return this.isForUpdate(relationRef) && relationRef instanceof QuTableRef;
	}

	void doPrepare() {
		this.args.prepareAccessInfo();
		this.querySql = null;
		this.queryTopSql = null;
		this.queryLimitSql = null;
		this.rowInsertSql = null;
		this.rowDeleteSql = null;
		this.rowUpdateSql = null;
	}

	private volatile QuerySql querySql;

	public final QuerySql getSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		QuerySql querySql = this.querySql;
		if (querySql == null) {
			synchronized (this) {
				querySql = this.querySql;
				if (querySql == null) {
					this.querySql = querySql = new QuerySql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return querySql;
	}

	private volatile QueryTopSql queryTopSql;

	public final QueryTopSql getQueryTopSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		QueryTopSql queryTopSql = this.queryTopSql;
		if (queryTopSql == null) {
			synchronized (this) {
				queryTopSql = this.queryTopSql;
				if (queryTopSql == null) {
					this.queryTopSql = queryTopSql = new QueryTopSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return queryTopSql;
	}

	private volatile QueryLimitSql queryLimitSql;

	public final QueryLimitSql getQueryLimitSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		QueryLimitSql queryLimitSql = this.queryLimitSql;
		if (queryLimitSql == null) {
			synchronized (this) {
				queryLimitSql = this.queryLimitSql;
				if (queryLimitSql == null) {
					this.queryLimitSql = queryLimitSql = new QueryLimitSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return queryLimitSql;
	}

	private volatile QueryRowCountSql queryRowCountSql;

	public final QueryRowCountSql getQueryRowCountSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		QueryRowCountSql queryRowCountSql = this.queryRowCountSql;
		if (queryRowCountSql == null) {
			synchronized (this) {
				queryRowCountSql = this.queryRowCountSql;
				if (queryRowCountSql == null) {
					this.queryRowCountSql = queryRowCountSql = new QueryRowCountSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return queryRowCountSql;
	}

	private volatile ModifySql rowInsertSql;

	final ModifySql getRowInsertSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ModifySql recordInsertSql = this.rowInsertSql;
		if (recordInsertSql == null) {
			synchronized (this) {
				recordInsertSql = this.rowInsertSql;
				if (recordInsertSql == null) {
					this.rowInsertSql = recordInsertSql = dbAdapter.dbMetadata.getRowInsertSql(this);
				}
			}
		}
		return recordInsertSql;
	}

	private volatile ModifySql rowUpdateSql;

	final ModifySql getRowUpdateSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ModifySql recordUpdateSql = this.rowUpdateSql;
		if (recordUpdateSql == null) {
			synchronized (this) {
				recordUpdateSql = this.rowUpdateSql;
				if (recordUpdateSql == null) {
					this.rowUpdateSql = recordUpdateSql = dbAdapter.dbMetadata.getRowUpdateSql(this);
				}
			}
		}
		return recordUpdateSql;
	}

	private volatile ModifySql rowDeleteSql;

	final ModifySql getRowDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ModifySql recordDeleteSql = this.rowDeleteSql;
		if (recordDeleteSql == null) {
			synchronized (this) {
				recordDeleteSql = this.rowDeleteSql;
				if (recordDeleteSql == null) {
					this.rowDeleteSql = recordDeleteSql = dbAdapter.dbMetadata.getRowDeleteSql(this);
				}
			}
		}
		return recordDeleteSql;
	}
}