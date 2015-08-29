package com.jiuqi.dna.core.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ������<br>
 * �����߼�Ϊӵ��Ȩ�޵����壬���Ա�����Ȩ�ޡ���ǰ��������Ҫ��Ϊ�û��ͽ�ɫ���֡�
 * 
 * @author LiuZhi 2009-11
 */
public interface Actor {

	/**
	 * Ĭ����֯����ID
	 */
	public static final GUID GLOBAL_ORG_ID = null;

	public static final int MAX_NAME_LENGTH = 50;

	public static final int MAX_TITLE_LENGTH = 50;

	public static final int MAX_DESCRIPTION_LENGTH = 128;

	/**
	 * ��÷�����ID<br>
	 * ������ID��ͬ�ַ������������Ψһ��
	 * 
	 * @return ���ط�����ID�����ؽ��������Ϊ��
	 */
	public GUID getID();

	/**
	 * ����������<br>
	 * ������������ͬ�ַ������������Ψһ��
	 * 
	 * @return ���ط��������ƣ����ؽ��������Ϊ��
	 */
	public String getName();

	/**
	 * ��ȡ�����߱���<br>
	 * һ������£������߱���ֻ������ʾ��
	 * 
	 * @return ���ط����߱��⣬���ؽ��������Ϊ��
	 */
	public String getTitle();

	/**
	 * ��÷����ߵ�״̬
	 * 
	 * @see com.jiuqi.dna.core.auth.ActorState
	 * @return ���ط����ߵ�ǰ״̬
	 */
	public ActorState getState();

	/**
	 * ��ȡ���ʵ�������Ϣ
	 * 
	 * @return ���ط����߱��⣬���ؽ������Ϊ��
	 */
	public String getDescription();

	/**
	 * �÷����Ѳ�֧�֣�����ʵ�ָ÷����Ĺ��ܣ�����ʹ�������������
	 */
	@Deprecated
	public int getMappingOrganizationCount();

	/**
	 * �÷����Ѳ�֧�֣�����ʵ�ָ÷����Ĺ��ܣ�����ʹ�������������
	 */
	@Deprecated
	public GUID getMappingOrganizationID(int index);

}
