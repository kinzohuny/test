/**
 * 
 */
package com.jiuqi.dna.core.auth;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.resource.ResourceToken;

/**
 * Ȩ�޹�����
 * 
 * ע��Ȩ�޹���ķ�ʽΪ���̳д��࣬��ע�ᵽdna.xml��
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
	 * @param name ����Ȩ�޵�Ψһ��ʶ
	 * @param group ����Ȩ����𣬼��ù������������Ȩ����Դ��
	 * @param description ������Ȩ����ϸ����
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
	 * �û�����Դ�Ĳ����Ƿ���Ȩ��
	 * 
	 * @param context
	 * @param user
	 * @param token
	 * @param ope
	 * @return
	 */
	public abstract boolean hasAuthority(Context context, User user, ResourceToken<TFacade> token, Operation<TFacade> ope);

	
	/**
	 * �Ƿ����Ȩ��
	 * 
	 * @return
	 */
	public final boolean isOperationAuth() {
		return this.operationAuth;
	}
	
	/**
	 * �Ƿ����ã������ʼ״̬��
	 * 
	 * @return
	 */
	public boolean isUsing() {
		return true;
	}
	
	/**
	 * ���ع������õ����������
	 * 
	 * @return
	 */
	public String getStartingPage() {
		return null;
	}
	
	/**
	 * ���ع�����Ȩ����Դ�����class
	 * 
	 * @return
	 */
	public abstract Class<TFacade> getFacadeClass();
	
	/**
	 * ���ع�����Ե���Դ���null��ʾ�����������Ч
	 * 
	 * @return
	 */
	public List<Object> getResourceCategories() {
		return null;
	}
	
	/**
	 * ���ع�����ԵĲ�����null��ʾ��Ȩ����Դ�����в�����Ч
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
	 * ���Ӳ���
	 * 
	 * @param name
	 * @param title
	 * @param dataType
	 * @throws IllegalArgumentException
	 *//*
	private final void appendParameter(String name, String title, DataType dataType) throws IllegalArgumentException {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("������ʶ����Ϊ��");
		}
		if (title == null || title.trim().length() == 0) {
			throw new IllegalArgumentException("�������ⲻ��Ϊ��");
		}
		if (dataType == null) {
			throw new IllegalArgumentException("�������Ͳ���Ϊ��");
		}
		for (Parameter p : params) {
			if (p.getName().equals(name)) {
				throw new IllegalArgumentException("����ͬ���Ĳ���");
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

