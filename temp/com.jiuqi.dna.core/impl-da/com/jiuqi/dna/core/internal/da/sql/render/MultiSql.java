package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.PlainSql;

public abstract class MultiSql implements ESql {

	public final ArrayList<PlainSql> sqls = new ArrayList<PlainSql>();
	public PlainSql std = new PlainSql();
}