package com.jiuqi.dna.core.spi.dist;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * �ֲ�ʽDNA�����µĻ��������
 * 
 * <p>
 * �����ڶ��壺�ڵ���صĻ��淶Χ���ڵ��ֹ�޸ĵĻ��淶Χ��
 * 
 * @author houchunlei
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DistCacheFilter {

	/**
	 * �������Ĺ���ģ��
	 * 
	 * @return
	 */
	public String template();
}