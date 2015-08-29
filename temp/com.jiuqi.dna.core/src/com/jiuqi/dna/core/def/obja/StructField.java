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
	 * 结构字段标识
	 */
	String name() default "";

	/**
	 * 标题
	 */
	String title() default "";

	/**
	 * 描述
	 */
	String description() default "";

	/**
	 * 当Java类型为long时要求StructField作为时间日期类型（毫秒）对待
	 */
	boolean asDate() default false;

	/**
	 * 是否是状态字段(复制和序列化时是否处理该字段)
	 */
	boolean stateField() default true;

	/**
	 * 是否递归复制或序列化字段的属性，默认为true<br/>
	 * 如果为true，在复制时做深拷贝，在序列化时序列化字段的数据<br/>
	 * 如果为false，在复制时只复制引用，在序列化时不处理该字段
	 */
	boolean recursive() default true;
}
