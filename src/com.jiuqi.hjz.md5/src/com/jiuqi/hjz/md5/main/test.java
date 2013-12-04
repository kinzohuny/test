package com.jiuqi.hjz.md5.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.Date;

import com.btw.jdbc.mysql.MysqlDriver;
import com.jiuqi.hjz.md5.util.MD5Util;

public class test {
	public static void main(String[] args) {
		long start = new Date().getTime();
		System.out.println("开始尝试...");
		String src = getLastStr();
		creatMd5Str(src, 90000L);
		long stop = new Date().getTime();
		System.out.println("本次生成耗时"+(Math.floor(stop-start)/1000)+"秒");
	}
	
	private static String getLastStr() {
		try {
			MysqlDriver driver = new MysqlDriver(null, null, null);
			ResultSet rs = driver.executeQuery("SELECT TARGET from md5 ORDER BY NUM DESC LIMIT 0,1");
			rs.next();
			return rs.getString("TARGET");
			
		} catch (Exception e) {
			
		}
		return null;
	}

	private static void creatMd5Str(String string, long l) {
		String srcStr = new String(string);
		MysqlDriver driver = new MysqlDriver(null, null, null);
		long k = l/100;
		for(long j=0;j<l;j++){
			String md5 = MD5Util.getMD5String(string);
			if(string.equals(md5)){
				System.out.println("经过"+l+"次尝试，终于成功啦！,结果为"+string);
				return;
			}else{
				driver.executeUpdate("insert into MD5 values(null,?, ?)",string,md5);
				string = md5;
			}
			if((j+1)%(k)==0){
				System.out.println("进度："+Math.floor((j+1)/k)+"%");
			}
		}
		System.out.println("原始生成字符串为\""+srcStr+"\"共尝试"+l+"次，没有成功！");
	}

	public static void writeFile(File f, String md5) throws IOException {

		f.createNewFile(); // 创建新文件
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		out.write(md5); // \r\n即为换行
		out.flush(); // 把缓存区内容压入文件
		out.close();
	}

	@SuppressWarnings("resource")
	public static String readFile(File f) throws IOException {

		InputStreamReader reader = new InputStreamReader(new FileInputStream(f)); // 建立一个输入流对象reader
		BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
		String line = "";
		line = br.readLine();
		return line;
	}
}
