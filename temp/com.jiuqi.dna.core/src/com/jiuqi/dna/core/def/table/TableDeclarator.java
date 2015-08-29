package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.MetaElementTemplateParams;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.DeclaratorBase;
import com.jiuqi.dna.core.impl.TableDeclareStub;
import com.jiuqi.dna.core.impl.TableDefineImpl;

/**
 * 表定义声明器，重载此类声明原生表
 * 
 * 表声明器即是定义表定义的容器,和对表定义各元素提供强引用的类
 * 
 * @author gaojingxin
 * 
 */
public abstract class TableDeclarator extends DeclaratorBase {

	private TableDeclareStub stub;

	public TableDeclarator() {
		super(false);
		// 加载DNASQL时，不加载表关系
		this.stub = (TableDeclareStub) DNASql.parseForDeclarator(this);
		final TableDefineImpl table = this.stub.getTable();
		this.table = table;
		this.f_RECID = table.f_recid;
		this.f_RECVER = table.f_recver;
	}

	public TableDeclarator(String name) {
		super(false);
		final TableDefineImpl table = new TableDefineImpl(name, this);
		this.table = table;
		this.f_RECID = table.f_recid;
		this.f_RECVER = table.f_recver;
	}

	public TableDeclarator(MetaElementTemplateParams params) {
		super(true);
		final TableDefineImpl table = new TableDefineImpl(params.getName(),
				this);
		this.table = table;
		this.f_RECID = table.f_recid;
		this.f_RECVER = table.f_recver;
	}

	@Override
	public final TableDefine getDefine() {
		return this.table;
	}

	@Override
	protected void declareUseRef(ObjectQuerier querier) {
		super.declareUseRef(querier);
		// 加载表关系，此时所有的表定义都已经实例化，不会发生找不到表的错误
		if (this.stub != null) {
			final TableDeclareStub aStub = this.stub;
			this.stub = null;
			aStub.fillRelations(querier);
		}
	}

	/**
	 * 逻辑表定义
	 */
	protected final TableDeclare table;

	/**
	 * 逻辑表RECID字段
	 */
	public final TableFieldDefine f_RECID;

	/**
	 * 逻辑表RECVER字段
	 */
	public final TableFieldDefine f_RECVER;

	private final static Class<?>[] intf_classes = { TableDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return TableDeclarator.intf_classes;
	}

	public final void setTableType(TableType tableType) {
		this.table.setTableType(tableType);
	}

	public final TableType getTableType() {
		return this.table !=null ? this.table.getTableType() : TableType.NORMAL;
	}
}