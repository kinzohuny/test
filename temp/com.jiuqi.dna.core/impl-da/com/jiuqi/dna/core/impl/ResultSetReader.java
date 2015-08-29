package com.jiuqi.dna.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.misc.ObjectBuilder;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

/**
 * ʹ��StructField��JDBC������Ķ�ȡ��
 * 
 * @author houchunlei
 * 
 */
public abstract class ResultSetReader extends TypeDetectorBase<Object, Object> {

	public final static Object read_no_return = null;
	public final static Object read_only_return = new Object();

	ResultSetReader(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * JDBC�����
	 */
	public ResultSet resultSet;

	/**
	 * ���ֵ��ŵĽṹ�ֶ�
	 */
	protected StructFieldDefineImpl targetField;

	/**
	 * ��ȡ������������
	 */
	protected int columnIndex;

	final static <TEntity> List<TEntity> readEntities(
			ObjectBuilder<TEntity> entityFactory, List<TEntity> to,
			MappingQueryStatementImpl mappingQuery, ResultSet resultSet)
			throws SQLException {
		if (to == null) {
			to = new ArrayList<TEntity>();
		}
		if (!resultSet.next()) {
			return to;
		}
		NamedDefineContainerImpl<QueryColumnImpl> columns = mappingQuery.columns;
		int cSize = columns.size();
		DataType[] types = new DataType[cSize];
		StructFieldDefineImpl[] fields = new StructFieldDefineImpl[cSize];
		for (int i = 0; i < cSize; i++) {
			QueryColumnImpl column = columns.get(i);
			types[i] = column.value().getType();
			fields[i] = column.field;

		}
		ResultSetReader reader = mappingQuery.mapping.newResultSetReader(resultSet);
		do {
			TEntity obj = mappingQuery.newEntity(entityFactory);
			reader.columnIndex = 1;
			reader.setObj(obj);
			for (int i = 0; i < cSize; i++) {
				reader.targetField = fields[i];
				types[i].detect(reader, null);
				reader.columnIndex++;
			}
			to.add(obj);
		} while (resultSet.next());
		return to;
	}

	final static <TEntity> TEntity readNextEntity(
			ObjectBuilder<TEntity> entityFactory,
			MappingQueryStatementImpl mappingQuery, ResultSet resultSet)
			throws SQLException {
		if (!resultSet.next()) {
			return null;
		}
		return mappingQuery.mapping.newResultSetReader(resultSet).readEntity(entityFactory, mappingQuery);
	}

	final <TEntity> TEntity readEntity(ObjectBuilder<TEntity> entityFactory,
			MappingQueryStatementImpl mappingQuery) {
		NamedDefineContainerImpl<QueryColumnImpl> columns = mappingQuery.columns;
		int cSize = columns.size();
		TEntity obj = mappingQuery.newEntity(entityFactory);
		this.setObj(obj);
		this.columnIndex = 1;
		for (int i = 0; i < cSize; i++) {
			QueryColumnImpl column = columns.get(i);
			this.targetField = column.field;
			column.value().getType().detect(this, null);
			this.columnIndex++;
		}
		return obj;
	}

	abstract void setObj(Object obj);
}
