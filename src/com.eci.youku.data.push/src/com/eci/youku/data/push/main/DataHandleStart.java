package com.eci.youku.data.push.main;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.eci.youku.data.push.task.DataFetchFromTB;
import com.eci.youku.data.push.task.DataPushToQYGJ;
import com.eci.youku.data.push.task.FullDataPushToQYGJ;
import com.taobao.api.internal.util.WebUtils;

public class DataHandleStart {

	public void startFetchFromTB(){
		
		Executors.newScheduledThreadPool(1)
			.scheduleAtFixedRate(new DataFetchFromTB(), 0, 5, TimeUnit.MINUTES);
	}
	
	public void startPushToQYGJ(){

		Executors.newScheduledThreadPool(1)
			.scheduleAtFixedRate(new DataPushToQYGJ(), 3, 5, TimeUnit.MINUTES);
	}
	
	/**
	 * 订单数据全量推送，如果不考虑先下单后注册的手机的情况，可以不运行此任务 。
	 */
	
	public void startFullPushToQYGJ(){

		//每天凌晨1点运行
		Executors.newScheduledThreadPool(1)
			.scheduleAtFixedRate(new FullDataPushToQYGJ(), getMinuteToHour(1), 1440, TimeUnit.MINUTES);
	}
	
	/**
	 * 订单数据全量推送，如果不考虑先下单后注册的手机的情况，可以不运行此任务 。
	 */
	private long getMinuteToHour(int hour){
		Calendar now = Calendar.getInstance();
		Calendar next = Calendar.getInstance();
		next.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),
				hour, 0, 0);
		if(now.get(Calendar.HOUR_OF_DAY)>=hour){
			next.add(Calendar.DATE, 1);
		}
		return (next.getTimeInMillis()-now.getTimeInMillis())/1000/60;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(WebUtils.doPost("http://server.sfinx.cn/ip", null, 3000, 3000));
		DataHandleStart handle = new DataHandleStart();
		handle.startFetchFromTB();
		handle.startPushToQYGJ();
		handle.startFullPushToQYGJ();
	}
}
