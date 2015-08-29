package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.SQLException;

/**
 * Óï¾äÖ´ÐÐÆ÷
 * 
 * @author houchunlei
 * 
 */
public interface SqlExecutor {

	void use(boolean forUpdate) throws SQLException;

	void unuse();
}