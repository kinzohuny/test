package com.jiuqi.dna.core.impl;

public interface OMVisitable {

	<TContext> void visit(OMVisitor<TContext> visitor, TContext context);
}