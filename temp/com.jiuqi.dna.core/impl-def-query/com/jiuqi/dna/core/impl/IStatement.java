package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.StatementDeclare;
import com.jiuqi.dna.core.internal.da.sql.render.ESql;

/**
 * 语句的内部接口
 * 
 * @author houchunlei
 * 
 */
interface IStatement extends StatementDeclare, Prepareble {

	/**
	 * 获取语句的sql
	 * 
	 * @param dbAdapter
	 * @return
	 */
	ESql getSql(DBAdapterImpl dbAdapter);

	/**
	 * 返回参数定义的结构对象
	 * 
	 * @return
	 */
	StructDefineImpl getArgumentsDefine();

	NamedDefineContainerImpl<StructFieldDefineImpl> getArguments();
}