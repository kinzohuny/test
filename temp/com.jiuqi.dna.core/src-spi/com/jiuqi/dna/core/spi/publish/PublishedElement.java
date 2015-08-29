package com.jiuqi.dna.core.spi.publish;

public abstract class PublishedElement extends
        com.jiuqi.dna.core.impl.PublishedElement {
	public final BundleToken getBundle() {
		return super.bundle;
	}

	public final SpaceToken getSpace() {
		return super.space;
	}
}
