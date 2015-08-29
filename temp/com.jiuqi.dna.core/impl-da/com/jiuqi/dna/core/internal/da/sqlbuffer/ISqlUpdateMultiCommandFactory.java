package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * mysql的同时update多表的语句
 * 
 * @author houchunlei
 * 
 */
public interface ISqlUpdateMultiCommandFactory {

	ISqlUpdateMultiBuffer updateMultiple(String table, String alias);
}
