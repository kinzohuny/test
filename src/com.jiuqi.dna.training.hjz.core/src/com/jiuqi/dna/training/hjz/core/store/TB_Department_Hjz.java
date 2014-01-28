package com.jiuqi.dna.training.hjz.core.store;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDeclare;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.TypeFactory;

public class TB_Department_Hjz extends TableDeclarator {

	public static final String TABLE_NAME ="Department_Hjz";

	public final TableFieldDefine f_DEPTNAME;
	public final TableFieldDefine f_DEPTMASTER;
	public final TableFieldDefine f_DEPTNUM;
	public final TableFieldDefine f_DEPTDATE;
	public final TableFieldDefine f_PARENT;
	public final TableFieldDefine f_REMARK;
	public final TableFieldDefine f_DEPTORDER;

	public static final String FN_DEPTNAME ="DEPTNAME";
	public static final String FN_DEPTMASTER ="DEPTMASTER";
	public static final String FN_DEPTNUM ="DEPTNUM";
	public static final String FN_DEPTDATE ="DEPTDATE";
	public static final String FN_PARENT ="PARENT";
	public static final String FN_REMARK ="REMARK";
	public static final String FN_DEPTORDER ="DEPTORDER";

	//���ɵ��øù��췽��.��ǰ��ֻ���ɿ��ʵ����.
	private TB_Department_Hjz() {
		super(TABLE_NAME);
		this.table.setTitle("����1");
		TableFieldDeclare field;
		this.f_DEPTNAME = field = this.table.newField(FN_DEPTNAME, TypeFactory.NVARCHAR(30));
		field.setTitle("��������");
		this.f_DEPTMASTER = field = this.table.newField(FN_DEPTMASTER, TypeFactory.NVARCHAR(30));
		field.setTitle("��������");
		this.f_DEPTNUM = field = this.table.newField(FN_DEPTNUM, TypeFactory.INT);
		field.setTitle("��������");
		this.f_DEPTDATE = field = this.table.newField(FN_DEPTDATE, TypeFactory.DATE);
		field.setTitle("����ʱ��");
		this.f_PARENT = field = this.table.newField(FN_PARENT, TypeFactory.GUID);
		field.setTitle("�ϼ�����");
		this.f_REMARK = field = this.table.newField(FN_REMARK, TypeFactory.NTEXT);
		field.setTitle("��ע");
		this.f_DEPTORDER = field = this.table.newField(FN_DEPTORDER, TypeFactory.LONG);
		field.setTitle("�����ֶ�");
	}

}
