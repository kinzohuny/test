package com.btw.server.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.btw.server.util.ServerUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public final class CacheManage {
	
	private final static Logger logger = Logger.getLogger(CacheManage.class);
	
	static Cache commonCache = null;
	
	private CacheManage(){
		
	}
	
	static {
		logger.info("cache init start...");
		
		InputStream inputStream = null;
		try {
			inputStream = ServerUtils.getFileInputStream("conf"+File.separator+"ehcache.xml");
			//inputStream = ClassLoader.getSystemResourceAsStream("config/properties/ehcache.xml");
			CacheManager cacheManager = CacheManager.create(inputStream);  
		    //根据配置文件获得Cache实例  
			commonCache = cacheManager.getCache("COMMON_CACHE");  
			commonCache.removeAll(); 
		} catch (Exception e) {
			logger.fatal("cache init error! system exit", e);
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

		logger.info("cache init success!");
	}
	
	public static void put(Object key, Object value){
		commonCache.put(new Element(key, value));
	}
	
	public static void put(Object key, Object value, int timeToIdleSeconds, int timeToLiveSeconds){
		commonCache.put(new Element(key, value, timeToIdleSeconds, timeToLiveSeconds));
	}
	
	public static Object get(Object key){
		Element element = commonCache.get(key);
		if(element==null){
			return null;
		}else{
			return element.getObjectValue();
		}
	}
	
	public static void remove(Object key){
		commonCache.remove(key);
	}
}
