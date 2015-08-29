package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.TypeFactory;

/**
 * 集群同步数据实体表定义
 * 
 * @see com.jiuqi.dna.core.impl.TD_CoreClusterData
 * @author leezizi 2012-12
 */
final class TD_CoreClusterData extends TableDeclarator {

	public final static String TABLE_NAME = "Core_ClusterData";

	public final TableFieldDefine f_id;
	public final TableFieldDefine f_index;
	public final TableFieldDefine f_from;
	public final TableFieldDefine f_time;
	public final TableFieldDefine f_data;

	public TD_CoreClusterData() {
		super(TABLE_NAME);
		this.f_id = this.table.f_RECID();
		this.f_index = this.table.newField("node_index", TypeFactory.INT);
		this.f_from = this.table.newField("from_index", TypeFactory.INT);
		this.f_time = this.table.newField("s_time", TypeFactory.LONG);
		this.f_data = this.table.newField("s_data", TypeFactory.BLOB);
		this.table.newIndex("idx_index", this.f_index);
		this.table.newIndex("idx_time", this.f_time);
	}
}