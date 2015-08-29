package com.jiuqi.dna.core.spi.dist;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.Filter;

/**
 * �ֲ�ʽDNAϵͳ�Ļ��������
 * 
 * <p>
 * 
 * @author houchunlei
 * 
 * @param <T>
 */
public interface DistCacheFilterFactory<T> {

	/**
	 * �����µĻ��������
	 * 
	 * @param context
	 * @param args
	 * @return
	 */
	public Filter<T> newInstance(Context context, String args);
}