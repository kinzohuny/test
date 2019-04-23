package com.btw.shiwu;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TranTest {

	private static final ThreadPoolExecutor fixedThreadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(20);
	
	public static void main(String[] args) {
		
		fixedThreadPool.execute(new TranThread());
	}
}
