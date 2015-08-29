package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.IndexType;
import com.jiuqi.dna.core.def.table.TableRelationType;
import com.jiuqi.dna.core.def.table.TableType;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;

enum TableXML implements DefineXML<TableDefineImpl> {

	/**
	 * 
	 * @author houchunlei
	 * 
	 */
	V19("1,9") {

		public final void render(TableDefineImpl table, SXElement element) {
			throw new UnsupportedOperationException();
			// NamedDefineImpl.render(table, element);
			// renderCategory(table, element);
			// SXElement dbTables = element.append(table_element_dbtables);
			// for (DBTableDefineImpl dbTable : table.dbTables) {
			// this.dbTableXML.render(dbTable,
			// dbTables.append(DBTableDefineImpl.dbtable_tag));
			// }
			// if (table.fields.size() > 2) {
			// SXElement p = element.append(table_element_fields);
			// for (TableFieldDefineImpl field : table.fields) {
			// if (field.isRECID() || field.isRECVER()) {
			// continue;
			// }
			// this.fieldXML.render(field,
			// p.append(TableFieldDefineImpl.field_tag));
			// }
			// }
			// if (table.indexes.size() > 0) {
			// SXElement p = element.append(table_element_indexes);
			// for (IndexDefineImpl index : table.indexes) {
			// this.indexXML.render(index,
			// p.append(IndexDefineImpl.index_tag));
			// }
			// }
			// if (table.relations.size() > 0) {
			// SXElement p = element.append(table_element_relations);
			// for (TableRelationDefineImpl relation : table.relations) {
			// relationXML.render(relation,
			// p.append(TableRelationDefineImpl.xml_name));
			// }
			// }
			// renderHierarchiy(table, element);
		}

		public final void merge(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.merge(table, element);
			mergeCategory(table, element);
			mergeId(table, element);
			for (SXElement e = element.firstChild(table_element_dbtables, DBTableDefineImpl.dbtable_tag); e != null; e = e.nextSibling(DBTableDefineImpl.dbtable_tag)) {
				final String name = e.getString(NamedDefineImpl.xml_attr_name).toUpperCase();
				DBTableDefineImpl dbTable = table.dbTables.find(name);
				if (dbTable == null) {
					dbTable = new DBTableDefineImpl(table, name);
					table.dbTables.add(dbTable);
				}
				this.dbTableXML.merge(dbTable, e);
			}
			for (SXElement e = element.firstChild(table_element_fields, TableFieldDefineImpl.field_tag); e != null; e = e.nextSibling(TableFieldDefineImpl.field_tag)) {
				final String tn = e.getString(FieldXML.field_attr_dbtable).toUpperCase();
				final DBTableDefineImpl dbTable = table.dbTables.get(tn);
				final String fn = e.getString(NamedDefineImpl.xml_attr_name);
				if (fn == null || fn.length() == 0) {
					throw new IllegalArgumentException("字段名称为空。");
				}
				if (fn.equalsIgnoreCase(TableDefineImpl.FIELD_NAME_RECVER) || fn.equalsIgnoreCase(TableDefineImpl.FIELD_NAME_RECID)) {
					continue;
				}
				TableFieldDefineImpl field = table.fields.find(fn);
				if (field == null) {
					field = TableFieldDefineImpl.newForMerge(table, dbTable, fn);
					dbTable.store(field);
					table.fields.add(field);
				} else if (field.dbTable != dbTable) {
					throw migrateField(field, field.dbTable, dbTable);
				}
				this.fieldXML.merge(field, e);
			}
			for (SXElement e = element.firstChild(table_element_indexes, IndexDefineImpl.index_tag); e != null; e = e.nextSibling(IndexDefineImpl.index_tag)) {
				final String in = e.getString(NamedDefineImpl.xml_attr_name);
				final String dbTableName = e.getString(IndexXML.index_attr_dbtable);
				final DBTableDefineImpl dbTable;
				if (dbTableName == null) {
					// td文件,不包含dbTable属性.
					TableFieldDefineImpl tf = table.fields.get(e.firstChild().getString("field"));
					dbTable = tf.getDBTable();
				} else {
					dbTable = table.dbTables.get(dbTableName.toUpperCase());
				}
				IndexDefineImpl index = table.indexes.find(in);
				if (index == null) {
					index = IndexDefineImpl.newForMerge(table, dbTable, in, IndexType.B_TREE);
					table.indexes.add(index);
				} else if (index.dbTable != dbTable) {
					throw migrateIndex(index, index.dbTable, dbTable);
				}
				this.indexXML.merge(index, e);
			}
		}

		private static final String attr_namedb_v19_only = "name-db";

		private final String namedbOf(SXElement element, String def) {
			return element.getAttribute(attr_namedb_v19_only, def);
		}

		private final DBTableXML dbTableXML = new DBTableXML() {

			private static final String dbtable_attr_pkindex = "recid-ix-name-db";
			private static final String dbtable_attr_lpkindex = "lpk-ix-name-db";

			public final void render(DBTableDefineImpl dbTable, SXElement e) {
				NamedDefineImpl.render(dbTable, e);
				e.setString(attr_namedb_v19_only, dbTable.name);
				e.setAttribute(dbtable_attr_pkindex, dbTable.getPkeyName());
				IndexDefineImpl logicalKey = dbTable.owner.logicalKey;
				if (dbTable.isPrimary() && logicalKey != null) {
					e.setAttribute(dbtable_attr_lpkindex, logicalKey.name);
				}
			}

			public final void merge(DBTableDefineImpl dbTable, SXElement e) {
				NamedDefineImpl.merge(dbTable, e);
				dbTable.setNamedb(namedbOf(e, dbTable.namedb()));
			}
		};

		private final FieldXML fieldXML = new FieldXML() {

			private static final String field_attr_type = "type";
			private static final String field_attr_key = "primary-key";
			private static final String field_attr_notnull = "keep-valid";
			private static final String field_element_default = "default";

			public final void render(TableFieldDefineImpl field, SXElement e) {
				NamedDefineImpl.render(field, e);
				e.setString(attr_namedb_v19_only, field.namedb());
				e.setAsType(field_attr_type, field.getType());
				e.setAttribute(field_attr_dbtable, field.getDBTable().name);
				e.setBoolean(field_attr_key, field.isPrimaryKey());
				e.setBoolean(field_attr_notnull, field.isKeepValid());
				if (field.getDefault() != null) {
					field.getDefault().renderInto(e.append(field_element_default));
				}
			}

			public final void merge(TableFieldDefineImpl field, SXElement e) {
				NamedDefineImpl.merge(field, e);
				field.setNamedb(namedbOf(e, field.namedb()));
				field.adjustType(e.getAsType(field_attr_type, null));
				field.setPrimaryKey(e.getBoolean(field_attr_key));
				field.setKeepValid(e.getBoolean(field_attr_notnull));
				SXElement defaultElement = e.firstChild(field_element_default);
				if (defaultElement != null) {
					field.setDefault(ConstExpr.loadConst(defaultElement.firstChild()));
				}
			}
		};

		private final IndexXML indexXML = new IndexXML() {

			public final void render(IndexDefineImpl index, SXElement e) {
				NamedDefineImpl.render(index, e);
				e.setString(attr_namedb_v19_only, index.name);
				e.setBoolean(index_attr_unique, index.isUnique());
				e.setString(index_attr_dbtable, index.dbTable.name);
				for (IndexItemImpl item : index.items) {
					this.render(item, e.append(IndexItemImpl.xml_tag));
				}
			}

			private final void render(IndexItemImpl item, SXElement e) {
				DefineBaseImpl.render(item, e);
				e.setString(item_attr_field, item.field.name);
				e.setBoolean(item_attr_desc, item.desc);
			}

			public final void merge(IndexDefineImpl index, SXElement element) {
				index.setNamedb(namedbOf(element, index.namedb()));
				index.setUnique(element.getBoolean(index_attr_unique));
				for (SXElement e = element.firstChild(IndexItemImpl.xml_tag); e != null; e = e.nextSibling(IndexItemImpl.xml_tag)) {
					final String fn = e.getString(item_attr_field);
					final TableFieldDefineImpl field = index.owner.fields.get(fn);
					IndexItemImpl item = index.findItem(field);
					if (item == null) {
						item = new IndexItemImpl(index, field, e.getBoolean(item_attr_desc));
						index.items.add(item);
					} else {
						this.merge(item, e);
					}
				}
			}

			private final void merge(IndexItemImpl item, SXElement element) {
				DefineBaseImpl.merge(item, element);
				item.desc = element.getBoolean(item_attr_desc);
			}
		};

	},

