package com.jiuqi.dna.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.auth.AuthorizedResourceItem;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

/**
 * 更新访问者授权任务
 * 
 * @see com.jiuqi.dna.core.invoke.SimpleTask
 * @author LiuZhi 2009-11
 */
@Deprecated
public abstract class UpdateActorAuthorityTask extends SimpleTask {

	/**
	 * 访问者ID
	 */
	public final GUID actorID;

	/**
	 * 组织机构ID，为空代表默认关联的组织机构ID
	 */
	public GUID orgID;

	/**
	 * 资源类别ID
	 */
	public final GUID resourceCategoryID;

	/**
	 * 需要提交修改的授权资源项列表
	 */
	public final List<AuthorizedResourceItem> authorityResourceTable = new ArrayList<AuthorizedResourceItem>();

	/**
	 * 新建更新访问者授权任务
	 * 
	 * @param actorID
	 *            访问者ID，不能为空
	 * @param orgID
	 *            组织机构ID，可为空，为空代表默认关联的组织机构ID
	 * @param resourceCategoryID
	 *            资源类别ID，不能为空
	 */
	protected UpdateActorAuthorityTask(GUID actorID, GUID orgID,
			GUID resourceCategoryID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		if (resourceCategoryID == null) {
			throw new NullArgumentException("resourceCategoryID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
		this.resourceCategoryID = resourceCategoryID;
	}

}
