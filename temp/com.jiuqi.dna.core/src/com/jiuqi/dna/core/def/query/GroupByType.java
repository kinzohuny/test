package com.jiuqi.dna.core.def.query;

/**
 * ��������
 * 
 * <p>
 * ��ѯĬ�Ϸ�������Ϊdefault
 * 
 * @author houchunlei
 * 
 */
public enum GroupByType {

	/**
	 * Ĭ�ϵķ������
	 */
	DEFAULT,

	/**
	 * ָ��������а���rollup���͵Ļ�����
	 */
	ROLL_UP,

	/**
	 * ָ��������а���cube���͵Ļ�����
	 * 
	 * @deprecated ��֧��
	 */
	@Deprecated
	CUBE;

}
