package com.jiuqi.dna.core.def.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ���ĳ�Ӷ���Ϊģ�͵�����
 * 
 * @author gaojingxin
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AsModelProperty {
	/**
	 * ������
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
}
