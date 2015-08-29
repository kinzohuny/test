package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.impl.DNASql;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;

/**
 * �������������
 * 
 * @author houchunlei
 * 
 */
public abstract class UpdateStatementDeclarator extends
		ModifyStatementDeclarator<UpdateStatementDefine> {

	/**
	 * ʹ��dna-sql�ű�������䶨��
	 * 
	 * <p>
	 * �ű��ļ�����Ϊ<strong>[����.update]</strong>,�ұ�������ͬ�İ���.
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
	 * ����ò��������Ĺ��캯��
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
