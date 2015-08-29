package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * mysql的同时delete多表语句
 * 
 * @author houchunlei
 * 
 */
public interface ISqlDeleteMultiCommandFactory {

	ISqlDeleteMultiBuffer deleteMulti(String table, String alias);
}
