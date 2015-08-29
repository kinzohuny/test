package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.ModifiableContainer;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * ��ѯ��䶨��
 * 
 * <p>
 * ����һ����ִ�еĲ�ѯ���ṹ
 * 
 * @see com.jiuqi.dna.core.def.query.QueryStatementDefine
 * 
 * @author gaojingxin
 * 
 */
public interface QueryStatementDeclare extends QueryStatementDefine,
		SelectDeclare, StatementDeclare, WithableDeclare {

	/**
	 * ʹ��������ѯ�ṹ���ӵ�ǰ��ѯ���Ĺ��조������ѯ���塱��
	 * 
	 * <p>
	 * ���������ѯ�ṹ�ǲ�ѯ��䶨�壬�ò������¡�����ж����With�飬�������¡�������塣
	 * 
	 * @param sample
	 * @return ������ѯ���壬����from�Ӿ��with�顣
	 */
	public DerivedQueryDeclare newDerivedQuery(SelectDefine sample);

	public ModifiableNamedElementContainer<? extends QueryColumnDeclare> getColumns();

	public QueryColumnDeclare newColumn(RelationColumnDefine field);

	public QueryColumnDeclare newColumn(RelationColumnDefine field, String alias);

	public QueryColumnDeclare newColumn(ValueExpression expr, String alias);

	public QueryColumnDeclare newColumn(ValueExpression expression);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @return �����������б�
	 */
	public ModifiableContainer<? extends OrderByItemDeclare> getOrderBys();

	/**
	 * �����������
	 * 
	 * <p>
	 * ���������union֮�����
	 * 
	 * @param field
	 *            �������ݵĹ�ϵ��
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column);

	/**
	 * �����������
	 * 
	 * <p>
	 * ���������union֮�����
	 * 
	 * @param field
	 *            �������ݵĹ�ϵ��
	 * @param isDesc
	 *            �Ƿ���
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc);

	/**
	 * �����������
	 * 
	 * <p>
	 * ���������union֮�����
	 * 
	 * @param value
	 *            �������ݵı��ʽ
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(ValueExpression value);

	/**
	 * �����������
	 * 
	 * <p>
	 * ���������union֮�����
	 * 
	 * @param value
	 *            �������ݵı��ʽ
	 * @param isDesc
	 *            �Ƿ���
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(ValueExpression value, boolean isDesc);

}
