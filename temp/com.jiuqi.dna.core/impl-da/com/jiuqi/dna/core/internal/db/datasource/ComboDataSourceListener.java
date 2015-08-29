package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.SQLException;

/**
 * �������ӵĴ򿪡��رա����䡢���ա��ȴ���
 * 
 * <p>
 * ʵ�ֱ����̰߳�ȫ
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
	 * ��ʼ�ر�����
	 * 
	 * <p>
	 * ��ʱ�����ӻ�ռ����active������
	 * <p>
	 * IDLE->CLOSING
	 * 
	 * @param conn
	 */
	void startClose(ListenedConnection conn);

	/**
	 * ��ɹر�����
	 * 
	 * <p>
	 * �����Ƿ����쳣�����Ӷ��ᱻ�����Ƴ���
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