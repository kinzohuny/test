/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDeclare;
import com.jiuqi.dna.core.type.TypeFactory;

/**
 * 集群情况下资源修改日志记录表
 * 
 * @author yangduanxue
 * 
 */
public final class TD_CoreResourceLog extends TableDeclarator {

	public final static String TABLE_NAME = "Core_ResourceLog";

	public final TableFieldDeclare f_facade;
	public final TableFieldDeclare f_modifyTimes;
	public final TableFieldDeclare f_quirk;

	public TD_CoreResourceLog() {
		super(TABLE_NAME);
		this.f_facade = this.table.newField("facade", TypeFactory.VARCHAR(200));
		this.f_facade.setPrimaryKey(true);
		this.f_modifyTimes = this.table.newField("modify_times", TypeFactory.INT);
		this.f_quirk = this.table.newField("quirk", TypeFactory.BOOLEAN);
	}
}