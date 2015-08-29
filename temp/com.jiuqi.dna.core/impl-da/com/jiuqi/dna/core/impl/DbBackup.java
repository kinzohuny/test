package com.jiuqi.dna.core.impl;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.db.IDatabaseCallback;
import com.jiuqi.dna.core.def.model.ModelDefine;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.info.InfoReporter;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceRef;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.datasource.StatementWrap;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbSync;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXElementBuilder;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DateParser;
import com.jiuqi.dna.core.type.EnumType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;

/**
 * 数据库备份器
 * 
 * @author gaojingxin
 * 
 */
public final class DbBackup {

	private DbBackup() {
	}

	static final String XML_TABLES = "tables.xml";

	static final File findBackupFile(ApplicationImpl application,
			DataSourceRef dsr) {
		if (dsr == null) {
			return null;
		}
		try {
			final File work = application.getDNAWork();
			final File f = new File(work, buildBackupFileName(dsr));
			if (!f.isFile()) {
				return null;
			}
			return f;
		} catch (Throwable e) {
			return null;
		}
	}

	static final String BAKCUP_FILE_POSTFIX = ".ddb";

	static final String buildBackupFileName(DataSourceRef dsr) {
		return dsr.dataSource.name.concat(BAKCUP_FILE_POSTFIX);
	}

	static final File calRestoredFile(ApplicationImpl application,
			DataSourceRef dsr) {
		final StringBuilder fn = new StringBuilder("rd/");
		final String t = DateParser.format(System.currentTimeMillis());
		for (int i = 0, c = t.length(); i < c; i++) {
			final char ch = t.charAt(i);
			if (('0' <= ch && ch <= '9') || ch == '.') {
				fn.append(ch);
			} else if (ch == ' ') {
				fn.append('-');
			}
		}
		final File folder = new File(application.getDNAWork(), fn.toString());
		folder.mkdirs();
		return new File(folder, buildBackupFileName(dsr));

	}

	static final String XML_ELEMENT_TABLES = "tables";
	static final String XML_ATTR_ROWCOUNT = "row-count";
	static final String FN_DATA1 = "data\\";
	static final String FN_DATA2 = "data/";
	static final String FN_CSV = ".csv";
	static final char char_empty_string = '^';
	static final String str_empty_string = "^";

	static final class Counter {

		private final InfoReporter ir;
		private float lastProgress;
		private long totalRows;
		private long finishedRows;
		private int tableCount;
		private int tableStructFinished;
		private final boolean forStartup;
		private final PrintStream print;

		final void incTotalRows(long rows) {
			this.totalRows += rows;
		}

		final void setTableCount(int tableCount) {
			this.tableCount = tableCount;
		}

		final void reportMessage(String message) {
			if (this.forStartup) {
				ResolveHelper.logStartInfo(message);
			} else {
				String msg = DateParser.format(System.currentTimeMillis(), DateParser.FORMAT_DATE_TIME_MS) + " >>> " + message;
				if (this.print != null) {
					this.print.println(msg);
				} else {
					System.out.println(msg);
				}
			}
		}

		final void oneRowFinished() {
			if (this.totalRows > 0) {
				this.finishedRows++;
				this.updateProgress(0.05f + 0.95f * this.finishedRows / this.totalRows, 1f);
			}
		}

		final void oneTableStructFinished() {
			this.tableStructFinished++;
			this.updateProgress(0.05f * this.tableStructFinished / this.tableCount, 0.05f);
		}

		Counter(ContextImpl<?, ?, ?> context) {
			this.ir = context;
			this.forStartup = context.kind == ContextKind.INITER;
			this.print = null;
		}

		Counter(ContextImpl<?, ?, ?> context, PrintStream ps) {
			this.ir = context;
			this.forStartup = context.kind == ContextKind.INITER;
			this.print = ps;
		}

		final void updateProgress(float progress, float limit) {
			if (this.ir == null) {
				return;
			}
			if (progress > limit) {
				progress = limit;
			}
			if (progress == 1f || progress - this.lastProgress >= 0.01f) {
				this.ir.setPartialProgress(progress);
				this.lastProgress = progress;
			}
		}
	}

