package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;

@StructClass
public final class RSIProperties {
	private int[] keys;
	private Object[] values;

	public final Object get(int key) {
		if (this.keys == null) {
			return null;
		}
		for (int i = 0; i < this.keys.length; i++) {
			if (this.keys[i] == key) {
				return this.values[i];
			}
		}
		return null;
	}

	public final Object set(int key, Object value) {
		if (this.keys == null) {
			if (value != null) {
				this.keys = new int[] { key };
				this.values = new Object[] { value };
			}
			return null;
		}
		int size = this.keys.length;
		for (int i = 0; i < size; i++) {
			if (this.keys[i] == key) {
				final Object ov = this.values[i];
				if (value != null) {
					this.values[i] = value;
				} else if (--size == 0) {
					this.keys = null;
					this.values = null;
				} else {
					final int[] newKeys = new int[size];
					final Object[] newValues = new Object[size];
					if (i > 0) {
						System.arraycopy(this.keys, 0, newKeys, 0, i);
						System.arraycopy(this.values, 0, newValues, 0, i);
					}
					size -= i;
					if (size > 0) {
						i++;
						System.arraycopy(this.keys, 0, newKeys, i, size);
						System.arraycopy(this.values, 0, newValues, i, size);
					}
				}
				return ov;
			}
		}
		if (value != null) {
			int[] newKeys = new int[size + 1];
			Object[] newValues = new Object[size + 1];
			System.arraycopy(this.keys, 0, newKeys, 0, size);
			System.arraycopy(this.values, 0, newValues, 0, size);
			this.keys = newKeys;
			this.values = newValues;
			this.keys[size] = key;
			this.values[size] = value;
		}
		return null;
	}

	RSIProperties() {
	}
}
