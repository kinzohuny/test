package com.btw.server.servlet.intf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.btw.server.dao.ManagerDao;
import com.btw.server.dao.PcDao;
import com.btw.server.model.PcModel;
import com.btw.server.util.ServerUtils;
import com.btw.server.util.StringUtils;

public class IpSyncServlet extends HttpServlet {

	private static final long serialVersionUID = 5557363654595492027L;
	private static final Logger logger = Logger.getLogger(IpSyncServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String result = "";
		String username = req.getParameter("username");
		String password = req.getParameter("password");

		try {
			if ((StringUtils.isNotEmpty(username)) && (StringUtils.isNotEmpty(password)) && ManagerDao.verify(username, password) > 0) {
				PcModel model = getPcModel(req);
				if(StringUtils.isNotEmpty(model.getServer_name())&&StringUtils.isNotEmpty(model.getServer_ip())){
					PcDao.insertUpdatePc(model);
					result = "ok";
				}
			}
		} catch (Exception e) {
			logger.error("login verify error!", e);
		}

		resp.setStatus(200);
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=utf-8");
		resp.getWriter().append(result);

		resp.getWriter().flush();
		resp.getWriter().close();
	}

	private PcModel getPcModel(HttpServletRequest req) {
		PcModel model = new PcModel();
		model.setServer_name(req.getParameter("server_name"));
		model.setServer_ip(ServerUtils.getRemoteIp(req));
		return model;
	}

}
