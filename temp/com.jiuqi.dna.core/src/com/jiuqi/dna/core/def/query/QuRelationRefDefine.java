package com.jiuqi.dna.core.def.query;

/**
 * ��ѯ������ʹ�õĹ�ϵ���ö���
 * 
 * <p>
 * �������ڲ�ѯ������ʹ�õĹ�ϵ����.Ϊ���ӿ�.
 * 
 * @author houchunlei
 */
public interface QuRelationRefDefine extends RelationRefDefine {

	/**
	 * �ñ������Ƿ�֧�ָ���
	 * 
	 * <p>
	 * ������������Ϊ֧�ָ���ʱ���ڲ�ѯ����Ĳ�ѯ���У��������Ŀ���߼����������Ϣ.
	 */
	public boolean getForUpdate();

	/**
	 * ǿ��ת��ΪTableReference����
	 * 
	 * <p>
	 * ȷ��isTableReference����true,�����׳��쳣
	 * 
	 * @return
	 */
	@Deprecated
	public QuTableRefDefine castAsTableRef();

	/**
	 * ǿ��ת��ΪQueryReference����
	 * 
	 * <p>
	 * ȷ��isQueryReference����true,�����׳��쳣
	 * 
	 * @return
	 */
	@Deprecated
	public QuQueryRefDefine castAsQueryRef();

}
