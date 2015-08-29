package com.jiuqi.dna.core.internal.da.sqlbuffer;

public abstract class SqlBuffer implements ISqlBuffer {

	@Override
	public String toString() {
		SqlStringBuffer sql = new SqlStringBuffer();
		this.writeTo(sql, null);
		return sql.toString();
	}
}