	/**
	 * 增加了物理表列对象，将其与逻辑表字段对应。
	 * 
	 * @author houchunlei
	 * 
	 */
	V20("2.0") {

		// changing in 2.0 was a failure.

		public final void render(TableDefineImpl define, SXElement element) {
			throw new UnsupportedOperationException();
		}

		public final void merge(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.merge(table, element);
			mergeCategory(table, element);
			StringKeyMap<StringKeyMap<DBColumn>> ts = new StringKeyMap<StringKeyMap<DBColumn>>(true);
			for (SXElement e = element.firstChild(table_element_dbtables, DBTableDefineImpl.dbtable_tag); e != null; e = e.nextSibling(DBTableDefineImpl.dbtable_tag)) {
				// already uppercase
				String tn = e.getString(NamedDefineImpl.xml_attr_name);
				DBTableDefineImpl dbTable = table.dbTables.find(tn);
				if (dbTable == null) {
					dbTable = new DBTableDefineImpl(table, tn);
					table.dbTables.add(dbTable);
				}
				ts.put(tn, this.merge(dbTable, e));
			}
			for (SXElement e = element.firstChild(table_element_fields, TableFieldDefineImpl.field_tag); e != null; e = e.nextSibling(TableFieldDefineImpl.field_tag)) {
				final String fn = e.getString(NamedDefineImpl.xml_attr_name);
				final String tn = e.getString(FieldXML.field_attr_dbtable);
				final DBTableDefineImpl dbTable = table.dbTables.get(tn);
				final String cn = e.getString(field_attr_column);
				TableFieldDefineImpl field = table.fields.find(fn);
				if (field == null) {
					field = TableFieldDefineImpl.newForMerge(table, dbTable, fn);
					table.fields.add(field);
					dbTable.store(field);
					field.setNamedb(cn);
				} else if (field.dbTable != dbTable) {
					throw migrateField(field, field.dbTable, dbTable);
				}
				if (field.isRECID() || field.isRECVER()) {
					continue;
				}
				NamedDefineImpl.merge(field, e);
				DBColumn column = ts.find(tn).find(cn);
				field.adjustType(column.type);
				if (column.defaultValue != null) {
					field.setDefault(column.defaultValue);
				}
				field.setKeepValid(column.notNull);
			}
			final String logicalKey = element.getString(table_attr_logicalkey);
			for (SXElement e = element.firstChild(table_element_indexes, IndexDefineImpl.index_tag); e != null; e = e.nextSibling(IndexDefineImpl.index_tag)) {
				final String in = e.getString(NamedDefineImpl.xml_attr_name);
				if (notEmpty(logicalKey) && in.equals(logicalKey)) {
					if (table.logicalKey == null) {
						table.logicalKey = new IndexDefineImpl(table, table.primary, in, IndexType.B_TREE);
					}
					table.logicalKey.setUnique(true);
					this.indexXML.merge(table.logicalKey, e);
				} else {
					final String tn = e.getString(IndexXML.index_attr_dbtable).toUpperCase();
					final DBTableDefineImpl dbTable = table.dbTables.get(tn);
					IndexDefineImpl index = table.indexes.find(in);
					if (index == null) {
						index = IndexDefineImpl.newForMerge(table, dbTable, in, IndexType.B_TREE);
						table.indexes.add(index);
					} else if (index.dbTable != dbTable) {
						throw migrateIndex(index, index.dbTable, dbTable);
					}
					this.indexXML.merge(index, e);
				}
			}
		}

		static final String field_attr_column = "column";

		static final String table_attr_logicalkey = "logical-key";

		static final String dbtable_element_columns = "columns";
		static final String column_tagname = "column";
		static final String column_element_default = "default";
		static final String column_attr_type = "type";
		static final String column_attr_not_null = "not-null";

		private final StringKeyMap<DBColumn> merge(DBTableDefineImpl table,
				SXElement element) {
			NamedDefineImpl.merge(table, element);
			StringKeyMap<DBColumn> columns = new StringKeyMap<DBColumn>(true);
			for (SXElement e = element.firstChild(dbtable_element_columns, column_tagname); e != null; e = e.nextSibling(column_tagname)) {
				final String name = e.getString(NamedDefineImpl.xml_attr_name);
				final DataType type = e.getAsType(column_attr_type, null);
				DBColumn column = new DBColumn(type);
				if (e.getBoolean(column_attr_not_null)) {
					column.notNull = true;
				}
				SXElement d = e.firstChild(column_element_default);
				if (d != null) {
					column.defaultValue = ConstExpr.loadConst(d.firstChild());
				}
				columns.put(name, column);
			}
			return columns;
		}

		final class DBColumn {

			final DataType type;
			boolean notNull;
			ConstExpr defaultValue;

			DBColumn(DataType type) {
				this.type = type;
			}

		}

		private final IndexXML indexXML = new IndexXML() {

			public final void render(IndexDefineImpl define, SXElement element) {
				throw new UnsupportedOperationException();
			}

			public final void merge(IndexDefineImpl index, SXElement element) {
				NamedDefineImpl.merge(index, element);
				index.setNamedb(index.name.toUpperCase());
				index.setUnique(element.getBoolean(index_attr_unique));
				for (SXElement e = element.firstChild(IndexItemImpl.xml_tag); e != null; e = e.nextSibling(IndexItemImpl.xml_tag)) {
					String fn = e.getString(item_attr_field);
					TableFieldDefineImpl field = index.owner.fields.get(fn);
					IndexItemImpl item = index.findItem(field);
					if (item == null) {
						item = IndexItemImpl.newForMerge(index, field);
						index.items.add(item);
					}
					this.merge(item, e);
				}
			}

			private final void merge(IndexItemImpl item, SXElement element) {
				DefineBaseImpl.merge(item, element);
				item.desc = element.getBoolean(item_attr_desc, item.desc);
			}

		};

	},

