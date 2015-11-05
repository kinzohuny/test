package com.btw.server.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.btw.server.constant.Constants;

public class PropertiesManage {

	private final static Logger logger = Logger.getLogger(PropertiesManage.class);
	private final static String propertiesPath = "server.properties";
	private static Map<String, String> map = new HashMap<String, String>();
	private static boolean isReady=false;

	public static void initProperties() throws IOException {

		logger.info("properties init start...");
		InputStreamReader reader = new InputStreamReader(
				PropertiesManage.class.getResourceAsStream("/"+propertiesPath),
				"utf-8");
		Properties props = new Properties();
		props.load(reader);

		setProperties(props, Constants.PROPERTIES_SERVER_PORT);
		setProperties(props, Constants.PROPERTIES_SERVER_TIME_OUT_S);
		setProperties(props, Constants.PROPERTIES_SERVER_INIT_THREADS);
		setProperties(props, Constants.PROPERTIES_SERVER_MAX_THREADS);
		setProperties(props, Constants.PROPERTIES_SERVER_MAX_QUEUED);
		
		setProperties(props, Constants.PROPERTIES_MYSQL_IP);
		setProperties(props, Constants.PROPERTIES_MYSQL_PORT);
		setProperties(props, Constants.PROPERTIES_MYSQL_DATABASE);
		setProperties(props, Constants.PROPERTIES_MYSQL_USERNAME);
		setProperties(props, Constants.PROPERTIES_MYSQL_PASSWORD);
		setProperties(props, Constants.PROPERTIES_MYSQL_INIT_CONNECTIONS);
		setProperties(props, Constants.PROPERTIES_MYSQL_INC_CONNECTIONS);
		setProperties(props, Constants.PROPERTIES_MYSQL_MAX_CONNECTIONS);
		
		isReady = true;

		logger.info("properties init success!");
	}
	
	public static String getProperties(String key){
		return map.get(key);
	}
	
	public static Boolean isReady(){
		return isReady;
	}
	
	private static void setProperties(Properties props, String propertiesName){
		String value = props.getProperty(propertiesName);
		map.put(propertiesName, value==null?"":value);
		logger.debug("  "+propertiesName+"="+value);
	}
}
