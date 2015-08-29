package com.jiuqi.dna.core.spi.def;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.invoke.Return;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * ģ�Ͷ����ύ����<br>
 * <code>context.handle(new DeclarePostTask(yourDeclare)) </code>
 * 
 * @author gaojingxin
 * 
 */
public final class DeclarePostTask extends SimpleTask {
	/**
	 * ���ύ�Ķ���<br>
	 * ���������᷵�أ�������ص����Է���Ӱ�죬���Լ���ʹ�á�
	 */
	@Return
	public final NamedDeclare designed;
	/**
	 * ��ʾϵͳ�Ƿ����ύ��Ӱ������ʱ��<br>
	 * ��ʹ��Ϊtrue,ϵͳҲ�������������Ƿ�Ӱ������ʱ<br>
	 * �������������Ի᷵�أ�true����Ӱ��������ʱ��false����û��Ӱ������ʱ
	 */
	@Return
	public boolean applyToRuntime;

	public DeclarePostTask(NamedDeclare designed) {
		this.designed = designed;
	}

	public DeclarePostTask(NamedDeclare designed, boolean applyToRuntime) {
		this(designed);
		this.applyToRuntime = applyToRuntime;
	}
}
