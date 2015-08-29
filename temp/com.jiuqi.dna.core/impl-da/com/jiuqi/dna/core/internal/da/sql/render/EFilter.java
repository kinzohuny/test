package com.jiuqi.dna.core.internal.da.sql.render;

public interface EFilter<TItem, TContext> {

	public boolean accept(TItem item, TContext context);
}
