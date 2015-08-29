package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaQueryRefBuffer extends HanaRelationRefBuffer implements
		ISqlJoinedQueryRefBuffer {

	final HanaSelectBuffer select;

	HanaQueryRefBuffer(HanaCommandBuffer command, String alias) {
		super(command, alias);
		this.select = new HanaSelectBuffer(command);
	}

	HanaQueryRefBuffer(HanaCommandBuffer command, String alias,
			TableJoinType type) {
		super(command, alias, type);
		this.select = new HanaSelectBuffer(command);
	}

	public final HanaSelectBuffer select() {
		return this.select;
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append('(');
		this.select.writeSelectTo(sql, args);
		sql.append(')');
	}
}