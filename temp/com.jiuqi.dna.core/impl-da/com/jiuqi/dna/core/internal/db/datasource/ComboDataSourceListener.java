package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.SQLException;

/**
 * 监听连接的打开、关闭、分配、回收、等待。
 * 
 * <p>
 * 实现必须线程安全
 * 
 * @author houchunlei
 * 
 */
public interface ComboDataSourceListener {

	/**
	 * NONE->CONNECTING
	 * 
	 * @param connId
	 */
	void startConnect(long connId);

	/**
	 * CONNECTING->NONE
	 * 
	 * @param connId
	 * @param e
	 */
	void finishConnect(long connId, SQLException e);

	/**
	 * CONNECTING->IDLE
	 * 
	 * @param conn
	 */
	void finishConnect(ListenedConnection conn);

	/**
	 * 开始关闭连接
	 * 
	 * <p>
	 * 此时，连接还占用着active计数。
	 * <p>
	 * IDLE->CLOSING
	 * 
	 * @param conn
	 */
	void startClose(ListenedConnection conn);

	/**
	 * 完成关闭连接
	 * 
	 * <p>
	 * 不管是否发生异常，连接都会被池中移除。
	 * <p>
	 * CLOSING->NONE
	 * 
	 * @param connId
	 * @param e
	 */
	void finishClose(long connId, SQLException e);

	/**
	 * IDLE->CHECKING
	 * 
	 * @param connId
	 */
	void startCheck(long connId);

	/**
	 * CHECKING->IDLE
	 * 
	 * @param connId
	 * @param e
	 */
	void finishCheck(long connId, SQLException e);

	/**
	 * IDLE->USING
	 * 
	 * @param conn
	 */
	void afterAlloc(ListenedConnection conn);

	/**
	 * USING->IDLE
	 * 
	 * @param conn
	 */
	void beforeRevoke(ListenedConnection conn);

	void startWait(long waitId);

	void finishWait(long waitId, InterruptedException e);

	void beforeDispose();
}