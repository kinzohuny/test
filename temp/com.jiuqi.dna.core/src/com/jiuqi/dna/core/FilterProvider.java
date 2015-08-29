package com.jiuqi.dna.core;

public interface FilterProvider<TItem> {

	public Filter<TItem> get();
}