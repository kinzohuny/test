package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableRelationDefine;

public final class NotEquiRelationException extends RuntimeException {

	private static final long serialVersionUID = -8365196318550032672L;

	public NotEquiRelationException(TableRelationDefine relation) {
		super("��ϵ����[" + relation.getName() + "]���ǵ�ֵ���ϵ����.");
	}
}
