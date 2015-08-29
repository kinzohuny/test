package com.jiuqi.dna.core.internal.db.sync;

import java.sql.SQLException;

import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;

/**
 * 表分区的器
 * 
 * @author houchunlei
 * 
 */
abstract class TablePartitioner {

	/**
	 * 尝试对指定的逻辑表执行拆分分区的操作
	 */
	public abstract void split(TableDefine table, PooledConnection conn)
			throws SQLException;
}
