package com.jiuqi.dna.core.impl;

import static com.jiuqi.dna.core.def.table.UnsupportedDbTableModificationException.NOT_EMPTY_TABLE;
import static com.jiuqi.dna.core.def.table.UnsupportedDbTableModificationException.PRIMARY_TABLE;
import static com.jiuqi.dna.core.def.table.UnsupportedTableFieldModificationException.INDEX_FIELD;
import static com.jiuqi.dna.core.def.table.UnsupportedTableFieldModificationException.KEY_FIELD;
import static com.jiuqi.dna.core.def.table.UnsupportedTableFieldModificationException.PARTITION_FIELD;
import static com.jiuqi.dna.core.def.table.UnsupportedTableFieldModificationException.SYSTEM_FIELD;
import static com.jiuqi.dna.core.def.table.UnsupportedTableModificationException.ACTION_MOVE;
import static com.jiuqi.dna.core.def.table.UnsupportedTableModificationException.ACTION_REMOVE;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.table.DBTableDefine;
import com.jiuqi.dna.core.def.table.HierarchyDeclare;
import com.jiuqi.dna.core.def.table.IndexType;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDeclare;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.def.table.TableRelationType;
import com.jiuqi.dna.core.def.table.TableType;
import com.jiuqi.dna.core.def.table.UnsupportedDbTableModificationException;
import com.jiuqi.dna.core.def.table.UnsupportedTableFieldModificationException;
import com.jiuqi.dna.core.exception.NamedDefineExistingException;
import com.jiuqi.dna.core.exception.NoPartitionDefineException;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeDelayAction;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.Typable;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.TypeDetectorBase;

/**
 * 逻辑表定义实现类
 * 
 * @author houchunlei
 */
