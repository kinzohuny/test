package com.jiuqi.dna.core.impl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NoCloseInputStreamWrapper extends FilterInputStream {

	public NoCloseInputStreamWrapper(InputStream in) {
		super(in);
	}

	@Override
	public void close() throws IOException {
		// Do nothing
	}
}
