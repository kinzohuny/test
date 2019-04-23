package com.eci.roy.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesManage {

	private final static Logger logger = Logger.getLogger(PropertiesManage.class);
	private final static String propertiesPath = "server.properties";
	private static Properties props;

	public static String getProperties(String key) throws IOException {
		if (props == null) {
			logger.info("properties init start...");
			InputStreamReader reader = new InputStreamReader(PropertiesManage.class.getResourceAsStream("/" + propertiesPath), "utf-8");
			props = new Properties();
			props.load(reader);
			logger.fatal("properties init success!");
		}
		return props.getProperty(key);
	}

}
