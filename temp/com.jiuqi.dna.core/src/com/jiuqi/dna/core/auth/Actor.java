package com.jiuqi.dna.core.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 访问者<br>
 * 访问者即为拥有权限的主体，可以被授予权限。当前访问者主要分为用户和角色两种。
 * 
 * @author LiuZhi 2009-11
 */
public interface Actor {

	/**
	 * 默认组织机构ID
	 */
	public static final GUID GLOBAL_ORG_ID = null;

	public static final int MAX_NAME_LENGTH = 50;

	public static final int MAX_TITLE_LENGTH = 50;

	public static final int MAX_DESCRIPTION_LENGTH = 128;

	/**
	 * 获得访问者ID<br>
	 * 访问者ID在同种访问者类别里面唯一。
	 * 
	 * @return 返回访问者ID，返回结果不可能为空
	 */
	public GUID getID();

	/**
	 * 访问者名称<br>
	 * 访问者名称在同种访问者类别里面唯一。
	 * 
	 * @return 返回访问者名称，返回结果不可能为空
	 */
	public String getName();

	/**
	 * 获取访问者标题<br>
	 * 一般情况下，访问者标题只用于显示。
	 * 
	 * @return 返回访问者标题，返回结果不可能为空
	 */
	public String getTitle();

	/**
	 * 获得访问者的状态
	 * 
	 * @see com.jiuqi.dna.core.auth.ActorState
	 * @return 返回访问者当前状态
	 */
	public ActorState getState();

	/**
	 * 获取访问的描述信息
	 * 
	 * @return 返回访问者标题，返回结果可能为空
	 */
	public String getDescription();

	/**
	 * 该方法已不支持，若需实现该方法的功能，建议使用其它替代方法
	 */
	@Deprecated
	public int getMappingOrganizationCount();

	/**
	 * 该方法已不支持，若需实现该方法的功能，建议使用其它替代方法
	 */
	@Deprecated
	public GUID getMappingOrganizationID(int index);

}
