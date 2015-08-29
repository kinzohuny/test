package com.jiuqi.dna.core.internal.db.sync;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.StoredProcedureDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.UserFunctionImpl;

public interface DbSync {

	void initDb();

	void sync(TableDefineImpl table) throws SQLException;

	void post(TableDefineImpl post, TableDefineImpl runtime)
			throws SQLException;

	void drop(TableDefineImpl table) throws SQLException;

	void restore(TableDefineImpl table) throws SQLException;

	void sync(StoredProcedureDefineImpl procedure) throws SQLException;

	void check(StoredProcedureDefineImpl procedure) throws SQLException;

	void sync(UserFunctionImpl function) throws SQLException;

	void unuse();
	
	TableDefineImpl synchroTableDefine(String tableName, String title, String category);
}