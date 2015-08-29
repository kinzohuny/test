package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.service.Publish.Mode;

/**
 * �ռ�Ԫ�ش����������������������������ַ����ʵ���Լ����ֶ�������ʵ��
 * 
 * @author gaojingxin
 * 
 * @param <TElement>
 *            Ԫ������
 */
final class SpaceElementBroker<TElement> extends
		ServiceInvokeeBase<TElement, Context, None, None, None> {
	final TElement element;

	SpaceElementBroker(TElement element, Mode publishMode) {
		if (element == null || publishMode == null) {
			throw new NullPointerException();
		}
		this.element = element;
		this.publishMode = publishMode;
	}

	@Override
	final void afterRegInvokeeToSpace(ServiceInvokeeEntry to, Space space,
			ExceptionCatcher catcher) {
		if (this.element instanceof DeclaratorBase) {
			DeclaratorBase declarator = (DeclaratorBase) this.element;
			NamedDefineImpl define = (NamedDefineImpl) declarator.getDefine();
			for (Class<?> intfClass : declarator.getDefineIntfRegClasses()) {
				space.regNamedDefineToSpace(intfClass, define, catcher);
			}
		}
	}

	@Override
	final TElement provide(Context context) throws Throwable {
		return this.element;
	}

	@Override
	final ServiceBase<Context> getService() {
		throw new UnsupportedOperationException();
	}

	final Object getElement() {
		return this.element;
	}

	@Override
	final Class<?> getTargetClass() {
		return this.element.getClass();
	}

	@Override
	final boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask) {
		return mask == MASK_ELEMENT;
	}
}
