package com.jiuqi.dna.core.def;

import com.jiuqi.dna.core.def.table.TableDefine;

/**
 * �Ƿ��������ڵ�Ԫ���ݶ���Ŀ�¡����
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
		return "�������[" + noneClone.getName() + "]���ǵ�ǰ�����ڱ������Ч��¡����.";
	}
}