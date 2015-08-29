package com.jiuqi.dna.core.exception;

import com.jiuqi.dna.core.auth.Operation;

/**
 * �޷���Ȩ���쳣<br>
 * ����û���ͼ��ĳ����Դ����δ��Ȩ�Ĳ��������׳����쳣��
 * 
 * @see com.jiuqi.dna.core.exception.CoreException
 * @author LiuZhi 2010-01-08
 */
public final class NoAccessAuthorityException extends CoreException {

	private static final long serialVersionUID = 9053494390777139222L;

	public NoAccessAuthorityException() {
		super("�޷���Ȩ�ޡ�");
	}

	/**
	 * �׳��޷���Ȩ���쳣<br>
	 * ����û���ͼ��ĳ����Դ����δ��Ȩ�Ĳ��������׳����쳣��
	 * 
	 * @param message
	 *            �쳣��Ϣ
	 */
	public NoAccessAuthorityException(String message) {
		super(message);
	}

	/**
	 * �׳��޷���Ȩ���쳣<br>
	 * ����û���ͼ��ĳ����Դ����δ��Ȩ�Ĳ��������׳����쳣��
	 * 
	 * @param resourceDescription
	 *            ��Դ��������Ϣ
	 * @param operation
	 *            ����Դ�Ĳ���
	 */
	public NoAccessAuthorityException(String resourceDescription,
			Operation<?> operation) {
		super("��ǰ�û�û�ж���Դ[" + resourceDescription + "]��[" + operation.getTitle()
				+ "]����Ȩ�ޡ�");
	}

}
