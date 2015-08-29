package com.jiuqi.dna.core.spi.def;

import com.jiuqi.dna.core.def.MetaElement;
import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Return;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * ģ�Ͷ����ύ����<br>
 * <code>context.handle(new DeclareRemoveTask(MetaElementType.TABLE,"YourTableName")) </code>
 * 
 * @author gaojingxin
 * 
 */
public final class DeclareRemoveTask extends SimpleTask {
	/**
	 * ��Ҫ�Ƴ��Ķ��������
	 */
	public final MetaElementType type;
	/**
	 * ���������
	 */
	public final String name;
	/**
	 * ��ʾϵͳ�Ƿ����ύ��Ӱ������ʱ��<br>
	 * ��ʹ��Ϊtrue,ϵͳҲ�������������Ƿ�Ӱ������ʱ<br>
	 * �������������Ի᷵�أ�true����Ӱ��������ʱ��false����û��Ӱ������ʱ
	 */
	@Return
	public boolean applyToRuntime;

	public DeclareRemoveTask(NamedDefine define, boolean applyToRuntime) {
		if (!(define instanceof MetaElement)) {
			throw new IllegalArgumentException("��Ч�Ķ���");
		}
		this.type = ((MetaElement) define).getMetaElementType();
		this.name = define.getName();
		this.applyToRuntime = applyToRuntime;
	}

	public DeclareRemoveTask(MetaElementType type, String name,
	        boolean applyToRuntime) {
		if (type == null) {
			throw new NullArgumentException("type");
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		this.type = type;
		this.name = name;
		this.applyToRuntime = applyToRuntime;
	}
}
