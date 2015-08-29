package com.jiuqi.dna.core;

import com.jiuqi.dna.core.auth.Actor;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.impl.BuildInUser;
import com.jiuqi.dna.core.type.GUID;

/**
 * 用户<br>
 * 在参与权限管理时，框架支持一个用户被关联多个组织机构，在不同的组织机构下，用户可以拥有不同的权限。<br>
 * 用户也可以被分配多个角色，用户继承所关联角色的所有权限。
 * 
 * @see com.jiuqi.dna.core.auth.Actor
 * @author gaojingxin
 */
public interface User extends Actor {

	/**
	 * 匿名用户名
	 */
	public final static String USER_NAME_ANONYM = "?";

	/**
	 * 调试用户名
	 */
	public final static String USER_NAME_DEBUGGER = "debugger";

	/**
	 * 系统用户名
	 */
	public static final String USER_NAME_SYSTEM = "system";
	/**
	 * 系统用户
	 */
	public final static User system = BuildInUser.system;

	/**
	 * 匿名用户
	 */
	public final static User anonym = BuildInUser.anonym;

	/**
	 * 调试用户名，系统必须带有-Dcom.jiuqi.dna.debug=true参数启动，该用户才可以使用。<br>
	 * 该用户接收任何密码，权限无穷大。
	 */
	public final static User debugger = BuildInUser.debugger;

	/**
	 * 返回当前用户是否是内健用户，如：anonym、debugger、system
	 * 
	 * @return
	 */
	public boolean isBuildInUser();

	/**
	 * 验证用户密码<br>
	 * 判断给定的密码与用户密码是否匹配。
	 * 
	 * @param password
	 *            明文密码，不能为空对象
	 * @return 匹配返回true，否则返回false
	 */
	public boolean validatePassword(String password);

	/**
	 * 验证用户密码<br>
	 * 判断给定的密码与用户密码是否匹配。
	 * 
	 * @param password
	 *            密文密码，不能为空对象
	 * @return 匹配返回true，否则返回false
	 */
	public boolean validatePassword(GUID password);

	/**
	 * 该方法已不支持，若需实现该方法的功能，建议使用其它替代方法
	 */
	@Deprecated
	public int getAssignedRoleCount();

	/**
	 * 该方法已不支持，若需实现该方法的功能，建议使用其它替代方法
	 */
	@Deprecated
	public Role getAssignedRole(int index);

	/**
	 * 该方法已不支持，若需实现该方法的功能，建议使用其它替代方法
	 */
	@Deprecated
	public int getPriorityIndex();
	
	/**
	 * 获取用户级别
	 * @return
	 */
	public String getLevel();

}
