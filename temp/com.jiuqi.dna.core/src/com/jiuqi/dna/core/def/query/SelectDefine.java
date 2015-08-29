package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * ��ѯ�ṹ����.����������select�ṹ,����:select,from,where,group by,having���Ӿ�.��������order by�Ӿ�.
 * 
 * @author houchunlei
 * 
 */
public interface SelectDefine extends RelationDefine, RelationRefDomainDefine {

	/**
	 * �������Ʋ��ҵ�ǰ��ѯ�ṹ����Ĺ�ϵ����.
	 * 
	 * @param name
	 *            ��ϵ��������.
	 * @return �������򷵻�null.
	 * @deprecated ʹ��findRelationRef���.
	 */
	@Deprecated
	public QuRelationRefDefine findReference(String name);

	/**
	 * �������ƻ�ȡ��ǰ��ѯ�ṹ����Ĺ�ϵ����.
	 * 
	 * @param name
	 *            ��ϵ��������.
	 * @return ���������׳��쳣.
	 * @deprecated ʹ��getRelationRef���.
	 */
	@Deprecated
	public QuRelationRefDefine getReference(String name);

	public QuRelationRefDefine findRelationRef(String name);

	public QuRelationRefDefine getRelationRef(String name);

	/**
	 * ���ص�ǰ��ѯ�ṹ�ĵ�һ����ϵ���ö���.
	 * 
	 * @return δ�����򷵻�null.
	 */
	public QuRelationRefDefine getRootReference();

	/**
	 * ���ص�ǰ��ѯ�ṹ��������й�ϵ���õ�<strong>�������</strong>�Ŀɵ����ӿ�.
	 * 
	 * @return δ�����κι�ϵ�����򷵻ؿյ���.
	 */
	public Iterable<? extends QuRelationRefDefine> getReferences();

	/**
	 * ����й�������,��where�Ӿ䶨������.
	 * 
	 * @return δ�����򷵻�null.
	 */
	public ConditionalExpression getCondition();

	/**
	 * ��ȡ���������.
	 * 
	 * @return δ�����򷵻�null.
	 */
	public Container<? extends GroupByItemDefine> getGroupBys();

	/**
	 * ��ȡ��������.
	 * 
	 * @see com.jiuqi.dna.core.def.query.GroupByType
	 * 
	 * @return Ĭ��ΪGroupByType.DEFAULT.
	 */
	public GroupByType getGroupByType();

	/**
	 * ��ȡ�����������.
	 * 
	 * @return δ�����򷵻�null.
	 */
	public ConditionalExpression getHaving();

	public SelectColumnDefine findColumn(String columnName);

	public SelectColumnDefine getColumn(String columnName);

	/**
	 * ��ȡselect�Ӿ��Ƿ��ų��ظ���,Ĭ��Ϊfalse,�����ų��ظ���.
	 * 
	 * @return
	 */
	public boolean getDistinct();

	/**
	 * �������ֶ��б�.
	 * 
	 * @return ���᷵��null.
	 */
	public NamedElementContainer<? extends SelectColumnDefine> getColumns();

	/**
	 * ���ؼ������㶨��.
	 * 
	 * @return δ�����򷵻�null.
	 */
	public Container<? extends SetOperateDefine> getSetOperates();

	/**
	 * ����ȫ��������,����ִ���κβ���,���ؿ�.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public Container<? extends OrderByItemDefine> getOrderBys();

}
