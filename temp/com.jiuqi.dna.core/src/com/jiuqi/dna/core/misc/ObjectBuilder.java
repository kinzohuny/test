package com.jiuqi.dna.core.misc;

/**
 * ��������
 * 
 * @author gaojingxin
 * 
 * @param <TObject>
 */
public interface ObjectBuilder<TObject> {
	public TObject build() throws Throwable;
}
