package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * mysql��ͬʱupdate�������
 * 
 * @author houchunlei
 * 
 */
public interface ISqlUpdateMultiCommandFactory {

	ISqlUpdateMultiBuffer updateMultiple(String table, String alias);
}
