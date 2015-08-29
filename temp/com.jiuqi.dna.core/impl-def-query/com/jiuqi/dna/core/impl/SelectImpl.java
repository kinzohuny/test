package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.ModifiableContainer;
import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.exp.RelationColumnRefExpr;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.query.DerivedQueryDefine;
import com.jiuqi.dna.core.def.query.GroupByItemDeclare;
import com.jiuqi.dna.core.def.query.GroupByType;
import com.jiuqi.dna.core.def.query.OrderByItemDeclare;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.def.query.RelationDefine;
import com.jiuqi.dna.core.def.query.SelectDeclare;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sql.render.EFilter;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;

/**
 * �����ѯ�ṹ����.
 * 
 * @param <TSelect>
 *            ��ѯ����ʵ����
 * @param <TColumn>
 *            ��ѯ�����ʵ����
 * 
 * @author houchunlei
 */
public abstract class SelectImpl<TSelect extends SelectImpl<TSelect, TColumn>, TColumn extends SelectColumnImpl<TSelect, TColumn>>
		extends NamedDefineImpl implements SelectDeclare, Relation,
		RelationRefDomain, RelationRefBuildableIntrl, OMVisitable {

	@Override
	protected boolean isNameCaseSensitive() {
		return false;
	}

	@Deprecated
	public final QuRelationRef findReference(String name) {
		return this.findRelationRef(name);
	}

	@Deprecated
	public final QuRelationRef getReference(String name) {
		return this.getRelationRef(name);
	}

	public final QuRelationRef findRelationRef(String name) {
		QuRootRelationRef rootRef = this.rootRelationRef;
		if (rootRef == null) {
			return null;
		} else if (rootRef.getName().equalsIgnoreCase(name)) {
			return rootRef;
		}
		return this.refs.find(name);
	}

	public final QuRelationRef getRelationRef(String name) {
		QuRelationRef relationRef = this.findRelationRef(name);
		if (relationRef == null) {
			throw missingRelationRef(name);
		}
		return relationRef;
	}

	public final RelationRef findRelationRefRecursively(String name) {
		RelationRef relationRef = this.findRelationRef(name);
		if (relationRef != null) {
			return relationRef;
		}
		for (RelationRefDomain parent = this.getDomain(); parent != null; parent = parent.getDomain()) {
			relationRef = parent.findRelationRef(name);
			if (relationRef != null) {
				return relationRef;
			}
		}
		return null;
	}

	public final RelationRef getRelationRefRecursively(String name) {
		RelationRef relationRef = this.findRelationRefRecursively(name);
		if (relationRef == null) {
			throw missingRelationRef(name);
		}
		return relationRef;
	}

	public final QuRelationRef getRootReference() {
		return this.rootRelationRef;
	}

	public final Iterable<QuRelationRef> getReferences() {
		if (this.rootRelationRef == null) {
			return emptyItrable;
		} else {
			return new Iterable<QuRelationRef>() {
				public Iterator<QuRelationRef> iterator() {
					return SelectImpl.this.rootRelationRef.iterator();
				}
			};
		}
	}

	public final QuRootTableRef newReference(TableDefine table) {
		if (table == null) {
			throw new NullArgumentException("����");
		}
		return this.newTableRef(table.getName(), (TableDefineImpl) table);
	}

	public final QuRootTableRef newReference(TableDeclarator table) {
		if (table == null) {
			throw new NullArgumentException("����");
		}
		TableDefineImpl t = (TableDefineImpl) table.getDefine();
		return this.newTableRef(t.name, t);
	}

	public final QuRootTableRef newReference(TableDefine table, String name) {
		return this.newTableRef(name, (TableDefineImpl) table);
	}

	public final QuRootTableRef newReference(TableDeclarator table, String name) {
		return this.newTableRef(name, (TableDefineImpl) table.getDefine());
	}

	public final DerivedQueryImpl newDerivedQuery() {
		return new DerivedQueryImpl(this);
	}

	public final QuRootQueryRef newReference(DerivedQueryDefine query) {
		if (query == null) {
			throw new NullArgumentException("��ѯ�ṹ");
		}
		DerivedQueryImpl dq = (DerivedQueryImpl) query;
		return this.newQueryRef(dq.name, dq);
	}

	public final QuRootQueryRef newReference(DerivedQueryDefine query,
			String name) {
		if (query == null) {
			throw new NullArgumentException("��ѯ�ṹ");
		}
		return this.newQueryRef(name, (DerivedQueryImpl) query);
	}

	public final RelationColumnRefImpl expOf(RelationColumnDefine column) {
		if (column == null) {
			throw new NullArgumentException("��ϵ�ж���");
		}
		return this.exprOf(column);
	}

	public final RelationColumnRefExpr expOf(String column) {
		if (column == null) {
			throw new NullArgumentException("��ϵ������");
		}
		if (this.rootRelationRef == null) {
			throw new UnsupportedOperationException("δ�����κι�ϵ����");
		}
		for (QuRelationRef relationRef : this.rootRelationRef) {
			RelationColumn c = relationRef.getTarget().findColumn(column);
			if (c != null) {
				return relationRef.expOf(c);
			}
		}
		throw new UnsupportedOperationException("�Ҳ���ָ�����ƵĹ�ϵ��");
	}

	public final boolean getDistinct() {
		return this.distinct;
	}

	public final void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public final NamedDefineContainerImpl<TColumn> getColumns() {
		return this.columns;
	}

	public final TColumn findColumn(String columnName) {
		return this.columns.find(columnName);
	}

	public final TColumn getColumn(String columnName) {
		return this.columns.get(columnName);
	}

	public final TColumn newColumn(RelationColumnDefine column) {
		if (column == null) {
			throw new NullArgumentException("��ϵ�ж���");
		}
		return this.newColumn(column, column.getName());
	}

	public final TColumn newColumn(RelationColumnDefine column, String name) {
		if (column == null) {
			throw new NullArgumentException("��ϵ�ж���");
		}
		return this.newColumn(name, this.exprOf(column));
	}

	public final TColumn newColumn(ValueExpression expr) {
		if (expr == null) {
			throw new NullArgumentException("����б��ʽ");
		}
		final String gen = this.generateColumnName();
		return this.newColumn(gen, (ValueExpr) expr);
	}

	public final TColumn newColumn(ValueExpression expr, String name) {
		if (expr == null) {
			throw new NullArgumentException("��ѯ�б��ʽ");
		}
		return this.newColumn(name, (ValueExpr) expr);
	}

	public final ConditionalExpr getCondition() {
		return this.where;
	}

	public final void setCondition(ConditionalExpression condition) {
		if (condition == null) {
			this.where = null;
		} else {
			ConditionalExpr where = (ConditionalExpr) condition;
			if (ContextVariableIntl.isStrictExprDomain()) {
				where.checkDomain(this);
			}
			this.where = where;
		}
	}

	public final GroupByType getGroupByType() {
		return this.groupbyType;
	}

	@SuppressWarnings("deprecation")
	public final void setGroupByType(GroupByType type) {
		if (type == null) {
			throw new NullArgumentException("�����������");
		}
		if (type == GroupByType.CUBE) {
			if (ContextVariableIntl.FORBIDE_CUBE_GROUPBY) {
				throw new CubeGroupbyNotSupportedException();
			} else {
				System.err.println(CubeGroupbyNotSupportedException.message());
			}
		}
		this.groupbyType = type;
	}

	public final MetaBaseContainerImpl<? extends GroupByItemImpl> getGroupBys() {
		return this.groupbys;
	}

	public final GroupByItemImpl newGroupBy(ValueExpression expr) {
		if (expr == null) {
			throw new NullArgumentException("������ʽ");
		}
		ValueExpr value = (ValueExpr) expr;
		if (ContextVariableIntl.isStrictExprDomain()) {
			value.checkDomain(this);
		}
		GroupByItemImpl groupby = new GroupByItemImpl(this, value);
		return this.addGroupByNoCheck(groupby);
	}

	public final GroupByItemDeclare newGroupBy(RelationColumnDefine column) {
		if (column == null) {
			throw new NullArgumentException("��ϵ�ж���");
		}
		GroupByItemImpl groupby = new GroupByItemImpl(this, this.exprOf(column));
		return this.addGroupByNoCheck(groupby);
	}

	public final ConditionalExpression getHaving() {
		return this.having;
	}

	public final void setHaving(ConditionalExpression condition) {
		if (condition == null) {
			this.having = null;
		} else {
			ConditionalExpr having = (ConditionalExpr) condition;
			if (ContextVariableIntl.isStrictExprDomain()) {
				having.checkDomain(this);
			}
			this.having = having;
		}
	}

	public final SubQueryImpl newSubQuery() {
		return new SubQueryImpl(this);
	}

	public final void union(DerivedQueryDefine query) {
		if (query == null) {
			throw new NullArgumentException("union��ѯ�ṹ");
		}
		this.union((DerivedQueryImpl) query, false);
	}

	public final void unionAll(DerivedQueryDefine query) {
		if (query == null) {
			throw new NullArgumentException("union��ѯ�ṹ");
		}
		this.union((DerivedQueryImpl) query, true);
	}

	public final MetaBaseContainerImpl<SetOperateImpl> getSetOperates() {
		return this.sets;
	}

	@Deprecated
	public ModifiableContainer<? extends OrderByItemDeclare> getOrderBys() {
		if (ContextVariableIntl.FORBIDE_SUBSELECT_ORDERBY) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			System.err.println(SubselectOrderbyNotSupportedException.message());
		}
		return null;
	}

	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column) {
		if (ContextVariableIntl.FORBIDE_SUBSELECT_ORDERBY) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			System.err.println(SubselectOrderbyNotSupportedException.message());
		}
		return null;
	}

	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc) {
		if (ContextVariableIntl.FORBIDE_SUBSELECT_ORDERBY) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			System.err.println(SubselectOrderbyNotSupportedException.message());
		}
		return null;
	}

	@Deprecated
	public OrderByItemDeclare newOrderBy(ValueExpression value) {
		if (ContextVariableIntl.FORBIDE_SUBSELECT_ORDERBY) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			System.err.println(SubselectOrderbyNotSupportedException.message());
		}
		return null;
	}

	@Deprecated
	public OrderByItemDeclare newOrderBy(ValueExpression value, boolean isDesc) {
		if (ContextVariableIntl.FORBIDE_SUBSELECT_ORDERBY) {
			throw new SubselectOrderbyNotSupportedException();
		} else {
			System.err.println(SubselectOrderbyNotSupportedException.message());
		}
		return null;
	}

	private static final Iterable<QuRelationRef> emptyItrable = new Iterable<QuRelationRef>() {
		public Iterator<QuRelationRef> iterator() {
			return emptyIterator;
		}
	};

	private static final Iterator<QuRelationRef> emptyIterator = new Iterator<QuRelationRef>() {

		public boolean hasNext() {
			return false;
		}

		public QuRelationRef next() {
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new NoSuchElementException();
		}
	};

	/**
	 * ��һ��������ϵ����
	 */
	private QuRootRelationRef rootRelationRef;

	/**
	 * ��ǰ��ѯ�µĹ�ϵ���ù�ϣ��
	 */
	public final StringKeyMap<QuRelationRef> refs = new StringKeyMap<QuRelationRef>(false);

	/**
	 * ��ѯ����
	 */
	ConditionalExpr where;

	/**
	 * ����Լ������
	 */
	ConditionalExpr having;

	/**
	 * �������
	 */
	MetaBaseContainerImpl<GroupByItemImpl> groupbys;

	/**
	 * ��������
	 */
	GroupByType groupbyType = GroupByType.DEFAULT;

	/**
	 * �Ƿ��ų��ظ���
	 */
	boolean distinct;

	/**
	 * ��ѯ�����
	 */
	public final NamedDefineContainerImpl<TColumn> columns = new NamedDefineContainerImpl<TColumn>(false);

	/**
	 * ��������
	 */
	MetaBaseContainerImpl<SetOperateImpl> sets;

	/**
	 * ʹ��ָ�����ƹ����ѯ����
	 * 
	 * @param name
	 */
	SelectImpl(String name) {
		super(name);
	}

	/**
	 * ���ص�һ�������Ĺ�ϵ����
	 */
	public final QuRootRelationRef rootRelationRef() {
		return this.rootRelationRef;
	}

	static final MissingDefineException missingRelationRef(String name) {
		return new MissingDefineException("����������Ϊ[" + name + "]�Ĺ�ϵ���ö���");
	}

	private final String ensureRefName(String name) {
		if (this.refs.containsKey(name)) {
			return NameUtl.buildIdentityName(name, 0, new Filter<String>() {
				public boolean accept(String item) {
					return SelectImpl.this.refs.containsKey(item);
				}
			});
		}
		return name;
	}

	/**
	 * ���Ӹ����ı�����
	 * 
	 * @param name
	 * @param table
	 * @return
	 */
	final QuRootTableRef newTableRef(String name, TableDefineImpl table) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("������");
		}
		if (table == null) {
			throw new NullArgumentException("����");
		}
		if (this.rootRelationRef == null) {
			QuRootTableRef tableRef = new QuRootTableRef(this, name, table, null);
			if (this instanceof QueryStatementBase) {
				tableRef.setForUpdate(true);
			}
			this.rootRelationRef = tableRef;
			this.refs.put(name, tableRef);
			return tableRef;
		} else {
			name = this.ensureRefName(name);
			QuRootRelationRef last = this.rootRelationRef.last();
			QuRootTableRef tableRef = new QuRootTableRef(this, name, table, last);
			last.setNext(tableRef);
			this.refs.put(name, tableRef, true);
			return tableRef;
		}
	}

	/**
	 * ���Ӹ����Ĳ�ѯ����
	 * 
	 * @param name
	 * @param dq
	 * @return
	 */
	final QuRootQueryRef newQueryRef(String name, DerivedQueryImpl dq) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("������");
		}
		if (dq == null) {
			throw new NullArgumentException("��ѯ�ṹ����");
		}
		if (ContextVariableIntl.isStrictExprDomain()) {
			dq.checkDomain(this);
		}
		if (this.rootRelationRef == null) {
			QuRootQueryRef queryRef = new QuRootQueryRef(this, name, dq, null);
			this.rootRelationRef = queryRef;
			this.refs.put(name, queryRef);
			return queryRef;
		} else {
			name = this.ensureRefName(name);
			QuRootRelationRef last = this.rootRelationRef.last();
			QuRootQueryRef queryRef = new QuRootQueryRef(this, name, dq, last);
			last.setNext(queryRef);
			this.refs.put(name, queryRef, true);
			return queryRef;
		}
	}

	/**
	 * �����ϵ�����ñ��ʽ
	 * 
	 * <p>
	 * �ӵ�һ��ָ��ù�ϵ�����ڹ�ϵԪ����������й���
	 * 
	 * @param column
	 * @return
	 */
	final RelationColumnRefImpl exprOf(RelationColumnDefine column) {
		RelationDefine relation = column.getOwner();
		if (relation == this.rootRelationRef.getTarget()) {
			return this.rootRelationRef.expOf(column);
		} else {
			for (QuRelationRef relationRef : this.rootRelationRef) {
				if (relationRef.getTarget() == relation) {
					return relationRef.expOf(column);
				}
			}
		}
		throw new MissingDefineException("������ָ���ϵ[" + relation.getName() + "]�Ĺ�ϵ����.");
	}

	final void appendAndCondition(ConditionalExpr condition) {
		if (this.where == null) {
			this.where = condition;
		} else {
			this.where = this.where.and(condition);
		}
	}

	private final String generateColumnName() {
		return "c".concat(String.valueOf(this.columns.size()));
	}

	/**
	 * ��������ж���
	 * 
	 * @param name
	 *            ��ѯ������,�ظ�ʱ��������
	 * @param expr
	 *            ��ѯ�еı��ʽ
	 * @return
	 */
	final TColumn newColumn(String name, ValueExpr expr) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("��ѯ�еı���");
		}
		if (expr == null) {
			throw new NullArgumentException("��ѯ�еı��ʽ");
		}
		if (this.columns.find(name) != null) {
			if (this.columnNameDetector == null) {
				this.columnNameDetector = new ColumnNameDetector();
			}
			name = NameUtl.buildIdentityName(name, 0, this.columnNameDetector);
		}

		if (ContextVariableIntl.isStrictExprDomain()) {
			expr.checkDomain(this);
		}
		TColumn column = this.newColumnOnly(name, this.generateColumnAlias(name), expr);
		this.columns.add(column);
		this.aliases.put(column.alias, column);
		return column;
	}

	private ColumnNameDetector columnNameDetector;

	private final class ColumnNameDetector implements Filter<String> {

		public boolean accept(String item) {
			return SelectImpl.this.columns.contains(item);
		}
	}

	private StringKeyMap<TColumn> aliases = new StringKeyMap<TColumn>(false);

	private final String generateColumnAlias(String name) {
		if (this.aliases.containsKey(name) || name.length() > 30) {
			if (this.columnAliasDetector == null) {
				this.columnAliasDetector = new ColumnAliasDetector();
			}
			name = NameUtl.buildIdentityName(name, 30, this.columnAliasDetector);
		}
		return name;
	}

	private ColumnAliasDetector columnAliasDetector;

	private final class ColumnAliasDetector implements Filter<String> {

		public boolean accept(String item) {
			return SelectImpl.this.aliases.containsKey(item) || item.length() > 30;
		}

	}

	protected abstract TColumn newColumnOnly(String name, String alias,
			ValueExpr expr);

	/**
	 * ���ݹ�ϵ���ü���ϵ�в��Ҳ�ѯ�ж���
	 * 
	 * @param relationRef
	 * @param column
	 * @return
	 */
	public final TColumn findColumn(RelationRef relationRef,
			RelationColumn column) {
		if (!(relationRef instanceof QuRelationRef)) {
			return null;
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			TColumn qc = this.columns.get(i);
			if (qc.value() instanceof RelationColumnRefExpr) {
				RelationColumnRefExpr columnRef = (RelationColumnRefExpr) qc.value();
				if (columnRef.getReference() == relationRef && columnRef.getColumn() == column) {
					return qc;
				}
			}
		}
		return null;
	}

	/**
	 * ���Һ�ָ�������õ�ָ���ֶεȼ۵Ĳ�ѯ��
	 */
	public final TColumn findEqualColumn(QuRelationRef relationRef,
			RelationColumn column) {
		TColumn qc = this.findColumn(relationRef, column);
		if (qc != null) {
			return qc;
		}
		EqualColumnRefDetector detector = new EqualColumnRefDetector();
		detector.relationRef = relationRef;
		detector.column = column;
		return this.findFirstEqualColumn0(detector);
	}

	private final TColumn findFirstEqualColumn0(EqualColumnRefDetector detector) {
		TColumn column;
		int start = detector.list.size();
		for (QuRelationRef relationRef : this.rootRelationRef) {
			relationRef.visit(detector, null);
		}
		if (this.where != null) {
			this.where.visit(detector, null);
		}
		int eSize = detector.list.size();
		for (int i = start; i < eSize; i++) {
			RelationColumnRefImpl columnRef = detector.list.get(i);
			if ((column = this.findColumn(columnRef.getReference(), columnRef.getColumn())) != null) {
				return column;
			}
		}
		// �������ҵȼ�������
		for (int i = start; i < eSize; i++) {
			RelationColumnRefImpl columnRef = detector.list.get(i);
			detector.relationRef = columnRef.getReference();
			detector.column = columnRef.getColumn();
			if ((column = this.findFirstEqualColumn0(detector)) != null) {
				return column;
			}
		}
		return null;
	}

	private static final class EqualColumnRefDetector extends
			TraversedExprVisitor<Object> {

		ArrayList<RelationColumnRefImpl> list = new ArrayList<RelationColumnRefImpl>();

		private RelationRef relationRef;
		private RelationColumn column;

		@Override
		public void visitPredicateExpr(PredicateExpr predicate, Object context) {
			if (predicate.isEqualsPredicate() && predicate.values[0] instanceof TableFieldRefImpl && predicate.values[1] instanceof TableFieldRefImpl) {
				TableFieldRefImpl fr0 = (TableFieldRefImpl) predicate.values[0];
				TableFieldRefImpl fr1 = (TableFieldRefImpl) predicate.values[1];
				if (fr0.tableRef == this.relationRef && fr0.field == this.column) {
					if (this.list.contains(fr1)) {
						this.list.add(fr1);
					}
				} else if (fr1.tableRef == this.relationRef && fr1.field == this.column) {
					if (this.list.contains(fr0)) {
						this.list.add(fr0);
					}
				}
			}
			super.visitPredicateExpr(predicate, context);
		}

	}

	final TColumn findRootRecidColumn() {
		QuRootRelationRef root = this.rootRelationRef;
		if (root != null && root instanceof QuRootTableRef) {
			return this.findColumn(root, ((QuRootTableRef) root).target.f_recid);
		} else {
			throw new IllegalArgumentException();
		}
	}

	final TColumn findRootRecverColumn() {
		QuRootRelationRef root = this.rootRelationRef;
		if (root != null && root instanceof QuRootTableRef) {
			return this.findColumn(root, ((QuRootTableRef) root).target.f_recver);
		} else {
			throw new IllegalArgumentException();
		}
	}

	final DataTypeInternal tryGetColumnFirstNonNullType(int index) {
		DataTypeInternal type = this.columns.get(index).getRecordType();
		if (type != NullType.TYPE) {
			return type;
		} else if (this.sets != null) {
			for (int i = 0, c = this.sets.size(); i < c; i++) {
				type = this.sets.get(i).target.tryGetColumnFirstNonNullType(index);
				if (type != NullType.TYPE) {
					return type;
				}
			}
		}
		return null;
	}

	private final GroupByItemImpl addGroupByNoCheck(GroupByItemImpl groupby) {
		if (this.groupbys == null) {
			this.groupbys = new MetaBaseContainerImpl<GroupByItemImpl>();
		}
		this.groupbys.add(groupby);
		return groupby;
	}

	private final SetOperateImpl union(DerivedQueryImpl query, boolean all) {
		if (query.isWith) {
			throw new UnsupportedOperationException("With���岻������union��");
		}
		if (ContextVariableIntl.isStrictExprDomain() && query.owner != this) {
			throw new UnsupportedOperationException("������ѯ�����ʹ�������");
		}
		if (this.sets == null) {
			this.sets = new MetaBaseContainerImpl<SetOperateImpl>();
		}
		SetOperateImpl so = new SetOperateImpl(this, query, all);
		this.sets.add(so);
		return so;
	}

	public final void validate() {
		if (this.rootRelationRef == null) {
			throw new UnsupportedOperationException("��ѯ�ṹ[" + this.name + "]δ�����κι�ϵ���á�");
		}
		if (this.columns.size() == 0) {
			throw new UnsupportedOperationException("��ѯ�ṹ[" + this.name + "]δ�����κ�����С�");
		}
		for (QuRelationRef relationRef : this.rootRelationRef) {
			relationRef.validate();
		}
	}

	public final void validateSingleRoot() {
		if (this.rootRelationRef.next() != null) {
			throw new UnsupportedOperationException("��ѯ�����˶��������ϵ����");
		}
	}

	/**
	 * ����ǰ��ѯ����ṹ��¡��Ŀ���ѯ����
	 * 
	 * @param target
	 * @param args
	 */
	final void cloneSelectTo(SelectImpl<?, ?> target, ArgumentableDefine args) {
		if (this.rootRelationRef != null) {
			this.rootRelationRef.cloneTo(target, args);
		}
		target.setDistinct(this.distinct);
		if (this.where != null) {
			target.setCondition(this.where.clone(target, args));
		}
		if (this.groupbys != null) {
			for (int i = 0, c = this.groupbys.size(); i < c; i++) {
				this.groupbys.get(i).cloneTo(target, args);
			}
		}
		target.groupbyType = this.groupbyType;
		if (this.having != null) {
			target.setHaving(this.having.clone(target, args));
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			this.columns.get(i).cloneTo(target, args);
		}
		if (this.sets != null) {
			for (int i = 0, c = this.sets.size(); i < c; i++) {
				this.sets.get(i).cloneTo(target, args);
			}
		}
	}

	public <TContext> void visit(OMVisitor<TContext> visitor, TContext context) {
		visitor.visitSelect(this, context);
	}

	public final void renderFullSelect(ISqlSelectBuffer buffer,
			TableUsages usages) {
		this.renderFullSelect(buffer, usages, null);
	}

	public final void renderFullSelect(ISqlSelectBuffer buffer,
			TableUsages usages, BitSet filtered) {
		this.renderFrom(buffer, usages);
		this.renderWhere(buffer, usages);
		this.renderGroupby(buffer, usages);
		this.renderHaving(buffer, usages);
		this.renderSelect(buffer, usages, filtered);
		this.renderUnion(buffer, usages, filtered);
	}

	public final <TContext> void renderFullSelect(ISqlSelectBuffer buffer,
			TableUsages usages,
			EFilter<SelectColumnImpl<?, ?>, TContext> filter, TContext context) {
		this.renderFrom(buffer, usages);
		this.renderWhere(buffer, usages);
		this.renderGroupby(buffer, usages);
		this.renderHaving(buffer, usages);
		this.renderUnion(buffer, usages, this.renderSelect(buffer, usages, filter, context));
	}

	public final void renderFrom(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.rootRelationRef() != null) {
			this.rootRelationRef().render(buffer, usages);
		}
	}

	public final void renderWhere(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.where != null) {
			this.where.render(buffer.where(), usages);
		}
	}

	public final void renderGroupby(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.groupbys != null) {
			for (int i = 0, c = this.groupbys.size(); i < c; i++) {
				this.groupbys.get(i).value().render(buffer.groupby(), usages);
			}
		}
		if (this.groupbyType == GroupByType.ROLL_UP) {
			buffer.rollup();
		}
	}

	public final void renderHaving(ISqlSelectBuffer buffer, TableUsages usages) {
		if (this.having != null) {
			this.having.render(buffer.having(), usages);
		}
	}

	public final void renderSelect(ISqlSelectBuffer buffer, TableUsages usages) {
		this.renderSelect(buffer, usages, null);
	}

	/**
	 * @param filtered
	 *            ���˵������.
	 */
	public final void renderSelect(ISqlSelectBuffer buffer, TableUsages usages,
			BitSet filtered) {
		if (this.distinct) {
			buffer.distinct();
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			if (filtered == null || !filtered.get(i)) {
				this.renderSelectColumn(buffer, usages, i);
			}
		}
	}

	/**
	 * @return ���ر����˵������.
	 */
	public final <TContext> BitSet renderSelect(ISqlSelectBuffer buffer,
			TableUsages usages,
			EFilter<SelectColumnImpl<?, ?>, TContext> filter, TContext context) {
		if (this.distinct) {
			buffer.distinct();
		}
		BitSet filtered = new BitSet();
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			SelectColumnImpl<?, ?> column = this.columns.get(i);
			if (filter != null && filter.accept(column, context)) {
				filtered.set(i);
			} else {
				this.renderSelectColumn(buffer, usages, i);
			}
		}
		return filtered;
	}

	public final void renderSelectColumn(ISqlSelectBuffer buffer,
			TableUsages usages, int i) {
		SelectColumnImpl<?, ?> column = this.columns.get(i);
		if (column.value() == NullExpr.NULL) {
			DataTypeInternal tryGet = this.tryGetColumnFirstNonNullType(i);
			if (tryGet == null) {
				buffer.newColumn(column.alias).loadNull(StringType.TYPE);
			} else {
				buffer.newColumn(column.alias).loadNull(tryGet);
			}
		} else {
			column.value().render(buffer.newColumn(column.alias), usages);
		}
	}

	public final void renderUnion(ISqlSelectBuffer buffer, TableUsages usages) {
		this.renderUnion(buffer, usages, null);
	}

	public final <TContext> void renderUnion(ISqlSelectBuffer buffer,
			TableUsages usages, BitSet filtered) {
		if (this.sets != null) {
			for (int i = 0, c = this.sets.size(); i < c; i++) {
				SetOperateImpl set = this.sets.get(i);
				ISqlSelectBuffer sb = buffer.union(set.all);
				set.render(sb, usages, filtered);
			}
		}
	}
}