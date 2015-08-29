package com.jiuqi.dna.core.impl;

/**
 * 不支持在子查询结构中使用ORDER BY子句
 * 
 * @author houchunlei
 * 
 */
public final class SubselectOrderbyNotSupportedException extends
		RuntimeException {

	private static final long serialVersionUID = -5546304362768363490L;

	public SubselectOrderbyNotSupportedException() {
		super(message());
	}

	public static final String message() {
		return "不支持在子查询结构中使用ORDER BY子句。";
	}

}
