package com.jiuqi.dna.training.hjz.service.intf;

import com.jiuqi.dna.core.type.GUID;

public interface Department {

	public GUID getId();
	public String getName();
	public String getMaster();
	public int getNum();
	public long getDate();
	public GUID getParent();
	public String getRemark();
	public long getOrder();
	public int getHashCode();
}
