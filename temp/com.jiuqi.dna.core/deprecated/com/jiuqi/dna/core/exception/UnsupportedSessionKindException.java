package com.jiuqi.dna.core.exception;

import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.SiteState;

/**
 * 站点状态不支持某种会话时抛出的异常
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public final class UnsupportedSessionKindException extends CoreException {
	private static final long serialVersionUID = 1L;
	/**
	 * 站点状态
	 */
	public final SiteState siteState;
	/**
	 * 会话状态
	 */
	public final SessionKind sessionKind;

	public UnsupportedSessionKindException(SiteState siteState,
			SessionKind sessionKind) {
		super("当站点为[" + siteState + "]状态时不支持创建[" + sessionKind + "]类型的会话");
		this.siteState = siteState;
		this.sessionKind = sessionKind;
	}
}
