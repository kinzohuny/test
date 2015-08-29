package com.jiuqi.dna.core.internal.db.monitor;

import static com.jiuqi.dna.core.internal.db.monitor.VariationStruct.VAR_DATE;
import static com.jiuqi.dna.core.internal.db.monitor.VariationStruct.VAR_OPERATION;
import static com.jiuqi.dna.core.internal.db.monitor.VariationStruct.VAR_VERSION;
import static com.jiuqi.dna.core.internal.db.monitor.VariationStruct.VAR_VERSION_TYPE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.da.DataManipulation;
import com.jiuqi.dna.core.db.monitor.Variation;
import com.jiuqi.dna.core.db.monitor.VariationVersion;
import com.jiuqi.dna.core.def.Namable;
import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.query.SQLFunc;
import com.jiuqi.dna.core.def.table.TableReferenceDeclare;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.QueryStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.impl.ValueExpr;
import com.jiuqi.dna.core.internal.common.ValueReader;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;
import com.jiuqi.dna.core.misc.Boundary;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

/**
 * 变化量的控制器
 * 
 * <p>
 * 非请求级别，被监视器长期持有。根据监视器的结构决定，在每次监视器结构变化后重新生成。
 * 
 * @author houchunlei
 * 
 */
final class VariationControl {

	final VariationMonitorImpl monitor;

	/**
	 * 监视器的版本
	 */
	final long version;

	/**
	 * 监事字段的个数
	 */
	final int size;

	/**
	 * 监视字段在列表中的序号
	 */
	private final HashMap<String, Integer> map;

	VariationControl(VariationMonitorImpl monitor, TableDefineImpl target,
			TableDefineImpl variation) {
		this.monitor = monitor;
		this.version = monitor.getVersion();
		this.size = monitor.watches.size();
		this.map = this.mapFor(monitor);
		this.initialize(target);
		this.entire = this.entire(variation);
		this.next = this.next(variation);
		this.between = this.between(variation);
		this.max = this.max(variation);
		this.deleteOutdated = this.deleteOutdated(variation);
		this.deleteSpecified = this.deleteSpecified(variation);
	}

	private int jdbcIndexForId;
	private int jdbcIndexForDate;
	private int jdbcIndexForOperation;
	private int jdbcIndexForVersion;
	private Bind[] jdbcIndexForOldValues;
	private Bind[] jdbcIndexForNewValues;

	/**
	 * 标准顺序的输出序号
	 */
	private final void initialize(TableDefineImpl target) {
		this.jdbcIndexForOldValues = new Bind[this.size];
		this.jdbcIndexForNewValues = new Bind[this.size];
		int jdbcIndex = 0;
		this.jdbcIndexForId = ++jdbcIndex;
		this.jdbcIndexForDate = ++jdbcIndex;
		this.jdbcIndexForOperation = ++jdbcIndex;
		this.jdbcIndexForVersion = ++jdbcIndex;
		for (int i = 0; i < this.monitor.watches.size(); i++) {
			final VariationMonitorFieldImpl mf = this.monitor.watches.get(i);
			final ValueReader reader = target.getColumn(mf.watchFN).getType().detect(READER, mf);
			this.jdbcIndexForOldValues[i] = new Bind(reader, ++jdbcIndex);
			this.jdbcIndexForNewValues[i] = new Bind(reader, ++jdbcIndex);
		}
	}

	/**
	 * 满足顺序的变化量输出的查询
	 * 
	 * @return
	 */
	private final QueryStatementImpl select(TableDefineImpl variation) {
		final QueryStatementImpl query = new QueryStatementImpl("zm");
		final TableReferenceDeclare tr = query.newReference(variation, "t");
		query.newColumn(tr.expOf(variation.f_recid));
		query.newColumn(tr.expOf(VAR_DATE));
		query.newColumn(tr.expOf(VAR_OPERATION));
		query.newColumn(tr.expOf(VAR_VERSION));
		for (int i = 0; i < this.monitor.watches.size(); i++) {
			final VariationMonitorFieldImpl mf = this.monitor.watches.get(i);
			query.newColumn(tr.expOf(mf.oldValueFN));
			query.newColumn(tr.expOf(mf.newValueFN));
		}
		query.newOrderBy(tr.expOf(VAR_VERSION));
		return query;
	}

