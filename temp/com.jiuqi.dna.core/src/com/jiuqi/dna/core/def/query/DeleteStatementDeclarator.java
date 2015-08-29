package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;

/**
 * 删除语句定义的声明器
 * 
 * @author houchunlei
 * 
 */
public abstract class DeleteStatementDeclarator extends
		ModifyStatementDeclarator<DeleteStatementDefine> {

	public DeleteStatementDeclarator(String name, TableDefine target) {
		this(name, target.getName(), target);
	}

	public DeleteStatementDeclarator(String name, TableDeclarator target) {
		this(name, target.getDefine().getName(), target.getDefine());
	}

	public DeleteStatementDeclarator(String name, String alias,
			TableDefine target) {
		super(true);
		this.statement = new DeleteStatementImpl(name, alias,
				(TableDefineImpl) target);
	}

	public DeleteStatementDeclarator(String name, String alias,
			TableDeclarator target) {
		this(name, target.getDefine().getName(), target.getDefine());
	}

	/**
	 * 使用dna-sql脚本构造语句定义
	 * 
	 * <p>
	 * 脚本文件名称为<strong>[类名.delete]</strong>,且必须在相同的包下.
	 */
	public DeleteStatementDeclarator() {
		super(false);
		this.statement = (DeleteStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * 请调用不带参数的构造函数
	 */
	@Deprecated
	public DeleteStatementDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	@Override
	public final DeleteStatementDefine getDefine() {
		return this.statement;
	}

	/**
	 * 删除命令定义
	 */
	/**
	 * 
	 */
	protected final DeleteStatementDeclare statement;

	// ------------------------------------------------------------------

	private final static Class<?>[] intf_classes = { DeleteStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return DeleteStatementDeclarator.intf_classes;
	}
}
