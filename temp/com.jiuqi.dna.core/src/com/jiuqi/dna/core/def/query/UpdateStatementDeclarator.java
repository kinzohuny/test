package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;

/**
 * 更新语句声明器
 * 
 * @author houchunlei
 * 
 */
public abstract class UpdateStatementDeclarator extends
		ModifyStatementDeclarator<UpdateStatementDefine> {

	/**
	 * 使用dna-sql脚本构造语句定义
	 * 
	 * <p>
	 * 脚本文件名称为<strong>[类名.update]</strong>,且必须在相同的包下.
	 */
	public UpdateStatementDeclarator() {
		super(false);
		this.statement = (UpdateStatementImpl) DNASql.parseForDeclarator(this);
	}

	public UpdateStatementDeclarator(String name, TableDeclarator target) {
		this(name, target.getDefine().getName(), target.getDefine());
	}

	public UpdateStatementDeclarator(String name, TableDefine target) {
		this(name, target.getName(), target);
	}

	public UpdateStatementDeclarator(String name, String alias,
			TableDeclarator target) {
		this(name, alias, target.getDefine());
	}

	public UpdateStatementDeclarator(String name, String alias,
			TableDefine target) {
		super(true);
		this.statement = new UpdateStatementImpl(name, alias,
				(TableDefineImpl) target);
	}

	/**
	 * 请调用不带参数的构造函数
	 */
	@Deprecated
	public UpdateStatementDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	@Override
	public final UpdateStatementDefine getDefine() {
		return this.statement;
	}

	protected final UpdateStatementDeclare statement;

	// --------------------------------------------

	private final static Class<?>[] intf_classes = { UpdateStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return UpdateStatementDeclarator.intf_classes;
	}
}
