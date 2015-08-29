package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.spi.auth.AuthorityInheritPath;

final class AuthorityInheritPathImplement implements AuthorityInheritPath {

	static final class NodeImplement implements Node {

		private NodeImplement(final ResourceToken<?> resource) {
			if (resource instanceof AccessControlCacheHolder<?, ?, ?>) {
				AccessControlCacheHolder<?, ?, ?> ACItem = (AccessControlCacheHolder<?, ?, ?>) resource;
				this.value = ACItem;
				this.title = ACItem.getAccessControlTitle();
				this.operations = ACItem.ownGroup.define.accessControlDefine.operations;
			} else if (resource instanceof AccessControlCacheHolderOfGroup) {
				AccessControlCacheHolderOfGroup ACGroup = (AccessControlCacheHolderOfGroup) resource;
				this.value = ACGroup;
				this.title = ACGroup.getTitle();
				this.operations = ACGroup.getOperations();
			} else {
				throw new UnsupportedAccessControlException(resource.getFacadeClass());
			}
		}

		public final NodeImplement getInheritNode() {
			return this.inheritNode;
		}

		public final ResourceToken<?> getValue() {
			return this.value;
		}

		public final String getTitle() {
			return this.title;
		}

		public final Operation<?>[] getOperations() {
			return this.operations;
		}

		final NodeImplement setInheritNode(
				final ResourceToken<?> resourceOfInheritNode) {
			return this.inheritNode = new NodeImplement(resourceOfInheritNode);
		}

		final ResourceToken<?> value;

		final String title;

		final Operation<?>[] operations;

		private NodeImplement inheritNode;

	}

	AuthorityInheritPathImplement(ResourceToken<?> resourceOfBaseNode) {
		this.baseNode = new NodeImplement(resourceOfBaseNode);
	}

	public final NodeImplement getBaseNode() {
		return this.baseNode;
	}

	final NodeImplement baseNode;

}
