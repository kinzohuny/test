package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * mysql��ͬʱdelete������
 * 
 * @author houchunlei
 * 
 */
public interface ISqlDeleteMultiCommandFactory {

	ISqlDeleteMultiBuffer deleteMulti(String table, String alias);
}
