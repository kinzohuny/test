package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.impl.DeclaratorBase;

/**
 *
 * 数据库语句声明器基类
 *
 * @param <TStatementDefine>
 *            语句类型
 *
 * @author houchunlei
 */
public abstract class StatementDeclarator<TStatementDefine extends StatementDefine>
		extends DeclaratorBase {

	@Override
	public abstract StatementDefine getDefine();

	StatementDeclarator(boolean cleanByCoreTag) {
		super(cleanByCoreTag);
		// 使得其他包的类无法继承
	}
}
