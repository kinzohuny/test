package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.type.GUID;

@SuppressWarnings({ "rawtypes" })
public interface AuthorizableResourceCategoryItem extends ResourceToken {

	public GUID getGUID();

	public String getTitle();

}
