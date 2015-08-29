package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class KingbaseConditionBuffer extends SqlBuffer implements
		ISqlConditionBuffer {
	final KingbaseSegmentBuffer scope;
	ArrayList<KingbaseExprBuffer> when = new ArrayList<KingbaseExprBuffer>();
	ArrayList<KingbaseSegmentBuffer> then = new ArrayList<KingbaseSegmentBuffer>();
	KingbaseSegmentBuffer elseThen;

	public KingbaseConditionBuffer(KingbaseSegmentBuffer scope) {
		this.scope = scope;
	}

	public ISqlExprBuffer newWhen() {
		KingbaseExprBuffer e = new KingbaseExprBuffer();
		this.when.add(e);
		return e;
	}

	public ISqlSegmentBuffer newThen() {
		KingbaseSegmentBuffer s = new KingbaseSegmentBuffer(this.scope);
		this.then.add(s);
		return s;
	}

	public ISqlSegmentBuffer elseThen() {
		if (this.elseThen == null) {
			this.elseThen = new KingbaseSegmentBuffer(this.scope);
		}
		return this.elseThen;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("if ");
		this.when.get(0).writeTo(sql, args);
		sql.append(" then ");
		this.then.get(0).writeTo(sql, args);
		for (int i = 1, c = this.when.size(); i < c; i++) {
			sql.append(" elsif ");
			this.when.get(i).writeTo(sql, args);
			sql.append(" then ");
			this.then.get(i).writeTo(sql, args);
		}
		if (this.elseThen != null) {
			sql.append(" else ");
			this.elseThen.writeTo(sql, args);
		}
		sql.append(" end if;");
	}
}