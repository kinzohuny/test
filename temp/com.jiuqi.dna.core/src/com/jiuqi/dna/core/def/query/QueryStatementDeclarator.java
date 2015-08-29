package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.QueryStatementImpl;

/**
 * ��ѯ��䶨��������
 * 
 * @see com.jiuqi.dna.core.def.query.QueryStatementDeclare
 * 
 * @author houchunlei
 * 
 */
public abstract class QueryStatementDeclarator extends
		StatementDeclarator<QueryStatementDefine> {

	/**
	 * ʹ��dna-sql�ű�������䶨��
	 * 
	 * <p>
	 * �ű��ļ�����Ϊ<strong>[����.query]</strong>,�ұ�������ͬ�İ���.
	 */
	public QueryStatementDeclarator() {
		super(false);
		this.query = (QueryStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * ����ò��������Ĺ��캯��
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
