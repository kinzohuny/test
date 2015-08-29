package com.jiuqi.dna.core.type;

/**
 * ����������
 * 
 * @author gaojingxin
 * 
 */
public interface Type extends TypeDigestible {
	/**
	 * ��ö�Ӧ�ĸ����
	 */
	public Type getRootType();

	/**
	 * �����ͻص�detector�����࣬��ȷ�����͵Ķ�̬��Ϣ
	 */
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> detector, TUserData userData)
			throws UnsupportedOperationException ;
}