	private static void nextZipEntry(ZipOutputStream zos, String name)
			throws Throwable {
		final ZipEntry ze = new ZipEntry(name);
		ze.setTime(System.currentTimeMillis());
		zos.putNextEntry(ze);
	}

	private static class DBTableBackupper {

		final boolean dispose(DBAdapterImpl dbAdapter) {
			this.closeRS();
			if (this.st != null) {
				dbAdapter.freeStatement(this.st);
				this.st = null;
				return true;
			}
			return false;
		}

		final boolean closeRS() {
			if (this.rs != null) {
				try {
					this.rs.close();
				} catch (Throwable e) {

				}
				this.rs = null;
				return true;
			}
			return false;
		}

		private StatementWrap st;
		private ResultSet rs;
		private GUID recid;
		private boolean match;
		private boolean eof;

		final ResultSet getRSToRead() {
			if (this.match) {
				return this.rs;
			}
			return null;
		}

		final GUID primaryNext() throws Throwable {
			if (this.rs.next()) {
				this.match = true;
				return this.recid = GUID.valueOf(this.rs.getBytes(1));
			} else {
				this.match = false;
				this.eof = true;
				return null;
			}
		}

		final boolean slaveTryNext(GUID primaryRecid) throws Throwable {
			if (this.eof) {
				return false;
			}
			for (;;) {
				GUID recid = this.recid;
				if (recid == null) {
					if (this.rs.next()) {
						this.recid = recid = GUID.valueOf(this.rs.getBytes(1));
					} else {
						this.eof = true;
						return this.match = false;
					}
				}
				final int c = primaryRecid.compareTo(recid);
				if (c == 0) {
					return this.match = true;
				} else if (c < 0) {
					return this.match = false;
				} else {
					this.recid = null;
				}
			}
		}

		final int queryResultSet(DBAdapterImpl dbAdapter,
				DBTableDefineImpl dbTable, int fieldStart, int baker,
				short[] fieldpos) throws Throwable {
			final TableFieldDefineImpl f_recid = dbTable.owner.f_recid;
			final StringBuilder sql = new StringBuilder("select ");
			final DbMetadata dbMetadata = dbAdapter.dbMetadata;
			dbMetadata.quoteId(sql, f_recid.namedb());
			final ArrayList<TableFieldDefineImpl> fields = dbTable.owner.fields;
			final short maxP = (short) (dbAdapter.dbMetadata.getMaxColumnsInSelect() * 8 / 10);
			short p = 2;
			int nextStart = 0;
			for (int i = fieldStart, c = fields.size(); i < c; i++) {
				final TableFieldDefineImpl tf = fields.get(i);
				if (tf != f_recid && tf.dbTable == dbTable) {
					sql.append(',');
					dbMetadata.quoteId(sql, tf.namedb());
					final int ii = i * 2;
					fieldpos[ii] = (short) baker;
					fieldpos[ii + 1] = p++;
					if (p == maxP) {
						nextStart = i + 1;
						if (nextStart == c) {
							nextStart = 0;
						}
						break;
					}
				}
			}
			sql.append(" from ");
			dbMetadata.quoteId(sql, dbTable.namedb());
			sql.append(" order by ");
			dbMetadata.quoteId(sql, f_recid.namedb());
			if (this.st == null) {
				this.st = dbAdapter.createStatement();
			}
			this.rs = this.st.executeQuery(sql.toString(), SqlSource.CORE_DML);
			this.eof = false;
			return nextStart;
		}
	}

