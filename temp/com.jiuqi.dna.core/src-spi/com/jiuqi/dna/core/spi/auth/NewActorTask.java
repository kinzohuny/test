package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * 新建访问者任务
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-11
 */
public abstract class NewActorTask extends SimpleTask {

	/**
	 * 访问者ID
	 */
	public final GUID id;

	/**
	 * 访问者名称
	 */
	public final String name;

	/**
	 * 访问者标题，为空时默认为访问者名称
	 */
	public String title;

	/**
	 * 访问者状态，为空时默认为可用状态
	 */
	public ActorState state;

	/**
	 * 访问者描述，可为空
	 */
	public String description;

	/**
	 * 新建访问者任务
	 * 
	 * @param id
	 *            访问者ID，不能为空
	 * @param name
	 *            访问者名称，不能为空
	 */
	protected NewActorTask(GUID id, String name) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		this.id = id;
		this.name = name;
	}

}
