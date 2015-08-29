package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.http.DNAHttpServlet;
import com.jiuqi.dna.core.system.SystemPrivilege;
import com.jiuqi.dna.core.system.SystemPrivilegeOperation;

public final class DBBackupServlet extends DNAHttpServlet {

	private static final long serialVersionUID = 1L;

	public static final Lock lock = new ReentrantLock();

	private static final String PARAM_ACTION = "action";
	private static final String ACTION_BACKUP = "backup";
	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_PASSWORD = "password";

	private final boolean hasAuthority(HttpServletRequest req,
			ContextImpl<?, ?, ?> context) {
		final String action = req.getParameter(PARAM_ACTION);
		if (!ACTION_BACKUP.equals(action)) {
			return false;
		}
		final String usr = req.getParameter(PARAM_USERNAME);
		final String pwd = req.getParameter(PARAM_PASSWORD);
		final User user;
		user = context.find(User.class, usr);
		if (user == null) {
			return false;
		} else if (user instanceof BuildInUser) {
			return false;
		} else if (!user.validatePassword(pwd)) {
			return false;
		}
		context.changeLoginUser(user);
		SystemPrivilege find = context.find(SystemPrivilegeOperation.EXECUTE, SystemPrivilege.class, SystemPrivilege.DB_BACKUP.getKey());
		if (find == null) {
			return false;
		}
		return true;
	}

	private final boolean tryBackup(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		final ApplicationImpl app = (ApplicationImpl) this.getApplication();
		final SessionImpl session = app.newSession(null, null);
		session.setHeartbeatTimeoutSec(0);
		session.setSessionTimeoutMinutes(0);
		try {
			ContextImpl<?, ?, ?> context = session.newContext(true);
			try {
				if (this.hasAuthority(req, context)) {
					if (lock.tryLock()) {
						try {
							resp.setContentType("application/x-msdownload");
							resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(DbBackup.buildBackupFileName(context.getDataSourceRef()), "UTF-8") + "\"");
							DbBackup.backup(context, resp.getOutputStream(), null);
						} catch (Throwable e) {
							e.printStackTrace();
						} finally {
							lock.unlock();
						}
					} else {
						showProgressing(resp);
					}
					return false;
				}
			} finally {
				context.dispose();
			}
		} finally {
			session.dispose(0);
		}
		return true;
	}

	private static final void showProgressing(HttpServletResponse resp)
			throws IOException {
		final PrintWriter writer = resp.getWriter();
		writer.println("<html><head><title>Database Backup</title></head>");
		writer.println("<body>");
		writer.println("Database Backup is processing.");
		writer.println("</body>");
		writer.println("</html>");
	}

	private static final void showForm(HttpServletRequest req,
			HttpServletResponse resp, boolean warning) throws IOException {
		final PrintWriter writer = resp.getWriter();
		writer.println("<html><head><title>dbexp</title></head>");
		writer.println("<body>");
		writer.println("Input Username and Password for Database Backup.<br/>");
		writer.println("<form action=\"/dna_core/db\" method=\"post\">");
		writer.println("Username: <input name=\"username\" type=\"text\"/><br/>");
		writer.println("Password: <input name=\"password\" type=\"password\"/><br/>");
		writer.println("<input type=\"hidden\" name=\"action\" value=\"backup\"/><br/>");
		writer.println("<input type=\"submit\" value=\"Backup\"/>");
		writer.println("</form>");
		if (warning) {
			writer.println("user can not backup database");
		}
		writer.println("</body>");
		writer.println("</html>");
	}

	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		showForm(req, resp, false);
	}

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (this.tryBackup(req, resp)) {
			showForm(req, resp, true);
		}
	}
}