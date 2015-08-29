/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.TypeFactory;

/**
 * 权限规则状态参数存储表
 * 
 * @author yangduanxue
 * 
 */
public final class TD_CoreAuthRule extends TableDeclarator {

	public final TableFieldDefine f_name;
	public final TableFieldDefine f_isusing;
	public final TableFieldDefine f_categories;
	public final TableFieldDefine f_operations;

	public TD_CoreAuthRule() {
		super("core_auth_rule");
		this.f_name = this.table.newField("name", TypeFactory.VARCHAR(50));
		this.f_isusing = this.table.newField("isusing", TypeFactory.BOOLEAN);
		this.f_categories = this.table.newField("categories", TypeFactory.VARCHAR(1000));
		this.f_operations = this.table.newField("operations", TypeFactory.VARCHAR(100));
	}
}
