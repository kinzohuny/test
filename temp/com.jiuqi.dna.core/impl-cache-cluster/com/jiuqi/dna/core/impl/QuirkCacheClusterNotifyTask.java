package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;

public final class QuirkCacheClusterNotifyTask extends SimpleTask {

	QuirkCacheClusterDataCollectTask synchronizeData;

	List<NetNodeImpl> nodeList;

	public QuirkCacheClusterNotifyTask(
			QuirkCacheClusterDataCollectTask synchronizeData) {
		this.synchronizeData = synchronizeData;
		this.nodeList = new ArrayList<NetNodeImpl>();
	}

}
