/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RITestHelper.java
 * Date 2009-4-24
 */
package com.jiuqi.dna.core.impl;

/**
 * TODO delete
 * 
 * @author LRJ
 * @version 1.0
 */
public final class RITestHelper {
	static long alldatasent = 0;
	static long alldataread = 0;

	static int loadclasscount = 0;
	static long loadclasscost = 0;

	static int sdahitcount = 0;
	static int sdaunhitcount = 0;

	public static void clear() {
		alldatasent = 0;
		alldataread = 0;
		loadclasscount = 0;
		loadclasscost = 0;
	}

	public static void printDataQuantity() {
		System.out.println("--------------- Data Quantity -------------------");
		System.out.format("data sent: %s%ndata read: %s%n all data: %s%n", RITestHelper.alldatasent, RITestHelper.alldataread, RITestHelper.alldataread + RITestHelper.alldatasent);
		System.out.println("-------------------------------------------------");
	}

	public static void printLoadClassTimeCost() {
		System.out.println("------------ Load Class Time Cost ---------------");
		System.out.format(" time: %s%ncount: %s%n  avg: %s%n", RITestHelper.loadclasscost, RITestHelper.loadclasscount, RITestHelper.loadclasscount == 0 ? -1 : RITestHelper.loadclasscost / RITestHelper.loadclasscount);
		System.out.println("-------------------------------------------------");
	}

	public static void printSDACount() {
		System.out.println("--------- Struct Define Adapter Count -----------");
		System.out.format("  hit: %s%nunhit: %s%ntotal: %s%n", sdahitcount, sdaunhitcount, sdahitcount + sdaunhitcount);
		System.out.println("-------------------------------------------------");
	}

	public static void printAll() {
		printDataQuantity();
		printLoadClassTimeCost();
		printSDACount();
	}
}
