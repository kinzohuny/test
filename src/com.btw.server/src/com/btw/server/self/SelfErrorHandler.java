package com.btw.server.self;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.handler.ErrorHandler;

/**
 * 不生效，奇怪
 * @author Kinzo
 *
 */
public class SelfErrorHandler extends ErrorHandler{
	
	public SelfErrorHandler() {
		super();
	}

	@Override
	protected void writeErrorPage(HttpServletRequest request, Writer writer,
			int code, String message, boolean showStacks) throws IOException {
		if (message == null) {
			message = HttpStatus.getMessage(code);
		}
		writer.write("<html>\n<head>\n");
		writeErrorPageHead(request, writer, code, message);
		writer.write("</head>\n<body bgcolor=\"black\">");
		writeErrorPageBody(request, writer, code, message, showStacks);
		writer.write("\n</body>\n</html>\n");
	}
	
	@Override
	protected void writeErrorPageHead(HttpServletRequest request,
			Writer writer, int code, String message) throws IOException {
		writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/>\n");
		writer.write("<title>Error</title>");
	}
	
	@Override
	protected void writeErrorPageBody(HttpServletRequest request,
			Writer writer, int code, String message, boolean showStacks)
			throws IOException {
		String uri = request.getRequestURI();
		writer.write("<center><h1><font color=\"white\">Whoops, looks like something went wrong.<font></h1></center>");
		writeErrorPageMessage(request, writer, code, message, uri);
		if (showStacks)
			writeErrorPageStacks(request, writer);

	}
	
	@Override
	protected void writeErrorPageMessage(HttpServletRequest request,
			Writer writer, int code, String message, String uri)
			throws IOException {
		writer.write("<hr /><h2>HTTP ERROR ");
		writer.write(Integer.toString(code));
		writer.write("</h2>\n<p>Problem accessing ");
		write(writer, uri);
		writer.write(". Reason:\n<pre>    ");
		write(writer, message);
		writer.write("</pre></p>");
	}
}
