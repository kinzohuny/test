package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaWithBuffer extends HanaSelectBuffer {

	final String name;

	HanaWithBuffer(HanaCommandBuffer command, String name) {
		super(command);
		this.name = HanaExprBuffer.quote(name);
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		super.writeSelectTo(sql, args);
	}
}