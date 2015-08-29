package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.spi.publish.Bundleable;

/**
 * ����������
 * 
 * @author gaojingxin
 * 
 * @param <TDefine>
 */
public abstract class DeclaratorBase implements Bundleable {

	/**
	 * ����׶��޷�������ö��޷���ɵ�����,�ڸ÷���������
	 * 
	 * @param querier
	 *            ����������
	 */
	protected void declareUseRef(ObjectQuerier querier) {
	}

	public abstract NamedDefine getDefine();

	/**
	 * ������߼���ܷ����ʵ����
	 */
	static ContextImpl<?, ?, ?> newInstanceByCore;

	/**
	 * ����Bundle;
	 */
	BundleStub bundle;

	public final BundleStub getBundle() {
		return this.bundle;
	}

	public DeclaratorBase(boolean cleanByCoreTag) {
		if (newInstanceByCore == null) {
			throw new UnsupportedOperationException("����������ֻ������ܴ�������֧�ֶ�������:" + this.getClass().getName());
		} else if (cleanByCoreTag) {
			newInstanceByCore = null;
		}
	}

	protected abstract Class<?>[] getDefineIntfRegClasses();

	private boolean refDeclared;

	final boolean tryDeclareUseRef(ObjectQuerier querier) {
		if (!this.refDeclared) {
			this.declareUseRef(querier);
			return this.refDeclared = true;
		} else {
			return false;
		}
	}
}