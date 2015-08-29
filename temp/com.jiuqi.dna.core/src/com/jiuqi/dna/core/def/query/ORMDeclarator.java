package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.misc.TypeArgFinder;

/**
 * ʵ���������<br>
 * ʵ�����������������һ��ʵ��󶨵Ĳ�ѯ����,
 * 
 * @author houchunlei
 * 
 * @param <TEntity>
 */
public abstract class ORMDeclarator<TEntity> extends
		StatementDeclarator<MappingQueryStatementDefine> {

	public ORMDeclarator() {
		super(false);
		this.orm = (MappingQueryStatementImpl) DNASql.parseForDeclarator(this);
	}

	public ORMDeclarator(ObjectQuerier oQuerier) {
		this();
	}

	public ORMDeclarator(String name) {
		super(true);
		this.orm = new MappingQueryStatementImpl(name, TypeArgFinder.get(this
				.getClass(), ORMDeclarator.class, 0), this);
	}

	protected final MappingQueryStatementDeclare newORM() {
		return ((MappingQueryStatementImpl) this.orm).clone();
	}

	@Override
	public final MappingQueryStatementDefine getDefine() {
		return this.orm;
	}

	protected final MappingQueryStatementDeclare orm;

	private final static Class<?>[] intf_classes = { MappingQueryStatementDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return ORMDeclarator.intf_classes;
	}
}
