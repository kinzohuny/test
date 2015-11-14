package com.eci.youku.data.push.main;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.eci.youku.data.push.task.DataFetchFromTB;
import com.eci.youku.data.push.task.DataPushToQYGJ;
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
	
	public static void main(String[] args) throws IOException {
		System.out.println(WebUtils.doPost("http://server.sfinx.cn/ip", null, 3000, 3000));;
		new DataHandleStart().startFetchFromTB();
	}
}
