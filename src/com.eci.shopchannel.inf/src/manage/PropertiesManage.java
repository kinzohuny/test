package manage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import utils.StringUtils;

public class PropertiesManage {

	private final static String propertiesPath = "server.properties";
	private static Map<String, String> map = new HashMap<String, String>();

	public static void initProperties() throws IOException {
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

		
	}
	
	public static String getProperties(String key){
		return map.get(key);
	}
	
	private static void setProperties(Properties props, String propertiesName){
		if (StringUtils.isNotEmpty(props.getProperty(propertiesName))) {
			map.put(propertiesName, props.getProperty(propertiesName));
		}else{
			error(propertiesName);
		}
	}

	private static void error(String propertiesName) {
		throw new IllegalArgumentException("Properties with name ["
				+ propertiesName + "] can not found, please check the "
				+ propertiesPath + " file!");
	}

}