	private static class TableBackupper implements
			TypeDetector<String, ResultSet> {
		// 2012-2-17，张乐明 数据库备份操作，一个逻辑表关联很多物理表时，出现越界异常，此处改成32 侯春磊确认。
		final DBTableBackupper[] dbTableBackuppers = new DBTableBackupper[32];
		private int backupperCount;
		private short[] fieldpos;

		private final DBAdapterImpl dbAdapter;
		private final ZipOutputStream zos;
		private final ArrayList<TableDefineImpl> tables;
		private final Counter counter;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		TableBackupper(ContextImpl<?, ?, ?> context, OutputStream out,
				PrintStream print) {
			this.tables = new ArrayList<TableDefineImpl>();
			DBAdapterImpl dbAdapter;
			try {
				dbAdapter = context.getDBAdapter();
			} catch (Throwable e) {
				dbAdapter = null;
			}
			this.dbAdapter = dbAdapter;
			if (dbAdapter != null) {
				context.occorAt.site.fillAllRuntimeDefines(TableDefine.class, (List) this.tables, this.dbAdapter.dataSourceRef);
			}
			this.counter = new Counter(context, print);
			this.counter.setTableCount(this.tables.size());
			this.zos = new ZipOutputStream(out);
		}

		final void finish() {
			for (int i = 0; i < this.backupperCount; i++) {
				if (!this.dbTableBackuppers[i].dispose(this.dbAdapter)) {
					break;
				}
			}
			try {
				this.zos.finish();
			} catch (Throwable e) {
			}
			this.counter.reportMessage("备份完毕");
		}

		private int colIndex;

		private void backupTableStructs() throws Throwable {
			this.counter.reportMessage("备份表结构");
			final SXElement tablesXML = SXElement.newDoc();
			final SXElement tablesE = tablesXML.append(XML_ELEMENT_TABLES);
			if (this.dbAdapter != null) {
				final StatementWrap st = this.dbAdapter.createStatement();
				try {
					final StringBuilder sql = new StringBuilder();
					for (int i = this.tables.size() - 1; i >= 0; i--) {
						long rowCount;
						final TableDefineImpl table = this.tables.get(i);
						if (table == TableDefineImpl.DUMMY) {
							this.tables.remove(i);
							continue;
						}
						try {
							sql.setLength(0);
							sql.append("select count(*) from ");
							this.dbAdapter.dbMetadata.quoteId(sql, table.primary.namedb());
							final ResultSet rs = st.executeQuery(sql.toString(), SqlSource.CORE_DML);
							try {
								if (rs.next()) {
									rowCount = rs.getLong(1);
									this.counter.incTotalRows(rowCount);
								} else {
									rowCount = 0;
								}
							} finally {
								rs.close();
							}
						} catch (Throwable e) {
							this.counter.oneTableStructFinished();
							this.counter.reportMessage("无效数据库表,表[" + table.name + "]");
							this.tables.remove(i);
							continue;
						}
						final SXElement tableE = table.renderInto(tablesE);
						tableE.setLong(XML_ATTR_ROWCOUNT, rowCount);
						this.counter.oneTableStructFinished();
					}
				} finally {
					this.dbAdapter.freeStatement(st);
				}
			}
			tablesE.setLong(XML_ATTR_ROWCOUNT, this.counter.totalRows);
			nextZipEntry(this.zos, XML_TABLES);
			final Writer w = new OutputStreamWriter(this.zos, Convert.utf8);
			tablesXML.render(w, true);
			w.flush();
			this.zos.closeEntry();
		}

		final void backup() throws Throwable {
			backup(null);
		}
		
		final void backup(final IDatabaseCallback<GUID,String> callback) throws Throwable {
			this.backupTableStructs();
			for (int i = 0, c = this.tables.size(); i < c; i++) {
				this.backupTable(this.tables.get(i),callback);
			}
			this.markSuccess();
		}

		private final void markSuccess() throws Throwable {
			nextZipEntry(this.zos, SUCCESS_MARK);
			Writer w = new OutputStreamWriter(this.zos, Convert.utf8);
			w.write("fin");
			w.flush();
			this.zos.closeEntry();
		}

		private final  void backupTable(TableDefineImpl table,final IDatabaseCallback<GUID,String> callback) throws Throwable {
			final long start = System.nanoTime();
			long rowCount = 0;
			final ArrayList<TableFieldDefineImpl> fields = table.fields;
			final int fieldCount = fields.size();
			final int fieldPosCount = fieldCount * 2;
			short[] fieldpos = this.fieldpos;
			if (fieldpos == null || fieldpos.length < fieldPosCount) {
				this.fieldpos = fieldpos = new short[fieldPosCount];
			}
			final int dbTables = table.dbTables.size();
			try {
				int actualCount = 0;
				for (int i = 0; i < dbTables; i++) {
					final DBTableDefineImpl dbTable = table.dbTables.get(i);
					int nextStart = 0;
					do {
						DBTableBackupper backupper = this.dbTableBackuppers[actualCount];
						if (backupper == null) {
							this.dbTableBackuppers[actualCount] = backupper = new DBTableBackupper();
							this.backupperCount++;
						} else {
							backupper.recid = null;
						}
						nextStart = backupper.queryResultSet(this.dbAdapter, dbTable, nextStart, actualCount, this.fieldpos);
						actualCount++;
					} while (nextStart > 0);
				}
				final DBTableBackupper primaryBackupper = this.dbTableBackuppers[0];
				CsvWriter writer = null;
				try {
					for (;;) {
						final GUID recid = primaryBackupper.primaryNext();
						if (recid == null) {
							break;
						}
						
						boolean isBackUp = false;
						if(null != callback) {
							isBackUp = isBackUp || callback.call(recid,table.getName());
						} else {
							isBackUp = true;
						}
						if(!isBackUp)
							continue;
						
						rowCount++;
						for (int i = 1; i < actualCount; i++) {
							this.dbTableBackuppers[i].slaveTryNext(recid);
						}
						if (writer == null) {
							nextZipEntry(this.zos, FN_DATA1 + table.name + FN_CSV);
							writer = new CsvWriter(this.zos, Convert.utf8);
							for (int fi = 0; fi < fieldCount; fi++) {
								writer.write(fields.get(fi).name);
							}
							writer.endRecord();
						}
						writer.write(recid.toString());
						int lastDBTableIndex = -1;
						ResultSet rs = null;
						for (int i = 0; i < fieldCount; i++) {
							final int p = i * 2;
							final int baker = fieldpos[p];
							this.colIndex = fieldpos[p + 1];
							if (this.colIndex > 0) {
								if (lastDBTableIndex != baker) {
									rs = this.dbTableBackuppers[baker].getRSToRead();
									lastDBTableIndex = baker;
								}
								if (writer == null) {
									nextZipEntry(this.zos, "data\\" + table.name + ".data");
									writer = new CsvWriter(this.zos, Convert.utf8);
									for (int fi = 0; fi < fieldCount; fi++) {
										writer.write(fields.get(i).name);
									}
									writer.endRecord();
								}
								if (rs != null) {
									writer.write(fields.get(i).getType().detect(this, rs), true);
								} else {
									writer.write(null);
								}
							}
						}
						if (writer != null) {
							writer.endRecord();
						}
						this.counter.oneRowFinished();
					}
				} finally {
					if (writer != null) {
						writer.flush();
					}
				}
			} finally {
				for (int i = 0; i < this.backupperCount; i++) {
					if (!this.dbTableBackuppers[i].closeRS()) {
						break;
					}
				}
			}
			this.counter.reportMessage("备份表数据完毕，表[" + table.name + "]，记录数[" + rowCount + "]，耗时（毫秒）[" + (System.nanoTime() - start) / 1000000 + "]");
		}

		public String inBinary(ResultSet userData, SequenceDataType type)
				throws Throwable {
			return this.inBytes(userData, null);
		}

		public String inBlob(ResultSet userData) throws Throwable {
			return this.inBytes(userData, null);
		}

		public String inBoolean(ResultSet userData) throws Throwable {
			final boolean value = userData.getBoolean(this.colIndex);
			if (userData.wasNull()) {
				return null;
			}
			return Convert.toString(value);
		}

		public String inByte(ResultSet userData) throws Throwable {
			final byte value = userData.getByte(this.colIndex);
			if (userData.wasNull()) {
				return null;
			}
			return Convert.toString(value);
		}

		public String inBytes(ResultSet userData, SequenceDataType type)
				throws Throwable {
			final byte[] bytes = userData.getBytes(this.colIndex);
			if (bytes == null) {
				return null;
			}
			return Convert.bytesToHex(bytes, true, true);
		}

		public String inChar(ResultSet userData, SequenceDataType type)
				throws Throwable {
			return this.inString(userData, type);
		}

		public String inCharacter(ResultSet userData) throws Throwable {
			return this.inString(userData, null);
		}

		public String inDate(ResultSet userData) throws Throwable {
			final Timestamp ts = userData.getTimestamp(this.colIndex);
			if (ts == null) {
				return null;
			}
			return Convert.dateToString(ts.getTime());
		}

		public String inDouble(ResultSet userData) throws Throwable {
			final double value = userData.getDouble(this.colIndex);
			if (userData.wasNull()) {
				return null;
			}
			return Convert.toString(value);
		}

		public String inFloat(ResultSet userData) throws Throwable {
			final float value = userData.getFloat(this.colIndex);
			if (userData.wasNull()) {
				return null;
			}
			return Convert.toString(value);
		}

		public String inGUID(ResultSet userData) throws Throwable {
			byte[] bytes = userData.getBytes(this.colIndex);
			if (bytes == null || bytes.length == 0) {
				return null;
			}
			if (bytes.length > 16) {
				final byte[] nb = new byte[16];
				System.arraycopy(bytes, 0, nb, 0, 16);
				bytes = nb;
			} else if (bytes.length < 16) {
				final byte[] nb = new byte[16];
				System.arraycopy(bytes, 0, nb, 0, bytes.length);
				bytes = nb;
			}
			return Convert.bytesToHex(bytes, false, true);
		}

		public String inInt(ResultSet userData) throws Throwable {
			final int value = userData.getInt(this.colIndex);
			if (userData.wasNull()) {
				return null;
			}
			return Convert.toString(value);
		}

		public String inLong(ResultSet userData) throws Throwable {
			final long value = userData.getLong(this.colIndex);
			if (userData.wasNull()) {
				return null;
			}
			return Convert.toString(value);
		}

		public String inNChar(ResultSet userData, SequenceDataType type)
				throws Throwable {
			return this.inString(userData, type);
		}

		public String inNText(ResultSet userData) throws Throwable {
			return this.inString(userData, null);
		}

		public String inNVarChar(ResultSet userData, SequenceDataType type)
				throws Throwable {
			return this.inString(userData, type);
		}

		public String inEnum(ResultSet userData, EnumType<?> type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inNull(ResultSet userData) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inNumeric(ResultSet userData, int precision, int scale)
				throws Throwable {
			return this.inDouble(userData);
		}

		public String inShort(ResultSet userData) throws Throwable {
			final short value = userData.getShort(this.colIndex);
			if (userData.wasNull()) {
				return null;
			}
			return Convert.toString(value);
		}

		public String inString(ResultSet userData, SequenceDataType type)
				throws Throwable {
			final String value = userData.getString(this.colIndex);
			if (userData.wasNull()) {
				return null;
			}
			if (value.length() == 0) {
				return str_empty_string;
			}
			if (value.charAt(0) == char_empty_string) {
				return str_empty_string.concat(value);
			}
			return value;
		}

		public String inVarBinary(ResultSet userData, SequenceDataType type)
				throws Throwable {
			return this.inBytes(userData, type);
		}

		public String inVarChar(ResultSet userData, SequenceDataType type)
				throws Throwable {
			return this.inString(userData, type);
		}

		public String inText(ResultSet userData) throws Throwable {
			return this.inString(userData, null);
		}

		public String inModel(ResultSet userData, ModelDefine type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inStruct(ResultSet userData, StructDefine type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inTable(ResultSet userData) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inUnknown(ResultSet userData) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inObject(ResultSet userData, ObjectDataType type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inQuery(ResultSet userData, QueryStatementDefine type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inRecordSet(ResultSet userData) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public String inResource(ResultSet userData, Class<?> facadeClass,
				Object category) throws Throwable {
			throw new UnsupportedOperationException();
		}

	}

	private static class TableRestorer {

		final NamedDefineContainerImpl<TableDefineImpl> tables = new NamedDefineContainerImpl<TableDefineImpl>(true);

		private final static class TableQuerier extends ObjectQuerierImpl {
			private final NamedDefineContainerImpl<TableDefineImpl> tables;

			TableQuerier(NamedDefineContainerImpl<TableDefineImpl> tables) {
				this.tables = tables;
			}

			@SuppressWarnings("unchecked")
			@Override
			public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
					throws UnsupportedOperationException {
				if (facadeClass == TableDefine.class && key instanceof String) {
					return (TFacade) this.tables.find((String) key);
				}
				return super.find(facadeClass, key);
			}
		}

		final ZipInputStream zis;
		final DBAdapterImpl dbAdapter;
		final Counter counter;
		final DbSync dbRefactor;

		private void loadTableDefines() throws Throwable {
			this.counter.reportMessage("装载表结构");
			final ZipEntry ze = this.zis.getNextEntry();
			if (!XML_TABLES.equals(ze.getName())) {
				this.counter.reportMessage("无效的备份流格式");
				throw new IllegalArgumentException("无效的备份流格式");
			}
			final SXMergeHelper helper = new SXMergeHelper(new TableQuerier(this.tables));
			final SXElementBuilder builder = new SXElementBuilder();

			for (SXElement tableE : builder.build(new InputStreamReader(new NoCloseInputStreamWrapper(this.zis), Convert.utf8)).getChildren(XML_ELEMENT_TABLES, TableDefineImpl.table_tag)) {
				final String tn = tableE.getAttribute(NamedDefineImpl.xml_attr_name);
				final TableDefineImpl table = new TableDefineImpl(tn, null);
				this.tables.add(table);
				table.merge(tableE, helper);
				this.counter.incTotalRows(tableE.getLong(XML_ATTR_ROWCOUNT));
			}
			helper.resolveDelayAction(CoreMetadataTableLoadStep.class, null);
			this.counter.setTableCount(this.tables.size());
		}

		final void finish() {
			this.dbRefactor.unuse();
		}

		private void restoreTableData(TableDefineImpl table) throws Throwable {
			final long start = System.nanoTime();
			long rowCount = 0;
			final CsvReader reader = new CsvReader(this.zis, Convert.utf8);
			reader.setSafetySwitch(false);
			final InsertStatementImpl is = new InsertStatementImpl("?", table);
			final StructFieldDefineImpl recidField = is.assignArgument(table.f_recid);
			final int fieldCount = table.fields.size();
			int recidIndex = 0;
			for (int i = 0; i < fieldCount; i++) {
				final TableFieldDefineImpl field = table.fields.get(i);
				if (field != table.f_recid) {
					is.assignArgument(field);
				} else {
					recidIndex = i;
				}
			}
			final NamedDefineContainerImpl<StructFieldDefineImpl> fields = is.arguments.fields;
			reader.skipRecord();// 标题
			final DBCommandProxy cmd = DBAdapterImpl.prepareStatement(this.context, is);
			try {
				final DynObj argObj = cmd.getArgumentsObj();
				while (reader.readRecord()) {
					rowCount++;
					recidField.setFieldValueAsStringNoCheck(argObj, reader.get(0));
					for (int i = 0; i < fieldCount; i++) {
						if (recidIndex != i) {
							final StructFieldDefineImpl field = fields.get(i);
							String s = reader.get(i);
							if (s == null || s.length() == 0) {
								field.setFieldValueNullNoCheck(argObj);
								continue;
							}
							if (s.length() == 1 && s.charAt(0) == char_empty_string) {
								s = "";
							} else if (s.charAt(0) == char_empty_string) {
								s = s.substring(1);
							}
							field.setFieldValueAsStringNoCheck(argObj, s);
						}
					}
					try {
						cmd.executeUpdate();
					} catch (Throwable ex) {
						this.counter.reportMessage("!!!插入数据时失败，表[" + table.name + "]，原因：" + ex.getMessage());
					}
					this.counter.oneRowFinished();
					if (this.counter.finishedRows % 1000 == 0) {
						this.dbAdapter.resolveTranse(true);
					}
				}
			} finally {
				cmd.unuse();
			}
			this.counter.reportMessage("还原表数据完毕，表[" + table.name + "]，记录数[" + rowCount + "]，耗时（毫秒）[" + (System.nanoTime() - start) / 1000000 + "]");
		}

		final void restore() throws Throwable {
			this.counter.reportMessage("数据还原开始");
			for (int i = this.tables.size() - 1; i >= 0; i--) {
				final TableDefineImpl table = this.tables.get(i);
				try {
					this.dbRefactor.restore(table);
					this.counter.reportMessage("重建表结构，表[" + table.name + "]");
				} catch (Throwable e) {
					this.counter.reportMessage("重建表结构失败，表[" + table.name + "]。");
					e.printStackTrace();
					this.tables.remove(i);

				}
				this.counter.oneTableStructFinished();
			}
			for (ZipEntry e = this.zis.getNextEntry(); e != null; e = this.zis.getNextEntry()) {
				final String eN = e.getName();
				if ((eN.startsWith(FN_DATA1) || eN.startsWith(FN_DATA2)) && eN.endsWith(FN_CSV)) {
					final String tableName = eN.substring(FN_DATA1.length(), eN.length() - FN_CSV.length());
					final int index = this.tables.indexOfName(tableName);
					if (index >= 0) {
						try {
							this.restoreTableData(this.tables.remove(index));
						} catch (Throwable ex) {
							this.counter.reportMessage("还原表数据失败，表[" + tableName + "]，原因：" + ex.getMessage());
						}
					} else {
						this.counter.reportMessage("无效数据项\"" + eN + "\"");
					}
				}
			}
			this.dbAdapter.resolveTranse(true);
			this.counter.reportMessage("数据还原完毕");
		}

		final ContextImpl<?, ?, ?> context;

		TableRestorer(ContextImpl<?, ?, ?> context, InputStream in)
				throws Throwable {
			this.context = context;
			this.dbAdapter = context.getDBAdapter();
			this.zis = new ZipInputStream(in);
			this.counter = new Counter(context);
			this.dbRefactor = this.dbAdapter.refactor();
			this.loadTableDefines();
		}

	}

	public static void backup(Context context, OutputStream out,
			PrintStream print) throws Throwable {
		final ContextImpl<?, ?, ?> ctx;
		if (context instanceof SituationImpl) {
			ctx = ((SituationImpl) context).usingSituation();
		} else {
			ctx = (ContextImpl<?, ?, ?>) context;
		}
		final TableBackupper backupper = new TableBackupper(ctx, out, print);
		try {
			backupper.backup();
		} finally {
			backupper.finish();
		}
	}
	
	public static void backup(Context context, OutputStream out,
			PrintStream print,IDatabaseCallback<GUID,String> callBack) throws Throwable {
		final ContextImpl<?, ?, ?> ctx;
		if (context instanceof SituationImpl) {
			ctx = ((SituationImpl) context).usingSituation();
		} else {
			ctx = (ContextImpl<?, ?, ?>) context;
		}
		final TableBackupper backupper = new TableBackupper(ctx, out, print);
		try {
			backupper.backup(callBack);
		} finally {
			backupper.finish();
		}
	}

	public static void restore(ContextImpl<?, ?, ?> context, InputStream in)
			throws Throwable {
		final TableRestorer restorer = new TableRestorer(context, in);
		try {
			restorer.restore();
		} finally {
			restorer.finish();
		}
	}

	public static final String SUCCESS_MARK = "fin";
}