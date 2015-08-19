package com.btw.test.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WebUtils {

	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";

	private WebUtils() {
	}

	/**
	 * 执行HTTP POST请求。
	 * 
	 * @param url 请求地址
	 * @param params 请求参数
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout)
			throws IOException {
		return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
	}

	/**
	 * 执行HTTP POST请求。
	 * 
	 * @param url 请求地址
	 * @param params 请求参数
	 * @param charset 字符集，如UTF-8, GBK, GB2312
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout,
			int readTimeout) throws IOException {
		return doPost(url, params, charset, connectTimeout, readTimeout, null);
	}

	public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout,
			int readTimeout, Map<String, String> headerMap) throws IOException {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query = buildQuery(params, charset);
		byte[] content = {};
		if (query != null) {
			content = query.getBytes(charset);
		}
		return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
	}

	/**
	 * 执行HTTP POST请求。
	 * 
	 * @param url 请求地址
	 * @param ctype 请求类型
	 * @param content 请求字节数组
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
			throws IOException {
		return _doPost(url, ctype, content, connectTimeout, readTimeout, null);
	}

	private static String _doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout,
			Map<String, String> headerMap) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				conn = getConnection(new URL(url), METHOD_POST, ctype, headerMap);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				logCommError(e, url, map.get("app_key"), map.get("method"), content);
				throw e;
			}
			try {
				out = conn.getOutputStream();
				out.write(content);
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				logCommError(e, conn, map.get("app_key"), map.get("method"), content);
				throw e;
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	/**
	 * 执行带文件上传的HTTP POST请求。
	 * 
	 * @param url 请求地址
	 * @param textParams 文本请求参数
	 * @param fileParams 文件请求参数
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams,
			int connectTimeout, int readTimeout) throws IOException {
		if (fileParams == null || fileParams.isEmpty()) {
			return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
		} else {
			return doPost(url, params, fileParams, DEFAULT_CHARSET, connectTimeout, readTimeout);
		}
	}

	public static String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams,
			String charset, int connectTimeout, int readTimeout) throws IOException {
		return doPost(url, params, fileParams, charset, connectTimeout, readTimeout, null);
	}

	/**
	 * 执行带文件上传的HTTP POST请求。
	 * 
	 * @param url 请求地址
	 * @param textParams 文本请求参数
	 * @param fileParams 文件请求参数
	 * @param charset 字符集，如UTF-8, GBK, GB2312
	 * @param headerMap 需要传递的header头，可以为空
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams,
			String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap) throws IOException {
		if (fileParams == null || fileParams.isEmpty()) {
			return doPost(url, params, charset, connectTimeout, readTimeout, headerMap);
		} else {
			return _doPostWithFile(url, params, fileParams, charset, connectTimeout, readTimeout, headerMap);
		}

	}

	private static String _doPostWithFile(String url, Map<String, String> params, Map<String, FileItem> fileParams,
			String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap) throws IOException {
		String boundary = System.currentTimeMillis() + ""; // 随机分隔线
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				String ctype = "multipart/form-data;charset=" + charset + ";boundary=" + boundary;
				conn = getConnection(new URL(url), METHOD_POST, ctype, headerMap);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				logCommError(e, url, map.get("app_key"), map.get("method"), params);
				throw e;
			}

			try {
				out = conn.getOutputStream();

				byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n").getBytes(charset);

				// 组装文本请求参数
				Set<Entry<String, String>> textEntrySet = params.entrySet();
				for (Entry<String, String> textEntry : textEntrySet) {
					byte[] textBytes = getTextEntry(textEntry.getKey(), textEntry.getValue(), charset);
					out.write(entryBoundaryBytes);
					out.write(textBytes);
				}

				// 组装文件请求参数
				Set<Entry<String, FileItem>> fileEntrySet = fileParams.entrySet();
				for (Entry<String, FileItem> fileEntry : fileEntrySet) {
					FileItem fileItem = fileEntry.getValue();
					if (fileItem.getContent() == null) {
						continue;
					}
					byte[] fileBytes = getFileEntry(fileEntry.getKey(), fileItem.getFileName(), fileItem.getMimeType(), charset);
					out.write(entryBoundaryBytes);
					out.write(fileBytes);
					out.write(fileItem.getContent());
				}

				// 添加请求结束标志
				byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n").getBytes(charset);
				out.write(endBoundaryBytes);
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				logCommError(e, conn, map.get("app_key"), map.get("method"), params);
				throw e;
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static byte[] getTextEntry(String fieldName, String fieldValue, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
		entry.append(fieldValue);
		return entry.toString().getBytes(charset);
	}

	private static byte[] getFileEntry(String fieldName, String fileName, String mimeType, String charset)
			throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\";filename=\"");
		entry.append(fileName);
		entry.append("\"\r\nContent-Type:");
		entry.append(mimeType);
		entry.append("\r\n\r\n");
		return entry.toString().getBytes(charset);
	}

	/**
	 * 执行HTTP GET请求。
	 * 
	 * @param url 请求地址
	 * @param params 请求参数
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doGet(String url, Map<String, String> params) throws IOException {
		return doGet(url, params, DEFAULT_CHARSET);
	}

	/**
	 * 执行HTTP GET请求。
	 * 
	 * @param url 请求地址
	 * @param params 请求参数
	 * @param charset 字符集，如UTF-8, GBK, GB2312
	 * @return 响应字符串
	 * @throws IOException
	 */
	public static String doGet(String url, Map<String, String> params, String charset) throws IOException {
		HttpURLConnection conn = null;
		String rsp = null;

		try {
			String ctype = "application/x-www-form-urlencoded;charset=" + charset;
			String query = buildQuery(params, charset);
			try {
				conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype, null);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				logCommError(e, url, map.get("app_key"), map.get("method"), params);
				throw e;
			}

			try {
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				logCommError(e, conn, map.get("app_key"), map.get("method"), params);
				throw e;
			}

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static HttpURLConnection getConnection(URL url, String method, String ctype, Map<String, String> headerMap) throws IOException {
		HttpURLConnection conn = null;
		if ("https".equals(url.getProtocol())) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}
			HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
			connHttps.setSSLSocketFactory(ctx.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;// 默认都认证通过
				}
			});
			conn = connHttps;
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}

		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("User-Agent", "top-sdk-java");
		conn.setRequestProperty("Content-Type", ctype);
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return conn;
	}

	private static URL buildGetUrl(String strUrl, String query) throws IOException {
		URL url = new URL(strUrl);
		if (StringUtils.isEmpty(query)) {
			return url;
		}

		if (StringUtils.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "&" + query;
			}
		}

		return new URL(strUrl);
	}

	private static String buildQuery(Map<String, String> params, String charset) throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}

		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;

		for (Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			// 忽略参数名或参数值为空的参数
			if (StringUtils.areNotEmpty(name, value)) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}

				query.append(name).append("=").append(URLEncoder.encode(value, charset));
			}
		}

		return query.toString();
	}

	private static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsString(conn.getInputStream(), charset);
		} else {
			String msg = getStreamAsString(es, charset);
			if (StringUtils.isEmpty(msg)) {
				throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
			} else {
				throw new IOException(msg);
			}
		}
	}

	private static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			Reader reader = new InputStreamReader(stream, charset);
			StringBuilder response = new StringBuilder();

			final char[] buff = new char[1024];
			int read = 0;
			while ((read = reader.read(buff)) > 0) {
				response.append(buff, 0, read);
			}

			return response.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

	/**
	 * 使用默认的UTF-8字符集反编码请求参数值。
	 * 
	 * @param value 参数值
	 * @return 反编码后的参数值
	 */
	public static String decode(String value) {
		return decode(value, DEFAULT_CHARSET);
	}

	/**
	 * 使用默认的UTF-8字符集编码请求参数值。
	 * 
	 * @param value 参数值
	 * @return 编码后的参数值
	 */
	public static String encode(String value) {
		return encode(value, DEFAULT_CHARSET);
	}

	/**
	 * 使用指定的字符集反编码请求参数值。
	 * 
	 * @param value 参数值
	 * @param charset 字符集
	 * @return 反编码后的参数值
	 */
	public static String decode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLDecoder.decode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * 使用指定的字符集编码请求参数值。
	 * 
	 * @param value 参数值
	 * @param charset 字符集
	 * @return 编码后的参数值
	 */
	public static String encode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLEncoder.encode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	private static Map<String, String> getParamsFromUrl(String url) {
		Map<String, String> map = null;
		if (url != null && url.indexOf('?') != -1) {
			map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
		}
		if (map == null) {
			map = new HashMap<String, String>();
		}
		return map;
	}

	/**
	 * 从URL中提取所有的参数。
	 * 
	 * @param query URL地址
	 * @return 参数映射
	 */
	public static Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String>();

		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if (param != null && param.length == 2) {
					result.put(param[0], param[1]);
				}
			}
		}

		return result;
	}
	
	private static void logCommError(Exception e, HttpURLConnection conn,
			String appKey, String method, byte[] content) {
		String contentString = null;
		try {
			contentString = new String(content, "UTF-8");
			_logCommError(e, conn, null, appKey, method, parseParam(contentString));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private static void logCommError(Exception e, String url, String appKey,
			String method, byte[] content) {
		String contentString = null;
		try {
			contentString = new String(content, "UTF-8");
			_logCommError(e, null, url, appKey, method, parseParam(contentString));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void logCommError(Exception e, HttpURLConnection conn,
			String appKey, String method, Map<String, String> params) {
		_logCommError(e, conn, null, appKey, method, params);
	}

	private static void logCommError(Exception e, String url, String appKey,
			String method, Map<String, String> params) {
		_logCommError(e, null, url, appKey, method, params);
	}
	
	private static void _logCommError(Exception e, HttpURLConnection conn, String url, String appKey, String method, Map<String, String> params) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String urlStr = null;
		String rspCode = "";
		if (conn != null) {
			try {
				urlStr = conn.getURL().toString();
				rspCode = "HTTP_ERROR_" + conn.getResponseCode();
			} catch (IOException ioe) {
			}
		} else {
			urlStr = url;
			rspCode = "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(df.format(new Date()));// 时间
		sb.append("^_^");
		sb.append(method);// API
		sb.append("^_^");
		sb.append(appKey);// APP
		sb.append("^_^");
		sb.append(getIp());// IP地址
		sb.append("^_^");
		sb.append(osName);// 操作系统
		sb.append("^_^");
		sb.append(urlStr);// 请求URL
		sb.append("^_^");
		sb.append(rspCode);
		sb.append("^_^");
		sb.append((e.getMessage() + "").replaceAll("\r\n", " "));
		System.out.println(sb.toString());
	}
	
	private static String ip = null;
	private static String osName = System.getProperties().getProperty("os.name");
	
	private static String getIp() {
		if (ip == null) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ip;
	}
	
	private static Map<String, String> parseParam(String contentString) {
		Map<String, String> params = new HashMap<String, String>();
		if (contentString == null || contentString.trim().equals("")) {
			return params;
		}
		String[] paramsArray = contentString.split("\\&");
		if (paramsArray != null) {
			for (String param : paramsArray) {
				String[] keyValue = param.split("=");
				if (keyValue != null && keyValue.length == 2) {
					params.put(keyValue[0], keyValue[1]);
				}
			}
		}
		return params;
	}

	private static class DefaultTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	}

	public class FileItem {

		private String fileName;
		private String mimeType;
		private byte[] content;
		private File file;

		/**
		 * 基于本地文件的构造器。
		 * 
		 * @param file 本地文件
		 */
		public FileItem(File file) {
			this.file = file;
		}

		/**
		 * 基于文件绝对路径的构造器。
		 * 
		 * @param filePath 文件绝对路径
		 */
		public FileItem(String filePath) {
			this(new File(filePath));
		}

		/**
		 * 基于文件名和字节流的构造器。
		 * 
		 * @param fileName 文件名
		 * @param content 文件字节流
		 */
		public FileItem(String fileName, byte[] content) {
			this.fileName = fileName;
			this.content = content;
		}

		/**
		 * 基于文件名、字节流和媒体类型的构造器。
		 * 
		 * @param fileName 文件名
		 * @param content 文件字节流
		 * @param mimeType 媒体类型
		 */
		public FileItem(String fileName, byte[] content, String mimeType) {
			this(fileName, content);
			this.mimeType = mimeType;
		}

		public String getFileName() {
			if (this.fileName == null && this.file != null && this.file.exists()) {
				this.fileName = file.getName();
			}
			return this.fileName;
		}

		public String getMimeType() throws IOException {
			if (this.mimeType == null) {
				this.mimeType = getMimeType(getContent());
			}
			return this.mimeType;
		}

		public byte[] getContent() throws IOException {
			if (this.content == null && this.file != null && this.file.exists()) {
				InputStream in = null;
				ByteArrayOutputStream out = null;

				try {
					in = new FileInputStream(this.file);
					out = new ByteArrayOutputStream();
					int ch;
					while ((ch = in.read()) != -1) {
						out.write(ch);
					}
					this.content = out.toByteArray();
				} finally {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				}
			}
			return this.content;
		}
		
		private String getMimeType(byte[] bytes) {
			String suffix = getFileSuffix(bytes);
			String mimeType;

			if ("JPG".equals(suffix)) {
				mimeType = "image/jpeg";
			} else if ("GIF".equals(suffix)) {
				mimeType = "image/gif";
			} else if ("PNG".equals(suffix)) {
				mimeType = "image/png";
			} else if ("BMP".equals(suffix)) {
				mimeType = "image/bmp";
			}else {
				mimeType = "application/octet-stream";
			}

			return mimeType;
		}
		
		/**
		 * 获取文件的真实后缀名。目前只支持JPG, GIF, PNG, BMP四种图片文件。
		 * 
		 * @param bytes 文件字节流
		 * @return JPG, GIF, PNG or null
		 */
		public String getFileSuffix(byte[] bytes) {
			if (bytes == null || bytes.length < 10) {
				return null;
			}

			if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
				return "GIF";
			} else if (bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
				return "PNG";
			} else if (bytes[6] == 'J' && bytes[7] == 'F' && bytes[8] == 'I' && bytes[9] == 'F') {
				return "JPG";
			} else if (bytes[0] == 'B' && bytes[1] == 'M') {
				return "BMP";
			} else {
				return null;
			}
		}

	}
	
}
