package com.jiuqi.dna.core.info;

import com.jiuqi.dna.core.def.info.InfoDefine;
import com.jiuqi.dna.core.type.GUID;

/**
 * ����е���Ϣʵ�����
 * 
 * @author gaojingxin
 * 
 */
public interface Info {
	/**
	 * ����������Ĺ�����Ϣ
	 */
	public ProcessInfo getProcess();

	/**
	 * �����Ϣ��Ķ���
	 */
	public InfoDefine getDefine();

	/**
	 * ��ȡ��ʼʱ��
	 */
	public long getTime();

	/**
	 * ��ȡ����ֵ
	 */
	public Object getParam(int index);

	/**
	 * ���ID����Ϊ��־����ʱʹ�ã���GUID��ʱ�����<br>
	 */
	public GUID getID();
}