	/**
	 * 查询全部
	 */
	private final QueryStatementImpl entire;

	private final QueryStatementImpl entire(TableDefineImpl variation) {
		return this.select(variation);
	}

	/**
	 * 从指定版本之后开始查询
	 */
	private final QueryStatementImpl next;

	private final QueryStatementImpl next(TableDefineImpl variation) {
		QueryStatementImpl query = this.select(variation);
		ValueExpr version = query.rootRelationRef().expOf(VAR_VERSION);
		ArgumentDefine arg = query.newArgument(VAR_VERSION, VAR_VERSION_TYPE);
		query.setCondition(version.xGreater(arg));
		return query;
	}

	private final QueryStatementImpl between;

	private final QueryStatementImpl between(TableDefineImpl variation) {
		QueryStatementImpl query = this.select(variation);
		ValueExpr version = query.rootRelationRef().expOf(VAR_VERSION);
		ArgumentDefine lower = query.newArgument("lower", VAR_VERSION_TYPE);
		ArgumentDefine upper = query.newArgument("upper", VAR_VERSION_TYPE);
		query.setCondition(version.xBtwn(lower, upper));
		return query;
	}

	/**
	 * 查询最大的版本
	 */
	private final QueryStatementImpl max;

	private final QueryStatementImpl max(TableDefineImpl variation) {
		QueryStatementImpl query = new QueryStatementImpl("z0532");
		TableReferenceDeclare tr = query.newReference(variation, "t");
		query.newColumn(SQLFunc.xMax(tr.expOf(VAR_VERSION)));
		return query;
	}

