package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.resource.ResourceToken;

/**
 * ��ȡָ����Դ��Ȩ�޼̳�·���ļ�
 * 
 * @author liuzhi
 * @see com.jiuqi.dna.core.spi.auth.AuthorityInhreitPath
 */
public final class GetAuthorityInheritPathKey {

	/**
	 * @param resourceOfBaseNode
	 *            ���ڵ����Դ��
	 */
	public GetAuthorityInheritPathKey(final ResourceToken<?> resourceOfBaseNode) {
		if (resourceOfBaseNode == null) {
			throw new NullArgumentException("resource");
		}
		this.resourceOfBaseNode = resourceOfBaseNode;
	}

	public final ResourceToken<?> resourceOfBaseNode;

}
