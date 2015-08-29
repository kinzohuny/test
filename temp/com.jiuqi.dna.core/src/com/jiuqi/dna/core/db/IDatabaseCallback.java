package com.jiuqi.dna.core.db;

public interface IDatabaseCallback<TArgument1, TArgument2> {
	
	public boolean call(TArgument1 arg1,TArgument2 argument2);
}
