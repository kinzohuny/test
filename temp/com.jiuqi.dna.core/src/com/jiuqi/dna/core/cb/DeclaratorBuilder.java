package com.jiuqi.dna.core.cb;

import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.impl.DeclaratorBuilderImpl;

/**
 * �������Ĵ���������
 * 
 * @author houchunlei
 * 
 */
public interface DeclaratorBuilder extends DefineHolder {

	/**
	 * �����������Ĺ�����
	 * 
	 * @author houchunlei
	 * 
	 */
	public interface DeclaratorBuilderFactory {

		public DeclaratorBuilder newInstance();
	}

	/**
	 * �����������ľ�̬����
	 */
	public static final DeclaratorBuilderFactory factory = new DeclaratorBuilderFactory() {

		public DeclaratorBuilder newInstance() {
			return DeclaratorBuilderImpl.newInstance();
		}
	};

	/**
	 * �����������Ĵ���
	 * 
	 * @param out
	 *            �������
	 * @param type
	 *            Ԫ��������
	 * @param name
	 *            Ŀ��Ԫ���ݶ���
	 * @param provider
	 *            Ԫ�����ṩ��
	 * @throws IllegalArgumentException
	 */
	public void build(Appendable out, MetaElementType type, String name,
			DefineProvider provider) throws IllegalArgumentException;
}