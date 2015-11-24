package com.btw.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesManage {

	private final static Logger logger = Logger.getLogger(PropertiesManage.class);
	
	public static final String MYSQL_IP = "MYSQL_IP";
	public static final String MYSQL_PORT = "MYSQL_PORT";
	public static final String MYSQL_DATABASE = "MYSQL_DATABASE";
	public static final String MYSQL_USERNAME = "MYSQL_USERNAME";
	public static final String MYSQL_PASSWORD = "MYSQL_PASSWORD";
	public static final String MYSQL_INIT_CONNECTIONS = "MYSQL_INIT_CONNECTIONS";
	public static final String MYSQL_INC_CONNECTIONS = "MYSQL_INC_CONNECTIONS";
	public static final String MYSQL_MAX_CONNECTIONS = "MYSQL_MAX_CONNECTIONS";
	

	public static final String SERVER_PORT = "SERVER_PORT";
	public static final String SERVER_TIME_OUT_S = "SERVER_TIME_OUT_S";
	public static final String SERVER_INIT_THREADS = "SERVER_INIT_THREADS";
	public static final String SERVER_MAX_THREADS = "SERVER_MAX_THREADS";
	public static final String SERVER_MAX_QUEUED = "SERVER_MAX_QUEUED";
	
	private static Properties properties;

	private PropertiesManage(){
		
	}
	
	static {
		logger.info("load system properties start...");
		
		properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = ClassLoader.getSystemResourceAsStream("config/properties/server.properties");
			properties.load(inputStream);
		} catch (IOException e) {
			logger.fatal("load system properties error! system exit!", e);
			System.exit(1);
		} finally {
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		
		logger.info("load system properties success!");
	}
	
	public static String getProperties(String key){
		return properties.getProperty(key);
	}
}
