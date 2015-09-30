package manage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class CachePool {
	
	private static final Logger logger = Logger.getLogger(CachePool.class);

	private volatile boolean updateFlag = true;// 正在更新时的阀门，为false时表示当前没有更新缓存，为true时表示当前正在更新缓存

	private volatile static CachePool mapCacheObject;// 缓存实例对象

	private static Map<String, Object> cacheMap = new ConcurrentHashMap<String, Object>();// 缓存容器

	private CachePool() {
		this.LoadCache();// 加载缓存
	}

	/**
	 * 采用单例模式获取缓存对象实例
	 * 
	 * @return
	 */
	public static CachePool getInstance() {
		if (null == mapCacheObject) {
			synchronized (CachePool.class) {
				if (null == mapCacheObject) {
					mapCacheObject = new CachePool();
				}
			}
		}
		return mapCacheObject;
	}

	/**
	 * 装载缓存
	 */
	private void LoadCache() {

		this.updateFlag = true;// 正在更新

		/********** 数据处理，将数据放入cacheMap缓存中 **begin *****
		***cacheMap.put("key1", "value1");
		***cacheMap.put("key2", "value2");
		********* 数据处理，将数据放入cacheMap缓存中 ***end *******/

		this.updateFlag = false;// 更新已完成

	}

	/**
	 * 添加缓存对象
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	public synchronized void add(String key, Object value) {
		
		while(this.updateFlag){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				logger.error("cache add error!key="+key+"&value="+value, e);
			}
		}
		this.updateFlag = true;
		cacheMap.put(key, value);
		this.updateFlag = false;
	}

	/**
	 * 返回缓存对象
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	public synchronized Object get(String key){

		while(this.updateFlag){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				logger.error("cache get error!key="+key, e);
			}
		}

		return cacheMap.get(key);
	}

	/**
	 * 获取缓存项大小
	 * 
	 * @return
	 */
	public int getCacheSize() {
		return cacheMap.size();
	}

	/**
	 * 重新装载
	 */
	public void ReLoadCache() {
		CachePool.cacheMap.clear();
		this.LoadCache();
	}
}
