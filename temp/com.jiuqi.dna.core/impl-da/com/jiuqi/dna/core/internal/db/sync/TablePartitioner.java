package com.jiuqi.dna.core.internal.db.sync;

import java.sql.SQLException;

import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;

/**
 * ���������
 * 
 * @author houchunlei
 * 
 */
abstract class TablePartitioner {

	/**
	 * ���Զ�ָ�����߼���ִ�в�ַ����Ĳ���
	 */
	public abstract void split(TableDefine table, PooledConnection conn)
			throws SQLException;
}
