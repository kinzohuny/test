package com.btw.test.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 在文件夹下，查找包含指定关键字的文件
 * @author Kinzo
 *
 */
public class FileSearch {
	
	public static void main(String[] args) {
		
		String path = "D:\\temp\\a";
		String key = "a";
		
		FileSearch search = new FileSearch(path);
		search.search(key);
	}

	public FileSearch(String filePath) {
		fileList = FileCount.getAllChildren(filePath);
	}

	List<String> fileList = new ArrayList<String>();

	public void search(String key) {
		
		if(StringUtils.isEmpty(key)){
			return;
		}

		Charset charset = Charset.forName("UTF8");
		CharsetDecoder decoder = charset.newDecoder();
		for (String path : fileList) {

			FileInputStream fis = null;
			FileChannel fc = null;
			try {
				fis = new FileInputStream(path);
				fc = fis.getChannel();
				int sz = (int) fc.size();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,sz);
				CharBuffer cb = decoder.decode(bb);
				String s = String.valueOf(cb);
				int n = s.indexOf(key);
				if (n > -1){
					System.out.println(key + " --- " + path + " --- " + n);
				}
				else{
//					System.out.println(key + " --- not found! ");
				}
			} catch(CharacterCodingException e1) {
				
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(fc!=null){
					try {
						fc.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fis!=null){
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

}