	/**
	 * 去掉了物理表列对象，XML结构与1.9类型。并且省略了各元素的属性。
	 * 
	 * @author houchunlei
	 * 
	 */
	V25("2.5") {

		public final void render(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.render(table, element);
			this.renderVer(element);
			renderId(table, element);
			renderCategory(table, element);
			renderTableType(table, element);//序列化逻辑表属性
			SXElement dbTables = element.append(table_element_dbtables);
			for (DBTableDefineImpl dbTable : table.dbTables) {
				dbTableXMLSinceV25.render(dbTable, dbTables.append(DBTableDefineImpl.dbtable_tag));
			}
			if (table.fields.size() > 2) {
				SXElement p = element.append(table_element_fields);
				for (TableFieldDefineImpl field : table.fields) {
					if (field.isRECID() || field.isRECVER()) {
						continue;
					}
					fieldXMLSinceV25.render(field, p.append(TableFieldDefineImpl.field_tag));
				}
			}
			if (table.indexes.size() > 0 || table.logicalKey != null) {
				SXElement indexes = element.append(table_element_indexes);
				if (table.indexes.size() > 0) {
					for (IndexDefineImpl index : table.indexes) {
						if (index.getType() == IndexType.B_TREE) {
							indexXMLSinceV25.render(index, indexes.append(IndexDefineImpl.index_tag));
						} else {
							bitmapIndexXML.render(index, indexes.append(IndexDefineImpl.bitmap_tag));
						}
					}
				}
				if (table.logicalKey != null) {
					indexXMLSinceV25.render(table.logicalKey, indexes.append(IndexDefineImpl.index_tag));
					element.setString(table_attr_logical_key_since25, table.logicalKey.name);
				}
			}
			if (table.relations.size() > 0) {
				SXElement p = element.append(table_element_relations);
				for (TableRelationDefineImpl relation : table.relations) {
					relationXML.render(relation, p.append(TableRelationDefineImpl.xml_name));
				}
			}
			renderHierarchiy(table, element);
		}

		public final void merge(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.merge(table, element);
			mergeCategory(table, element);
			mergeId(table, element);
			mergeTableType(table, element);//反序列化逻辑表属性
			for (SXElement e = element.firstChild(table_element_dbtables, DBTableDefineImpl.dbtable_tag); e != null; e = e.nextSibling(DBTableDefineImpl.dbtable_tag)) {
				String tn = e.getString(NamedDefineImpl.xml_attr_name);
				DBTableDefineImpl dbTable = table.dbTables.find(tn);
				if (dbTable == null) {
					dbTable = new DBTableDefineImpl(table, tn);
					table.dbTables.add(dbTable);
				}
				dbTableXMLSinceV25.merge(dbTable, e);
			}
			for (SXElement e = element.firstChild(table_element_fields, TableFieldDefineImpl.field_tag); e != null; e = e.nextSibling(TableFieldDefineImpl.field_tag)) {
				final String tn = e.getString(FieldXML.field_attr_dbtable);
				final DBTableDefineImpl dbTable = notEmpty(tn) ? table.dbTables.get(tn) : table.primary;
				final String fn = e.getString(NamedDefineImpl.xml_attr_name);
				TableFieldDefineImpl field = table.fields.find(fn);
				if (field == null) {
					field = TableFieldDefineImpl.newForMerge(table, dbTable, fn);
					table.fields.add(field);
					dbTable.store(field);
				} else if (field.dbTable != dbTable) {
					throw migrateField(field, field.dbTable, dbTable);
				}
				fieldXMLSinceV25.merge(field, e);
			}
			final String logicalKey = element.getString(table_attr_logical_key_since25);
			for (SXElement e = element.firstChild(table_element_indexes, IndexDefineImpl.index_tag); e != null; e = e.nextSibling(IndexDefineImpl.index_tag)) {
				final String in = e.getString(NamedDefineImpl.xml_attr_name);
				if (notEmpty(logicalKey) && in.equals(logicalKey)) {
					if (table.logicalKey == null) {
						table.logicalKey = new IndexDefineImpl(table, table.primary, in, IndexType.B_TREE);
					}
					table.logicalKey.setUnique(true);
					indexXMLSinceV25.merge(table.logicalKey, e);
				} else {
					final String tn = e.getString(IndexXML.index_attr_dbtable);
					final DBTableDefineImpl dbTable = notEmpty(tn) ? table.dbTables.get(tn.toUpperCase()) : table.primary;
					IndexDefineImpl index = table.indexes.find(in);
					if (index == null) {
						index = IndexDefineImpl.newForMerge(table, dbTable, in, IndexType.B_TREE);
						table.indexes.add(index);
					} else if (index.dbTable != dbTable) {
						throw migrateIndex(index, index.dbTable, dbTable);
					}
					indexXMLSinceV25.merge(index, e);
				}
			}
			for (SXElement e = element.firstChild(table_element_indexes, IndexDefineImpl.bitmap_tag); e != null; e = e.nextSibling(IndexDefineImpl.bitmap_tag)) {
				final String in = e.getString(NamedDefineImpl.xml_attr_name);
				final String tn = e.getString(IndexXML.index_attr_dbtable);
				final DBTableDefineImpl dbTable = notEmpty(tn) ? table.dbTables.get(tn.toUpperCase()) : table.primary;
				IndexDefineImpl index = table.indexes.find(in);
				if (index == null) {
					index = IndexDefineImpl.newForMerge(table, dbTable, in, IndexType.BITMAP);
					table.indexes.add(index);
				} else if (index.dbTable != dbTable) {
					throw migrateIndex(index, index.dbTable, dbTable);
				}
				bitmapIndexXML.merge(index, e);
			}
			mergeHierarchy(table, element);
		}
	},

