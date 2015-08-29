package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * ���ϵ����
 * 
 * @author gaojingxin
 * 
 */
public interface TableRelationDefine extends TableReferenceDefine {

	/**
	 * ��ù�ϵ��������
	 * 
	 * @return ���ر���
	 */
	public TableDefine getOwner();

	/**
	 * ��ȡ��ϵ�������������
	 * 
	 * @return
	 */
	public ConditionalExpression getJoinCondition();

	/**
	 * ��ȡ��ϵ���������
	 * 
	 * @see com.jiuqi.dna.core.def.table.TableRelationType
	 * 
	 * @return
	 */
	public TableRelationType getRelationType();

	/**
	 * ��ȡ��ǰ���ϵ�Ƿ��ǵ�ֵ���ϵ
	 * 
	 * @return
	 */
	public boolean isEquiRelation();

	/**
	 * ��ȡ��ֵ��ϵ�е�ǰ����ֶζ���
	 * 
	 * <p>
	 * isEquiRelationΪtrueʱ�ɵ��ø÷����������׳��쳣��
	 * 
	 * @return
	 */
	public TableFieldDefine getEquiRelationSelfField();

	/**
	 * ��ȡ��ֵ��ϵ��Ŀ�����ֶζ���
	 * 
	 * <p>
	 * isEquiRelationΪtrueʱ�ɵ��ø÷����������׳��쳣��
	 * 
	 * @return
	 */
	public TableFieldDefine getEquiRelationTargetField();

	/**
	 * ����ǰ���ϵת���ɵ�ֵ���ϵ
	 * 
	 * <p>
	 * <strong>ǿ�ҽ��鲻��ʹ�ø÷�����ֱ��ʹ��getEquiRelationSelfField()
	 * ��getEquiRelationTargetField().</strong>
	 * 
	 * <p>
	 * isEquiRelation()����trueʱ���Ե��ø÷���
	 * 
	 * @return
	 */
	@Deprecated
	public TableEquiRelationDefine castAsEquiRelation();
}