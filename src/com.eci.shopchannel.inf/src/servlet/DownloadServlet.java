package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.StringUtils;

/**
 * 随机码生成
 */
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String filePath = req.getRequestURI();
		if(StringUtils.isNotEmpty(filePath)){
			InputStream inputStream = ResourceServlet.class.getResourceAsStream(filePath);
			if(inputStream!=null){
				resp.setHeader("content-type", "application/"+filePath.split("\\.")[filePath.split("\\.").length-1]);
				resp.addHeader("content-disposition", "attachment;filename="+filePath.split("/")[filePath.split("/").length-1]);
		        int read = 0;
		        byte[] bytes = new byte[1024];
		       
		        OutputStream outputStream = resp.getOutputStream();
		        while((read = inputStream.read(bytes)) != -1) {
		            outputStream.write(bytes, 0, read);
		        }
		        outputStream.flush();
		        outputStream.close();
			}
		}
	}
}