	/**
	 * 
	 * <ul>
	 * <li>表、字段、表关系的name属性会自动转换成大写，使用display属性，存储原始的未大写化的名称。
	 * <li>物理表对象在旧版就会自动将name大写，保持不变。
	 * </ul>
	 * 
	 * 
	 * @author houchunlei
	 * 
	 */
	@Deprecated
	V35("3.5") {

		public final void render(TableDefineImpl table, SXElement element) {
			V25.render(table, element);
		}

		public final void merge(TableDefineImpl table, SXElement element) {
			NamedDefineImpl.merge(table, element);
			mergeCategory(table, element);
			mergeId(table, element);
			for (SXElement e = element.firstChild(table_element_dbtables, DBTableDefineImpl.dbtable_tag); e != null; e = e.nextSibling(DBTableDefineImpl.dbtable_tag)) {
				final String tn = e.getString(NamedDefineImpl.xml_attr_name);
				DBTableDefineImpl dbTable = table.dbTables.find(tn);
				if (dbTable == null) {
					dbTable = new DBTableDefineImpl(table, tn);
					table.dbTables.add(dbTable);
				}
				dbTableXMLSinceV25.merge(dbTable, e);
			}
			for (SXElement e = element.firstChild(table_element_fields, TableFieldDefineImpl.field_tag); e != null; e = e.nextSibling(TableFieldDefineImpl.field_tag)) {
				final String tn = e.getString(FieldXML.field_attr_dbtable);
				final DBTableDefineImpl dbTable = notEmpty(tn) ? table.dbTables.get(tn) : table.primary;
				// using diplay first !!!!!
				final String fn = coalesceDisplayAndName(e);
				TableFieldDefineImpl field = table.fields.find(fn);
				if (field == null) {
					field = TableFieldDefineImpl.newForMerge(table, dbTable, fn);
					table.fields.add(field);
					dbTable.store(field);
				} else if (field.dbTable != dbTable) {
					throw migrateField(field, field.dbTable, dbTable);
				}
				fieldXMLSinceV25.merge(field, e);
			}
			final String logicalKeyName = element.getString(table_attr_logical_key_since25);
			for (SXElement e = element.firstChild(table_element_indexes, IndexDefineImpl.index_tag); e != null; e = e.nextSibling(IndexDefineImpl.index_tag)) {
				final String in = e.getString(NamedDefineImpl.xml_attr_name);
				if (notEmpty(logicalKeyName) && in.equals(logicalKeyName)) {
					if (table.logicalKey == null) {
						table.logicalKey = new IndexDefineImpl(table, table.primary, in, IndexType.B_TREE);
					}
					table.logicalKey.setUnique(true);
					indexXMLSinceV25.merge(table.logicalKey, e);
				} else {
					final String tn = e.getString(IndexXML.index_attr_dbtable);
					final DBTableDefineImpl dbTable = notEmpty(tn) ? table.dbTables.get(tn.toUpperCase()) : table.primary;
					IndexDefineImpl index = table.indexes.find(in);
					if (index == null) {
						index = IndexDefineImpl.newForMerge(table, dbTable, in, IndexType.B_TREE);
						table.indexes.add(index);
					} else if (index.dbTable != dbTable) {
						throw migrateIndex(index, index.dbTable, dbTable);
					}
					indexXMLSinceV25.merge(index, e);
				}
			}
			mergeHierarchy(table, element);
		}
	},

