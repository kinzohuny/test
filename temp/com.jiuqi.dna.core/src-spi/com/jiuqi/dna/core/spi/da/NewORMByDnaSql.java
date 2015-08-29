package com.jiuqi.dna.core.spi.da;

import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 由系统调用，请求DnaSql引擎将dnaSql编译生成MappingQueryDecare;
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
