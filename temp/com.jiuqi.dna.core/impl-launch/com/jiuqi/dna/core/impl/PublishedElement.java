package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.service.Publish;

/**
 * 发布的元素的信息
 * 
 * @author gaojingxin
 * 
 */
public class PublishedElement extends StartupEntry {
	/**
	 * 所在空间
	 */
	protected Space space;
	/**
	 * 所在bundle
	 */
	protected BundleStub bundle;
	/**
	 * 可见性
	 */
	Publish.Mode publishMode;
}
