package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.SetOperator;

/**
 * ���������
 * 
 * @author houchunlei
 * 
 */
public enum SetOperatorImpl implements SetOperator {

	/**
	 * ��
	 */
	UNION {
	},

	/**
	 * ��
	 */
	UNION_ALL {
	},

	/**
	 * ��
	 */
	DIFFERENCE {
	},

	/**
	 * ��
	 */
	INTERSECT {
	};
}