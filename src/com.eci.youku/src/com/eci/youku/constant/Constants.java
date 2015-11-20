package com.eci.youku.constant;

import java.math.BigDecimal;

public class Constants {

	public static final String SESSION_IS_LOGIN = "SESSION_IS_LOGIN";
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
	
	public static final String PROPERTIES_TASK_INIT_THREADS="TASK_INIT_THREADS";
	public static final String PROPERTIES_TASK_MAX_THREADS="TASK_MAX_THREADS";
	public static final String PROPERTIES_TASK_MAX_QUEUED="TASK_MAX_QUEUED";
	
	public static final BigDecimal smsFee = new BigDecimal("0.05");
}
