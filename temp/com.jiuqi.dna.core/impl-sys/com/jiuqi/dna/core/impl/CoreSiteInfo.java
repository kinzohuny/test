/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.AsTable;
import com.jiuqi.dna.core.def.table.AsTableField;
import com.jiuqi.dna.core.def.table.AsTableField.DBType;
import com.jiuqi.dna.core.type.GUID;

/**
 * 站点信息表
 * 
 * @author gaojingxin
 * 
 */
@AsTable
final class CoreSiteInfo {

	@AsTableField(isRecid = true)
	public GUID RECID;

	@AsTableField(isRecver = true)
	public long RECVER;

	@AsTableField(dbType = DBType.Date)
	public long createTime;

	@AsTableField(dbType = DBType.Text)
	public String xml;
}
