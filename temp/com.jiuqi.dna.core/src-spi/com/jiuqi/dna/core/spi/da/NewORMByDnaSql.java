package com.jiuqi.dna.core.spi.da;

import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * ��ϵͳ���ã�����DnaSql���潫dnaSql��������MappingQueryDecare;
 * 
 * @author gaojingxin
 * 
 */
public class NewORMByDnaSql extends NewByDnaSql {
	public final StructDefine structDefine;

	private NewORMByDnaSql(String dnaSql, StructDefine structDefine) {
		super(dnaSql);
		if (structDefine == null) {
			throw new NullArgumentException("structDefine");
		}
		this.structDefine = structDefine;
	}
}
