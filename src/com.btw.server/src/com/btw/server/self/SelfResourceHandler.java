package com.btw.server.self;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.PathResource;
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

		if (!(HttpMethod.GET.is(request.getMethod()))) {
			if (!(HttpMethod.HEAD.is(request.getMethod()))) {
				super.handle(target, baseRequest, request, response);
				return;
			}
			skipContentBody = true;
		}

		Resource resource = getResource(request);

		if (LOG.isDebugEnabled()) {
			if (resource == null)
				LOG.debug("resource=null", new Object[0]);
			else {
				LOG.debug(
						"resource={} alias={} exists={}",
						new Object[] { resource, resource.getAlias(),
								Boolean.valueOf(resource.exists()) });
			}

		}

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

		baseRequest.setHandled(true);

		if (resource.isDirectory()) {
			String pathInfo = request.getPathInfo();
			boolean endsWithSlash = ((pathInfo == null) ? request
					.getServletPath() : pathInfo).endsWith("/");
			if (!(endsWithSlash)) {
				response.sendRedirect(response.encodeRedirectURL(URIUtil
						.addPaths(request.getRequestURI(), "/")));
				return;
			}

			Resource welcome = getWelcome(resource);
			if ((welcome != null) && (welcome.exists())) {
				resource = welcome;
			} else {
				this.doDirectory(baseRequest, request, response, resource);
				//此处不设置处理状态
//				baseRequest.setHandled(true);
				return;
			}

		}

		long last_modified = resource.lastModified();
		String etag = null;
		if (isEtags()) {
			String ifnm = request
					.getHeader(HttpHeader.IF_NONE_MATCH.asString());
			etag = resource.getWeakETag();
			if ((ifnm != null) && (resource != null) && (ifnm.equals(etag))) {
				response.setStatus(304);
				baseRequest.getResponse().getHttpFields()
						.put(HttpHeader.ETAG, etag);
				return;
			}

		}

		if (last_modified > 0L) {
			long if_modified = request
					.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
			if ((if_modified > 0L)
					&& (last_modified / 1000L <= if_modified / 1000L)) {
				response.setStatus(304);
				return;
			}

		}

		String mime = getMimeTypes().getMimeByExtension(resource.toString());
		if (mime == null)
			mime = getMimeTypes().getMimeByExtension(request.getPathInfo());
		doResponseHeaders(response, resource, mime);
		if (isEtags())
			baseRequest.getResponse().getHttpFields()
					.put(HttpHeader.ETAG, etag);
		if (last_modified > 0L) {
			response.setDateHeader(HttpHeader.LAST_MODIFIED.asString(),
					last_modified);
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

		if (!(out instanceof HttpOutput)) {
			resource.writeTo(out, 0L, resource.length());
		} else {
			int min_async_size = (getMinAsyncContentLength() == 0) ? response
					.getBufferSize() : getMinAsyncContentLength();

			if ((request.isAsyncSupported()) && (min_async_size > 0)
					&& (resource.length() >= min_async_size)) {
				final AsyncContext async = request.startAsync();
				async.setTimeout(0L);
				Callback callback = new Callback() {
					public void succeeded() {
						async.complete();
					}

					public void failed(Throwable x) {
						LOG.warn(x.toString(), new Object[0]);
						LOG.debug(x);
						async.complete();
					}
				};
				if ((getMinMemoryMappedContentLength() > 0)
						&& (resource.length() > getMinMemoryMappedContentLength())
						&& (resource.length() < 2147483647L)
						&& (resource instanceof PathResource)) {
					ByteBuffer buffer = BufferUtil.toMappedBuffer(resource
							.getFile());
					((HttpOutput) out).sendContent(buffer, callback);
				} else {
					ReadableByteChannel channel = resource
							.getReadableByteChannel();
					if (channel != null)
						((HttpOutput) out).sendContent(channel, callback);
					else {
						((HttpOutput) out).sendContent(
								resource.getInputStream(), callback);
					}

				}

			} else if ((getMinMemoryMappedContentLength() > 0)
					&& (resource.length() > getMinMemoryMappedContentLength())
					&& (resource instanceof PathResource)) {
				ByteBuffer buffer = BufferUtil.toMappedBuffer(resource
						.getFile());
				((HttpOutput) out).sendContent(buffer);
			} else {
				ReadableByteChannel channel = resource.getReadableByteChannel();
				if (channel != null)
					((HttpOutput) out).sendContent(channel);
				else
					((HttpOutput) out).sendContent(resource.getInputStream());
			}
		}
	}
	
	protected void doDirectory(Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, Resource resource) throws IOException {
		if (isDirectoriesListed()) {
			String listing = resource.getListHTML(request.getRequestURI(),
					request.getPathInfo().lastIndexOf("/") > 0);
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println(listing);
			//如果允许list，列出list并标记为已处理
			baseRequest.setHandled(true);
		} else {
			//如果不允许list，标记为未处理，允许下一个handler继续处理。
			baseRequest.setHandled(false);
		}
	}
}
