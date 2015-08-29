package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * 删除访问者任务
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-11
 */
public abstract class DeleteActorTask extends SimpleTask {

	/**
	 * 访问者ID
	 */
	public final GUID actorID;

	/**
	 * 新建删除访问者任务
	 * 
	 * @param actorID
	 *            访问者ID，不能为空
	 */
	protected DeleteActorTask(GUID actorID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
	}

}
