package com.jiuqi.dna.core.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ����Ȩ��Դ�����
 * 
 * <p>
 * ����Ȩ��Դ�������һ������Ŀ���Ȩ��Դ�DNA���Ϊÿ������Ȩ��Դ���ඨ����һ����ĸ���Դ�
 * ��Ϊ����Ȩ��Դ����ͨ���ö��󣬳��˿��Ի�ȡ�����Ŀ���Ȩ��Դ����Ϣ�⣬�����Ի�ȡ������Դ�����ID�������������Դ��������Ϣ��
 * 
 * @see com.jiuqi.dna.core.auth.AuthorizedResourceItem
 * @author LiuZhi 2009-11
 */
@Deprecated
public interface AuthorizedResourceCategoryItem extends AuthorizedResourceItem {

	/**
	 * ��ȡ����Ȩ��Դ���Ĳ�������
	 * 
	 * @return ���ؿ���Ȩ��Դ���Ĳ�����������
	 */
	public Operation<?>[] getResourceOperations();

	/**
	 * ��ȡ����Ȩ��Դ���ID
	 * 
	 * @return ���ؿ���Ȩ��Դ���ID��GUID��
	 */
	@Deprecated
	public GUID getResourceCategoryID();

}
