package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.QueryStatementImpl;

/**
 * 查询语句定义声明器
 * 
 * @see com.jiuqi.dna.core.def.query.QueryStatementDeclare
 * 
 * @author houchunlei
 * 
 */
public abstract class QueryStatementDeclarator extends
		StatementDeclarator<QueryStatementDefine> {

	/**
	 * 使用dna-sql脚本构造语句定义
	 * 
	 * <p>
	 * 脚本文件名称为<strong>[类名.query]</strong>,且必须在相同的包下.
	 */
	public QueryStatementDeclarator() {
		super(false);
		this.query = (QueryStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * 请调用不带参数的构造函数
	 */
	@Deprecated
	public QueryStatementDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	public QueryStatementDeclarator(String name) {
		super(true);
		this.query = new QueryStatementImpl(name);
	}

	@Override
	public final QueryStatementDefine getDefine() {
		return this.query;
	}

	protected final QueryStatementDeclare query;

	private final static Class<?>[] intf_classes = { QueryStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return intf_classes;
	}
}
