/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ReturnReceivable.java
 * Date 2009-3-17
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
interface ReturnReceivable {
	void setResult(Object result);

	void setRemoteException(ThrowableAdapter exception);
}
