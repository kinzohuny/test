package com.jiuqi.dna.core.def.query;

/**
 * ����ɾ������������������
 * 
 * @author houchunlei
 * 
 * @param <TStatement>
 */
public abstract class ModifyStatementDeclarator<TStatement extends ModifyStatementDefine>
        extends StatementDeclarator<TStatement> {

	@Override
	public abstract ModifyStatementDefine getDefine();

	ModifyStatementDeclarator(boolean cleanByCoreTag) {
		super(cleanByCoreTag);
		// ʹ�������������޷��̳�
	}
}
