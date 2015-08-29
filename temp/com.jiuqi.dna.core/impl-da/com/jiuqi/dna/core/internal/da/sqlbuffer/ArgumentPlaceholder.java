package com.jiuqi.dna.core.internal.da.sqlbuffer;

import com.jiuqi.dna.core.impl.DataTypeInternal;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.type.DataType;

public final class ArgumentPlaceholder extends ParameterPlaceholder {

	public final StructFieldDefineImpl arg;
	public final DataTypeInternal type;

	public ArgumentPlaceholder(StructFieldDefineImpl arg, DataType type) {
		this.arg = arg;
		this.type = (DataTypeInternal) type;
	}
}