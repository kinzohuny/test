package com.jiuqi.dna.core.model;

import java.util.List;

import com.jiuqi.dna.core.Context;

public interface ModelPropertyListener<TPropertyData> {
	public void PropertyChanged(Context context, ModelMonitor monitor,
	        List<TPropertyData> changes);
}
