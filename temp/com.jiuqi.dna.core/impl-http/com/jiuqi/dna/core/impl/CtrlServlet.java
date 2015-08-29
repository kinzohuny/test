package com.jiuqi.dna.core.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.spi.application.AppUtil;
import com.jiuqi.dna.core.spi.application.Session;

/**
 * √‹¬Î»≈¬“Servlet
 * 
 * @author gaojingxin
 * 
 */
@SuppressWarnings("deprecation")
public final class CtrlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String P_ACTN = "act";
	private static final String PV_ACTN_KS = "ks";
	private static final String PV_ACTN_KS_SID = "sid";
	private static final String PV_ACTN_KS_VC = "vc";

	private void killSession(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			final Session session = AppUtil.getDefaultApp().getSession(Long.parseLong(req.getParameter(PV_ACTN_KS_SID)));
			if (session.getVerificationCode() == Long.parseLong(req.getParameter(PV_ACTN_KS_VC))) {
				session.dispose(1);// “Ï≤Ωœ˙ªŸ
				resp.getWriter().write("OK");
				return;
			}
		} catch (Throwable e) {
		}
		resp.getWriter().write("ERROR");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		final String action = req.getParameter(P_ACTN);
		if (PV_ACTN_KS.equalsIgnoreCase(action)) {
			this.killSession(req, resp);
		} else {
			resp.getWriter().write("To kill a session use get method by the url like this: http://<host>[:<port>]/dna_core/ctrl?act=ks&sid=<id of the sessions to be killed>&vc=<Verification code >");
		}
	}
}
