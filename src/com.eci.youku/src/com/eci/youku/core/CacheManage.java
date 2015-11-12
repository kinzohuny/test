package com.eci.youku.core;

import java.sql.SQLException;

import org.apache.log4j.Logger;

public class CacheManage {
	
	private static final Logger logger = Logger.getLogger(CacheManage.class);
	
	public static void initCache() throws SQLException{

		logger.info("cache init start...");
		logger.info("cache init success!");
	}
	
	public static void refreshCache() throws SQLException{
		CachePool.getInstance().ReLoadCache();
		initCache();
	}
	

	
}
