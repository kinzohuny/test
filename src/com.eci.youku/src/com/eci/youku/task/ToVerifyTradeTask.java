package com.eci.youku.task;

import org.apache.log4j.Logger;

import com.eci.youku.dao.ValidTradeDao;

public class ToVerifyTradeTask implements Runnable{

	private static final Logger logger = Logger.getLogger(ToVerifyTradeTask.class);
	
	private ValidTradeDao validTradeDao = new ValidTradeDao();
	
	@Override
	public void run() {
		logger.info("ToVerifyTradeTask start...");
		try {
			validTradeDao.fetch();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("ToVerifyTradeTask finish...");
	}

}
