package com.jiuqi.dna.core.def;

import com.jiuqi.dna.core.def.table.TableDefine;

/**
 * 非法的运行期的元数据定义的克隆副本
 * 
 * @author houchunlei
 * 
 */
public final class IllegalRuntimeClonedCopyException extends RuntimeException {

	private static final long serialVersionUID = -4214723425606491345L;

	public final TableDefine runtime;

	public final TableDefine noneClone;

	public IllegalRuntimeClonedCopyException(TableDefine runtime,
			TableDefine noneClone) {
		super(message(noneClone));
		this.runtime = runtime;
		this.noneClone = noneClone;
	}

	public static final String message(TableDefine noneClone) {
		return "表定义对象[" + noneClone.getName() + "]不是当前运行期表定义的有效克隆副本.";
	}
}