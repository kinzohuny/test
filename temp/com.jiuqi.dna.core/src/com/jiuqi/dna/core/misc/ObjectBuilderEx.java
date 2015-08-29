package com.jiuqi.dna.core.misc;

/**
 * ��������չ�ӿ�
 * 
 * @author gaojingxin
 * 
 * @param <TObject> ��������
 * @param <TUserData> �û���������
 */
public interface ObjectBuilderEx<TObject, TUserData> {
	public TObject build(TUserData userData) throws Throwable;
}
