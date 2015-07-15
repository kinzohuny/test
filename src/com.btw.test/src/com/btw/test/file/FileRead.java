package com.btw.test.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * 读取文件内容，忘了要干啥了。
 * @author Kinzo
 *
 */
public class FileRead {

	public static void main(String[] args) {
//		String path = "/resources/cookies.txt";
		String path = "C:\\1.txt";
		InputStream in = FileRead.class.getResourceAsStream(path);
		System.out.println(stream2String(in, "UTF-8"));
//		File file = new File(path);
	}
	
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
	
    /**
     * 文件转换为字符串
     *
     * @param in            字节流
     * @param charset 文件的字符集
     * @return 文件内容
     */
    public static String stream2String(InputStream in, String charset) {
            StringBuffer sb = new StringBuffer();
            try {
                    Reader r = new InputStreamReader(in, charset);
                    int length = 0;
                    for (char[] c = new char[1024]; (length = r.read(c)) != -1;) {
                            sb.append(c, 0, length);
                    }
                    r.close();
            } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            return sb.toString();
    } 
}
