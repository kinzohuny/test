package com.jiuqi.dna.core.exception;

import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.SiteState;

/**
 * վ��״̬��֧��ĳ�ֻỰʱ�׳����쳣
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public final class UnsupportedSessionKindException extends CoreException {
	private static final long serialVersionUID = 1L;
	/**
	 * վ��״̬
	 */
	public final SiteState siteState;
	/**
	 * �Ự״̬
	 */
	public final SessionKind sessionKind;

	public UnsupportedSessionKindException(SiteState siteState,
			SessionKind sessionKind) {
		super("��վ��Ϊ[" + siteState + "]״̬ʱ��֧�ִ���[" + sessionKind + "]���͵ĻỰ");
		this.siteState = siteState;
		this.sessionKind = sessionKind;
	}
}
