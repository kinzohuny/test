package com.jiuqi.dna.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.IteratedRecord;
import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.da.RecordState;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.Typable;

/**
 * ��¼��ʵ����
 * 
 * @author houchunlei
 * 
 */
public final class RecordSetImpl implements RecordSet, IteratedRecord {

	public final RecordSetImpl getRootType() {
		return this;
	}

	public final int getTupleElementCount() {
		return this.statement.columns.size();
	}

	public final Typable getTupleElementType(int index) {
		return this.statement.columns.get(index).value();
	}

	public final RecordSetFieldContainerImpl getFields() {
		return this.fields;
	}

	public final boolean isEmpty() {
		return this.records.size() == 0;
	}

	public final int getRecordCount() {
		return this.records.size();
	}

	public final boolean first() {
		return this.setRecordIndex(0);
	}

	public final boolean previous() {
		return this.setRecordIndex(this.currentRecordIndex - 1);
	}

	public final boolean next() {
		return this.setRecordIndex(this.currentRecordIndex + 1);
	}

	public final boolean isLast() {
		return this.currentRecordIndex == this.records.size();
	}

	public final boolean absolute(int index) {
		return this.setRecordIndex(index);
	}

	public final boolean relative(int rows) {
		return this.setRecordIndex(this.currentRecordIndex + rows);
	}

	public final int getPosition() {
		return this.currentRecordIndex;
	}

	public final RecordState getRecordState() {
		this.validateCurrentRecord();
		switch (this.currentRecord.getRecordState()) {
		case DynObj.r_new:
			return RecordState.NEW;
		case DynObj.r_new_modified:
			return RecordState.NEW_MODIFIED;
		case DynObj.r_db:
			return RecordState.IN_DB;
		case DynObj.r_db_deleting:
			return RecordState.IN_DB_DELETING;
		case DynObj.r_db_modifing:
			return RecordState.IN_DB_MODIFING;
		default:
			throw new UnsupportedOperationException("��Ч�ļ�¼״̬");
		}
	}

	public final void append() {
		DynObj record = this.statement.newRecordObj(DynObj.r_new);
		this.records.add(record);
		this.addModifingRecord(record);
		this.setRecordIndex(this.records.size() - 1);
	}

	public final boolean delete() {
		this.validateCurrentRecord();
		DynObj record = this.records.remove(this.currentRecordIndex);
		switch (record.getRecordState()) {
		case DynObj.r_new:
		case DynObj.r_new_modified:
			this.modifiedRecords.remove(record);
			break;
		case DynObj.r_db_modifing:
			record.setRecordState(DynObj.r_db_deleting);
			break;
		case DynObj.r_db:
			record.setRecordState(DynObj.r_db_deleting);
			this.addModifingRecord(record);
			break;
		default:
			throw new IllegalStateException();
		}
		return this.setRecordIndex(this.currentRecordIndex);
	}

	@SuppressWarnings("fallthrough")
	public final int update(final DBAdapter context) throws SQLException {
		if (context == null) {
			throw new NullPointerException();
		}
		if (this.modifiedRecords == null || this.modifiedRecords.isEmpty()) {
			return 0;
		}
		QueryStatementImpl query = this.statement;
		final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		adapter.checkAccessible();
		SqlModifier deleter = null;
		SqlModifier saver = null;
		int result = 0;
		try {
			for (int i = 0, c = this.modifiedRecords.size(); i < c; i++) {
				DynObj record = this.modifiedRecords.get(i);
				switch (record.getRecordState()) {
				case DynObj.r_new_modified:
				case DynObj.r_new:
				case DynObj.r_db_modifing:
					if (saver == null) {
						saver = query.getRowSaveSql(adapter).newExecutor(adapter, null);
					}
					// HCL ���ܱ�֤������ȷ�ĸ��¼���
					if (saver.updateRow(record)) {
						record.setRecordState(DynObj.r_db);
						result++;
					}
					break;
				case DynObj.r_db_deleting:
					if (deleter == null) {
						deleter = query.getRowDeleteSql(adapter).newExecutor(adapter, null);
					}
					if (deleter.updateRow(record)) {
						record.define = null;
						result++;
					}
					break;
				}
			}
			this.modifiedRecords.clear();
		} finally {
			if (saver != null) {
				saver.unuse();
			}
			if (deleter != null) {
				deleter.unuse();
			}
		}
		return result;
	}

	public final void reQuery(DBAdapter context, Object... argumetns)
			throws SQLException {
		DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		adapter.checkAccessible();
		DBCommandProxy proxy = DBAdapterImpl.prepareStatement((ContextImpl<?, ?, ?>) context, this.statement);
		try {
			proxy.setArgumentValues(argumetns);
			QuCommand command = (QuCommand) proxy.command;
			this.loadRecordSet(command.ensureQuerier().query(command.argValueObj));
		} finally {
			proxy.unuse();
		}
	}

