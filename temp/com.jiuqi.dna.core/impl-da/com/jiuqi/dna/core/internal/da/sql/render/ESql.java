package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlExecutor;

/**
 * ¿ÉÖ´ÐÐµÄSql
 * 
 * @author houchunlei
 * 
 */
public interface ESql {

	SqlExecutor newExecutor(DBAdapterImpl adapter, ActiveChangable notify);
}