package com.btw.test.crontab;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.btw.test.utils.DateUtils;

public class JobTest implements Runnable{

	public static void main(String[] args) {
		new JobTest().start();
	}
	
	private ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
	
	public void start(){
		service.scheduleAtFixedRate(this, 10, 3, TimeUnit.SECONDS);
	}

	private static long num = 0;
	
	@Override
	public void run() {
		System.out.println("I'm running " + num + " times! " + DateUtils.getNowTime());

		if(num<Long.MAX_VALUE){
			num += 1;
		}else{
			num = 0;
		}
	}
	
}
