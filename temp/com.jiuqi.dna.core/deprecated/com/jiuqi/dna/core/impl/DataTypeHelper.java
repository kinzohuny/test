/**
 * Copyright (C) 2007-2008 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File DataTypeHelper.java
 * Date 2008-12-4
 */
package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Type;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
// TODO ע��
final class DataTypeHelper {
	static DataType readDataType(Undigester undigester) throws IOException,
			StructDefineNotFoundException {
		return DataTypeUndigester.undigestType(undigester);
	}

	static void skipData(InternalDeserializer sod, DataType type) {
		// TODO ��˫���ṹ����Ե�ʱ���÷������ᱻ���á�
		// �����Ժ�֧��˫���ṹ������ڲ��죬Ӧ��ʵ����һ������
		throw new UnsupportedOperationException();
	}

	static Type undigestType(Undigester undigester) throws IOException,
			StructDefineNotFoundException {
		// XXX Ŀǰ���������֧��DataType�Ķ�ȡ����Type֧���в�������
		return DataTypeUndigester.undigestType(undigester);
	}
}