	@Deprecated
	V351("3.51") {

		public final void render(TableDefineImpl table, SXElement element) {
			V25.render(table, element);
		}

		public final void merge(TableDefineImpl table, SXElement element) {
			TableXML.V25.merge(table, element);
		}
	};

	final String ver;

	private TableXML(String ver) {
		this.ver = ver;
	}

	static final TableXML detect(SXElement element) {
		String ver = element.getAttribute(table_attr_ver);
		for (TableXML tx : TableXML.values()) {
			if (tx.ver.equals(ver)) {
				return tx;
			}
		}
		return V19;
	}

	private static final String table_attr_ver = "ver";
	private static final String table_attr_category = "category";
	private static final String table_attr_id = "id";

	final void renderVer(SXElement element) {
		element.setAttribute(table_attr_ver, this.ver);
	}

	private static final void renderCategory(TableDefineImpl table,
			SXElement element) {
		element.setAttribute(table_attr_category, table.getCategory());
	}

	private static final void mergeCategory(TableDefineImpl table,
			SXElement element) {
		table.category = element.getAttribute(table_attr_category, table.category);
	}

	private static final void renderId(TableDefineImpl table, SXElement element) {
		if (table.id != null) {
			element.setAttribute(table_attr_id, table.id.toString());
		}
	}

