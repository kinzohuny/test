/**
 * Copyright (C) 2007-2008 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File ResourceFacadeKeyFields.java
 * Date 2008-10-20
 */
package com.jiuqi.dna.core.resource;

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
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResourceKeyFields {
	String[] value();
}
