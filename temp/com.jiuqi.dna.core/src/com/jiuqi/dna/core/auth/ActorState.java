package com.jiuqi.dna.core.auth;

/**
 * ������״̬<br>
 * Ŀǰ���ֻ֧��<code>NORMAL</code>(����)<code>DISABLE</code>(����)����״̬��
 * 
 * @author LiuZhi 2009-11
 */
public enum ActorState {

	/**
	 * ����״̬
	 */
	NORMAL,

	/**
	 * ����״̬
	 */
	DISABLE,

	/**
	 * ��Ч״̬
	 */
	DISPOSED;

	public static final ActorState DEFUALT_STATE;

	static {
		DEFUALT_STATE = ActorState.NORMAL;
	}

}
