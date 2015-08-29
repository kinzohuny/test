package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;

/**
 * ɾ����䶨���������
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
	 * ʹ��dna-sql�ű�������䶨��
	 * 
	 * <p>
	 * �ű��ļ�����Ϊ<strong>[����.delete]</strong>,�ұ�������ͬ�İ���.
	 */
	public DeleteStatementDeclarator() {
		super(false);
		this.statement = (DeleteStatementImpl) DNASql.parseForDeclarator(this);
	}

	/**
	 * ����ò��������Ĺ��캯��
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
	 * ɾ�������
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
