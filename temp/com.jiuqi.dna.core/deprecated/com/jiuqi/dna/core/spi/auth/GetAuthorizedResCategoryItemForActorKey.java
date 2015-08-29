package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.GUID;

/**
 * Ϊ��������Ȩʱ����ȡ���п���Ȩ��Դ�����ļ�
 * 
 * @author LiuZhi 2009-11
 */
@Deprecated
public abstract class GetAuthorizedResCategoryItemForActorKey {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * ��֯����ID��Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	public GUID orgID;

	/**
	 * �½���ȡ���п���Ȩ��Դ�����ļ�
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	protected GetAuthorizedResCategoryItemForActorKey(GUID actorID, GUID orgID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
	}

}
