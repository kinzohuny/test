/**
 * 
 */
package org.eclipse.equinox.launcher;

import java.io.ByteArrayOutputStream;

/**
 * @author huangkaibin
 *
 */
public class ByteArrayBuffer extends ByteArrayOutputStream {

	/**
	 * 
	 */
	public ByteArrayBuffer() {
	}

	/**
	 * @param size
	 */
	public ByteArrayBuffer(int size) {
		super(size);
	}

	synchronized byte[] getByteArray() {
		return buf;
	}

	public synchronized void consumed(int count) {
		if (count >= this.count) {
			reset();
			return;
		}
		System.arraycopy(buf, count, buf, 0, this.count - count);
	}

}
