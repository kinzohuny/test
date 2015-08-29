/**
 * 
 */
package com.jiuqi.dna.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.internal.adaptor.Locker;
import org.eclipse.core.runtime.internal.adaptor.Locker_JavaIo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author huangkaibin
 *
 */
public class ServerDaemon implements Constants {

	private static ServerDaemon instance;

	public static ServerDaemon getInstance() {
		if (instance == null) {
			instance = new ServerDaemon();
		}
		return instance;
	}

	public static void startServer() throws Exception {
		getInstance().start();
	}

	public static void stopServer() throws Exception {
		getInstance().stop();
		System.exit(0);
	}

	public static void restartServer() throws Exception {
		getInstance().stop();
		getInstance().start();
	}

	private Locker locker = null;

	private void start() throws Exception {
		String filePath = System.getProperty(ROOT_PATH) + "/work/pid";
		File file = new File(filePath);
		if (locker == null) {
			locker = new Locker_JavaIo(file);
		}
		if (locker.isLocked()) {
			throw new RuntimeException("Server is already running!");
		}
		locker.lock();
	}

	private void stop() throws Exception {
		HttpURLConnection conn = null;
		try {
			URL url = getUrl();
			conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("action", "stop");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			OutputStream out = conn.getOutputStream();
			out.write(1);
			out.close();
			InputStream in = conn.getInputStream();
			while (in.read() != -1) {
			}
			if (locker != null) {
				locker.release();
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private URL getUrl() throws Exception {
		String path = System.getProperty(ROOT_PATH) + "/work/dna-server.xml";
		String host = null;
		String port = null;
		InputStream input = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			input = new FileInputStream(path);
			Document doc = builder.parse(input);
			Element root = doc.getDocumentElement();
			NodeList listenList = root.getElementsByTagName("listen");
			if (listenList != null && listenList.getLength() > 0) {
				Node node = listenList.item(0);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					host = elem.getAttribute("host");
					port = elem.getAttribute("port");
				}
			}
		} finally {
			input.close();
		}
		if (host == null || "".equals(host)) {
			host = "localhost";
		}
		int portNum = -1;
		if (port != null && !"".equals(port)) {
			portNum = Integer.valueOf(port);
		}
		return new URL("http", host, portNum, "sys");
	}

}
