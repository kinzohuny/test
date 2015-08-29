/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File AsyncEvent.java
 * Date 2009-4-16
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.Event;

/**
 * �첽�¼��ľ����
 * 
 * @author LRJ
 * @version 1.0
 */
public interface AsyncEvent extends AsyncHandle {
	/**
	 * ��ȡ�¼�����
	 * 
	 * @return �¼�����
	 */
	Event getEvent();

	/**
	 * �Ƿ���Ҫ�ȴ����еĴ�����̶���ɡ�
	 */
	boolean needWait();
}
