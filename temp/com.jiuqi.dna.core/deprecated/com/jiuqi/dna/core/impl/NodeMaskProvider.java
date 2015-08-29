/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File NodeMaskProvider.java
 * Date 2009-6-25
 */
package com.jiuqi.dna.core.impl;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
interface NodeMaskProvider {

	int getGlobalMask();

	int getLocalMask();
}
