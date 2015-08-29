package com.jiuqi.dna.core.def.table;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记某类映射到数据表
 * 
 * @author gaojingxin
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AsTable {

	/**
	 * 标题
	 */
	String title() default "";

	/**
	 * 描述
	 */
	String description() default "";

	String dbName() default "";

}
