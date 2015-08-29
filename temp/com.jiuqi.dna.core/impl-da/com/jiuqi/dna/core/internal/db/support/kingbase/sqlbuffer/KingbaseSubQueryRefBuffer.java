package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class KingbaseSubQueryRefBuffer extends KingbaseRelationRefBuffer
		implements ISqlJoinedQueryRefBuffer {

	KingbaseSelectBuffer select;

	public KingbaseSubQueryRefBuffer(String alias) {
		super(alias);
	}

	public KingbaseSubQueryRefBuffer(String alias, TableJoinType type) {
		super(alias, type);
	}

	public KingbaseSelectBuffer select() {
		if (this.select == null) {
			this.select = new KingbaseSelectBuffer();
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
