package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.HierarchyOperator;

@SuppressWarnings("deprecation")
public enum HierarchyOperatorImpl implements HierarchyOperator {

	/**
	 * ָʾ���ڵ�RECID�ı��ʽ
	 */
	PARENT_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * ָʾ��Ե�n�����Ƚ��ı��ʽ
	 */
	RELATIVE_ANCESTOR_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * ָʾ���Ե�n�����ڵ�ı��ʽ
	 */
	ABUSOLUTE_ANCESTOR_RECID {

		@Override
		final GUIDType getType() {
			return GUIDType.TYPE;
		}

	},

	/**
	 * ָʾ�ڵ㼶����ȵı��ʽ
	 */
	LEVEVL_OF {

		@Override
		final IntType getType() {
			return IntType.TYPE;
		}

	};

	abstract DataTypeBase getType();
}