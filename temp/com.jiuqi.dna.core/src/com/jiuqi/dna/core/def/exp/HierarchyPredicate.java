package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.impl.HierarchyPredicateImpl;

/**
 * ¼¶´ÎÎ½´Ê
 * 
 * @author houchunlei
 * 
 */
@Deprecated
public interface HierarchyPredicate {

	@Deprecated
	public static final HierarchyPredicate IS_LEAF = HierarchyPredicateImpl.IS_LEAF;
	@Deprecated
	public static final HierarchyPredicate IS_CHILD_OF = HierarchyPredicateImpl.IS_CHILD_OF;
	@Deprecated
	public static final HierarchyPredicate IS_DESCENDANT_OF = HierarchyPredicateImpl.IS_DESCENDANT_OF;
	@Deprecated
	public static final HierarchyPredicate IS_RELATIVE_DESCENDANT_OF = HierarchyPredicateImpl.IS_RELATIVE_DESCENDANT_OF;
	@Deprecated
	public static final HierarchyPredicate IS_RANGE_DESCENDANT_OF = HierarchyPredicateImpl.IS_RANGE_DESCENDANT_OF;
	@Deprecated
	public static final HierarchyPredicate IS_PARENT_OF = HierarchyPredicateImpl.IS_PARENT_OF;
	@Deprecated
	public static final HierarchyPredicate IS_ANCESTOR_OF = HierarchyPredicateImpl.IS_ANCESTOR_OF;
}