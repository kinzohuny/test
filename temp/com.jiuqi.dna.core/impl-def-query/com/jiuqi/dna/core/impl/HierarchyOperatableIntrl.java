package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.HierarchyOperatable;
import com.jiuqi.dna.core.def.query.RelationRefDefine;
import com.jiuqi.dna.core.def.table.HierarchyDefine;

@SuppressWarnings("deprecation")
public interface HierarchyOperatableIntrl extends HierarchyOperatable {

	public HierarchyOperateExpr xParentRECID(HierarchyDefine hierarchy);

	public HierarchyOperateExpr xAncestorRECID(HierarchyDefine hierarchy,
			Object relative);

	public HierarchyOperateExpr xAncestorRECIDOfLevel(
			HierarchyDefine hierarchy, Object absolute);

	public HierarchyOperateExpr xLevelOf(HierarchyDefine hierarchy);

	public HierarchyPredicateExpr xIsLeaf(HierarchyDefine hierarchy);

	public HierarchyPredicateExpr xIsChildOf(HierarchyDefine hierarchy,
			RelationRefDefine parent);

	public HierarchyPredicateExpr xIsDescendantOf(HierarchyDefine hierarchy,
			RelationRefDefine ancestor);

	public HierarchyPredicateExpr xIsDescendantOf(HierarchyDefine hierarchy,
			RelationRefDefine ancestor, Object range);

	public HierarchyPredicateExpr xIsRelativeDescendantOf(
			HierarchyDefine hierarchy, RelationRefDefine ancestor,
			Object relative);

	public HierarchyPredicateExpr xIsParentOf(HierarchyDefine hierarchy,
			RelationRefDefine child);

	public HierarchyPredicateExpr xIsAncestorOf(HierarchyDefine hierarchy,
			RelationRefDefine descendant);

	public HierarchyPredicateExpr xIsRelativeAncestorOf(
			HierarchyDefine hierarchy, RelationRefDefine descendant,
			Object relative);
}