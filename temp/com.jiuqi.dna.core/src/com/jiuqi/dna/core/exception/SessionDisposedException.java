package com.jiuqi.dna.core.exception;

public final class SessionDisposedException extends DisposedException {

	private static final long serialVersionUID = 1L;

	/**
	 * �رյ�������
	 * 
	 * @author gaojingxin
	 * 
	 */
	public enum SessionDisposedKind {
		/**
		 * һ��ر�
		 */
		NORMAL,
		/**
		 * ���ȵĻỰ�������Ǳ���DNA�������Session��
		 */
		OBSOLETE,
		/**
		 * ��½�û�ʧЧ
		 */
		USERINVALID,
	}

	/**
	 * ���Ǳ���DNA�������Session
	 */
	public final SessionDisposedKind kind;
	/**
	 * ���Ǳ���DNA�������Session������2.5��ǰ�İ汾
	 */
	@Deprecated
	public final boolean obsolete;

	public SessionDisposedException(SessionDisposedKind kind) {
		super("�Ự�Ѿ����ٻ����");
		this.kind = kind;
		this.obsolete = kind == SessionDisposedKind.OBSOLETE;
	}
}
