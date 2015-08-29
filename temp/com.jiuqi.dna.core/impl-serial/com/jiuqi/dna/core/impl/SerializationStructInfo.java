package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.type.GUID;

/**
 * 序列化结构信息，用于支持两节点间存在版本不同的结构类型间兼容性问题
 * 
 * @author gaojingxin
 * 
 */
@StructClass
public class SerializationStructInfo {
	String name;
	String[] fieldNames;
	GUID[] fieldTypeIDs;
}
