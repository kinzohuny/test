/**
 * 
 */
package com.jiuqi.dna.core.auth;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.resource.ResourceToken;

/**
 * 权限规则定义
 * 
 * 注册权限规则的方式为：继承此类，并注册到dna.xml中
 * 
 * @author yangduanxue
 * @param <TFacade>
 *
 */
public abstract class AuthorityRule<TFacade> {

	private final String name;
	private final String group;
	private final String description;
	private final boolean operationAuth;
	//private List<Parameter> params = new ArrayList<AuthorityRule.Parameter>();
	/**
	 * @param name 规则权限的唯一标识
	 * @param group 规则权限类别，即该规则是针对那种权限资源的
	 * @param description 规则授权的详细描述
	 */
	public AuthorityRule(String name, String group, String description) {
		this(name, group, description, true);
	}
	
	public AuthorityRule(String name, String group, String description, boolean operationAuth) {
		this.name = name;
		this.group = group;
		this.description = description;
		this.operationAuth = operationAuth;
	}
	
	/**
	 * 用户对资源的操作是否有权限
	 * 
	 * @param context
	 * @param user
	 * @param token
	 * @param ope
	 * @return
	 */
	public abstract boolean hasAuthority(Context context, User user, ResourceToken<TFacade> token, Operation<TFacade> ope);

	
	/**
	 * 是否操作权限
	 * 
	 * @return
	 */
	public final boolean isOperationAuth() {
		return this.operationAuth;
	}
	
	/**
	 * 是否启用（规则初始状态）
	 * 
	 * @return
	 */
	public boolean isUsing() {
		return true;
	}
	
	/**
	 * 返回规则启用导航处理界面
	 * 
	 * @return
	 */
	public String getStartingPage() {
		return null;
	}
	
	/**
	 * 返回规则中权限资源的外观class
	 * 
	 * @return
	 */
	public abstract Class<TFacade> getFacadeClass();
	
	/**
	 * 返回规则针对的资源类别，null表示对所有类别生效
	 * 
	 * @return
	 */
	public List<Object> getResourceCategories() {
		return null;
	}
	
	/**
	 * 返回规则针对的操作，null表示对权限资源的所有操作生效
	 * 
	 * @return
	 */
	public List<Operation<?>> getOpertions() {
		return null;
	} 
	
	/**
	 * @return the title
	 */
	public final String getGroup() {
		return group;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}


	/**
	 * 增加参数
	 * 
	 * @param name
	 * @param title
	 * @param dataType
	 * @throws IllegalArgumentException
	 *//*
	private final void appendParameter(String name, String title, DataType dataType) throws IllegalArgumentException {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("参数标识不能为空");
		}
		if (title == null || title.trim().length() == 0) {
			throw new IllegalArgumentException("参数标题不能为空");
		}
		if (dataType == null) {
			throw new IllegalArgumentException("参数类型不能为空");
		}
		for (Parameter p : params) {
			if (p.getName().equals(name)) {
				throw new IllegalArgumentException("存在同名的参数");
			}
		}
		this.params.add(new Parameter(name, title, dataType));
	}
	
	public final List<Parameter> getParameters() {
		return this.params;
	}*/

	
	/*private static final class Parameter {
		private String name;
		private String title;
		private DataType dataType;
		*//**
		 * @param name
		 * @param title
		 * @param dataType
		 *//*
		public Parameter(String name, String title, DataType dataType) {
			super();
			this.name = name;
			this.title = title;
			this.dataType = dataType;
		}
		*//**
		 * @return the name
		 *//*
		public final String getName() {
			return name;
		}
		*//**
		 * @param name the name to set
		 *//*
		public final void setName(String name) {
			this.name = name;
		}
		*//**
		 * @return the title
		 *//*
		public final String getTitle() {
			return title;
		}
		*//**
		 * @param title the title to set
		 *//*
		public final void setTitle(String title) {
			this.title = title;
		}
		*//**
		 * @return the dataType
		 *//*
		public final DataType getDataType() {
			return dataType;
		}
		*//**
		 * @param dataType the dataType to set
		 *//*
		public final void setDataType(DataType dataType) {
			this.dataType = dataType;
		}
		
	} */
}

