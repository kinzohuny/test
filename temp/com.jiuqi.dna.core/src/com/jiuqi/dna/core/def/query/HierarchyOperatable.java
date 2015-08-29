package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.HierarchyOperateExpression;
import com.jiuqi.dna.core.def.exp.HierarchyPredicateExpression;
import com.jiuqi.dna.core.def.table.HierarchyDefine;

/**
 * @author houchunlei
 * @deprecated �ýӿ������壬ֻӦ��ʹ�����ӽӿڡ�
 */
@Deprecated
public interface HierarchyOperatable {

	/**
	 * ʹ��ָ���ļ��ζ���,���ؽڵ��ֱ�Ӹ��ڵ��RECID�ı��ʽ
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @return
	 */
	@Deprecated
	public HierarchyOperateExpression xParentRECID(HierarchyDefine hierarchy);

	/**
	 * ʹ��ָ���ļ��ζ���,���ؽڵ����ϵ�n�����ڵ��RECID�ı��ʽ.
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���.
	 * @param relative
	 *            ���ϸ��ڵ�ļ���,��ΧΪ[1,31].��ʹ��ValueExpression.builderת��Ϊ���ͱ��ʽ.
	 * 
	 * @return
	 */
	@Deprecated
	public HierarchyOperateExpression xAncestorRECID(HierarchyDefine hierarchy,
			Object relative);

	/**
	 * ʹ��ָ���ļ��ζ���,���ؽڵ�������Ϊn�ĸ��ڵ��RECID�ı��ʽ
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @param absolute
	 *            ���ڵ�ľ������,������ȷ�ΧΪ[1,31].��ʹ��ValueExpression.builderת��Ϊ���ͱ��ʽ.
	 * 
	 * @return
	 */
	@Deprecated
	public HierarchyOperateExpression xAncestorRECIDOfLevel(
			HierarchyDefine hierarchy, Object absolute);

	/**
	 * ʹ��ָ�����ζ���,���ؽڵ�ļ�����ȵı��ʽ
	 * 
	 * <p>
	 * ��ȵķ�ΧΪ[1,32]
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @return
	 */
	@Deprecated
	public HierarchyOperateExpression xLevelOf(HierarchyDefine hierarchy);

	/**
	 * ʹ��ָ���ļ��ζ���,���ص�ǰ�������޶��м��ĸ����Ƿ�ΪҶ�ӽڵ���������ʽ
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @return
	 */
	@Deprecated
	public HierarchyPredicateExpression xIsLeaf(HierarchyDefine hierarchy);

	/**
	 * ʹ��ָ���ļ��ζ���,���ص�ǰ�������޶��м�����Ŀ��������޶��м���ֱ���ӽڵ���������ʽ
	 * 
	 * <p>
	 * �ʺϴӸ��ڵ㷶Χ�Ѿ�ȷ��,��ѯ�ӽڵ�����. ���ʹ�ø÷������ӽڵ��ѯ���ڵ���Ч��ʮ�ֵ���
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @param parent
	 *            ָ�����ڵ��м��ı�����,��Ŀ���Ӧ�ڵ�ǰ�����õ�Ŀ���һ��
	 * @return
	 */
	@Deprecated
	public HierarchyPredicateExpression xIsChildOf(HierarchyDefine hierarchy,
			RelationRefDefine parent);

