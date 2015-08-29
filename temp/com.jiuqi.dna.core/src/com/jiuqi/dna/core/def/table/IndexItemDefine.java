package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.DefineBase;

/**
 * Ë÷Òı×Ö¶Î¶¨Òå
 * 
 * @author gaojingxin
 * 
 */
public interface IndexItemDefine extends DefineBase {

	/**
	 * ÅÅĞò×Ö¶Î
	 * 
	 * @return ·µ»ØÅÅĞò×Ö¶Î
	 */
	public TableFieldDefine getField();

	/**
	 * ÊÇ·ñÊÇÉıĞò
	 * 
	 * @return ·µ»Ø
	 */
	public boolean isDesc();
}