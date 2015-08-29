package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaWithRefBuffer extends HanaRelationRefBuffer implements
		ISqlJoinedWithRefBuffer {

	final HanaWithBuffer with;

	HanaWithRefBuffer(HanaCommandBuffer command, String target, String alias) {
		super(command, alias);
		if (!(command instanceof HanaQueryBuffer)) {
			throw new UnsupportedOperationException();
		}
		HanaQueryBuffer query = (HanaQueryBuffer) command;
		this.with = query.getWith(target);
	}

	HanaWithRefBuffer(HanaCommandBuffer command, String target, String alias,
			TableJoinType type) {
		super(command, alias, type);
		if (!(command instanceof HanaQueryBuffer)) {
			throw new UnsupportedOperationException();
		}
		HanaQueryBuffer query = (HanaQueryBuffer) command;
		this.with = query.getWith(target);
	}

	@Override
	protected void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append('(');
		this.with.writeTo(sql, args);
		sql.append(')');
	}

}
