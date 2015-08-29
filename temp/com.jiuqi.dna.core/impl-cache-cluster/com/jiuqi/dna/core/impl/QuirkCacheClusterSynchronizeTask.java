package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;

public final class QuirkCacheClusterSynchronizeTask extends SimpleTask {

	List<QuirkCacheClusterData> cacheDataList;

	QuirkCacheClusterSynchronizeTask(List<QuirkCacheClusterData> cacheDataList) {
		this.cacheDataList = cacheDataList;
	}

}
