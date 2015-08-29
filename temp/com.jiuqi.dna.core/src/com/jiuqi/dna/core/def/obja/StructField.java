/**
 * Copyright (C) 2007-2008 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File StateField.java
 * Date 2008-6-30
 */
package com.jiuqi.dna.core.def.obja;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StructField {
	/**
	 * �ṹ�ֶα�ʶ
	 */
	String name() default "";

	/**
	 * ����
	 */
	String title() default "";

	/**
	 * ����
	 */
	String description() default "";

	/**
	 * ��Java����ΪlongʱҪ��StructField��Ϊʱ���������ͣ����룩�Դ�
	 */
	boolean asDate() default false;

	/**
	 * �Ƿ���״̬�ֶ�(���ƺ����л�ʱ�Ƿ�����ֶ�)
	 */
	boolean stateField() default true;

	/**
	 * �Ƿ�ݹ鸴�ƻ����л��ֶε����ԣ�Ĭ��Ϊtrue<br/>
	 * ���Ϊtrue���ڸ���ʱ������������л�ʱ���л��ֶε�����<br/>
	 * ���Ϊfalse���ڸ���ʱֻ�������ã������л�ʱ��������ֶ�
	 */
	boolean recursive() default true;
}
