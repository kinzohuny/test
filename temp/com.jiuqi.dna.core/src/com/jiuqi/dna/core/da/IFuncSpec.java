package com.jiuqi.dna.core.da;

import com.jiuqi.dna.core.da.SQLFuncSpec.SQLFuncPattern;
import com.jiuqi.dna.core.type.DataType;

public interface IFuncSpec {

	public SQLFuncPattern accept(DataType[] types);

	public String functionName();
}