	public final void reQuery(DBCommand dbCommand) throws SQLException {
		QuCommand command = (QuCommand) ((DBCommandProxy) dbCommand).command;
		command.adapter.checkAccessible();
		if (command.query != this.statement) {
			throw new IllegalArgumentException("���������ݼ�������");
		}
		this.loadRecordSet(command.ensureQuerier().query(command.argValueObj));
	}

	final QueryStatementImpl statement;

	final RecordSetFieldContainerImpl fields;

	public RecordSetImpl(QueryStatementImpl query) {
		this.statement = query;
		int columnSize = query.columns.size();
		this.fields = new RecordSetFieldContainerImpl(columnSize);
		for (int i = 0; i < columnSize; i++) {
			this.fields.add(new RecordSetFieldImpl(this, query.columns.get(i)));
		}
	}

	/**
	 * ���ص�ǰָ��ָ��ļ�¼����,ֻ����ȡ����ֵ����
	 */
	final DynObj getRecordRead() {
		this.validateCurrentRecord();
		return this.currentRecord;
	}

	/**
	 * ���ص�ǰָ��ָ��ļ�¼����,�����޸ļ�¼�����������
	 */
	final DynObj getRecordWrite() {
		this.validateCurrentRecord();
		// ֻ�޸�״̬Ϊdefault�ļ�¼����,appended��modified���ֲ���
		switch (this.currentRecord.getRecordState()) {
		case DynObj.r_db:
			this.currentRecord.setRecordState(DynObj.r_db_modifing);
			this.addModifingRecord(this.currentRecord);
			break;
		case DynObj.r_new:
			this.currentRecord.setRecordState(DynObj.r_new_modified);
			break;
		}
		return this.currentRecord;
	}

	/**
	 * ����¼��
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	public final void loadRecordSet(ResultSet resultSet) throws SQLException {
		try {
			this.records.clear();
			if (this.modifiedRecords != null) {
				this.modifiedRecords.clear();
			}
			ResultSetDynObjReader.readRecords(this.statement, resultSet, this.records);
			this.setRecordIndex(-1);
		} finally {
			resultSet.close();
		}
	}

	final void iterateResultSet(ContextImpl<?, ?, ?> context,
			ResultSet resultSet, RecordIterateAction action) throws Throwable {
		try {
			ResultSetDynObjReader reader = new ResultSetDynObjReader(resultSet);
			DynObj record = this.statement.newRecordObj(DynObj.r_db);
			reader.obj = record;
			this.currentRecord = record;
			ArrayList<StructFieldDefineImpl> fields = this.statement.mapping.fields;
			int cSize = fields.size();
			long recordIndex = 0L;
			while (resultSet.next()) {
				reader.columnIndex = 1;
				for (int i = 0; i < cSize; i++) {
					StructFieldDefineImpl field = fields.get(i);
					reader.targetField = field;
					field.type.detect(reader, record);
					reader.columnIndex++;
				}
				if (action.iterate(context, this, recordIndex++)) {
					break;
				}
			}
		} finally {
			resultSet.close();
		}
	}

	private final void validateCurrentRecord() {
		if (this.currentRecord == null) {
			throw new NullPointerException("��ǰ��¼Ϊ��");
		}
	}

	private final void addModifingRecord(DynObj record) {
		if (this.modifiedRecords == null) {
			this.modifiedRecords = new ArrayList<DynObj>();
		}
		this.modifiedRecords.add(record);
	}

	/**
	 * ����ָ��λ��,�����Ƿ���Ч��¼
	 * 
	 * @param index
	 * @return
	 */
	private final boolean setRecordIndex(int index) {
		int recCount = this.records.size();
		if (index > recCount) {
			throw new IllegalStateException("�α��Ѿ��ִ��¼����β��");
		}
		if (index < 0) {
			this.currentRecord = null;
			this.currentRecordIndex = -1;
			return false;
		} else if (index < recCount) {
			this.currentRecord = this.records.get(index);
			this.currentRecordIndex = index;
			return true;
		} else {
			this.currentRecord = null;
			this.currentRecordIndex = recCount;
			return false;
		}
	}

	public final Object getCurrentRO() {
		return this.currentRecord;
	}

	public final int positionOfRO(Object ro) {
		if (ro == null) {
			throw new NullArgumentException("ro");
		}
		DynObj r = (DynObj) ro;
		if (r.define != this.statement.mapping) {
			throw new IllegalArgumentException("����ro�����ڱ����ݼ�");
		}
		return this.records.indexOf(ro);
	}

	public final boolean setCurrentRO(Object ro) {
		int p = this.positionOfRO(ro);
		if (p >= 0) {
			this.setRecordIndex(p);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ǰ��¼����
	 */
	private DynObj currentRecord;

	private int currentRecordIndex;

	private final ArrayList<DynObj> records = new ArrayList<DynObj>(0);

	private ArrayList<DynObj> modifiedRecords;

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.RECORDSET_H);
		this.statement.digestType(digester);
	}
}
