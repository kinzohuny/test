package com.jiuqi.dna.core.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ����Ȩ��Դ��<br>
 * �ڽ���Ȩ�޹���ʱʹ�ã��û�ͨ����ȡ����Ȩ��Դ���б��õ����е���Ȩ��Ϣ����ͨ���޸��ύ����Ȩ��Դ��޸���Ȩ��
 * 
 * @author LiuZhi 2009-11
 */
@Deprecated
public interface AuthorizedResourceItem {

	public GUID getID();

	/**
	 * ��ȡ����Ȩ��Դ�����
	 * 
	 * @return ���ؿ���Ȩ��Դ�����
	 */
	public String getTitle();

	/**
	 * ��ȡ����Ȩ��Դ���ж�ָ����������Ȩ��Ϣ
	 * 
	 * @param operation
	 *            ָ������
	 * @return ���ؿ���Ȩ��Դ���ж�ָ����������Ȩ��Ϣ
	 */
	public Authority getAuthority(Operation<?> operation);

	/**
	 * ���ÿ���Ȩ��Դ���ָ����������Ȩ
	 * 
	 * @param operation
	 *            ָ������
	 * @param authority
	 *            ��Ȩ��Ϣ
	 */
	public void setAuthority(Operation<?> operation, Authority authority);

}
