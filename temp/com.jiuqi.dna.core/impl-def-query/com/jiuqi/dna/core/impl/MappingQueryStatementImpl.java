package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDeclare;
import com.jiuqi.dna.core.def.query.ORMDeclarator;
import com.jiuqi.dna.core.exception.NamedDefineExistingException;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByLpkDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByLpkQuerySql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByRecidDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByRecidQuerySql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByRecidsDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjRecverDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjRecverUpdateSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.statement.UnsupportedAssignmentException;
import com.jiuqi.dna.core.misc.ObjectBuilder;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.type.AssignCapability;

/**
 * 带对象映射的查询,即每个查询输出列绑定到一个java实体的属性上.
 * 
 * @author houchunlei
 * 
 */
public final class MappingQueryStatementImpl extends QueryStatementBase
		implements MappingQueryStatementDeclare, Declarative<ORMDeclarator<?>> {

	public final ORMDeclarator<?> getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.ORM;
	}

	public final StructDefineImpl getMappingTarget() {
		return this.mapping;
	}

	public final void setAutoBind(boolean isAutoBind) {
		this.isAutoBind = isAutoBind;
	}

	public final boolean isAutoBind() {
		return this.isAutoBind;
	}

	@Override
	public final String getXMLTagName() {
		return xml_tag;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.maskTrue(xml_attr_autobind, this.isAutoBind);
	}

	@Override
	public final MappingQueryStatementImpl clone() {
		MappingQueryStatementImpl target = new MappingQueryStatementImpl(this.name, this.mapping);
		super.cloneSelectTo(target, this);
		return target;
	}

	final static String xml_tag = "orm-query";
	final static String xml_attr_autobind = "auto-bind";

	/**
	 * 是否自动榜定
	 */
	private boolean isAutoBind = true;

	final ORMDeclarator<?> declarator;

	public MappingQueryStatementImpl(String name, Class<?> soClass) {
		this(name, DataTypeBase.getStaticStructDefine(soClass));
	}

	public MappingQueryStatementImpl(String name, Class<?> soClass,
			ORMDeclarator<?> declarator) {
		this(name, DataTypeBase.getStaticStructDefine(soClass), declarator);
	}

	public MappingQueryStatementImpl(String name, StructDefineImpl target) {
		this(name, target, null);
	}

	public MappingQueryStatementImpl(String name, StructDefineImpl target,
			ORMDeclarator<?> declarator) {
		super(name);
		if (target == null) {
			throw new NullPointerException();
		}
		this.mapping = target;
		this.declarator = declarator;
	}

	@Override
	protected final QueryColumnImpl newColumnOnly(String name, String alias,
			ValueExpr expr) {
		QueryColumnImpl column = new QueryColumnImpl(this, name, alias, expr);
		StructFieldDefineImpl field = this.mapping.fields.get(name);
		AssignCapability ac = field.getType().isAssignableFrom(expr.getType());
		if (ac == AssignCapability.NO) {
			switch (ContextVariableIntl.isStrictAssignType()) {
			case 1:
				System.err.println(UnsupportedAssignmentException.message(this, column, field));
				break;
			case 2:
				new UnsupportedAssignmentException(this, column, field).printStackTrace();
				break;
			case 3:
				throw new UnsupportedAssignmentException(this, column, field);
			}
		}
		column.field = field;
		return column;
	}

	/**
	 * 构造映射的对象
	 * 
	 * @param <TEntity>
	 * 
	 * @param entityFactory
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final <TEntity> TEntity newEntity(ObjectBuilder<TEntity> entityFactory) {
		if (entityFactory != null) {
			TEntity result;
			try {
				result = entityFactory.build();
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
			if (result != null) {
				if (!this.mapping.soClass.isInstance(result)) {
					throw new IllegalArgumentException("实体工厂产生的实体类型不符");
				}
				return result;
			}
		}
		return (TEntity) this.mapping.newEmptySO();
	}

	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.isAutoBind = element.getBoolean(xml_attr_autobind, this.isAutoBind);
	}

	@Override
	final void doPrepare() {
		super.doPrepare();
		if (this.isAutoBind) {
			this.tryMapRest();
		}
		this.objRecveredDeleteSql = null;
		this.objRecveredUpdateSql = null;
		this.objIdDeleteSql = null;
		this.objIdsDeleteSql = null;
		this.objLpkDeleteSql = null;
		this.objIdQuerySql = null;
		this.objLpkQuerySql = null;
	}

	private final StringKeyMap<QueryColumnImpl> getMappedField() {
		StringKeyMap<QueryColumnImpl> mapped = new StringKeyMap<QueryColumnImpl>(false, this.mapping.fields.size());
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			final QueryColumnImpl column = this.columns.get(i);
			final String javaFieldName = column.field.name;
			try {
				mapped.put(javaFieldName, column, true);
			} catch (NamedDefineExistingException e) {
				QueryColumnImpl bounded = mapped.find(javaFieldName);
				throw new IllegalArgumentException("在绑定查询定义[" + this.name + "]中，实体字段[" + javaFieldName + "]已经绑定到查询输出列[" + bounded.name + "]，不允许再将查询输出列[" + column.name + "]绑定到该实体字段上。");
			}
		}
		return mapped;
	}

	private final void tryMapRest() {
		StringKeyMap<QueryColumnImpl> mapped = this.getMappedField();
		if (mapped.size() == this.mapping.fields.size()) {
			return;
		}
		next: for (int i = 0, c = this.mapping.fields.size(); i < c; i++) {
			StructFieldDefineImpl javaField = this.mapping.fields.get(i);
			// 还未绑定字段
			if (mapped.containsKey(javaField.name)) {
				continue;
			}
			for (QuRelationRef relaitonRef : this.rootRelationRef()) {
				if (!(relaitonRef instanceof QuTableRef)) {
					continue;
				}
				QuTableRef tableRef = (QuTableRef) relaitonRef;
				// 该表引用目标是否有同名的表字段
				TableFieldDefineImpl tf = tableRef.getTarget().fields.find(javaField.name);
				if (tf != null) {
					AssignCapability ac = javaField.type.isAssignableFrom(tf.getType());
					// 该字段类型能转换到java字段类型
					if (ac == AssignCapability.NO) {
						continue;
					}
					// 且该引用下的该字段还未输出
					if (this.findColumn(tableRef, tf) == null) {
						// 输出并绑定
						QueryColumnImpl column = this.newColumn(javaField.name, tableRef.expOf(tf));
						mapped.put(javaField.name, column);
						continue next;
					}
				}
			}
		}
	}

	static final IllegalStatementDefineException rootModifyNotSupported(
			MappingQueryStatementImpl statement) {
		return new IllegalStatementDefineException(statement, "ORM[" + statement.name + "]的根引用不支持更新。");
	}

	public static final void fillLpkWhere(ISqlExprBuffer where, String alias,
			TableFieldDefineImpl[] fields, ArgumentPlaceholder[] args) {
		for (int i = 0; i < fields.length; i++) {
			where.loadColumnRef(alias, fields[i].namedb());
			where.loadParam(args[i]);
			where.eq();
		}
		where.and(args.length);
	}

	private volatile ObjRecverDeleteSql objRecveredDeleteSql;

	final ObjRecverDeleteSql getObjRecveredDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ObjRecverDeleteSql objRecveredDeleteSql = this.objRecveredDeleteSql;
		if (objRecveredDeleteSql == null) {
			synchronized (this) {
				objRecveredDeleteSql = this.objRecveredDeleteSql;
				if (objRecveredDeleteSql == null) {
					this.objRecveredDeleteSql = objRecveredDeleteSql = new ObjRecverDeleteSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return objRecveredDeleteSql;
	}

	private volatile ObjRecverUpdateSql objRecveredUpdateSql;

	final ObjRecverUpdateSql getObjRecveredUpdateSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ObjRecverUpdateSql objRecveredUpdateSql = this.objRecveredUpdateSql;
		if (objRecveredUpdateSql == null) {
			synchronized (this) {
				objRecveredUpdateSql = this.objRecveredUpdateSql;
				if (objRecveredUpdateSql == null) {
					this.objRecveredUpdateSql = objRecveredUpdateSql = new ObjRecverUpdateSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return objRecveredUpdateSql;
	}

	private volatile ObjByRecidDeleteSql objIdDeleteSql;

	final ObjByRecidDeleteSql getByRecidDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ObjByRecidDeleteSql byIdDeleteSql = this.objIdDeleteSql;
		if (byIdDeleteSql == null) {
			synchronized (this) {
				byIdDeleteSql = this.objIdDeleteSql;
				if (byIdDeleteSql == null) {
					this.objIdDeleteSql = byIdDeleteSql = new ObjByRecidDeleteSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return byIdDeleteSql;
	}

	private volatile ObjByRecidsDeleteSql objIdsDeleteSql;

	final ObjByRecidsDeleteSql getByRecidsDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ObjByRecidsDeleteSql byIdsDeleteSql = this.objIdsDeleteSql;
		if (byIdsDeleteSql == null) {
			synchronized (this) {
				byIdsDeleteSql = this.objIdsDeleteSql;
				if (byIdsDeleteSql == null) {
					this.objIdsDeleteSql = byIdsDeleteSql = new ObjByRecidsDeleteSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return byIdsDeleteSql;
	}

	private volatile ObjByLpkDeleteSql objLpkDeleteSql;

	final ObjByLpkDeleteSql getByLpkDeleteSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ObjByLpkDeleteSql byLpkDeleteSql = this.objLpkDeleteSql;
		if (byLpkDeleteSql == null) {
			synchronized (this) {
				byLpkDeleteSql = this.objLpkDeleteSql;
				if (byLpkDeleteSql == null) {
					this.objLpkDeleteSql = byLpkDeleteSql = new ObjByLpkDeleteSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return byLpkDeleteSql;
	}

	private volatile ObjByRecidQuerySql objIdQuerySql;

	final ObjByRecidQuerySql getByRecidQuerySql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ObjByRecidQuerySql byIdQuerySql = this.objIdQuerySql;
		if (byIdQuerySql == null) {
			synchronized (this) {
				byIdQuerySql = this.objIdQuerySql;
				if (byIdQuerySql == null) {
					this.objIdQuerySql = byIdQuerySql = new ObjByRecidQuerySql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return byIdQuerySql;
	}

	private volatile ObjByLpkQuerySql objLpkQuerySql;

	final ObjByLpkQuerySql getByLpkQuerySql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ObjByLpkQuerySql byLpkQuerySql = this.objLpkQuerySql;
		if (byLpkQuerySql == null) {
			synchronized (this) {
				byLpkQuerySql = this.objLpkQuerySql;
				if (byLpkQuerySql == null) {
					this.objLpkQuerySql = byLpkQuerySql = new ObjByLpkQuerySql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return byLpkQuerySql;
	}

	// i must say i dont like this guy
}