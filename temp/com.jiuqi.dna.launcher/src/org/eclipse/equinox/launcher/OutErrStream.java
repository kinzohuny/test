/**
 * 
 */
package org.eclipse.equinox.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * 
 * @author yangduanxue
 * @date 2009-11-5
 */
final class OutErrStream extends OutputStream {
	
	private final int maxSize;
	private final String logPath;
		
	private final PrintStream oldOut = System.out;
	
	protected ZipOutputStream zos;
	protected ZipEntry ze;
	private int entryCount;
	
	public OutErrStream(int maxSize, String logPath) {
		this.maxSize = maxSize;
		this.logPath = logPath;
		File f = new File(logPath);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		this.oldOut.write(b);
	}
	
	@Override
	public void write(byte b[]) throws IOException {
		this.oldOut.write(b);
	}
	
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		this.oldOut.write(b, off, len);
		if (this.zos == null) {
			this.initZos(0);
			this.entryCount ++;
		}
		this.ze.setSize(this.ze.getSize() + len);
		if (this.ze.getSize() > this.maxSize) {
			this.entryCount ++;
			if (this.entryCount <= 1) {
				this.zos.closeEntry();
				this.ze = new ZipEntry("log"+"_"+(this.entryCount-1)+".txt");
				this.ze.setSize(len);
				this.zos.putNextEntry(this.ze);
			} else {
				this.zos.close();
				this.initZos(len);
				this.entryCount = 1;
			}
		}
		this.zos.write(b, off, len);
	}
	
	@Override
	public void flush() throws IOException {
		this.oldOut.flush();
		if (this.zos != null) {
			this.zos.flush();
		}
	}
	
	@Override
	public void close() throws IOException {
		this.oldOut.close();
		if (this.zos != null) {
			this.zos.close();
		}
	}
	
	private final void initZos(int entrySize) {
		try {
			String filePath = this.logPath+"/"+formatDateTime(System.currentTimeMillis())+".zip";
			this.zos = new ZipOutputStream(new FileOutputStream(filePath));
			this.ze = new ZipEntry("log_0.txt");
			this.ze.setSize(entrySize);
			this.zos.putNextEntry(this.ze);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final String formatDateTime(long date) {
		if (date == 0) {
			return "";
		}
		String datePart = new java.sql.Date(date).toString();
		String timePart = new java.sql.Time(date).toString();
		return datePart.concat(" ").concat(timePart).replace(":", "");
	}
	

}
