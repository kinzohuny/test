package com.jiuqi.dna.core.def.table;

/**
 * ��ֵ���ϵ
 * 
 * @author houchunlei
 * 
 */
@Deprecated
public interface TableEquiRelationDefine extends TableRelationDefine {

	/**
	 * ��ֵ�����е�ǰ����ֶζ���
	 * 
	 * @return
	 */
	public TableFieldDefine getSelfField();

	/**
	 * ��ֵ������Ŀ�����ֶζ���
	 * 
	 * @return
	 */
	public TableFieldDefine getTargetField();
}