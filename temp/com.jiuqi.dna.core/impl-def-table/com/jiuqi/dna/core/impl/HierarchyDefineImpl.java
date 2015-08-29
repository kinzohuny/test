package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.HierarchyDeclare;

/**
 * ���ζ���ʵ����
 * 
 * @author houchunlei
 * 
 */
public final class HierarchyDefineImpl extends NamedDefineImpl implements
		HierarchyDeclare {

	public final int getMaxLevel() {
		return this.maxlevel;
	}

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final void setMaxLevel(int maxLevel) {
		if (maxLevel < 0 || HierarchyDefineImpl.MAX_LEVEL < maxLevel) {
			throw new IllegalArgumentException("�����֧�ֵ���󼶴�ֵ");
		}
		if (maxLevel < this.maxlevel) {
			throw new IllegalArgumentException("�µ���󼶴�ֵС�ڵ�ǰ��󼶴�ֵ");
		}
		this.maxlevel = maxLevel;
	}

	/**
	 * ��󼶴�
	 */
	static final int MAX_LEVEL = 32;

	/**
	 * ���α����������
	 */
	static final String COLUMN_NAME_RECID = TableDefineImpl.FIELD_DBNAME_RECID;

	/**
	 * ���α��·������
	 */
	static final String COLUMN_NAME_PATH = "PATH";

	/**
	 * ���α��״̬����
	 */
	static final String COLUMN_NAME_STATUS = "STATUS";

	/**
	 * ���α��Ψһ������ǰ׺
	 */
	static final String PATH_INDEX_PREFIX = "HX_";

	/**
	 * ״̬�е�0λ��ʶ-�Ƿ�ΪҶ�ӽڵ�
	 */
	static final int STATUS_MASK_IS_LEAF = 1 << 0;

	/**
	 * �����߼�����
	 */
	final TableDefineImpl owner;

	/**
	 * ���
	 */
	final int sequence;

	/**
	 * ��󼶴����
	 */
	int maxlevel;

	/**
	 * ���α�����,��д
	 */
	String tableName;

	/**
	 * ������������
	 */
	String pkIndex;

	/**
	 * ·����������
	 */
	String pathIndex;

	HierarchyDefineImpl(TableDefineImpl owner, String name, int maxLevel) {
		super(name);
		this.owner = owner;
		// HCL
		this.sequence = owner.hierarchies.size();
		this.maxlevel = maxLevel;
		this.tableName = owner.name.toUpperCase() + "_H" + this.sequence;
	}

	final int index() {
		return this.owner.hierarchies.indexOf(this);
	}

	static final HierarchyDefineImpl newForMerge(TableDefineImpl owner,
			String name) {
		return new HierarchyDefineImpl(owner, name);
	}

	/**
	 * newForMerge
	 */
	private HierarchyDefineImpl(TableDefineImpl owner, String name) {
		super(name);
		this.owner = owner;
		this.sequence = owner.hierarchies.size();
	}

	// final void ensureValid(final DbMetadata dbMetadata,
	// final DbNamespace tables, final DbNamespace indexes) {
	// if (this.tableName.length() > dbMetadata.getMaxTableNameLength()
	// || tables.contains(this.tableName)
	// || dbMetadata.filterKeyword(this.tableName)) {
	// this.tableName = NameUtl.buildIdentityName(this.tableName,
	// dbMetadata.getMaxTableNameLength(), new Filter<String>() {
	// public boolean accept(String item) {
	// return tables.contains(item)
	// || dbMetadata.filterKeyword(item);
	// }
	// });
	// }
	// if (this.pkIndex == null) {
	// this.pkIndex = TableDefineImpl.DNA_PK_PREFIX.concat(this.tableName);
	// }
	// final String pkname = this.pkIndex;
	// if (pkname.length() > dbMetadata.getMaxIndexNameLength()
	// || dbMetadata.filterKeyword(pkname) || indexes.contains(pkname)) {
	// this.pkIndex = NameUtl.buildIdentityName(pkname,
	// dbMetadata.getMaxIndexNameLength(), new Filter<String>() {
	// public boolean accept(String item) {
	// return dbMetadata.filterKeyword(pkname)
	// || indexes.contains(pkname);
	// }
	// });
	// }
	// }

	// final <TContainer, TElement> void ensurePathIndexValid(
	// final DbMetadata dbMetadata, final DbNamespace indexes) {
	// if (this.pathIndex == null) {
	// this.pathIndex = PATH_INDEX_PREFIX.concat(this.tableName());
	// }
	// final String pathIndex = this.pathIndex;
	// if (pathIndex.length() > dbMetadata.getMaxIndexNameLength()
	// || dbMetadata.filterKeyword(pathIndex)
	// || indexes.contains(pathIndex)) {
	// this.pathIndex = NameUtl.buildIdentityName(pathIndex,
	// dbMetadata.getMaxIndexNameLength(), new Filter<String>() {
	// public boolean accept(String item) {
	// return dbMetadata.filterKeyword(pathIndex)
	// || indexes.contains(pathIndex);
	// }
	// });
	// }
	// }

	final String tableName() {
		if (this.tableName == null) {
			throw new NullPointerException("������");
		}
		return this.tableName;
	}

	final String pkIndex() {
		if (this.pkIndex == null) {
			throw new NullPointerException("������������");
		}
		return this.pkIndex;
	}

	final String pathIndex() {
		if (this.pathIndex == null) {
			throw new NullPointerException("����·������������Ϊ��");
		}
		return this.pathIndex;
	}

	final int getPathLength() {
		return this.maxlevel * 17;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "hierarchy";

	final HierarchyDefineImpl clone(TableDefineImpl owner) {
		return new HierarchyDefineImpl(owner, this);
	}

	private HierarchyDefineImpl(TableDefineImpl owner,
			HierarchyDefineImpl sample) {
		super(sample);
		this.owner = owner;
		this.sequence = sample.sequence;
		this.maxlevel = sample.maxlevel;
		this.tableName = sample.tableName;
		this.pkIndex = sample.pkIndex;
		this.pathIndex = sample.pathIndex;
	}

	// private volatile HierarchyMoveSql hierarchyMoveSql;
	//
	// final HierarchyMoveSql getHierarchyMoveSql(DbMetadata dbMetadata) {
	// if (this.hierarchyMoveSql == null) {
	// synchronized (this) {
	// if (this.hierarchyMoveSql == null) {
	// this.hierarchyMoveSql = new HierarchyMoveSql(dbMetadata,
	// this);
	// }
	// }
	// }
	// return this.hierarchyMoveSql;
	// }

}
