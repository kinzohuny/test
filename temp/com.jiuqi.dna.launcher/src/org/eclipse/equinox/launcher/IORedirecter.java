/**
 * 
 */
package org.eclipse.equinox.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * @author yangduanxue
 *
 */
final class IORedirecter {

	private static final String SERVER_LOG_FILE = "work/server.log";//$NON-NLS-1$

	static PrintStream redirectOut;

	static MultiInputStream redirectIn;

	/**
	 * 重定向标准输出流到到server.log文件中
	 */
	static final void redirectIO(String serverRootPath) {
		try {
			File sLogFile = new File(serverRootPath, SERVER_LOG_FILE);
			redirectIn = new MultiInputStream(System.in);
			redirectOut = new PrintStream(new FileOutputStream(sLogFile), true);
			System.setIn(redirectIn);
			System.setOut(redirectOut);
			System.setErr(redirectOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
