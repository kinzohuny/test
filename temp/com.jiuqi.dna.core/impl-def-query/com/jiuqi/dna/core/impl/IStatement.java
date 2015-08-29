package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StatementDeclare;
import com.jiuqi.dna.core.internal.da.sql.render.ESql;

/**
 * �����ڲ��ӿ�
 * 
 * @author houchunlei
 * 
 */
interface IStatement extends StatementDeclare, Prepareble {

	/**
	 * ��ȡ����sql
	 * 
	 * @param dbAdapter
	 * @return
	 */
	ESql getSql(DBAdapterImpl dbAdapter);

	/**
	 * ���ز�������Ľṹ����
	 * 
	 * @return
	 */
	StructDefineImpl getArgumentsDefine();

	NamedDefineContainerImpl<StructFieldDefineImpl> getArguments();
}