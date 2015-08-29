package com.jiuqi.dna.core.internal.da.sql.execute;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;

public final class SqlReplacer {

	private char[] cs;
	private int limit;

	public SqlReplacer(PlainSql sql) {
		final int len = sql.text().length();
		this.cs = new char[len + 100];
		sql.text().getChars(0, len, this.cs, 0);
		this.limit = len;
		this.paramters = new ArrayList<ParameterPlaceholder>(sql.parameters.size());
		for (ParameterPlaceholder pp : sql.parameters) {
			this.paramters.add(pp);
		}
		int[] offsets = sql.calcOffsets();
		this.offsets = new ArrayList<Integer>();
		for (int i : offsets) {
			this.offsets.add(i);
		}
	}

	public final ArrayList<ParameterPlaceholder> paramters;
	private final ArrayList<Integer> offsets;

	public final void replaceInt(ParameterPlaceholder pp, int value) {
		this.replaceEach(pp, String.valueOf(value));
	}

	public final void replaceLong(ParameterPlaceholder pp, long value) {
		this.replaceEach(pp, String.valueOf(value));
	}

	private final void replaceEach(ParameterPlaceholder pp, String str) {
		for (int i = 0; i < this.paramters.size();) {
			if (this.paramters.get(i) == pp) {
				this.replaceOne(i, str);
			} else {
				i++;
			}
		}
	}

	private final void replaceOne(int param, String str) {
		final int strl = str.length();
		final int diff = strl - 1;
		final int offset = this.offsets.get(param);
		if (diff > 0) {
			if (this.limit + diff > this.cs.length) {
				char[] ncs = new char[(int) (this.cs.length * 2.16)];
				System.arraycopy(this.cs, 0, ncs, 0, this.limit);
				this.cs = ncs;
			}
			System.arraycopy(this.cs, offset, this.cs, offset + strl - 1, this.limit - offset);
			this.limit += diff;
		}
		this.paramters.remove(param);
		this.offsets.remove(param);
		for (int i = param; i < this.offsets.size(); i++) {
			this.offsets.set(i, this.offsets.get(i) + diff);
		}
		str.getChars(0, strl, this.cs, offset);
	}

	public final String sqltext() {
		return new String(this.cs, 0, this.limit);
	}

	@Override
	public final String toString() {
		return this.sqltext();
	}
}