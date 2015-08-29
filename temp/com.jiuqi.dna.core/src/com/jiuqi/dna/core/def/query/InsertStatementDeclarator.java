package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;

/**
 * 插入语句定义声明器
 * 
 * @author houchunlei
 * 
 */
public abstract class InsertStatementDeclarator extends
		ModifyStatementDeclarator<InsertStatementDefine> {

	/**
	 * 使用dna-sql脚本构造语句定义
	 * 
	 * <p>
	 * 脚本文件名称为<strong>[类名.insert]</strong>,且必须在相同的包下.
	 * 
	 * @param oQuerier
	 */
	public InsertStatementDeclarator() {
		super(false);
		this.statement = (InsertStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * 请调用不带参数的构造函数
	 */
	@Deprecated
	public InsertStatementDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	public InsertStatementDeclarator(String name,
			TableDeclarator tableDeclarator) {
		this(name, tableDeclarator.getDefine());
	}

	public InsertStatementDeclarator(String name, TableDefine table) {
		super(true);
		this.statement = new InsertStatementImpl(name, (TableDefineImpl) table,
				this);
	}

	@Override
	public final InsertStatementDefine getDefine() {
		return this.statement;
	}

	protected final InsertStatementDeclare statement;

	// --------------------------------------------

	private final static Class<?>[] intf_classes = { InsertStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return InsertStatementDeclarator.intf_classes;
	}
}
