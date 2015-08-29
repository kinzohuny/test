package com.jiuqi.dna.core;

public interface HeavyTreeNodeFilter<TItem> extends TreeNodeFilter<TItem> {
	public Acception accept(Context context, TItem item, int absoluteLevel,
			int relativeLevel);
}
