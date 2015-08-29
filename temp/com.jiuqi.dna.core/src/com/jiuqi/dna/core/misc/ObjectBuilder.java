package com.jiuqi.dna.core.misc;

/**
 * 对象构造借口
 * 
 * @author gaojingxin
 * 
 * @param <TObject>
 */
public interface ObjectBuilder<TObject> {
	public TObject build() throws Throwable;
}
