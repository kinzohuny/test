package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.resource.ResourceToken;

/**
 * 获取指定资源的权限继承路径的键
 * 
 * @author liuzhi
 * @see com.jiuqi.dna.core.spi.auth.AuthorityInhreitPath
 */
public final class GetAuthorityInheritPathKey {

	/**
	 * @param resourceOfBaseNode
	 *            基节点的资源项
	 */
	public GetAuthorityInheritPathKey(final ResourceToken<?> resourceOfBaseNode) {
		if (resourceOfBaseNode == null) {
			throw new NullArgumentException("resource");
		}
		this.resourceOfBaseNode = resourceOfBaseNode;
	}

	public final ResourceToken<?> resourceOfBaseNode;

}
