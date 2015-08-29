package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 该任务只用于修改了可授权资源外观类后，更新相应的权限信息。
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
