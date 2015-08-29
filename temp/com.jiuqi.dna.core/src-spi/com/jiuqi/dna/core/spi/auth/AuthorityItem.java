package com.jiuqi.dna.core.spi.auth;

import java.util.HashMap;
import java.util.Map;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.impl.AuthorityEntry;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.type.GUID;

public abstract class AuthorityItem extends AuthorityEntry {
	
	public Map<Integer, Integer> authes = new HashMap<Integer, Integer>();// ��������Ȩ���¼���Ҫ����Ϣ

	protected AuthorityItem(ResourceToken<?> resourceToken) {
		super(resourceToken);
	}

	/**
	 * ��ȡ��Ȩ���Ӧ����Դ����������������Դ�����򷵻ؿ�
	 * 
	 * @return
	 */
	public final ResourceToken<?> getResourceToken() {
		return super.getAccessControlItem();
	}

	/**
	 * ��ȡ��Դ��Ĳ����б�
	 */
	@Override
	public final Operation<?>[] getOperations() {
		return super.getOperations();
	}

	/**
	 * �ж���Ȩ���Ƿ��������Ȩ����Ϣ
	 */
	@Override
	public final boolean filled() {
		return super.filled();
	}

	/**
	 * �жϱ���Ȩ����Ե�ǰ��Դ�Ĳ���Ȩ���Ƿ��Ǽ̳е���
	 */
	@Override
	public final boolean isInherit(Operation<?> operation) {
		return super.isInherit(operation);
	}

	/**
	 * ���ñ���Ȩ����Ե�ǰ��Դ��Ȩ��
	 */
	@Override
	public final void setAuthority(Operation<?> operation, Authority authority) {
		this.authes.put(operation.getMask(), authority.code);
		super.setAuthority(operation, authority);
	}

	@Override
	public final Authority getAuthority(Operation<?> operation) {
		return super.getAuthority(operation);
	}

	/**
	 * �жϱ���Ȩ�����Ƿ�Ե�ǰ��Դ�в���Ȩ��
	 */
	public final boolean hasAuthority(Operation<?> operation) {
		return super.hasOperationAuthority(operation);
	}

	/**
	 * �жϵ�ǰ��¼�û��Ƿ�ӵ�жԵ�ǰ��Դ����ȨȨ��
	 */
	public final boolean hasAuthAuthority(Operation<?> operation) {
		return super.hasAccreditAuthority(operation);
	}

	@Override
	public final GUID getGroupIdentifier() {
		return super.getGroupIdentifier();
	}

	@Override
	public final GUID getItemIdentifier() {
		return super.getItemIdentifier();
	}

	// @Override
	// public final List<OperationAuthorityInformation.Entry>
	// getAuthorityEntryList() {
	// return super.getAuthorityEntryList();
	// }

}
