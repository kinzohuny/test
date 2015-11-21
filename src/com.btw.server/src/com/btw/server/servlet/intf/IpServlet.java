package com.btw.server.servlet.intf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btw.server.util.ServerUtils;

public class IpServlet extends HttpServlet {

	private static final long serialVersionUID = -6643621523214055849L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String result = ServerUtils.getRemoteIp(req);

		resp.setStatus(200);
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=utf-8");
		resp.getWriter().append(result);

		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
