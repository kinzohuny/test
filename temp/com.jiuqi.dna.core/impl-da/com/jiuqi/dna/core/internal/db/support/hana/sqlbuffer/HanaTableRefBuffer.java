package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaTableRefBuffer extends HanaRelationRefBuffer implements
		ISqlJoinedTableRefBuffer {

	final String table;

	HanaTableRefBuffer(HanaCommandBuffer command, String table, String alias) {
		super(command, alias);
		this.table = HanaExprBuffer.quote(table);
	}

	HanaTableRefBuffer(HanaCommandBuffer command, String table, String alias,
			TableJoinType type) {
		super(command, alias, type);
		this.table = HanaExprBuffer.quote(table);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append(this.table);
	}
}