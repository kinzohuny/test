package com.jiuqi.dna.core.spi.application;

/**
 * 会话初始化接口，供外部调用
 * 
 * @author gaojingxin
 * 
 * @param <TUserData>
 */
public interface SessionIniter<TUserData> {
	/**
	 * 会话创建后调用该方法，抛出异常则会话被销毁
	 * 
	 */
	public void initSession(Session session, TUserData userData)
			throws Throwable;
}
