package com.btw.test.md5.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.Date;

import com.btw.test.jdbc.MysqlDriver;
import com.btw.test.md5.util.MD5Util;

public class test {
	public static void main(String[] args) {
		long start = new Date().getTime();
		System.out.println("��ʼ����...");
		String src = getLastStr();
		creatMd5Str(src, 90000L);
		long stop = new Date().getTime();
		System.out.println("�������ɺ�ʱ"+(Math.floor(stop-start)/1000)+"��");
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
				System.out.println("����"+l+"�γ��ԣ����ڳɹ�����,���Ϊ"+string);
				return;
			}else{
				driver.executeUpdate("insert into MD5 values(null,?, ?)",string,md5);
				string = md5;
			}
			if((j+1)%(k)==0){
				System.out.println("���ȣ�"+Math.floor((j+1)/k)+"%");
			}
		}
		System.out.println("ԭʼ�����ַ���Ϊ\""+srcStr+"\"������"+l+"�Σ�û�гɹ���");
	}

	public static void writeFile(File f, String md5) throws IOException {

		f.createNewFile(); // �������ļ�
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		out.write(md5); // \r\n��Ϊ����
		out.flush(); // �ѻ���������ѹ���ļ�
		out.close();
	}

	@SuppressWarnings("resource")
	public static String readFile(File f) throws IOException {

		InputStreamReader reader = new InputStreamReader(new FileInputStream(f)); // ����һ������������reader
		BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
		String line = "";
		line = br.readLine();
		return line;
	}
}
