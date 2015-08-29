package com.jiuqi.dna.core.internal.db.support.db2.sync;

import com.jiuqi.dna.core.internal.db.sync.DbTable;

final class Db2Table extends
		DbTable<Db2Table, Db2Column, Db2DataType, Db2Index> {

	Db2Table(String name) {
		super(name);
	}

	@Override
	protected final Db2Column newColumnOnly(String name) {
		return new Db2Column(this, name);
	}

	@Override
	protected final Db2Index newIndexOnly(String name, boolean unique) {
		return new Db2Index(this, name, unique);
	}

	// final void reorg(PooledConnection pc) throws SQLException {
	// Statement statement = pc.jdbcCreateStatement();
	// try {
	// StringBuilder reorg = new StringBuilder();
	// reorg.append("call sysproc.admin_cmd('reorg table \"");
	// reorg.append(this.name);
	// reorg.append("\"')");
	// statement.execute(reorg.toString());
	// } finally {
	// statement.close();
	// }
	// }
}