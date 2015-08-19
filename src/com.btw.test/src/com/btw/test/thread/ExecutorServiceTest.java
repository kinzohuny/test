package com.btw.test.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {

	public static void main(String[] args) throws InterruptedException {
//		test1();
//		test2();
//		test3();
		test4();
	}
	
	//线程池处理异步任务
	public static void test1() throws InterruptedException {
	    // 创建一个固定大小的线程池
	    ExecutorService service = Executors.newFixedThreadPool(3); 
	    System.out.println("Task ready!Go!");
	    for (int i = 0; i < 10; i++) {
	        final int j = i;
	        Runnable run = new Runnable() {
	            @Override
	            public void run() {
	            	long id = Thread.currentThread().getId();
	        		System.out.println("task"+j + " is start on Thread-"+id+"!");
	        		try {
	        			Thread.sleep(5000);
	        		} catch (InterruptedException e) {
	        			e.printStackTrace();
	        		}
	        		System.out.println("task"+j + " is stopon Thread-"+id+"!");
	            }
	        };
	        // 在未来某个时间执行给定的命令
	        service.execute(run);
	    }
	    System.out.println("Task commit over !");
	    // 关闭启动线程
	    service.shutdown();
	    // 等待子线程结束，再继续执行下面的代码
	    service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
	    System.out.println("all thread complete");
	}
	
	public static int num1 = 0;
	public static int num2 = 0;
	//线程池处理异步任务，并关注线程安全
	public static void test2() throws InterruptedException {
	    // 创建一个固定大小的线程池
	    ExecutorService service = Executors.newFixedThreadPool(3); 
	    System.out.println("Task ready!Go!");
	    for (int i = 0; i < 100000; i++) {
	        Runnable run = new Runnable() {
	            @Override
	            public void run() {
	            	addNum1();
	            	addNum2();
	            }
	        };
	        // 在未来某个时间执行给定的命令
	        service.execute(run);
	    }
	    System.out.println("Task commit over !");
	    // 关闭启动线程
	    service.shutdown();
	    // 等待子线程结束，再继续执行下面的代码
	    service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
	    System.out.println("all thread complete, num1="+num1+", num2="+num2);
	}
	//安全的
	public  synchronized static void addNum1(){
		num1++;
	}
	//不安全的
	public static void addNum2(){
		num2++;
	}
	
	//线程池处理异步任务，并关注线程安全
	public static void test3() throws InterruptedException {
	    // 创建一个固定大小的线程池
	    ExecutorService service = Executors.newFixedThreadPool(3); 
	    System.out.println("Task ready!Go!");
	    for (int i = 0; i < 100000; i++) {
	        Runnable run = new Runnable() {
	            @Override
	            public void run() {
	            	addNum3();
	            }
	        };
	        // 在未来某个时间执行给定的命令
	        service.execute(run);
	    }
	    System.out.println("Task commit over !");
	    // 关闭启动线程
	    service.shutdown();
	    // 等待子线程结束，再继续执行下面的代码
	    service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
	    System.out.println("all thread complete, num1="+num1+", num2="+num2);
	}
	//安全的
	public  synchronized static void addNum3(){
		num1++;
		//调用不安全的
		addNum2();
	}
	
	//线程池处理异步任务，并关注线程安全
	public static void test4() throws InterruptedException {
	    // 创建一个固定大小的线程池
	    ExecutorService service = Executors.newFixedThreadPool(30); 
	    System.out.println("Task ready!Go!");
	    for (int i = 0; i < 100000; i++) {
	        Runnable run = new Runnable() {
	            @Override
	            public void run() {
	            	addNum3();
	            }
	        };
	        // 在未来某个时间执行给定的命令
	        service.execute(run);
	    }
	    System.out.println("Task commit over !");
	    // 不关闭线程池
	    System.out.println("all thread complete, num1="+num1+", num2="+num2);
	}
}