package com.jiuqi.dna.core.impl;

interface SQLVisitable {
	public <T> void accept(T visitorContext, SQLVisitor<T> visitor);
}
