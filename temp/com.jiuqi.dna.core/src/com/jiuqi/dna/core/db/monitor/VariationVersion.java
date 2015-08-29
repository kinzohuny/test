package com.jiuqi.dna.core.db.monitor;

public final class VariationVersion implements Comparable<VariationVersion> {

	public final long value;

	public VariationVersion(long value) {
		this.value = value;
	}

	public VariationVersion(String value) {
		this.value = Long.valueOf(value);
	}

	public int compareTo(VariationVersion o) {
		long thisVal = this.value;
		long anotherVal = o.value;
		return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
	}

	@Override
	public final int hashCode() {
		return (int) (this.value ^ (this.value >>> 32));
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof VariationVersion) {
			return this.value == ((VariationVersion) obj).value;
		}
		return false;
	}

	@Override
	public final String toString() {
		return Long.toString(this.value);
	}
}