/**
 * 
 */
package org.eclipse.equinox.launcher;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author huangkaibin
 *
 */
public class MultiInputStream extends FilterInputStream {

	private ByteArrayBuffer buffer;

	/**
	 * @param in
	 */
	public MultiInputStream(InputStream in) {
		super(in);
	}

	protected ByteArrayBuffer getBuffer() {
		if (buffer == null) {
			buffer = new ByteArrayBuffer();
		}
		return buffer;
	}

	@Override
	public synchronized int read() throws IOException {
		ByteArrayBuffer buffer = getBuffer();
		if (buffer.size() > 0) {
			int value = buffer.getByteArray()[0];
			buffer.consumed(1);
			return value;
		}
		return super.read();
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		ByteArrayBuffer buffer = getBuffer();
		if (buffer.size() > 0) {
			int count = Math.min(buffer.size(), len);
			System.arraycopy(buffer.getByteArray(), 0, b, off, count);
			buffer.consumed(count);
			return count;
		}
		return super.read(b, off, len);
	}

	public synchronized void sendMessage(String message) {
		try {
			// XXX
			System.out.print(message);
			getBuffer().write(message.getBytes());
		} catch (IOException e) {
			// ignore
		}
	}

	@Override
	public int available() throws IOException {
		return super.available() + getBuffer().size();
	}

}
