package com.jiuqi.dna.core.spi.def;

import com.jiuqi.dna.core.def.MetaElement;
import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Return;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 模型定义提交任务<br>
 * <code>context.handle(new DeclareRemoveTask(MetaElementType.TABLE,"YourTableName")) </code>
 * 
 * @author gaojingxin
 * 
 */
public final class DeclareRemoveTask extends SimpleTask {
	/**
	 * 需要移除的定义的类型
	 */
	public final MetaElementType type;
	/**
	 * 定义的名称
	 */
	public final String name;
	/**
	 * 提示系统是否在提交后即影响运行时，<br>
	 * 即使设为true,系统也会根据情况考虑是否影响运行时<br>
	 * 任务结束后该属性会返回，true代表影响了运行时，false代表没有影响运行时
	 */
	@Return
	public boolean applyToRuntime;

	public DeclareRemoveTask(NamedDefine define, boolean applyToRuntime) {
		if (!(define instanceof MetaElement)) {
			throw new IllegalArgumentException("无效的定义");
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
