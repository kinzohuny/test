package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession s = req.getSession();
		resp.setContentType("text/html;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().println("<title>Login</title>");
		//body
		resp.getWriter().println("<form id=\"login\" method=\"post\">");
		resp.getWriter().println("<span>账号：</span><input name=\"name\"></input><br>");
		resp.getWriter().println("<span>密码：</span><input name=\"password\"></input><br>");
		resp.getWriter().println("<span>验证码：</span><input name=\"random\"></input><br>");
		resp.getWriter().println("<img src=\"/randomcode?reg=ok\" onclick=\"this.src='/randomcode?reg=ok&temp='+Math.random();\" />");
		resp.getWriter().println("<input type=\"submit\" value=\"登录\" />");
		resp.getWriter().println("</form>");

		resp.getWriter().flush();
		resp.getWriter().close();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	private String toString(Object obj){
		StringBuffer sb = new StringBuffer();
		if(obj instanceof String[]){
			String[] array = (String[]) obj;
			String separator = "";
			for(String str : array){
				sb.append(separator).append(str);
				separator = ",";
			}
		}
		return sb.toString();
	}
}
