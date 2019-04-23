package com.eci.roy.constant;


public class Constants {

	public static final String SESSION_IS_LOGIN = "SESSION_IS_LOGIN";
	public static final String SESSION_USER_ID = "SESSION_USER_ID";
	public static final String SESSION_USER_NAME = "SESSION_USER_NAME";
	public static final String SESSION_RANDOM = "SESSION_RANDOM";
	public static final String SESSION_POST_TOKEN = "SESSION_POST_TOKEN";
	
	public static final String PROPERTIES_MYSQL_IP = "MYSQL_IP";
	public static final String PROPERTIES_MYSQL_PORT = "MYSQL_PORT";
	public static final String PROPERTIES_MYSQL_DATABASE = "MYSQL_DATABASE";
	public static final String PROPERTIES_MYSQL_USERNAME = "MYSQL_USERNAME";
	public static final String PROPERTIES_MYSQL_PASSWORD = "MYSQL_PASSWORD";
	public static final String PROPERTIES_MYSQL_INIT_CONNECTIONS = "MYSQL_INIT_CONNECTIONS";
	public static final String PROPERTIES_MYSQL_INC_CONNECTIONS = "MYSQL_INC_CONNECTIONS";
	public static final String PROPERTIES_MYSQL_MAX_CONNECTIONS = "MYSQL_MAX_CONNECTIONS";
	

	public static final String PROPERTIES_SERVER_PORT = "SERVER_PORT";
	public static final String PROPERTIES_SERVER_TIME_OUT_S = "SERVER_TIME_OUT_S";
	public static final String PROPERTIES_SERVER_INIT_THREADS = "SERVER_INIT_THREADS";
	public static final String PROPERTIES_SERVER_MAX_THREADS = "SERVER_MAX_THREADS";
	public static final String PROPERTIES_SERVER_MAX_QUEUED = "SERVER_MAX_QUEUED";
	
	public static final String WAKEUP_MASK_IP = "10.11.10.255";
	public static final int WAKEUP_PORT = 54321;
	
	public static final String CACHE_PREFIX_IP_FORBIDDEN = "FORBIDDEN_IP:";
	public static final int IP_FORBIDDEN_TIMEOUT_MS = 1000*60*5;//5分钟
	public static final int IP_FORBIDDEN_TIMES = 5;//5次
	
}
