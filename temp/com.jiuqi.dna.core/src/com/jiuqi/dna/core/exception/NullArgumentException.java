package com.jiuqi.dna.core.exception;

/**
 * ����Ϊ���쳣
 * 
 * @author gaojingxin
 * 
 */
public class NullArgumentException extends CoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3342006009116895632L;

	public NullArgumentException(String argumentName) {
		super("����[" + argumentName + "]����Ϊ��");
	}
}
