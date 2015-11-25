package com.btw.server.self;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;

public class SelfResourceHandler extends ResourceHandler {
	
	private static final Logger LOG = Log.getLogger(SelfResourceHandler.class);

	public SelfResourceHandler() {
		super();
	}
	
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (baseRequest.isHandled()) {
			return;
		}
		boolean skipContentBody = false;

		if (!("GET".equals(request.getMethod()))) {
			if (!("HEAD".equals(request.getMethod()))) {
				super.handle(target, baseRequest, request, response);
				return;
			}
			skipContentBody = true;
		}

		Resource resource = getResource(request);

		if ((resource == null) || (!(resource.exists()))) {
			if (target.endsWith("/jetty-dir.css")) {
				resource = getStylesheet();
				if (resource == null)
					return;
				response.setContentType("text/css");
			} else {
				super.handle(target, baseRequest, request, response);
				return;
			}
		}

		if ((!(isAliases())) && (resource.getAlias() != null)) {
			LOG.info(resource + " aliased to " + resource.getAlias(),
					new Object[0]);
			return;
		}

		baseRequest.setHandled(true);

		if (resource.isDirectory()) {
			if (!(request.getPathInfo().endsWith("/"))) {
				response.sendRedirect(response.encodeRedirectURL(URIUtil
						.addPaths(request.getRequestURI(), "/")));
				return;
			}

			Resource welcome = getWelcome(resource);
			if ((welcome != null) && (welcome.exists())) {
				resource = welcome;
			} else {
				//此处不进行handled标记，而是根据doDirectory的结果标记。
				this.doDirectory(baseRequest, request, response, resource);
				return;
			}

		}

		long last_modified = resource.lastModified();
		String etag = null;
		if (isEtags()) {
			String ifnm = request.getHeader("If-None-Match");
			etag = resource.getWeakETag();
			if ((ifnm != null) && (resource != null) && (ifnm.equals(etag))) {
				response.setStatus(304);
				baseRequest.getResponse().getHttpFields()
						.put(HttpHeaders.ETAG_BUFFER, etag);
				return;
			}

		}

		if (last_modified > 0L) {
			long if_modified = request.getDateHeader("If-Modified-Since");
			if ((if_modified > 0L)
					&& (last_modified / 1000L <= if_modified / 1000L)) {
				response.setStatus(304);
				return;
			}
		}

		Buffer mime = getMimeTypes().getMimeByExtension(resource.toString());
		if (mime == null) {
			mime = getMimeTypes().getMimeByExtension(request.getPathInfo());
		}

		doResponseHeaders(response, resource, (mime != null) ? mime.toString()
				: null);
		response.setDateHeader("Last-Modified", last_modified);
		if (isEtags()) {
			baseRequest.getResponse().getHttpFields()
					.put(HttpHeaders.ETAG_BUFFER, etag);
		}
		if (skipContentBody) {
			return;
		}
		OutputStream out = null;
		try {
			out = response.getOutputStream();
		} catch (IllegalStateException e) {
			out = new WriterOutputStream(response.getWriter());
		}

		if (out instanceof AbstractHttpConnection.Output) {
			((AbstractHttpConnection.Output) out).sendContent(resource
					.getInputStream());
		} else {
			resource.writeTo(out, 0L, resource.length());
		}
	}
	
	protected void doDirectory(Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, Resource resource) throws IOException {
		if (isDirectoriesListed()) {
			String listing = resource.getListHTML(request.getRequestURI(),
					request.getPathInfo().lastIndexOf("/") > 0);
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println(listing);
			//如果允许list，列出list并标记为已处理
			baseRequest.setHandled(true);
		} else {
			//如果不允许list，标记为未处理，允许下一个handler继续处理。
			baseRequest.setHandled(false);
		}
	}
}
