package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.misc.Obfuscater;

/**
 * 密码扰乱Servlet
 * 
 * @author gaojingxin
 * 
 */
public final class ObfuscateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private long lastTime;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		final Enumeration<String> names = req.getParameterNames();
		if (names.hasMoreElements()) {
			final String obf = names.nextElement();
			if (!names.hasMoreElements()) {
				final String v = req.getParameter(obf);
				if (v == null || v.length() == 0) {
					final long now = System.currentTimeMillis();
					final String respStr;
					final long c = now - this.lastTime;
					final boolean obfOrUnobf = !req.getServletPath().endsWith("unobf");
					if (c > 5000) {
						if (obfOrUnobf) {
							respStr = "下面整行为扰乱后的字符串：<br>".concat(Obfuscater.obfuscate(obf));
						} else {
							respStr = "下面整行为反扰乱后的字符串：<br>".concat(Obfuscater.unobfuscate(obf));
						}
					} else {
						respStr = "不允许频繁调用，请5秒钟后重试！";
					}
					this.lastTime = now;
					resp.setCharacterEncoding("GBK");
					resp.getWriter().write(respStr);
					return;
				}
			}
		}
		super.doGet(req, resp);
	}
}
