package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.SQLException;

/**
 * ���ִ����
 * 
 * @author houchunlei
 * 
 */
public interface SqlExecutor {

	void use(boolean forUpdate) throws SQLException;

	void unuse();
}