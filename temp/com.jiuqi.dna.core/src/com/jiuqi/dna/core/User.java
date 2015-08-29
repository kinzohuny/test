package com.jiuqi.dna.core;

import com.jiuqi.dna.core.auth.Actor;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.impl.BuildInUser;
import com.jiuqi.dna.core.type.GUID;

/**
 * �û�<br>
 * �ڲ���Ȩ�޹���ʱ�����֧��һ���û������������֯�������ڲ�ͬ����֯�����£��û�����ӵ�в�ͬ��Ȩ�ޡ�<br>
 * �û�Ҳ���Ա���������ɫ���û��̳���������ɫ������Ȩ�ޡ�
 * 
 * @see com.jiuqi.dna.core.auth.Actor
 * @author gaojingxin
 */
public interface User extends Actor {

	/**
	 * �����û���
	 */
	public final static String USER_NAME_ANONYM = "?";

	/**
	 * �����û���
	 */
	public final static String USER_NAME_DEBUGGER = "debugger";

	/**
	 * ϵͳ�û���
	 */
	public static final String USER_NAME_SYSTEM = "system";
	/**
	 * ϵͳ�û�
	 */
	public final static User system = BuildInUser.system;

	/**
	 * �����û�
	 */
	public final static User anonym = BuildInUser.anonym;

	/**
	 * �����û�����ϵͳ�������-Dcom.jiuqi.dna.debug=true�������������û��ſ���ʹ�á�<br>
	 * ���û������κ����룬Ȩ�������
	 */
	public final static User debugger = BuildInUser.debugger;

	/**
	 * ���ص�ǰ�û��Ƿ����ڽ��û����磺anonym��debugger��system
	 * 
	 * @return
	 */
	public boolean isBuildInUser();

	/**
	 * ��֤�û�����<br>
	 * �жϸ������������û������Ƿ�ƥ�䡣
	 * 
	 * @param password
	 *            �������룬����Ϊ�ն���
	 * @return ƥ�䷵��true�����򷵻�false
	 */
	public boolean validatePassword(String password);

	/**
	 * ��֤�û�����<br>
	 * �жϸ������������û������Ƿ�ƥ�䡣
	 * 
	 * @param password
	 *            �������룬����Ϊ�ն���
	 * @return ƥ�䷵��true�����򷵻�false
	 */
	public boolean validatePassword(GUID password);

	/**
	 * �÷����Ѳ�֧�֣�����ʵ�ָ÷����Ĺ��ܣ�����ʹ�������������
	 */
	@Deprecated
	public int getAssignedRoleCount();

	/**
	 * �÷����Ѳ�֧�֣�����ʵ�ָ÷����Ĺ��ܣ�����ʹ�������������
	 */
	@Deprecated
	public Role getAssignedRole(int index);

	/**
	 * �÷����Ѳ�֧�֣�����ʵ�ָ÷����Ĺ��ܣ�����ʹ�������������
	 */
	@Deprecated
	public int getPriorityIndex();
	
	/**
	 * ��ȡ�û�����
	 * @return
	 */
	public String getLevel();

}
