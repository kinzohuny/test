package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDeclare;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.TypeFactory;

public final class VariationMonitorTB extends TableDeclarator {

	public static final String NAME = "CORE_MONITOR";

	public final TableFieldDefine name;
	public final TableFieldDefine target;
	public final TableFieldDefine variation;
	public final TableFieldDefine trigger;
	public final TableFieldDefine setting;

	public static final String fn_name = "name";
	public static final String fn_target = "target";
	public static final String fn_variation = "vartiation";
	public static final String fn_trigger = "trg";
	static public final String fn_setting = "setting";

	public VariationMonitorTB() {
		super(NAME);
		TableFieldDeclare f;
		this.name = f = this.table.newPrimaryField(fn_name, TypeFactory.NVARCHAR(20));
		f.setKeepValid(true);
		this.target = f = this.table.newField(fn_target, TypeFactory.NVARCHAR(30));
		f.setKeepValid(true);
		this.variation = f = this.table.newField(fn_variation, TypeFactory.NVARCHAR(30));
		f.setKeepValid(true);
		this.trigger = f = this.table.newField(fn_trigger, TypeFactory.NVARCHAR(30));
		f.setKeepValid(true);
		this.setting = f = this.table.newField(fn_setting, TypeFactory.NTEXT);
	}
}