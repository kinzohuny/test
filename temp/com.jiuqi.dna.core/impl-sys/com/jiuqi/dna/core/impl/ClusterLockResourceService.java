package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;

public final class ClusterLockResourceService extends
		ResourceServiceBase<SynClusterClock, SynClusterClock, SynClusterClock> {

	private static final String TITLE = "集群资源锁";

	protected ClusterLockResourceService() {
		super(TITLE, ResourceKind.SINGLETON_IN_CLUSTER);
	}

	@Override
	protected void initResources(
			Context context,
			ResourceInserter<SynClusterClock, SynClusterClock, SynClusterClock> initializer)
			throws Throwable {
		final SynClusterClock synClusterClock = new SynClusterClock();
		initializer.putResource(synClusterClock);
	}

}

@StructClass
final class SynClusterClock {

}