public final class TableDefineImpl extends NamedDefineImpl implements
		TableDeclare, Relation, RelationRefOwner, ContainerListener,
		Declarative<TableDeclarator> {

	@Override
	protected final boolean isNameCaseSensitive() {
		return false;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.TABLE;
	}

	public final TableDeclarator getDeclarator() {
		return this.declarator;
	}

	public final TableDefineImpl getRootType() {
		return this;
	}

	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inTable(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.TABLE_H);
		this.digestAuthAndName(digester);
		short c = (short) this.fields.size();
		digester.update(c);
		for (int i = 0; i < c; i++) {
			this.fields.get(i).digestType(digester);
		}
	}

	public final int getTupleElementCount() {
		return this.fields.size();
	}

	public final Typable getTupleElementType(int index) {
		return this.fields.get(index);
	}

	public final boolean isOriginal() {
		return this.declarator != null;
	}

	public final DBTableDefineImpl getPrimaryDBTable() {
		return this.primary;
	}

	public final NamedDefineContainerImpl<DBTableDefineImpl> getDBTables() {
		return this.dbTables;
	}

	public final TableFieldDefineImpl f_RECID() {
		return this.f_recid;
	}

	public final TableFieldDefineImpl f_RECVER() {
		return this.f_recver;
	}

	public final NamedDefineContainerImpl<TableFieldDefineImpl> getFields() {
		return this.fields;
	}

	public final TableFieldDefineImpl getColumn(String columnName) {
		return this.fields.get(columnName);
	}

	public final TableFieldDefineImpl findColumn(String columnName) {
		return this.fields.find(columnName);
	}

	public final NamedDefineContainerImpl<IndexDefineImpl> getIndexes() {
		return this.indexes;
	}

	public final NamedDefineContainerImpl<TableRelationDefineImpl> getRelations() {
		return this.relations;
	}

	public final NamedDefineContainerImpl<? extends HierarchyDeclare> getHierarchies() {
		return this.hierarchies;
	}

	public final DBTableDefineImpl newDBTable(String tableName) {
		return this.newDbTable(tableName);
	}

	public final TableFieldDefineImpl newPrimaryField(String name, DataType type) {
		return this.newField(this.primary, name, type, true);
	}

	public final TableFieldDefineImpl newField(String name, DataType type) {
		return this.newField(this.primary, name, type, false);
	}

	public final TableFieldDefineImpl newField(String name, DataType type,
			DBTableDefine dbTable) {
		if (dbTable == null) {
			throw new NullArgumentException("物理表定义");
		}
		return this.newField((DBTableDefineImpl) dbTable, name, type, false);
	}

	public final IndexDefineImpl newIndex(String name) {
		return this.newIndex(this.primary, name, false, IndexType.B_TREE);
	}

	public final IndexDefineImpl newIndex(String name, IndexType type) {
		return this.newIndex(this.primary, name, false, type);
	}

	public final IndexDefineImpl newIndex(String name, TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("索引字段");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		IndexDefineImpl index = this.newIndex(f.dbTable, name, false, IndexType.B_TREE);
		index.addItem(f, false);
		return index;
	}

	public final IndexDefineImpl newIndex(String name, TableFieldDefine field,
			TableFieldDefine... others) {
		if (field == null) {
			throw new NullArgumentException("索引字段");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		IndexDefineImpl index = this.newIndex(f.dbTable, name, false, IndexType.B_TREE);
		index.addItem(f, false);
		for (TableFieldDefine o : others) {
			index.addItem(o, false);
		}
		return index;
	}

	public final IndexDefineImpl newIndex(String name, IndexType type,
			TableFieldDefine field, TableFieldDefine... others) {
		if (field == null) {
			throw new NullArgumentException("索引字段");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		IndexDefineImpl index = this.newIndex(f.dbTable, name, false, type);
		index.addItem(f, false);
		for (TableFieldDefine o : others) {
			index.addItem(o, false);
		}
		return index;
	}

	public final TableRelationDefineImpl newRelation(String name,
			TableDefine target, TableRelationType type) {
		if (target == null) {
			throw new NullArgumentException("关系目标表定义");
		}
		return this.newRelation(name, (TableDefineImpl) target, type);
	}

	public final TableRelationDefineImpl newRelation(String name,
			TableDeclarator target, TableRelationType type) {
		if (target == null) {
			throw new NullArgumentException("关系目标表定义");
		}
		return this.newRelation(name, (TableDefineImpl) target.getDefine(), type);
	}

	public final TableRelationDefineImpl newRelation(String name,
			TableFieldDefine selfField, TableDeclarator target,
			TableFieldDefine targetField, TableRelationType type) {
		if (target == null) {
			throw new NullArgumentException("关系定义的目标表");
		}
		return this.newRelation(name, selfField, target.getDefine(), targetField, type);
	}

	public final TableRelationDefineImpl newRelation(String name,
			TableFieldDefine selfField, TableDefine target,
			TableFieldDefine targetField, TableRelationType type) {
		if (target == null) {
			throw new NullArgumentException("关系定义的目标表");
		}
		if (selfField == null || selfField.getOwner() != this) {
			throw new NullArgumentException("等值表关系的本表字段");
		} else if (selfField.getOwner() != this) {
			throw new IllegalArgumentException("新建等值表关系定义[" + name + "]中,指定的本表字段[" + selfField.getName() + "]不属于当前逻辑表定义[" + this.name + "].");
		}
		if (targetField == null) {
			throw new NullArgumentException("等值表关系的目标字段");
		} else if (targetField.getOwner() != target) {
			throw new IllegalArgumentException("新建等值表关系定义[" + name + "]中,指定的目标表字段[" + targetField.getName() + "]不属于目标逻辑表定义[" + target.getName() + "].");
		}
		TableRelationDefineImpl relation = this.newRelation(name, (TableDefineImpl) target, type);
		relation.setJoinCondition(this.expOf(selfField).xEq(relation.expOf(targetField)));
		return relation;
	}

	public final HierarchyDefineImpl newHierarchy(String name, int maxlevel) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("级次名");
		}
		if (maxlevel < 0 || HierarchyDefineImpl.MAX_LEVEL < maxlevel) {
			throw new IllegalArgumentException("错误或不支持的最大级次值[" + maxlevel + "].");
		}
		if (this.hierarchies.find(name) != null) {
			throw existing(name, "级次");
		}
		HierarchyDefineImpl hierarchy = new HierarchyDefineImpl(this, name, maxlevel);
		this.hierarchies.add(hierarchy);
		return hierarchy;
	}

	public final boolean isPartitioned() {
		return this.partfields.size() > 0;
	}

	public final NamedDefineContainerImpl<TableFieldDefineImpl> getPartitionFields() {
		return this.partfields;
	}

	public final void setPartitionFields(TableFieldDefine field,
			TableFieldDefine... others) {
		this.partfields.clear();
		this.addPartField(field);
		if (others != null) {
			for (TableFieldDefine other : others) {
				this.addPartField(other);
			}
		}
	}

	public final void addPartitionField(TableFieldDefine field,
			TableFieldDefine... others) {
		this.addPartField(field);
		if (others != null) {
			for (TableFieldDefine other : others) {
				this.addPartField(other);
			}
		}
	}

	public final int getMaxPartitionCount() {
		if (!this.isPartitioned()) {
			throw new NoPartitionDefineException(this);
		}
		return this.maxPartCount;
	}

	public final void setMaxPartitionCount(int maxPartCount) {
		if (!this.isPartitioned()) {
			throw new NoPartitionDefineException(this);
		}
		if (maxPartCount < 0) {
			throw new IllegalArgumentException("错误的最大分区个数[" + maxPartCount + "].");
		}
		this.maxPartCount = maxPartCount;
	}

	public final int getPartitionSuggestion() {
		if (!this.isPartitioned()) {
			throw new NoPartitionDefineException(this);
		}
		return this.partSuggestion;
	}

	public final void setParitionSuggestion(int suggestion) {
		if (suggestion < 0) {
			throw new IllegalArgumentException("错误的分区建议行大小[" + suggestion + "].");
		}
		this.partSuggestion = suggestion;
	}

	public final String getCategory() {
		return this.category;
	}

	public final void setCategory(String category) {
		// so strange !!!
		if (category == null || category.length() == 0) {
			return;
		}
		this.category = category;
	}

	public final TableFieldRefImpl expOf(TableFieldDefine field) {
		return this.selfRef.expOf(field);
	}

	public static final String DUMMY_NAME = "DUMMY";

	public static final TableDefineImpl DUMMY = new TableDefineImpl();

	public static final String FIELD_NAME_RECID = "RECID";
	public static final String FIELD_NAME_RECVER = "RECVER";

	public static final String FIELD_DBNAME_RECVER = "RECVER";
	public static final String FIELD_DBNAME_RECID = "RECID";

	public static final String FILED_TITLE_RECID = "行标识";
	public static final String FILED_TITLE_RECVER = "行版本";

	/**
	 * 物理主键的前缀名
	 */
	static final String DNA_PK_PREFIX = "PK_";

	/**
	 * 逻辑主键的前缀名
	 */
	static final String DNA_LK_PREFIX = "LK_";

	/**
	 * 最大级次定义数
	 */
	static final int MAX_HIERARCHY_SIZE = 32;

	/**
	 * 表定义元数据数据库存储的RECID
	 */
	GUID id;

	/**
	 * 表定义声明
	 */
	public final TableDeclarator declarator;

	/**
	 * 行标识字段
	 */
	public final TableFieldDefineImpl f_recid;

	/**
	 * 行版本字段
	 */
	public final TableFieldDefineImpl f_recver;

	/**
	 * 类别,基础数据,报表主题,单据主题等
	 */
	public String category;

	/**
	 * 主物理表
	 */
	public final DBTableDefineImpl primary;

	/**
	 * 物理表定义列表
	 */
	public final NamedDefineContainerImpl<DBTableDefineImpl> dbTables;

	/**
	 * 字段定义列表
	 */
	public final NamedDefineContainerImpl<TableFieldDefineImpl> fields;

	/**
	 * 逻辑主键索引
	 */
	public IndexDefineImpl logicalKey;

	/**
	 * 索引定义列表
	 */
	public final NamedDefineContainerImpl<IndexDefineImpl> indexes;

	/**
	 * 表关系定义列表
	 */
	public final NamedDefineContainerImpl<TableRelationDefineImpl> relations;

	/**
	 * 级次定义
	 */
	public final NamedDefineContainerImpl<HierarchyDefineImpl> hierarchies;

	/**
	 * 当前表自引用,表关系中使用
	 */
	final TableSelfRef selfRef;
	
	/**
	 * 逻辑表类型（普通表、全局临时表）
	 */
	public TableType tableType;

	private TableDefineImpl() {
		super(DUMMY_NAME);
		this.tableType = TableType.NORMAL;//初始化逻辑表类型为普通表
		this.declarator = null;
		this.dbTables = new NamedDefineContainerImpl<DBTableDefineImpl>(false, this);
		this.fields = new NamedDefineContainerImpl<TableFieldDefineImpl>(false, this);
		this.indexes = new NamedDefineContainerImpl<IndexDefineImpl>(true, this);
		this.relations = new NamedDefineContainerImpl<TableRelationDefineImpl>(false, this);
		this.hierarchies = new NamedDefineContainerImpl<HierarchyDefineImpl>(false, this);
		this.partfields = new NamedDefineContainerImpl<TableFieldDefineImpl>(false, this);
		this.dbTables.add(this.primary = new DBTableDefineImpl(this, this.name));
		this.f_recid = null;
		this.f_recver = null;
		this.selfRef = new TableSelfRef(this);
	}

	public TableDefineImpl(String name, TableDeclarator declarator) {
		super(name);
		this.declarator = declarator;
		this.dbTables = new NamedDefineContainerImpl<DBTableDefineImpl>(false, this);
		this.fields = new NamedDefineContainerImpl<TableFieldDefineImpl>(false, this);
		this.indexes = new NamedDefineContainerImpl<IndexDefineImpl>(true, this);
		this.relations = new NamedDefineContainerImpl<TableRelationDefineImpl>(false, this);
		this.hierarchies = new NamedDefineContainerImpl<HierarchyDefineImpl>(false, this);
		this.partfields = new NamedDefineContainerImpl<TableFieldDefineImpl>(false, this);
		this.primary = new DBTableDefineImpl(this, name);
		this.dbTables.add(this.primary);
		this.fields.add(this.f_recid = this.newRecid(this));
		this.primary.store(this.f_recid);
		this.fields.add(this.f_recver = this.newRecver(this));
		this.primary.store(this.f_recver);
		this.selfRef = new TableSelfRef(this);
		this.tableType = declarator != null ? declarator.getTableType() : TableType.NORMAL;
	}
	
	public void setTableType(TableType type){
	    this.tableType = type;
    }

	public TableType getTableType(){
	    return this.tableType;
    }
	
	private final boolean isStatic() {
		return this.declarator != null && DeclaratorBase.newInstanceByCore != null;
	}

	private final TableFieldDefineImpl newRecid(TableDefineImpl table) {
		TableFieldDefineImpl recid = new TableFieldDefineImpl(table, table.primary, FIELD_NAME_RECID, FIELD_DBNAME_RECID, GUIDType.TYPE, true, this.isStatic());
		recid.setTitle(FILED_TITLE_RECID);
		return recid;
	}

	private final TableFieldDefineImpl newRecver(TableDefineImpl table) {
		TableFieldDefineImpl recver = new TableFieldDefineImpl(table, table.primary, FIELD_NAME_RECVER, FIELD_DBNAME_RECVER, LongType.TYPE, false, this.isStatic());
		recver.setTitle(FILED_TITLE_RECVER);
		recver.setDefault(LongConstExpr.ZERO_LONG);
		return recver;
	}

	final DBTableDefineImpl newDbTable(String tableName) {
		if (tableName == null || tableName.length() == 0) {
			throw new NullArgumentException("物理表表名");
		}
		if (this.dbTables.find(tableName) != null) {
			throw existing(tableName, "物理表");
		}
		DBTableDefineImpl dbTable = new DBTableDefineImpl(this, tableName);
		this.dbTables.add(dbTable);
		return dbTable;
	}

	final TableFieldDefineImpl newField(DBTableDefineImpl dbTable, String name,
			DataType type, boolean logicalKey) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("逻辑表字段名称");
		}
		if (type == null) {
			throw new NullArgumentException("逻辑表字段类型");
		}
		if (this.fields.find(name) != null) {
			throw existing(name, "字段");
		}
		if (dbTable.owner != this) {
			throw notOwnTable(this, dbTable);
		}
		if (logicalKey && dbTable != this.primary) {
			throw keyOnSlave(this.name, name);
		}
		String namedb = name.toUpperCase();
		if (dbTable.fields.containsKey(namedb)) {
			throw new IllegalArgumentException();
		}
		TableFieldDefineImpl field = new TableFieldDefineImpl(this, dbTable, name, namedb, (DataTypeInternal) type, false, this.isStatic());
		if (logicalKey) {
			field.setPrimaryKey(logicalKey);
		}
		this.fields.add(field);
		dbTable.store(field);
		return field;
	}

	static final int SUGGEST_DBTABLE_MAX_COLUMN = 200;

	public final TableFieldDefineImpl findFieldUsingNamedb(String namedb) {
		for (int i = 0, c = this.dbTables.size(); i < c; i++) {
			TableFieldDefineImpl field = this.dbTables.get(i).fields.find(namedb);
			if (field != null) {
				return field;
			}
		}
		return null;
	}

	final IndexDefineImpl newIndex(DBTableDefineImpl dbTable, String name,
			boolean unique, IndexType type) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("索引名称");
		}
		if (type == null) {
			throw new IllegalArgumentException();
		}
		if (dbTable.owner != this) {
			throw notOwnTable(this, dbTable);
		}
		if (this.indexes.find(name) != null) {
			throw existing(name, "索引");
		}
		IndexDefineImpl index = new IndexDefineImpl(this, dbTable, name, type);
		if (type == IndexType.B_TREE) {
			index.setUnique(unique);
		}
		this.indexes.add(index);
		return index;
	}

	private static final IllegalArgumentException notOwnTable(
			TableDefineImpl t, DBTableDefineImpl dt) {
		return new IllegalArgumentException("物理表[" + dt.name + "]不属于当前逻辑表[" + t.name + "]。");
	}

	final TableRelationDefineImpl newRelation(String name,
			TableDefineImpl target, TableRelationType type) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("表关系名称");
		}
		if (type == null) {
			throw new NullArgumentException("表关系类型");
		}
		if (this.relations.find(name) != null) {
			throw existing(name, "表关系");
		}
		if (name.equals(this.selfRef.name)) {
			throw new UnsupportedOperationException("新建表关系名称[" + name + "]不能与逻辑表名称一致.");
		}
		TableRelationDefineImpl relation = new TableRelationDefineImpl(this, name, target);
		relation.type = type;
		this.relations.add(relation);
		return relation;
	}

	private static final NamedDefineExistingException existing(String name,
			String cat) {
		return new NamedDefineExistingException("名称为[" + name + "]的" + cat + "定义已经存在。");
	}

	private static final IllegalTableDefineExceptiopn keyOnSlave(String table,
			String field) {
		return new IllegalTableDefineExceptiopn(null, "逻辑主键字段[" + field + "]不属于逻辑表[" + table + "]的主物理表。");
	}

	static final UnsupportedOperationException cantSetPrimaryKey(
			TableFieldDefineImpl field) {
		return new UnsupportedOperationException("逻辑表[" + field.owner.name + "]不支持设置字段[" + field.name + "]的逻辑主键属性。");
	}

	final boolean addKey(TableFieldDefineImpl field) {
		if (field.isRECID() || field.isRECVER()) {
			throw cantSetPrimaryKey(field);
		}
		if (!field.dbTable.isPrimary()) {
			throw keyOnSlave(this.name, field.name);
		}
		if (this.logicalKey == null) {
			this.logicalKey = this.newLogicalKeyOnly();
			this.logicalKey.addItem(field, false);
			field.setKeepValid(true);
			return true;
		} else if (this.logicalKey.findItem(field) == null) {
			this.logicalKey.addItem(field, false);
			field.setKeepValid(true);
			return true;
		}
		return false;
	}

	private final IndexDefineImpl newLogicalKeyOnly() {
		IndexDefineImpl index = new IndexDefineImpl(this, this.primary, DNA_LK_PREFIX.concat(this.name.toUpperCase()), IndexType.B_TREE);
		index.setUnique(true);
		return index;
	}

	final boolean removeKey(TableFieldDefineImpl field) {
		if (field.isRECID() || field.isRECVER()) {
			throw cantSetPrimaryKey(field);
		}
		if (this.logicalKey == null) {
			return false;
		}
		final IndexItemImpl item = this.logicalKey.findItem(field);
		if (item == null) {
			return false;
		} else {
			this.logicalKey.items.remove(item);
			if (this.logicalKey.items.size() == 0) {
				this.logicalKey = null;
			}
			return true;
		}
	}

	public final void checkLogicalKeyAvaiable() {
		if (this.logicalKey == null) {
			throw new UnsupportedOperationException("逻辑表[" + this.name + "]未定义逻辑主键.");
		}
	}

	/**
	 * 最大分区个数
	 */
	private int maxPartCount;

	/**
	 * 分区建议大小
	 */
	private int partSuggestion;

	/**
	 * 表分区字段
	 */
	public final NamedDefineContainerImpl<TableFieldDefineImpl> partfields;

	/**
	 * 检查将被作为分区的字段
	 * 
	 * <p>
	 * 属于当前逻辑表;属于主表;类型可用作分区;不在分区字段列表里.
	 */
	private final TableFieldDefineImpl checkPartField(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("字段定义");
		}
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		if (f.owner != this) {
			throw new IllegalArgumentException("分区字段[" + f.name + "]不属于当前逻辑表[" + this.name + "]。");
		}
		if (!f.dbTable.isPrimary()) {
			throw new IllegalTableDefineExceptiopn(this, "分区字段[" + f.name + "]未存储在逻辑表[" + this.name + "]的主物理表上。");
		}
		if (this.partfields.find(f.name) != null) {
			throw new IllegalTableDefineExceptiopn(this, "逻辑表[" + this.name + "]定义了重复的分区字段[" + f.name + "]。");
		}
		f.getType().detect(partitionFieldTypeValidator, f);
		return f;
	}

	private static final TypeDetectorBase<Object, TableFieldDefineImpl> partitionFieldTypeValidator = new TypeDetectorBase<Object, TableFieldDefineImpl>() {

		@Override
		public Object inBoolean(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inShort(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inInt(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inLong(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inFloat(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inDouble(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inNumeric(TableFieldDefineImpl field, int precision,
				int scale) throws Throwable {
			return null;
		}

		@Override
		public Object inChar(TableFieldDefineImpl field, SequenceDataType type)
				throws Throwable {
			return null;
		}

		@Override
		public Object inVarChar(TableFieldDefineImpl field,
				SequenceDataType type) throws Throwable {
			return null;
		}

		@Override
		public Object inText(TableFieldDefineImpl field) throws Throwable {
			throw fieldTypeUnsupportPartition(field);
		}

		@Override
		public Object inNChar(TableFieldDefineImpl field, SequenceDataType type)
				throws Throwable {
			return null;
		}

		@Override
		public Object inNVarChar(TableFieldDefineImpl field,
				SequenceDataType type) throws Throwable {
			return null;
		}

		@Override
		public Object inNText(TableFieldDefineImpl field) throws Throwable {
			throw fieldTypeUnsupportPartition(field);
		}

		@Override
		public Object inBinary(TableFieldDefineImpl field, SequenceDataType type)
				throws Throwable {
			return null;
		}

		@Override
		public Object inVarBinary(TableFieldDefineImpl field,
				SequenceDataType type) throws Throwable {
			return null;
		}

		@Override
		public Object inBlob(TableFieldDefineImpl field) throws Throwable {
			throw fieldTypeUnsupportPartition(field);
		}

		@Override
		public Object inGUID(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

		@Override
		public Object inDate(TableFieldDefineImpl field) throws Throwable {
			return null;
		}

	};

	private static final IllegalTableDefineExceptiopn fieldTypeUnsupportPartition(
			TableFieldDefineImpl field) {
		return new IllegalTableDefineExceptiopn(field.owner, "字段[" + field.owner.name + "." + field.name + "]的数据类型为[" + field.getType().toString() + "]，不能作为分区字段。");
	}

	final void addPartField(TableFieldDefine field) {
		if (field == null) {
			throw new NullArgumentException("分区字段");
		}
		this.partfields.add(this.checkPartField(field));
	}

	// final void initPartAttr(DbMetadata dbMetadata) {
	// if (this.isPartitioned()) {
	// if (this.maxPartCount <= 0) {
	// this.maxPartCount = dbMetadata.getMaxTablePartCount();
	// }
	// if (this.partSuggestion <= 0) {
	// this.partSuggestion = dbMetadata.getDefaultPartSuggestion();
	// }
	// }
	// }

	public final void beforeMoving(ContainerImpl<?> container, int from, int to) {
		if (container == this.dbTables) {
			if (from == 0 || to == 0) {
				throw new UnsupportedDbTableModificationException(this, this.primary, ACTION_MOVE, PRIMARY_TABLE);
			}
		} else if (container == this.fields) {
			if (from == 0 || to == 0) {
				throw new UnsupportedTableFieldModificationException(this, this.f_recid, ACTION_MOVE, SYSTEM_FIELD);
			} else if (from == 1 || to == 1) {
				throw new UnsupportedTableFieldModificationException(this, this.f_recver, ACTION_MOVE, SYSTEM_FIELD);
			}
		}
	}

	public final void beforeClearing(ContainerImpl<?> container) {
		if (container == this.dbTables) {
			throw new UnsupportedDbTableModificationException(this, this.primary, ACTION_REMOVE, PRIMARY_TABLE);
		} else if (container == this.fields) {
			throw new UnsupportedTableFieldModificationException(this, this.f_recid, ACTION_REMOVE, SYSTEM_FIELD);
		}
	}

	public final void beforeRemoving(ContainerImpl<?> container, int index) {
		if (container == this.dbTables) {
			this.beforeRemoving(this.dbTables.get(index));
		} else if (container == this.fields) {
			this.beforeRemoving(this.fields.get(index));
		}
	}

	public final void beforeRemoving(ContainerImpl<?> container, Object o) {
		if (container == this.dbTables) {
			this.beforeRemoving((DBTableDefineImpl) o);
		} else if (container == this.fields) {
			this.beforeRemoving((TableFieldDefineImpl) o);
		}
	}

	private final void beforeRemoving(DBTableDefineImpl t) {
		if (t == this.primary) {
			throw new UnsupportedDbTableModificationException(this, this.primary, ACTION_REMOVE, PRIMARY_TABLE);
		}
		if (t.getFieldCount() > 0) {
			throw new UnsupportedDbTableModificationException(this, this.primary, ACTION_REMOVE, NOT_EMPTY_TABLE);
		}
	}

	private final void beforeRemoving(TableFieldDefineImpl f) {
		if (f == this.f_recid || f == this.f_recver) {
			throw new UnsupportedTableFieldModificationException(this, f, ACTION_REMOVE, SYSTEM_FIELD);
		}
		if (f.isPrimaryKey()) {
			throw new UnsupportedTableFieldModificationException(this, f, ACTION_REMOVE, KEY_FIELD);
		}
		if (this.partfields.contains(f)) {
			throw new UnsupportedTableFieldModificationException(this, f, ACTION_REMOVE, PARTITION_FIELD);
		}
		for (int i = 0, c = this.indexes.size(); i < c; i++) {
			IndexDefineImpl index = this.indexes.get(i);
			if (index.findItem(f) != null) {
				throw new UnsupportedTableFieldModificationException(this, f, ACTION_REMOVE, INDEX_FIELD);
			}
		}
		f.dbTable.unstore(f);
	}

	public final TableRef findRelationRef(String name) {
		if (this.selfRef.name.equalsIgnoreCase(name)) {
			return this.selfRef;
		}
		return this.relations.find(name);
	}

	public final TableRef getRelationRef(String name) {
		TableRef relationRef = this.findRelationRef(name);
		if (relationRef != null) {
			return relationRef;
		}
		throw new MissingDefineException();
	}

	/**
	 * 克隆表定义
	 * 
	 * @param querier
	 * @return
	 */
	final TableDefineImpl clone(ObjectQuerier querier) {
		return new TableDefineImpl(this, querier);
	}

	private TableDefineImpl(TableDefineImpl sample, ObjectQuerier querier) {
		super(sample);
		this.id = sample.id;
		this.category = sample.category;
		this.tableType = sample.tableType;
		this.declarator = null;
		this.dbTables = new NamedDefineContainerImpl<DBTableDefineImpl>(false, this);
		this.fields = new NamedDefineContainerImpl<TableFieldDefineImpl>(false, this);
		this.indexes = new NamedDefineContainerImpl<IndexDefineImpl>(true, this);
		this.relations = new NamedDefineContainerImpl<TableRelationDefineImpl>(false, this);
		this.hierarchies = new NamedDefineContainerImpl<HierarchyDefineImpl>(false, this);
		this.partfields = new NamedDefineContainerImpl<TableFieldDefineImpl>(false, this);
		this.primary = sample.primary.clone(this);
		this.dbTables.add(this.primary);
		for (int i = 1, c = sample.dbTables.size(); i < c; i++) {
			this.dbTables.add(sample.dbTables.get(i).clone(this));
		}
		this.fields.add(this.f_recid = sample.f_recid.clone(this));
		this.fields.add(this.f_recver = sample.f_recver.clone(this));
		for (int i = 2, c = sample.fields.size(); i < c; i++) {
			this.fields.add(sample.fields.get(i).clone(this));
		}
		if (sample.logicalKey != null) {
			this.logicalKey = sample.logicalKey.clone(this);
		}
		for (int i = 0, c = sample.indexes.size(); i < c; i++) {

			this.indexes.add(sample.indexes.get(i).clone(this));
		}
		for (int i = 0, c = sample.hierarchies.size(); i < c; i++) {
			this.hierarchies.add(sample.hierarchies.get(i).clone(this));
		}
		this.selfRef = new TableSelfRef(this);
		for (int i = 0, c = sample.relations.size(); i < c; i++) {
			this.relations.add(sample.relations.get(i).clone(this, querier));
		}
		if (sample.partfields.size() > 0) {
			for (int i = 0, c = sample.partfields.size(); i < c; i++) {
				this.partfields.add(this.fields.get(sample.partfields.get(i).name));
			}
		}
		this.maxPartCount = sample.maxPartCount;
		this.partSuggestion = sample.partSuggestion;
	}

	// private final boolean isPartitionModified(TableDefineImpl cloned) {
	// if (this.isPartitioned() != cloned.isPartitioned()) {
	// return true;
	// }
	// if (this.isPartitioned()) {
	// final int c = this.partfields.size();
	// if (cloned.partfields.size() != c) {
	// return true;
	// }
	// for (int i = 0; i < c; i++) {
	// TableFieldDefineImpl cur = this.partfields.get(i);
	// TableFieldDefineImpl sam = cloned.partfields.get(i);
	// if (cur.name.equals(sam.name)
	// && cur.namedb().equals(sam.namedb())) {
	// continue;
	// }
	// return true;
	// }
	// }
	// return false;
	// }

	/**
	 * 完全转化为样本结构
	 * 
	 * @param clone
	 *            当前表的克隆
	 * @param querier
	 */
	final void assignFrom(TableDefineImpl clone, ObjectQuerier querier) {
		// HCL 完善表分区的检查
		// if (this.isPartitionModified(cloned)) {
		// throw new UnsupportedOperationException("已发布逻辑表定义的表分区定义不能修改。");
		// }
		super.assignFrom(clone);
		this.category = clone.category;
		this.tableType = clone.tableType;//逻辑表类型
		this.assignDbTablesFrom(clone);
		this.assignFieldsFrom(clone);
		this.assignIndexesFrom(clone);
		this.indexes.trunc(clone.indexes.size());
		this.assignRelationsFrom(clone, querier);
		this.relations.trunc(clone.relations.size());
		this.assignHierarchiesFrom(clone);
		this.hierarchies.trunc(clone.hierarchies.size());
		// !! field removing depend on indexe defines
		this.fields.trunc(clone.fields.size());
		// !! dbtable removing depend on field defines
		this.dbTables.trunc(clone.dbTables.size());
	}

	private final void assignDbTablesFrom(TableDefineImpl clone) {
		this.primary.assignFrom(clone.primary);
		for (int i = 1, c = clone.dbTables.size(); i < c; i++) {
			DBTableDefineImpl from = clone.dbTables.get(i);
			DBTableDefineImpl to = this.dbTables.find(from.name);
			if (to == null) {
				to = from.clone(this);
				this.dbTables.add(i, to);
			} else {
				this.dbTables.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
	}

	private final void assignFieldsFrom(TableDefineImpl clone) {
		for (int i = 0, c = clone.fields.size(); i < c; i++) {
			TableFieldDefineImpl from = clone.fields.get(i);
			if (from.isRECID() || from.isRECVER()) {
				continue;
			}
			TableFieldDefineImpl to = this.fields.find(from.name);
			if (to == null) {
				to = from.clone(this);
				this.fields.add(i, to);
			} else {
				this.fields.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
	}

	private final void assignIndexesFrom(TableDefineImpl clone) {
		if (clone.logicalKey != null) {
			if (this.logicalKey != null) {
				this.logicalKey.assignFrom(clone.logicalKey);
			} else {
				this.logicalKey = clone.logicalKey.clone(this);
			}
		} else {
			this.logicalKey = null;
		}
		for (int i = 0, c = clone.indexes.size(); i < c; i++) {
			IndexDefineImpl from = clone.indexes.get(i);
			IndexDefineImpl to = this.indexes.find(from.name);
			if (to == null) {
				to = from.clone(this);
				this.indexes.add(i, to);
			} else {
				this.indexes.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
	}

	private final void assignHierarchiesFrom(TableDefineImpl clone) {
		for (int i = 0, c = clone.hierarchies.size(); i < c; i++) {
			HierarchyDefineImpl from = clone.hierarchies.get(i);
			HierarchyDefineImpl to = this.hierarchies.find(from.name);
			if (to == null) {
				to = from.clone(this);
				this.hierarchies.add(i, to);
			} else {
				this.hierarchies.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
	}

	private final void assignRelationsFrom(TableDefineImpl clone,
			ObjectQuerier querier) {
		for (int i = 0, c = clone.relations.size(); i < c; i++) {
			TableRelationDefineImpl from = clone.relations.get(i);
			TableRelationDefineImpl to = this.relations.find(from.name);
			if (to == null) {
				to = from.clone(this, querier);
				this.relations.add(i, to);
			} else {
				this.relations.ensureElementAt(to, i);
				to.assignFrom(from);
			}
		}
	}

	@Override
	public final String getXMLTagName() {
		return table_tag;
	}

	static final String table_tag = "tabledefine";

	@Override
	public final void render(SXElement element) {
		TableXML.V25.render(this, element);
	}

	// only called from startup step, thread safe
	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		TableXML.detect(element).merge(this, element);
		this.delayResolveRelationForLoadStep(element, helper);
	}

	private final void delayResolveRelationForLoadStep(SXElement element,
			SXMergeHelper helper) {
		for (SXElement e = element.firstChild(TableXML.table_element_relations, TableRelationDefineImpl.xml_name); e != null; e = e.nextSibling(TableRelationDefineImpl.xml_name)) {
			final String rn = e.getString(NamedDefineImpl.xml_attr_name);
			final String target = e.getString(TableRef.xml_attr_table);
			TableRelationDefineImpl relation = this.relations.find(rn);
			if (relation == null) {
				helper.addDelayAction(CoreMetadataTableLoadStep.class, new RelationStartupMerger(e, rn));
			} else if (!target.equals(relation.target.name)) {
				// HCL
				throw new UnsupportedOperationException("同名表关系定义目标表不同.");
			} else {
				TableXML.relationXML.merge(relation, e);
			}
		}
	}

	private final class RelationStartupMerger implements
			SXMergeDelayAction<Class<?>> {

		final SXElement element;

		final String name;

		RelationStartupMerger(SXElement element, String name) {
			this.element = element;
			this.name = name;
		}

		public void doAction(Class<?> at, SXMergeHelper helper,
				SXElement atElement) {
			String targetName = this.element.getAttribute(TableRef.xml_attr_table, null);
			if (targetName == null) {
				// 兼容早期版本
				targetName = this.element.getAttribute("target", null);
			}
			if (targetName == null) {
				throw new IllegalArgumentException("定义表关系的XML对象结构错误.");
			}
			TableRelationDefineImpl relation = TableDefineImpl.this.relations.find(this.name);
			if (relation == null) {
				TableDefineImpl target = (TableDefineImpl) helper.querier.get(TableDefine.class, targetName);
				relation = new TableRelationDefineImpl(TableDefineImpl.this, this.name, target);
				TableDefineImpl.this.relations.add(relation);
			}
			TableXML.relationXML.merge(relation, this.element);
		}
	}

	// support for ide
	final void mergeDelayRelation(SXElement element, SXMergeHelper helper) {
		TableXML.detect(element).merge(this, element);
		this.mergeRelationDelay(element, helper);
	}

	private final void mergeRelationDelay(SXElement element,
			SXMergeHelper helper) {
		for (SXElement e = element.firstChild(TableXML.table_element_relations, TableRelationDefineImpl.xml_name); e != null; e = e.nextSibling(TableRelationDefineImpl.xml_name)) {
			final String rn = e.getString(NamedDefineImpl.xml_attr_name);
			final String target = e.getString(TableRef.xml_attr_table);
			TableRelationDefineImpl relation = this.relations.find(rn);
			if (relation == null) {
				helper.addDelayAction(this, new RelationMerger(this, e));
			} else if (!target.equals(relation.target.name)) {
				// HCL
				throw new UnsupportedOperationException("同名表关系定义目标表不同.");
			} else {
				TableXML.relationXML.merge(relation, e);
			}
		}
	}

	private static final class RelationMerger implements
			SXMergeDelayAction<TableDefineImpl> {

		final TableDefineImpl table;
		final SXElement element;

		RelationMerger(TableDefineImpl table, SXElement element) {
			this.table = table;
			this.element = element;
		}

		public void doAction(TableDefineImpl at, SXMergeHelper helper,
				SXElement atElement) {
			String targetName = this.element.getAttribute(TableRef.xml_attr_table, null);
			if (targetName == null) {
				// 兼容早期版本
				targetName = this.element.getAttribute("target", null);
			}
			if (targetName == null) {
				throw new IllegalArgumentException("定义表关系的XML对象结构错误.");
			}
			final String rn = this.element.getString(xml_attr_name);
			TableRelationDefineImpl relation = this.table.relations.find(rn);
			if (relation == null) {
				TableDefineImpl target = (TableDefineImpl) helper.querier.get(TableDefine.class, targetName);
				relation = this.table.newRelation(rn, target, TableRelationType.REFERENCE);
			}
			TableXML.relationXML.merge(relation, this.element);
		}

	}


}