	private static final void mergeId(TableDefineImpl table, SXElement element) {
		String s = element.getAttribute(table_attr_id);
		if (s != null && s.length() == 32) {
			table.id = GUID.valueOf(s);
		}
	}
	
	private static final void renderTableType(TableDefineImpl table, SXElement element) {
		if(table.tableType != null) {
			element.setAttribute(table_element_table_type, table.tableType.name());
		}
	}
	
	private static final void mergeTableType(TableDefineImpl table, SXElement element) {
		String tableType = element.getAttribute(table_element_table_type);
		if(tableType != null) {
			if(tableType.equals(TableType.GLOBAL_TEMPORARY.name())) {
				table.setTableType(TableType.GLOBAL_TEMPORARY);
			} else {
				table.setTableType(TableType.NORMAL);
			}
		} else {
			table.setTableType(TableType.NORMAL);
		}
	}
	/**
	 * 逻辑表的属性，指示其逻辑主键的名称。在indexes元素中，同名称的索引，作为逻辑主键，不加入Table.indexes列表中。
	 */
	static final String table_attr_logical_key_since25 = "logical-key";

	static final String table_element_dbtables = "dbtables";
	static final String table_element_fields = "fields";
	static final String table_element_indexes = "indexs";
	static final String table_element_relations = "relations";
	static final String table_element_hierarchies = "hierarchies";
	static final String table_element_table_type = "tableType";//逻辑表类型属性
	
	static final UnsupportedOperationException migrateField(
			TableFieldDefineImpl field, DBTableDefineImpl from,
			DBTableDefineImpl to) {
		// HCL
		return new UnsupportedOperationException("尝试跨物理表移动字段定义[" + field.name + "],从物理表[" + from.name + "]到[" + to.name + "].");
	}

	static final UnsupportedOperationException migrateIndex(
			IndexDefineImpl index, DBTableDefineImpl from, DBTableDefineImpl to) {
		// HCL
		return new UnsupportedOperationException("尝试跨物理表移动索引定义[" + index.name + "],从物理表[" + from.name + "]到[" + to.name + "].");
	}

	static abstract class DBTableXML implements DefineXML<DBTableDefineImpl> {

		static final String dbtable_attr_namedb_since25 = "namedb";
		static final String dbtable_attr_pkname_since25 = "pk-name";
	}

	static abstract class FieldXML implements DefineXML<TableFieldDefineImpl> {

		static final String field_attr_dbtable = "dbtable";
		static final String field_attr_type_since25 = "type";
		static final String field_attr_notnull_since25 = "notnull";
		static final String field_attr_templated_since25 = "templated";
		static final String field_attr_namedb_since25 = "namedb";
		static final String field_element_default_since25 = "default";
	}

	static abstract class IndexXML implements DefineXML<IndexDefineImpl> {

		static final String index_attr_dbtable = "dbtable";
		static final String index_attr_unique = "unique";
		static final String index_attr_namedb_since25 = "namedb";

		static final String item_attr_field = "field";
		static final String item_attr_desc = "desc";
	}

	private static final DBTableXML dbTableXMLSinceV25 = new DBTableXML() {

		public final void render(DBTableDefineImpl dbTable, SXElement element) {
			NamedDefineImpl.render(dbTable, element);
			element.setString(dbtable_attr_namedb_since25, dbTable.namedb());
			element.setString(dbtable_attr_pkname_since25, dbTable.getPkeyName());
		}

		public final void merge(DBTableDefineImpl dbTable, SXElement element) {
			NamedDefineImpl.merge(dbTable, element);
			dbTable.setNamedb(element.getString(dbtable_attr_namedb_since25));
			dbTable.setPkeyName(element.getString(dbtable_attr_pkname_since25));
		}

	};

