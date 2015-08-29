package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.def.table.AsTable;
import com.jiuqi.dna.core.def.table.AsTableField;
import com.jiuqi.dna.core.def.table.AsTableField.DBType;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.EnumType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;
import com.jiuqi.dna.core.type.TypeFactory;

/**
 * ʵ�������
 * 
 * @author gaojingxin
 * 
 */
public final class EntityTableUtil {

	private EntityTableUtil() {
		// nothing
	}

	public static void buildTableAndOrm(TableDefineImpl table,
			MappingQueryStatementImpl orm) {
		// ���ñ�����
		AsTable asTable = orm.mapping.soClass.getAnnotation(AsTable.class);
		if (asTable != null) {
			table.setTitle(asTable.title());
			table.setDescription(asTable.description());
			String namedb = asTable.dbName();
			if (namedb != null && namedb.length() > 0) {
				table.primary.setNamedb(namedb);
			}
		}
		QuRelationRef tableRef = orm.newTableRef("T", table);
		// ����
		StructFieldDefineImpl recidField = null;
		// �а汾
		StructFieldDefineImpl recverField = null;
		// �߼������ֶ�
		ArrayList<StructFieldDefineImpl> pkFields = new ArrayList<StructFieldDefineImpl>(4);
		for (StructFieldDefineImpl sf : orm.mapping.fields) {
			if (sf.asTableField != null && sf.asTableField.pkOrdinal() >= 0) {
				// �߼�������ordinal����
				insert: {
					int ordinal = sf.asTableField.pkOrdinal();
					for (int i = 0, c = pkFields.size(); i < c; i++) {
						if (ordinal < pkFields.get(i).asTableField.pkOrdinal()) {
							pkFields.add(i, sf);
							break insert;
						}
					}
					pkFields.add(sf);
				}
			}
			// recid
			if (sf.asTableField != null && sf.asTableField.isRecid()) {
				if (recidField != null) {
					throw new IllegalArgumentException("�ظ�����recid�ֶ�");
				} else if (sf.type != GUIDType.TYPE) {
					throw new IllegalArgumentException("recid�ֶ����Ͳ�ΪGUID");
				}
				recidField = sf;
			}
			// recver
			if (sf.asTableField != null && sf.asTableField.isRecver()) {
				if (recverField != null) {
					throw new IllegalArgumentException("�ظ�����recver�ֶ�");
				} else if (sf.type != LongType.TYPE) {
					throw new IllegalArgumentException("recver�ֶ����Ͳ���ΪLONG");
				}
				recverField = sf;
			}
		}
		if (recidField != null) {
			orm.newColumn(recidField.name, tableRef.expOf(table.f_recid), recidField);
		}
		if (recverField != null) {
			orm.newColumn(recverField.name, tableRef.expOf(table.f_recver), recverField);
		}
		// �����߼������ֶ�
		for (StructFieldDefineImpl sf : pkFields) {
			// �߼���������������,������atf��dbTable�ֶ�
			String assignName = sf.asTableField.name();
			TableFieldDefineImpl tf = table.newPrimaryField((assignName != null) && (assignName.length() > 0) ? assignName : sf.name, sf.type.getRootType().detect(parser, sf.asTableField));
			setFieldAttr(tf, sf.asTableField);
			orm.newColumn(sf.name, tableRef.expOf(tf), sf);
		}
		// ���߼������ֶ�
		for (StructFieldDefineImpl sf : orm.mapping.fields) {
			// ���AsTable�������ֶλ��߱����AsTableField���ж�Ϊ����ֶ�
			if ((asTable != null || sf.asTableField != null) && !pkFields.contains(sf) && sf != recidField && sf != recverField) {
				String fn;
				if (sf.asTableField != null) {
					fn = sf.asTableField.name();
					if (fn == null || fn.length() == 0) {
						fn = sf.name;
					}
				} else {
					fn = sf.name;
				}
				TableFieldDefineImpl tf = table.newField(fn, sf.type.getRootType().detect(parser, sf.asTableField));
				setFieldAttr(tf, sf.asTableField);
				orm.newColumn(sf.name, tableRef.expOf(tf), sf);
			}
		}
	}

