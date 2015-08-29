package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.query.MappingQueryStatementDeclare;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDefine;
import com.jiuqi.dna.core.impl.EntityTableUtil;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.misc.TypeArgFinder;

/**
 * ʵ�����
 * 
 * <p>
 * �ö����൱��ʡ����ORM�Ķ��壬���Ƕ�ʵ��Ĳ�ѯ�����ڵ�����ȫ����ʡ�
 * 
 * @author gaojingxin
 * 
 * @param <TEntity>
 *            ���Ӧ��ʵ�������
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