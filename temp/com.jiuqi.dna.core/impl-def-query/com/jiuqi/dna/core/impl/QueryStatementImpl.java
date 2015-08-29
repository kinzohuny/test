package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.query.QueryStatementDeclarator;
import com.jiuqi.dna.core.internal.da.sql.render.RowSaveSql;

/**
 * 查询语句定义实现类
 * 
 * @author houchunlei
 * 
 */
public class QueryStatementImpl extends QueryStatementBase implements
		Declarative<QueryStatementDeclarator> {

	public final QueryStatementDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.QUERY;
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_query_statement;
	}

	@Override
	public final QueryStatementImpl clone() {
		QueryStatementImpl target = new QueryStatementImpl(this.name);
		this.cloneTo(target);
		return target;
	}

	static final String xml_element_query_statement = "query-statement";

	final QueryStatementDeclarator declarator;

	public QueryStatementImpl(String name) {
		super(name);
		this.declarator = null;
	}

	public QueryStatementImpl(String name, QueryStatementDeclarator declarator) {
		super(name);
		this.declarator = declarator;
	}

	public QueryStatementImpl(String name, StructDefineImpl argumentsRef) {
		super(name, argumentsRef);
		this.declarator = null;
	}

	@Override
	protected QueryColumnImpl newColumnOnly(String name, String alias,
			ValueExpr expr) {
		return new QueryColumnImpl(this, name, alias, expr);
	}

	/**
	 * 计算结果集对象的字段类型
	 * 
	 * @param index
	 * @return
	 */
	final DataTypeInternal getStructFieldType(int index) {
		QueryColumnImpl column = this.columns.get(index);
		DataTypeInternal type = column.getRecordType();
		if (type == NullType.TYPE) {
			type = this.tryGetColumnFirstNonNullType(index);
		}
		if (type == null || type == NullType.TYPE) {
			return NullType.TYPE;
		} else if (type instanceof NumberType && column.usingBigDecimal) {
			return RefDataType.bigDecimalType;
		}
		return type;
	}

	final DynObj newRecordObj(int state) {
		DynObj record = new DynObj();
		record.setRecordState(state);
		this.mapping.prepareSONoCheck(record);
		this.mapping.initBinFieldsNullMask(record);
		return record;
	}

	@Override
	final void doPrepare() {
		super.doPrepare();
		this.mapping = new RecordStructDefine(this);
		this.rowSaveSql = null;
	}

	private volatile RowSaveSql rowSaveSql;

	final RowSaveSql getRowSaveSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		RowSaveSql recordSaveSql = this.rowSaveSql;
		if (recordSaveSql == null) {
			synchronized (this) {
				recordSaveSql = this.rowSaveSql;
				if (recordSaveSql == null) {
					this.rowSaveSql = recordSaveSql = new RowSaveSql(dbAdapter.dbMetadata, this);
				}
			}
		}
		return recordSaveSql;
	}
}