package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.invoke.SimpleTask;

@StructClass
public final class GetClusterSessionCountTask extends SimpleTask {

	private int count;
	public final boolean excludeBuildinUser;

	public GetClusterSessionCountTask(boolean excludeBuildinUser) {
		this.excludeBuildinUser = excludeBuildinUser;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return this.count;
	}

}