	/**
	 * ʹ��ָ���ļ��ζ���,���ص�ǰ�������޶��м�����Ŀ��������޶��м�������ڵ�(ֱ���ӻ����ӽڵ�)���������ʽ
	 * 
	 * <p>
	 * �ʺϴӸ��ڵ㷶Χ�Ѿ�ȷ��,��ѯ�ӽڵ�����.���ʹ�ø÷������ӽڵ��ѯ���ڵ���Ч��ʮ�ֵ���.
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @param ancestor
	 *            ָ�����Ƚڵ��м��ı�����,��Ŀ���Ӧ�ڵ�ǰ�����õ�Ŀ���һ��
	 * @return
	 */
	@Deprecated
	public HierarchyPredicateExpression xIsDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor);

	/**
	 * ʹ��ָ���ļ��ζ���,���ص�ǰ�������޶��м�����Ŀ��������޶��м��Ĳ�����n����ȵ�����ڵ���������ʽ
	 * <p>
	 * �ʺϴӸ��ڵ㷶Χ�Ѿ�ȷ��,��ѯ�ӽڵ�����. ���ʹ�ø÷������ӽڵ��ѯ���ڵ���Ч��ʮ�ֵ���.
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @param ancestor
	 *            ָ�����Ƚڵ��м��ı�����,��Ŀ���Ӧ�ڵ�ǰ�����õ�Ŀ���һ��
	 * @param relative
	 *            ָ����n��������ڵ�Ŀ��ڵ����µ�n������,ȡֵ��ΧΪ[1,31],ֱ���ӽڵ�Ϊ1.��ʹ��ValueExpression.
	 *            builderת��Ϊ���ͱ��ʽ.
	 * @return
	 */
	@Deprecated
	public HierarchyPredicateExpression xIsDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor, Object range);

	/**
	 * ʹ��ָ���ļ��ζ���,���ص�ǰ�������޶��м�����Ŀ��������޶��м��ĵ�n��������ڵ���������ʽ
	 * 
	 * <p>
	 * �ʺϴӸ��ڵ㷶Χ�Ѿ�ȷ��,��ѯ�ӽڵ�����. ���ʹ�ø÷������ӽڵ��ѯ���ڵ���Ч��ʮ�ֵ���.
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @param ancestor
	 *            ָ�����Ƚڵ��м��ı�����,��Ŀ���Ӧ�ڵ�ǰ�����õ�Ŀ���һ��
	 * @param relative
	 *            ������ȵ����ֵ.��ʹ��ValueExpression.builderת��Ϊ���ͱ��ʽ.
	 * @return
	 */
	@Deprecated
	public HierarchyPredicateExpression xIsRelativeDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor,
			Object relative);

	/**
	 * ʹ��ָ���ļ��ζ���,���ص�ǰ�������޶��м�����Ŀ��������޶��м��ĵ�ֱ�Ӹ��ڵ���������ʽ
	 * 
	 * <p>
	 * �ʺϴ��ӽڵ㷶Χ�Ѿ�ȷ��,��ѯ���ڵ�����. ���ʹ�ø÷����Ӹ��ڵ��ѯ�ӽڵ���Ч��ʮ�ֵ���.
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @param child
	 *            ָ���ӽڵ��м��ı�����,��Ŀ���Ӧ�ڵ�ǰ�����õ�Ŀ���һ��
	 * 
	 * @return
	 */
	@Deprecated
	public HierarchyPredicateExpression xIsParentOf(HierarchyDefine hierarchy,
			RelationRefDefine child);

	/**
	 * ʹ��ָ���ļ��ζ���,���ص�ǰ�������޶��м�����Ŀ��������޶��м������Ƚڵ�(ֱ�ӻ��Ӹ��ڵ�)���������ʽ
	 * 
	 * <p>
	 * �ʺϴ��ӽڵ㷶Χ�Ѿ�ȷ��,��ѯ���ڵ�����. ���ʹ�ø÷����Ӹ��ڵ��ѯ�ӽڵ���Ч��ʮ�ֵ���.
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @param descendant
	 *            ָ���ӽڵ��м��ı�����,��Ŀ���Ӧ�ڵ�ǰ�����õ�Ŀ���һ��
	 * 
	 * @return
	 */
	@Deprecated
	public HierarchyPredicateExpression xIsAncestorOf(
			HierarchyDefine hierarchy, RelationRefDefine descendant);

	/**
	 * ʹ��ָ���ļ��ζ���,���ص�ǰ�������޶��м�����Ŀ��������޶��м������Ƚڵ�(ֱ�ӻ��Ӹ��ڵ�)���������ʽ
	 * 
	 * <p>
	 * �ʺϴ��ӽڵ㷶Χ�Ѿ�ȷ��,��ѯ���ڵ�����. ���ʹ�ø÷����Ӹ��ڵ��ѯ�ӽڵ���Ч��ʮ�ֵ���.
	 * 
	 * @param hierarchy
	 *            ʹ�õļ��ζ���,Ӧ���ڵ�ǰ�����õ�Ŀ���߼���
	 * @param descendant
	 *            ָ���ӽڵ��м��ı�����,��Ŀ���Ӧ�ڵ�ǰ�����õ�Ŀ���һ��
	 * @param relative
	 *            ������ȵ����ֵ.��ʹ��ValueExpression.builderת��Ϊ���ͱ��ʽ.
	 * @return
	 */
	@Deprecated
	public HierarchyPredicateExpression xIsRelativeAncestorOf(
			HierarchyDefine hierarchy, RelationRefDefine descendant,
			Object relative);
}