package com.jiuqi.dna.core.internal.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

public final class Strings {

	private Strings() {
	}

	public static final String readString(InputStream is, Charset charset)
			throws IOException {
		if (is == null) {
			throw new IllegalArgumentException();
		}
		final InputStreamReader isr = new InputStreamReader(is, charset);
		try {
			char[] str = new char[500];
			int strl = 0;
			for (int start = 0;;) {
				int l = str.length;
				int r = isr.read(str, start, l - start);
				if (r >= 0) {
					strl += r;
					if (strl == l) {
						char[] newstr = new char[l * 2];
						System.arraycopy(str, 0, newstr, 0, l);
						str = newstr;
					}
					start = strl;
				} else {
					break;
				}
			}
			return new String(str, 0, strl);
		} finally {
			isr.close();
		}
	}

	public static final String readString(Class<?> locator, String resource,
			Charset charset) throws IOException {
		final InputStream is = locator.getResourceAsStream(resource);
		if (is == null) {
			return null;
		}
		try {
			return readString(is, charset);
		} finally {
			is.close();
		}
	}

	public static final String readString(Class<?> locator, String name,
			String[] modifiers, Charset charset) throws IOException {
		for (String modifier : modifiers) {
			final InputStream is = locator.getResourceAsStream(name + "." + modifier);
			if (is == null) {
				continue;
			}
			try {
				return readString(is, charset);
			} finally {
				is.close();
			}
		}
		return null;
	}

	public static final void readLines(InputStream is, Charset charset,
			Collection<String> c) throws IOException {
		if (is == null) {
			throw new IllegalArgumentException();
		}
		final InputStreamReader isr = new InputStreamReader(is, charset);
		try {
			final BufferedReader br = new BufferedReader(isr);
			try {
				for (String s = br.readLine(); s != null && s.trim().length() > 0;) {
					c.add(s);
					s = br.readLine();
				}
			} finally {
				br.close();
			}
		} finally {
			isr.close();
		}
	}

	public static final String[] readLines(InputStream is, Charset charset)
			throws IOException {
		ArrayList<String> l = new ArrayList<String>();
		readLines(is, charset, l);
		return l.toArray(new String[l.size()]);
	}
}