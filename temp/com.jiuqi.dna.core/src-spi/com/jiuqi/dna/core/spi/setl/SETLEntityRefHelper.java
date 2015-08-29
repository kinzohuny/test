package com.jiuqi.dna.core.spi.setl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.type.GUID;

/**
 * ʵ�����ð����ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface SETLEntityRefHelper {

	public interface EntityRefs {
		public Object findNode(GUID recid);

		public Object addChild(Object parent, GUID recid, String code);
	}

	public void fillEntityRefs(Context context, EntityRefs entityRefs);
}