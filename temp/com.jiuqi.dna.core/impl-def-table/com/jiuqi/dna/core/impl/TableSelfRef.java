package com.jiuqi.dna.core.impl;

/**
 * �߼������������
 * 
 * @author houchunlei
 * 
 */
final class TableSelfRef extends StandaloneTableRef {

	TableSelfRef(TableDefineImpl target) {
		super(target.name, target);
	}

	@Override
	public final String getXMLTagName() {
		throw new UnsupportedOperationException();
	}

}
