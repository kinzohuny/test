package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.service.Publish;

/**
 * ������Ԫ�ص���Ϣ
 * 
 * @author gaojingxin
 * 
 */
public class PublishedElement extends StartupEntry {
	/**
	 * ���ڿռ�
	 */
	protected Space space;
	/**
	 * ����bundle
	 */
	protected BundleStub bundle;
	/**
	 * �ɼ���
	 */
	Publish.Mode publishMode;
}
