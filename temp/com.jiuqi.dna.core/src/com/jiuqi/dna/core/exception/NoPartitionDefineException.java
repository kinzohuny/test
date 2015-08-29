package com.jiuqi.dna.core.exception;

import com.jiuqi.dna.core.def.table.TableDefine;

/**
 * 未定义表分区异常
 * 
 * <p>
 * 未定义表分区，却调用了表分区的相关方法时抛出此异常
 * 
 * @author houchunlei
 * 
 */
public class NoPartitionDefineException extends CoreException {

	private static final long serialVersionUID = 2962995469146334871L;

	public final TableDefine table;

	public NoPartitionDefineException(TableDefine table) {
		super("逻辑表[" + table.getName() + "]没有表分区定义.");
		this.table = table;
	}
}
