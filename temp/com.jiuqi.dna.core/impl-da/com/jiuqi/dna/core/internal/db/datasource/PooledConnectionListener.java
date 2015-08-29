package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.SQLException;

/**
 * ���ݿ����ӵļ����ӿ�
 * 
 * <p>
 * �����α�Ĵ������ͷ�,ִ�����,�ύ����.
 * 
 * @author houchunlei
 * 
 */
public interface PooledConnectionListener {

	void afterCreateStatement(long connId, long cursorId, SQLException e);

	void afterCloseStatement(long connId, long cursorId, SQLException e);

	void startExecute(long connId, long cursorId, String sql, SqlSource source);

	void finishExecute(long connId, long cursorId, String sql,
			SqlSource source, long start, SQLException e);

	void startCommit(long connId, boolean commit);

	void finishCommit(long connId, boolean commit, long start, SQLException e);

	void afterStartTran(long connId, SQLException e);

	void afterStopTran(long connId, SQLException e);
}