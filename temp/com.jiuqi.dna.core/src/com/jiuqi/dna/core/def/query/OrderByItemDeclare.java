package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.DeclareBase;

/**
 * �������
 * 
 * @see com.jiuqi.dna.core.def.query.OrderByItemDefine
 * 
 * @author gaojingxin
 * 
 */
public interface OrderByItemDeclare extends OrderByItemDefine, DeclareBase {

	/**
	 * �����Ƿ��ǵ�������
	 * 
	 * @param value
	 */
	public void setDesc(boolean value);
}
