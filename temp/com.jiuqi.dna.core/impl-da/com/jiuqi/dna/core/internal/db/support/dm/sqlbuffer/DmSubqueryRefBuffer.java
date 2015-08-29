package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class DmSubqueryRefBuffer extends DmSourceBuffer implements
		ISqlJoinedQueryRefBuffer {

	DmSelectBuffer select;

	DmSubqueryRefBuffer(String alias) {
		super(alias);
	}

	DmSubqueryRefBuffer(String alias, TableJoinType type) {
		super(alias, type);
	}

	public ISqlSelectBuffer select() {
		if (this.select == null) {
			this.select = new DmSelectBuffer();
		}
		return this.select;
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append('(');
		this.select.writeTo(sql, args);
		sql.append(')');
	}
}