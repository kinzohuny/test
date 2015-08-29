package com.jiuqi.dna.core.exception;

import com.jiuqi.dna.core.def.table.TableDefine;

/**
 * ��һ�µķ��������쳣
 * 
 * <p>
 * ������������ֶ����ʹ�����߷������������ݿⲻƥ��ʱ�׳����쳣
 * 
 * @author houchunlei
 * 
 */
public final class InconsistentPartitionException extends CoreException {

	private static final long serialVersionUID = 1L;

	public InconsistentPartitionException(TableDefine table) {
		super("����[" + table.getName() + "]�ķ������������ݿⲻƥ��");
	}

}
