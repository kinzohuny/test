/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File IntIdentifiable.java
 * Date 2009-3-10
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
interface IntKeyHashable<T extends IntKeyHashable<T>> extends IntIdentifiable,
		Linkable<T> {
}