	private static final FieldXML fieldXMLSinceV25 = new FieldXML() {

		public final void render(TableFieldDefineImpl field, SXElement element) {
			NamedDefineImpl.render(field, element);
			if (!field.dbTable.isPrimary()) {
				element.setString(field_attr_dbtable, field.dbTable.name);
			}
			if (!field.name.equals(field.namedb())) {
				element.setString(field_attr_namedb_since25, field.namedb());
			}
			element.setAsType(field_attr_type_since25, field.getType());
			if (field.isKeepValid()) {
				element.setBoolean(field_attr_notnull_since25, true);
			}
			if (field.templated) {
				element.setBoolean(field_attr_templated_since25, true);
			}
			if (field.getDefault() != null) {
				field.getDefault().renderInto(element.append(field_element_default_since25));
			}
		}

		public final void merge(TableFieldDefineImpl field, SXElement element) {
			NamedDefineImpl.merge(field, element);
			final String namedb = element.getString(field_attr_namedb_since25);
			if (notEmpty(namedb)) {
				field.setNamedb(namedb);
			}
			field.adjustType(element.getAsType(field_attr_type_since25, null));
			field.setKeepValid(element.getBoolean(field_attr_notnull_since25));
			field.setTemplated(element.getBoolean(field_attr_templated_since25));
			SXElement defaultElement = element.firstChild(field_element_default_since25);
			if (defaultElement != null) {
				field.setDefault(ConstExpr.loadConst(defaultElement.firstChild()));
			}
		}
	};

	private static final IndexXML bitmapIndexXML = new IndexXML() {

		public void render(IndexDefineImpl index, SXElement element) {
			NamedDefineImpl.render(index, element);
			element.setString(index_attr_namedb_since25, index.namedb());
			if (!index.dbTable.isPrimary()) {
				element.setString(index_attr_dbtable, index.dbTable.name);
			}
			for (IndexItemImpl item : index.items) {
				this.render(item, element.append(IndexItemImpl.xml_tag));
			}
		}

		private final void render(IndexItemImpl item, SXElement element) {
			DefineBaseImpl.render(item, element);
			element.setString(item_attr_field, item.field.name);
			if (item.desc) {
				element.setBoolean(item_attr_desc, true);
			}
		}

		public void merge(IndexDefineImpl index, SXElement element) {
			NamedDefineImpl.merge(index, element);
			index.setNamedb(element.getString(index_attr_namedb_since25));
			for (SXElement e = element.firstChild(IndexItemImpl.xml_tag); e != null; e = e.nextSibling(IndexItemImpl.xml_tag)) {
				String fn = e.getString(item_attr_field);
				TableFieldDefineImpl field = index.owner.fields.get(fn);
				IndexItemImpl item = index.findItem(field);
				if (item == null) {
					item = IndexItemImpl.newForMerge(index, field);
					index.items.add(item);
				}
				this.merge(item, e);
			}
		}

		private final void merge(IndexItemImpl item, SXElement element) {
			DefineBaseImpl.merge(item, element);
			item.desc = element.getBoolean(item_attr_desc, item.desc);
		}
	};

	private static final IndexXML indexXMLSinceV25 = new IndexXML() {

		public final void render(IndexDefineImpl index, SXElement element) {
			NamedDefineImpl.render(index, element);
			element.setString(index_attr_namedb_since25, index.namedb());
			if (index.isUnique()) {
				element.setBoolean(index_attr_unique, true);
			}
			if (!index.dbTable.isPrimary()) {
				element.setString(index_attr_dbtable, index.dbTable.name);
			}
			for (IndexItemImpl item : index.items) {
				this.render(item, element.append(IndexItemImpl.xml_tag));
			}
		}

		private final void render(IndexItemImpl item, SXElement element) {
			DefineBaseImpl.render(item, element);
			element.setString(item_attr_field, item.field.name);
			if (item.desc) {
				element.setBoolean(item_attr_desc, true);
			}
		}

		public final void merge(IndexDefineImpl index, SXElement element) {
			NamedDefineImpl.merge(index, element);
			index.setNamedb(element.getString(index_attr_namedb_since25));
			index.setUnique(element.getBoolean(index_attr_unique));
			for (SXElement e = element.firstChild(IndexItemImpl.xml_tag); e != null; e = e.nextSibling(IndexItemImpl.xml_tag)) {
				String fn = e.getString(item_attr_field);
				TableFieldDefineImpl field = index.owner.fields.get(fn);
				IndexItemImpl item = index.findItem(field);
				if (item == null) {
					item = IndexItemImpl.newForMerge(index, field);
					index.items.add(item);
				}
				this.merge(item, e);
			}
		}

		private final void merge(IndexItemImpl item, SXElement element) {
			DefineBaseImpl.merge(item, element);
			item.desc = element.getBoolean(item_attr_desc, item.desc);
		}
	};

