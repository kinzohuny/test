package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.HashMap;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class KingbasePredefinedSubQueryBuffer extends KingbaseSelectBuffer {
	final String alias;

	public KingbasePredefinedSubQueryBuffer(String alias) {
		this.alias = alias;
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append(this.alias).append(" as (");
		super.writeTo(sql, args);
		sql.append(')');
	}

	public String addSQL(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append('(');
		super.writeTo(sql, args);
		sql.append(')');
		return this.alias;
	}

	public String addSQLWith(SqlStringBuffer sql, HashMap hmsql,
			List<ParameterPlaceholder> args) {
		sql.append('(');
		super.writeWithTo(sql, hmsql, args);
		sql.append(')');
		return this.alias;
	}
}
