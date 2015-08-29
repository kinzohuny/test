package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.type.DataType;

public class PlainSql {

	public final boolean isNull() {
		return this.text == null || this.text.length() == 0;
	}

	@Override
	public final String toString() {
		return this.text;
	}

	public final void build(ISqlCommandBuffer buffer) {
		this.text = buffer.build(this.parameters);
	}

	public final void build(CharSequence sql) {
		this.text = sql.toString();
	}

	public static final ArgumentPlaceholder arg(StructFieldDefineImpl sf,
			DataType type) {
		return new ArgumentPlaceholder(sf, type);
	}

	private String text;

	public final ArrayList<ParameterPlaceholder> parameters = new ArrayList<ParameterPlaceholder>();

	public final String text() {
		return this.text;
	}

	private int[] offsets;

	public final int[] calcOffsets() {
		if (this.offsets == null) {
			synchronized (this) {
				if (this.offsets == null) {
					int[] offsets = new int[this.parameters.size()];
					boolean outStr = true;
					final String str = this.text;
					final int strl = this.text.length();
					for (int i = 0, param = 0; i < strl; i++) {
						char c = str.charAt(i);
						switch (c) {
						case '\'':
							outStr = !outStr;
							break;
						case '?':
							if (outStr) {
								offsets[param++] = i;
							}
							break;
						}
					}
					this.offsets = offsets;
				}
			}
		}
		return this.offsets;
	}
}