package com.jiuqi.dna.training.hjz.core.intf;

import com.jiuqi.dna.core.type.GUID;

public interface DepartmentResource {

	public GUID getId();
	public String getName();
	public String getMaster();
	public int getNum();
	public long getDate();
	public GUID getParent();
	public String getRemark();
	public long getOrder();
}
