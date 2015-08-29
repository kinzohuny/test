package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.SetOperator;

/**
 * 集合运算符
 * 
 * @author houchunlei
 * 
 */
public enum SetOperatorImpl implements SetOperator {

	/**
	 * 与
	 */
	UNION {
	},

	/**
	 * 与
	 */
	UNION_ALL {
	},

	/**
	 * 差
	 */
	DIFFERENCE {
	},

	/**
	 * 交
	 */
	INTERSECT {
	};
}