package com.jiuqi.dna.core.service;

import com.jiuqi.dna.core.impl.DeclaratorBase;

/**
 * �ֲ���������ע����������ʵ������������������ص�ϵͳ����
 * 
 * @author gaojingxin
 * 
 */
public interface NativeDeclaratorResolver {
	/**
	 * ʵ����������������
	 * 
	 * @param <TDeclarator>
	 *            ����������
	 * @param declaratorClass
	 *            ��������
	 * @param aditionalArgs
	 *            ����Ĳ���
	 * @return ������������ʵ��
	 */
	public <TDeclarator extends DeclaratorBase> TDeclarator resolveDeclarator(
			Class<TDeclarator> declaratorClass, Object... aditionalArgs);
}
