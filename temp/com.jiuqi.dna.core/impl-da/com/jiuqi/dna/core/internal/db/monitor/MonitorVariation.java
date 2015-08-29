package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.table.TableDeclare;
import com.jiuqi.dna.core.def.table.TableFieldDeclare;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

@Deprecated
final class MonitorVariation {

	static final String VAR_DATE = "VAR_DATE";
	static final String VAR_OPERATION = "VAR_OPERATION";
	static final String VAR_VERSION = "VAR_VERSION";

	static final DataType VAR_DATE_TYPE = TypeFactory.DATE;
	static final DataType VAR_OPERATION_TYPE = TypeFactory.NVARCHAR(2);
	static final DataType VAR_VERSION_TYPE = TypeFactory.LONG;

	final VariationMonitorImpl monitor;

	/**
	 * 变化量表的逻辑表定义
	 */
	final TableDefineImpl table;

	/**
	 * 时间字段：触发操作的时间
	 */
	final TableFieldDefineImpl date;
	/**
	 * 操作字段：触发操作是增、删、改类型。值为：I、U、D。
	 */
	final TableFieldDefineImpl operation;
	/**
	 * 事务版本字段：标识一次事务，有序递增。数据库重启后不会重置。
	 */
	final TableFieldDefineImpl version;

	MonitorVariation(VariationMonitorImpl monitor, TableDefineImpl table) {
		this.monitor = monitor;
		this.table = table;
		this.date = table.getColumn(VAR_DATE);
		this.operation = table.getColumn(VAR_OPERATION);
		this.version = table.getColumn(VAR_VERSION);
		this.deleteRange = this.deleteRange();
		this.deleteEntire = this.deleteEntire();
	}

	private final DeleteStatementImpl deleteRange;

	private final DeleteStatementImpl deleteRange() {
		final DeleteStatementImpl delete = new DeleteStatementImpl("rutime", "t", this.table);
		final ArgumentDefine from = delete.newArgument("from", this.version);
		final ArgumentDefine to = delete.newArgument("to", this.version);
		delete.setCondition(delete.expOf(this.version).xGE(from).and(delete.expOf(this.version).xLE(to)));
		return delete;
	}

	private final DeleteStatementImpl deleteEntire;

	private final DeleteStatementImpl deleteEntire() {
		final DeleteStatementImpl delete = new DeleteStatementImpl("rutime", "t", this.table);
		return delete;
	}

	/**
	 * 删除范围：version >= @from and version <= @to
	 * 
	 * @param adapter
	 * @param from
	 * @param to
	 */
	final int deleteRange(DBAdapter adapter, Long from, Long to) {
		return adapter.executeUpdate(this.deleteRange, from.toString(), to.toString());
	}

	final int deleteEntire(DBAdapter adapter) {
		return adapter.executeUpdate(this.deleteEntire);
	}

	/**
	 * 根据监视字段的绑定关系，构造变化量表的结构
	 * 
	 * @param target
	 * @param variation
	 * @param watches
	 */
	static final void build(TableDefineImpl target, TableDeclare variation,
			Iterable<VariationMonitorFieldMapping> watches) {
		variation.newField(VAR_DATE, VAR_DATE_TYPE);
		variation.newField(VAR_OPERATION, VAR_OPERATION_TYPE);
		variation.newField(VAR_VERSION, VAR_VERSION_TYPE);
		for (VariationMonitorFieldMapping map : watches) {
			buildWatch(target, variation, map);
		}
	}

	static final void buildWatch(TableDefineImpl target,
			TableDeclare variation, VariationMonitorFieldMapping map) {
		final TableFieldDefineImpl f = target.fields.find(map.field.getName());
		if (f == null) {
			throw new IllegalStateException("构建监视器的变化量表对象时错误：监视字段[" + map.field.getName() + "]不存在。");
		}
		final DataType type = f.getType();
		TableFieldDeclare of = variation.findColumn(map.oldValueFN);
		if (of == null) {
			variation.newField(map.oldValueFN, type);
		} else if (of.getType() != type) {
			throw new UnsupportedOperationException();
		}
		TableFieldDeclare nf = variation.findColumn(map.newValueFN);
		if (nf == null) {
			variation.newField(map.newValueFN, type);
		} else if (nf.getType() != type) {
			throw new UnsupportedOperationException();
		}
	}
}