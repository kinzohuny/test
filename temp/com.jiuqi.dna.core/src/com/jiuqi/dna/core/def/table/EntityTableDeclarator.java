package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.query.MappingQueryStatementDeclare;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDefine;
import com.jiuqi.dna.core.impl.EntityTableUtil;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.misc.TypeArgFinder;

/**
 * 实体表定义
 * 
 * <p>
 * 该定义相当于省略了ORM的定义，但是对实体的查询仅限于单个或全体访问。
 * 
 * @author gaojingxin
 * 
 * @param <TEntity>
 *            表对应的实体的类型
 */
public abstract class EntityTableDeclarator<TEntity> extends TableDeclarator {

	protected final MappingQueryStatementDeclare orm;

	public final MappingQueryStatementDefine getMappingQueryDefine() {
		return this.orm;
	}

	public EntityTableDeclarator(String name) {
		super(name);
		Class<?> entityClass = TypeArgFinder.get(this.getClass(),
				EntityTableDeclarator.class, 0);
		MappingQueryStatementImpl ormImpl = null;
		this.orm = ormImpl = new MappingQueryStatementImpl(name, entityClass);
		EntityTableUtil.buildTableAndOrm((TableDefineImpl) this.table, ormImpl);
	}
}