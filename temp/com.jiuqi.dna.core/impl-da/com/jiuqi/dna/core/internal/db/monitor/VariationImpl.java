package com.jiuqi.dna.core.internal.db.monitor;

import java.sql.Timestamp;

import com.jiuqi.dna.core.da.DataManipulation;
import com.jiuqi.dna.core.db.monitor.Variation;
import com.jiuqi.dna.core.db.monitor.VariationMonitorField;
import com.jiuqi.dna.core.db.monitor.VariationVersion;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

final class VariationImpl implements Variation {

	final VariationSetImpl set;
	final GUID id;
	final Timestamp instant;
	final DataManipulation operation;
	final VariationVersion version;
	final int size;
	final ReadableValue[] oldValues;
	final ReadableValue[] newValues;

	VariationImpl(VariationSetImpl set, GUID id, Timestamp instant,
			DataManipulation operation, long version) {
		this.set = set;
		this.id = id;
		this.instant = instant;
		this.operation = operation;
		// XXX 优化对象构造
		this.version = new VariationVersion(version);
		this.size = set.loader.size;
		this.oldValues = new ReadableValue[this.size];
		this.newValues = new ReadableValue[this.size];
	}

	public final GUID id() {
		return this.id;
	}

	public final Timestamp instant() {
		return this.instant;
	}

	public final DataManipulation operation() {
		return this.operation;
	}

	public final VariationVersion version() {
		return this.version;
	}

	public final int size() {
		return this.size;
	}

	public final ReadableValue oldValue(int field) {
		return this.oldValues[field];
	}

	public final ReadableValue oldValue(VariationMonitorField field) {
		if (field == null) {
			throw new NullPointerException("监控字段定义为空。");
		}
		final VariationMonitorFieldImpl f = (VariationMonitorFieldImpl) field;
		if (f.monitor != this.set.loader.monitor) {
			throw new IllegalArgumentException("不是当前监视器的监视字段。");
		}
		return this.oldValues[this.set.loader.indexFor(f)];
	}

	public final ReadableValue oldValue(TableFieldDefine field) {
		if (field == null) {
			throw new NullPointerException("逻辑表字段定义为空。");
		}
		final TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		return this.oldValues[this.set.loader.indexFor(f)];
	}

	public final ReadableValue newValue(int field) {
		return this.newValues[field];
	}

	public final ReadableValue newValue(VariationMonitorField field) {
		if (field == null) {
			throw new NullPointerException("监控字段定义为空。");
		}
		final VariationMonitorFieldImpl f = (VariationMonitorFieldImpl) field;
		if (f.monitor != this.set.loader.monitor) {
			throw new IllegalArgumentException("不是当前监视器的监视字段。");
		}
		return this.newValues[this.set.loader.indexFor(f)];
	}

	public final ReadableValue newValue(TableFieldDefine field) {
		if (field == null) {
			throw new NullPointerException("逻辑表字段定义为空。");
		}
		final TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		return this.newValues[this.set.loader.indexFor(f)];
	}
}