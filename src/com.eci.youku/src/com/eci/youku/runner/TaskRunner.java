package com.eci.youku.runner;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.eci.youku.task.ToVerifyTradeTask;
import com.eci.youku.task.YoukuVipTask;

public class TaskRunner {

	public void start(){
		
		//有效订单抽取任务
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new ToVerifyTradeTask(), 0, 5, TimeUnit.MINUTES);
		
		//会员权益发放及短信通知任务
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new YoukuVipTask(), 1, 5, TimeUnit.MINUTES);
		
		
	}
}
