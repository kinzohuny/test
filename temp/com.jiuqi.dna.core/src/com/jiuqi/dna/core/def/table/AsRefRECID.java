package com.jiuqi.dna.core.def.table;

public @interface AsRefRECID {

	String targetAlias() default "";

	String targetTable();
}
