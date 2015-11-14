package com.eci.youku.data.push.main;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.eci.youku.data.push.task.DataFetchFromTB;
import com.eci.youku.data.push.task.DataPushToQYGJ;
import com.taobao.api.internal.util.WebUtils;

public class DataHandleStart {

	ScheduledExecutorService service1 = Executors.newScheduledThreadPool(1);
	ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);

	public void start(){
		service1.scheduleAtFixedRate(new DataFetchFromTB(), 1, 5, TimeUnit.MINUTES);

		service2.scheduleAtFixedRate(new DataPushToQYGJ(), 5, 5, TimeUnit.MINUTES);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(WebUtils.doPost("http://server.sfinx.cn/ip", null, 3000, 3000));;
	}
}
