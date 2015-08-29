package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * ������ֻ�����޸��˿���Ȩ��Դ�����󣬸�����Ӧ��Ȩ����Ϣ��
 * 
 * @author liuzhi
 * 
 */
public class UpdateResourceCategoryTask extends SimpleTask {

	public final Class<?> oldFacadeClass;

	public final Class<?> newFacadeClass;

	public UpdateResourceCategoryTask(final Class<?> oldFacadeClass,
			final Class<?> newFacadeClass) {
		if (oldFacadeClass == null) {
			throw new NullArgumentException("oldFacadeClass");
		}
		if (newFacadeClass == null) {
			throw new NullArgumentException("newFacadeClass");
		}
		this.oldFacadeClass = oldFacadeClass;
		this.newFacadeClass = newFacadeClass;
	}

}
