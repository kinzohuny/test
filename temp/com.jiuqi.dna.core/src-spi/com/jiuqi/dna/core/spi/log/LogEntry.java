package com.jiuqi.dna.core.spi.log;

import com.jiuqi.dna.core.RemoteInfo;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.info.Info;

/**
 * ��־��
 * 
 * @author gaojingxin
 * 
 */
public interface LogEntry {
	/**
	 * ��ûỰID
	 */
	public long getSessionID();

	/**
	 * ����û���Ϣ
	 */
	public User getUser();

	/**
	 * ���Զ����Ϣ
	 */
	public RemoteInfo getRemoteInfo();

	/**
	 * �����Ϣ���Ҫʱ����ǿ��ת��ΪProcessInfo
	 */
	public Info getInfo();

	/**
	 * �����־�����<br>
	 * ����־�����Ϊ"PROCESS_BEGIN"ʱ<br>
	 * �������ʱ��ʹ��ProcessInfo.getID()��Ϊ��¼��RECID
	 */
	public LogEntryKind getKind();
}
