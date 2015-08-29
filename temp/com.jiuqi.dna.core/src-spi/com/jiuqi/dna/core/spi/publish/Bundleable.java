package com.jiuqi.dna.core.spi.publish;

/**
 * 指名与Bundle有关的接口
 * 
 * @author gaojingxin
 * 
 */
public interface Bundleable {
	/**
	 * 返回所属的Bundle，可能返回null表示不确定
	 */
	public BundleToken getBundle();
}
