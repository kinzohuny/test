package com.btw.test.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计指定文件夹下各类型文件数量
 * @author Kinzo
 *
 */
public class FileCount {

	public static void main(String[] args) {
		Map<String, Integer> map = groupByType("D:\\workspace\\tomcat_dev\\com.coozoe.crm.sync.ysf");
		for(String type : map.keySet()){
			System.out.println(type+":"+map.get(type));
		}

	}

	private static Map<String, Integer> groupByType(String pathname){
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<String> list = getAllChildren(pathname);
		for(String str : list){
			String type = str.split("\\.")[str.split("\\.").length-1];
			if(map.get(type)==null){
				map.put(type, 1);
			}else{
				map.put(type, map.get(type)+1);
			}
		}
		return map;
	}
	
	public static List<String> getAllChildren(String pathname){
		File file = new File(pathname);
		List<String> list = new ArrayList<String>();
		if(!file.exists()){
			
		}else if(file.isFile()){
			list.add(pathname);
		}else{
			for(String childPath: file.list()){
				list.addAll(getAllChildren(pathname+File.separator+childPath));
			}
		}
		return list;
	}
	
}
