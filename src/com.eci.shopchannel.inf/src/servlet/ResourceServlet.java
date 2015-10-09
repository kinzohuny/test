package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.StringUtils;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String filePath = req.getRequestURI();
		if(StringUtils.isNotEmpty(filePath)){
			InputStream inputStream = ResourceServlet.class.getResourceAsStream(filePath);
			if(inputStream!=null){
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setCharacterEncoding("UTF-8");
				//resp.setContentType("application/javascript; charset=utf-8"); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line = null;
		        while((line = reader.readLine())!= null){ 
		    		resp.getWriter().println(line);
		        }
				resp.getWriter().flush();
				resp.getWriter().close();
			}
		}
	}
}
