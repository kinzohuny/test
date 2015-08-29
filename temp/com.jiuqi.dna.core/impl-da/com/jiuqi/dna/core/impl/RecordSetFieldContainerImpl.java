package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.da.RecordSetFieldContainer;
import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.query.QueryColumnDefine;
import com.jiuqi.dna.core.misc.SafeItrList;

/**
 * 记录集字段列容器实现类
 * 
 * @author houchunlei
 * 
 */
final class RecordSetFieldContainerImpl extends SafeItrList<RecordSetFieldImpl>
		implements RecordSetFieldContainer<RecordSetFieldImpl> {

	private static final long serialVersionUID = 7848818063137442811L;

	RecordSetFieldContainerImpl(int initialCapacity) {
		super(initialCapacity);
	}

	public RecordSetFieldImpl find(QueryColumnDefine column)
			throws IllegalArgumentException {
		if (column == null) {
			throw new NullPointerException();
		}
		for (int i = 0, c = this.size(); i < c; i++) {
			RecordSetFieldImpl field = this.get(i);
			if (field.column == column) {
				return field;
			}
		}
		return null;
	}

	public RecordSetFieldImpl get(QueryColumnDefine column)
			throws MissingDefineException, IllegalArgumentException {
		RecordSetFieldImpl field = this.find(column);
		if (field == null) {
			throw new MissingDefineException();
		}
		return field;
	}

}