	/**
	 * ���ݱ��ֶε�annotation����������
	 * 
	 * @param tf
	 *            ���ֶζ���
	 * @param atf
	 *            ʵ���ֶε�ע��
	 */
	private static final void setFieldAttr(TableFieldDefineImpl tf,
			AsTableField atf) {
		if (atf == null) {
			return;
		}
		tf.setTitle(atf.title());
		tf.setDescription(atf.description());
		String namedb = atf.nameInDB();
		if (namedb != null && namedb.length() > 0) {
			tf.setNamedb(namedb);
		}
		tf.setKeepValid(atf.isRequired());
	}

	private static final TypeDetectorBase<DataType, AsTableField> parser = new TypeDetectorBase<DataType, AsTableField>() {

		@Override
		public DataType inBoolean(AsTableField atf) throws Throwable {
			return TypeFactory.BOOLEAN;
		}

		@Override
		public DataType inBytes(AsTableField atf, SequenceDataType type)
				throws Throwable {
			if (atf == null) {
				return TypeFactory.VARBINARY32;
			} else {
				switch (atf.dbType()) {
				case Default:
					return TypeFactory.VARBINARY(atf.length());
				case Binary:
					return TypeFactory.BINARY(atf.length());
				case Varbinary:
					return TypeFactory.VARBINARY(atf.length());
				case Blob:
					return TypeFactory.BLOB;
				default:
					return TypeFactory.VARBINARY(atf.length());
				}
			}
		}

		@Override
		public DataType inDate(AsTableField atf) throws Throwable {
			return TypeFactory.DATE;
		}

		@Override
		public DataType inDouble(AsTableField atf) throws Throwable {
			if (atf == null || atf.dbType() != DBType.Numeric) {
				return TypeFactory.DOUBLE;
			} else {
				return TypeFactory.NUMERIC(atf.precision(), atf.scale());
			}
		}

		@Override
		public DataType inEnum(AsTableField atf, EnumType<?> type)
				throws Throwable {
			if (atf == null) {
				return TypeFactory.INT;
			}
			switch (atf.dbType()) {
			case Char:
				return TypeFactory.CHAR(atf.length());
			case Varchar:
				return TypeFactory.VARCHAR(atf.length());
			case NChar:
				return TypeFactory.NCHAR(atf.length());
			case NVarchar:
				return TypeFactory.NVARCHAR(atf.length());
			default:
				return TypeFactory.INT;
			}
		}

		@Override
		public DataType inFloat(AsTableField atf) throws Throwable {
			if (atf == null || atf.dbType() != DBType.Numeric) {
				return TypeFactory.FLOAT;
			} else {
				return TypeFactory.NUMERIC(atf.precision(), atf.scale());
			}
		}

		@Override
		public DataType inGUID(AsTableField atf) throws Throwable {
			return TypeFactory.GUID;
		}

		@Override
		public DataType inInt(AsTableField atf) throws Throwable {
			return TypeFactory.INT;
		}

		@Override
		public DataType inLong(AsTableField atf) throws Throwable {
			if (atf == null || atf.dbType() != DBType.Date) {
				return TypeFactory.LONG;
			} else {
				return TypeFactory.DATE;
			}
		}

		@Override
		public DataType inShort(AsTableField atf) throws Throwable {
			return TypeFactory.SHORT;
		}

		@Override
		public DataType inString(AsTableField atf, SequenceDataType type)
				throws Throwable {
			if (atf == null) {
				return TypeFactory.VARCHAR32;
			} else {
				switch (atf.dbType()) {
				case Default:
					return TypeFactory.VARCHAR(atf.length());
				case Char:
					return TypeFactory.CHAR(atf.length());
				case Varchar:
					return TypeFactory.VARCHAR(atf.length());
				case Text:
					return TypeFactory.TEXT;
				case NChar:
					return TypeFactory.NCHAR(atf.length());
				case NVarchar:
					return TypeFactory.NVARCHAR(atf.length());
				case NText:
					return TypeFactory.NTEXT;
				default:
					return TypeFactory.VARCHAR(atf.length());
				}
			}
		}

	};
}
