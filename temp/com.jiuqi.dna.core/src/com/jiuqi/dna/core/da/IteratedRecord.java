package com.jiuqi.dna.core.da;

/**
 * 被迭代的记录对象
 * 
 * @author houchunlei
 * 
 */
public interface IteratedRecord {

	RecordSetFieldContainer<? extends ReadOnlyRecordSetField> getFields();
}