	static final DefineXML<TableRelationDefineImpl> relationXML = new DefineXML<TableRelationDefineImpl>() {

		static final String relation_attr_type = "type";
		static final String relation_element_condition = "condition";

		public void render(TableRelationDefineImpl relation, SXElement element) {
			NamedDefineImpl.render(relation, element);
			element.setString(TableRef.xml_attr_table, relation.target.name);
			element.setEnum(relation_attr_type, relation.type);
			if (relation.condition != null) {
				relation.condition.renderInto(element.append(relation_element_condition));
			}
		}

		public final void merge(TableRelationDefineImpl relation,
				SXElement element) {
			NamedDefineImpl.merge(relation, element);
			relation.type = element.getEnum(TableRelationType.class, relation_attr_type, relation.type);
			SXElement e = element.firstChild(relation_element_condition, null);
			if (e != null) {
				relation.condition = ConditionalExpr.loadCondition(e, relation.owner, null);
			}
		}
	};

	private static final DefineXML<HierarchyDefineImpl> hierarchyXML = new DefineXML<HierarchyDefineImpl>() {

		static final String hierarchy_attr_maxlevel = "maxlevel";
		static final String hierarchy_attr_table = "table";
		static final String hierarchy_attr_pkindex = "pk-index";
		static final String hierarchy_attr_pathindex = "path-index";

		public void render(HierarchyDefineImpl hierarchy, SXElement element) {
			NamedDefineImpl.render(hierarchy, element);
			element.setInt(hierarchy_attr_maxlevel, hierarchy.maxlevel);
			element.setString(hierarchy_attr_table, hierarchy.tableName);
			element.setString(hierarchy_attr_pkindex, hierarchy.pkIndex);
			element.setString(hierarchy_attr_pathindex, hierarchy.pathIndex);
		}

		public void merge(HierarchyDefineImpl hierarchy, SXElement element) {
			NamedDefineImpl.merge(hierarchy, element);
			hierarchy.maxlevel = element.getInt(hierarchy_attr_maxlevel, hierarchy.maxlevel);
			hierarchy.tableName = element.getAttribute(hierarchy_attr_table, hierarchy.tableName);
			hierarchy.pkIndex = element.getAttribute(hierarchy_attr_pkindex, hierarchy.pkIndex);
			hierarchy.pathIndex = element.getAttribute(hierarchy_attr_pathindex, hierarchy.pathIndex);
		}
	};

	static final void renderRelation(TableDefineImpl table, SXElement element) {
		if (table.relations.size() > 0) {
			SXElement p = element.append(table_element_relations);
			for (TableRelationDefineImpl relation : table.relations) {
				relationXML.render(relation, p.append(TableRelationDefineImpl.xml_name));
			}
		}
	}

	static final void renderHierarchiy(TableDefineImpl table, SXElement element) {
		if (table.hierarchies.size() > 0) {
			SXElement p = element.append(table_element_hierarchies);
			for (HierarchyDefineImpl hierarchy : table.hierarchies) {
				hierarchyXML.render(hierarchy, p.append(HierarchyDefineImpl.xml_name));
			}
		}
	}

	static final void mergeHierarchy(TableDefineImpl table, SXElement element) {
		for (SXElement e = element.firstChild(table_element_hierarchies, HierarchyDefineImpl.xml_name); e != null; e = e.nextSibling(HierarchyDefineImpl.xml_name)) {
			String hn = e.getString(NamedDefineImpl.xml_attr_name);
			HierarchyDefineImpl hierarchy = table.hierarchies.find(hn);
			if (hierarchy == null) {
				hierarchy = HierarchyDefineImpl.newForMerge(table, hn);
				table.hierarchies.add(hierarchy);
			}
			hierarchyXML.merge(hierarchy, e);
		}
	}

	static final String coalesceDisplayAndName(SXElement element) {
		final String display = element.getString(attr_display_only35);
		if (display != null && display.length() > 0) {
			return display;
		}
		final String name = element.getString(NamedDefineImpl.xml_attr_name);
		if (name == null || name.length() == 0) {
			throw new IllegalStateException();
		}
		return name;
	}

	static final <T> T coalesce(T v, T... vs) {
		if (v != null) {
			return v;
		} else {
			for (T o : vs) {
				if (o != null) {
					return o;
				}
			}
			return null;
		}
	}

	static boolean notEmpty(String s) {
		return s != null && s.length() != 0;
	}

	/**
	 * table,field,relation
	 */
	static final String attr_display_only35 = "display";

}
