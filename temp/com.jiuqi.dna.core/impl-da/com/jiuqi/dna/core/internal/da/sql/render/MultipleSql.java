package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.PlainSql;

public abstract class MultipleSql extends PlainSql implements ESql {

	public final ArrayList<PlainSql> others = new ArrayList<PlainSql>();

	public final PlainSql addSql() {
		PlainSql sql = new PlainSql();
		this.others.add(sql);
		return sql;
	}
}