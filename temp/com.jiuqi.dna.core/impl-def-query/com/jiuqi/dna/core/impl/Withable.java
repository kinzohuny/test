package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.WithableDeclare;

interface Withable extends WithableDeclare {

	public NamedDefineContainerImpl<DerivedQueryImpl> getWiths();

	public DerivedQueryImpl newWith(String name);

}
