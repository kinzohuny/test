package com.jiuqi.dna.core.log;

import com.jiuqi.dna.core.impl.DNALogManagerInternal;

public final class DNALogManager {

	/**
	 * 获取日志记录器
	 * 
	 * @param category
	 *            日志的记录的标识，格式为由"/"分开的标识符序列，标识符只能由字母、数字、下划线构成。如：XX/XX/XX
	 * @return 返回对应的日志记录器
	 */
	public static final Logger getLogger(final String category) {
		return DNALogManagerInternal.getLogger(category);
	}

}