	private final HashMap<String, Integer> mapFor(VariationMonitorImpl monitor) {
		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < monitor.watches.size(); i++) {
			final VariationMonitorFieldImpl mf = monitor.watches.get(i);
			map.put(mf.name, i);
		}
		return map;
	}

	private final DeleteStatementImpl deleteOutdated;

	private final DeleteStatementImpl deleteOutdated(TableDefineImpl variation) {
		DeleteStatementImpl delete = new DeleteStatementImpl("ftf", "t", variation, null);
		delete.setCondition(delete.expOf(VAR_VERSION).xLE(delete.newArgument(VAR_VERSION, VAR_VERSION_TYPE)));
		return delete;
	}

	private final DeleteStatementImpl deleteSpecified;

	private final DeleteStatementImpl deleteSpecified(TableDefineImpl variation) {
		DeleteStatementImpl delete = new DeleteStatementImpl("c", "t", variation, null);
		delete.setCondition(delete.expOf(variation.f_recid).xEq(delete.newArgument(variation.f_recid)));
		return delete;
	}

	final int indexFor(Namable namable) {
		Integer o = this.map.get(namable.getName());
		if (o == null) {
			throw new IllegalArgumentException();
		}
		return o.intValue();
	}

	static final TypeDetectorBase<ValueReader, VariationMonitorFieldImpl> READER = new TypeDetectorBase<ValueReader, VariationMonitorFieldImpl>() {

		@Override
		public ValueReader inBoolean(VariationMonitorFieldImpl userData)
				throws Throwable {
			return ValueReader.BOOLEAN;
		}

		@Override
		public ValueReader inInt(VariationMonitorFieldImpl userData)
				throws Throwable {
			return ValueReader.INT;
		}

		@Override
		public ValueReader inLong(VariationMonitorFieldImpl userData)
				throws Throwable {
			return ValueReader.LONG;
		}

		@Override
		public ValueReader inDouble(VariationMonitorFieldImpl userData)
				throws Throwable {
			return ValueReader.DOUBLE;
		}

		@Override
		public ValueReader inString(VariationMonitorFieldImpl userData,
				SequenceDataType type) throws Throwable {
			return ValueReader.STRING;
		}

		@Override
		public ValueReader inBytes(VariationMonitorFieldImpl userData,
				SequenceDataType type) throws Throwable {
			return ValueReader.BYTES;
		}

		@Override
		public ValueReader inGUID(VariationMonitorFieldImpl userData)
				throws Throwable {
			return ValueReader.GUID_;
		}

		@Override
		public ValueReader inDate(VariationMonitorFieldImpl userData)
				throws Throwable {
			return ValueReader.DATE;
		}
	};

	static final class Bind {

		final ValueReader reader;
		final int jdbcIndex;

		Bind(ValueReader reader, int jdbcIndex) {
			this.reader = reader;
			this.jdbcIndex = jdbcIndex;
		}
	}

	private final VariationSetImpl read(final ResultSet resultSet)
			throws SQLException {
		final VariationSetImpl set = new VariationSetImpl(this);
		while (resultSet.next()) {
			final GUID id = GUID.valueOf(resultSet.getBytes(this.jdbcIndexForId));
			final Timestamp instant = resultSet.getTimestamp(this.jdbcIndexForDate);
			final DataManipulation operation = typeOf(resultSet.getString(this.jdbcIndexForOperation));
			final long version = resultSet.getLong(this.jdbcIndexForVersion);
			final VariationImpl variation = new VariationImpl(set, id, instant, operation, new Long(version));
			for (int i = 0; i < this.jdbcIndexForOldValues.length; i++) {
				final Bind bind = this.jdbcIndexForOldValues[i];
				variation.oldValues[i] = bind.reader.read(resultSet, bind.jdbcIndex);
			}
			for (int i = 0; i < this.jdbcIndexForNewValues.length; i++) {
				final Bind bind = this.jdbcIndexForNewValues[i];
				variation.newValues[i] = bind.reader.read(resultSet, bind.jdbcIndex);
			}
			set.add(variation);
		}
		return set;
	}

	/**
	 * 从指定事务版本开始，装载指定个数的事务的变化量。
	 * 
	 * @param context
	 * @param from
	 * @return
	 */
	final VariationSetImpl get(DBAdapter context, Long from) {
		final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		final PreparedStatementWrap pstmt;
		if (from == null) {
			pstmt = adapter.prepareStatement(this.entire.getSql(adapter).text());
		} else {
			pstmt = adapter.prepareStatement(this.next.getSql(adapter).text());
		}
		try {
			if (from != null) {
				pstmt.setLong(1, from.longValue());
			}
			final ResultSet resultSet = pstmt.executeQuery();
			try {
				return this.read(resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		} finally {
			adapter.freeStatement(pstmt);
		}
	}

	final VariationSetImpl get(DBAdapter context, Boundary<Long> lower,
			Boundary<Long> upper) {
		final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		final PreparedStatementWrap pstmt = adapter.prepareStatement(this.between.getSql(adapter).text());
		try {
			pstmt.setString(1, lower.value.toString());
			pstmt.setString(2, upper.value.toString());
			final ResultSet resultSet = pstmt.executeQuery();
			try {
				return this.read(resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		} finally {
			adapter.freeStatement(pstmt);
		}
	}

	static final DataManipulation typeOf(String t) {
		if (t.equals("I")) {
			return DataManipulation.INSERT;
		} else if (t.equals("D")) {
			return DataManipulation.DELETE;
		} else if (t.equals("U")) {
			return DataManipulation.UPDATE;
		}
		throw new IllegalStateException();
	}

	final VariationVersion max(DBAdapter context) {
		final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		final PreparedStatementWrap pstmt = adapter.prepareStatement(this.max.getSql(adapter).text());
		try {
			final ResultSet resultSet = pstmt.executeQuery();
			try {
				if (resultSet.next()) {
					final long l = resultSet.getLong(1);
					if (resultSet.wasNull()) {
						return null;
					}
					return new VariationVersion(l);
				}
				return null;
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		} finally {
			adapter.freeStatement(pstmt);
		}
	}

	final int remove(DBAdapter context, long outdated) {
		final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		final ModifySql sql = this.deleteOutdated.getSql(adapter);
		final PreparedStatementWrap pstmt = adapter.prepareStatement(((PlainSql) sql).text());
		try {
			pstmt.setLong(1, outdated);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		} finally {
			adapter.freeStatement(pstmt);
		}
	}

	final int removeSpecified(DBAdapter context, Iterable<Variation> it) {
		final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		final ModifySql sql = this.deleteSpecified.getSql(adapter);
		final PreparedStatementWrap pstmt = adapter.prepareStatement(((PlainSql) sql).text());
		try {
			int c = 0;
			for (Variation var : it) {
				pstmt.setBytes(1, var.id().toBytes());
				c += pstmt.executeUpdate();
			}
			return c;
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		} finally {
			adapter.freeStatement(pstmt);
		}
	}
}