package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class OracleSubQueryRefBuffer extends OracleRelationRefBuffer implements
		ISqlJoinedQueryRefBuffer {
	OracleSelectBuffer select;

	public OracleSubQueryRefBuffer(String alias) {
		super(alias);
	}

	public OracleSubQueryRefBuffer(String alias, TableJoinType type) {
		super(alias, type);
	}

	public OracleSelectBuffer select() {
		if (this.select == null) {
			this.select = new OracleSelectBuffer();
			this.select.replace(this.targetAlias, this.alternateAlias);
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
