package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.impl.DeclaratorBase;

/**
 *
 * ���ݿ��������������
 *
 * @param <TStatementDefine>
 *            �������
 *
 * @author houchunlei
 */
public abstract class StatementDeclarator<TStatementDefine extends StatementDefine>
		extends DeclaratorBase {

	@Override
	public abstract StatementDefine getDefine();

	StatementDeclarator(boolean cleanByCoreTag) {
		super(cleanByCoreTag);
		// ʹ�������������޷��̳�
	}
}
