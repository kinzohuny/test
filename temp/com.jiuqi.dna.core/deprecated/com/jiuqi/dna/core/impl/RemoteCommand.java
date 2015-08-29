/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteCommand.java
 * Date 2009-3-10
 */
package com.jiuqi.dna.core.impl;

import java.io.IOException;

/**
 * Զ�����
 * 
 * @author LRJ
 * @version 1.0
 */
interface RemoteCommand {
	/**
	 * ��ȡԶ����������ݰ��Ĵ��롣
	 * 
	 * @return Զ����������ݰ��Ĵ��롣
	 */
	PacketCode getPacketCode();

	void writeTo(StructuredObjectSerializer serializer) throws IOException,
			StructDefineNotFoundException;
}
