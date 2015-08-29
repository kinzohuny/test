package com.jiuqi.dna.core.internal.common;

import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.Application;

@SuppressWarnings("deprecation")
public final class IntlAppUtl {

	public static final Application getDefault() {
		return AppUtil.getDefaultApp();
	}
}