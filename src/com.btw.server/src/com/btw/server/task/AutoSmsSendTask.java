package com.btw.server.task;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.btw.server.dao.SmsRecDao;
import com.btw.server.handle.SuyuSmsHandle;
import com.btw.server.model.SmsRecModel;
import com.btw.server.util.StringUtils;
import com.taobao.api.domain.BmcResult;


public class AutoSmsSendTask {

	private static final Logger logger = Logger.getLogger(AutoSmsSendTask.class);
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	private SmsRecDao smsRecDao = new SmsRecDao();
	
	public void start(){
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				try {
					logger.debug("AutoSmsSendTask starting...");
					// 加载短信队列 群发
					List<SmsRecModel> smsRecModelList = smsRecDao.queryToSendList();
					if(!smsRecModelList.isEmpty()){
						for(SmsRecModel smsRecModel : smsRecModelList){

							//改为发送中状态
							smsRecModel.setStatus(9);
							smsRecDao.updateStatus(smsRecModel);
							BmcResult result = null;
							try {
								//发送中
								logger.debug("sending:tid="+smsRecModel.getTid()+"&mobile="+smsRecModel.getMobile()+"&args="+smsRecModel.getArgs());
								result = SuyuSmsHandle.send(smsRecModel.getTid(), smsRecModel.getArgs(), smsRecModel.getMobile());
								logger.debug("result:"+StringUtils.toJSON(result));
							} catch (Exception e) {
								logger.error(e);
								smsRecModel.setStatus(3);
								smsRecModel.setResult(e.getMessage());
							}
							//更新发送结果
							if(smsRecModel.getStatus()!=3){
								smsRecModel.setStatus(result.getSuccessful()?1:2);
								smsRecModel.setResult(StringUtils.toJSON(result));
							}
							smsRecDao.updateResult(smsRecModel);
							
						}
					}

					logger.debug("AutoSmsSendTask finished! c="+smsRecModelList.size());
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		};
		scheduledExecutorService.scheduleAtFixedRate(task, 30, 10, TimeUnit.SECONDS);
	}
	
	
}
