package com.jiuqi.dna.core.impl;

import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;

final class DNARollingFileAppender extends RollingFileAppender {

	DNARollingFileAppender(final Layout layout, final String filename,
			final boolean isAllAppender) throws IOException {
		super(layout, filename);
		this.isAllAppender = isAllAppender;
	}

	@Override
	public final boolean isAsSevereAsThreshold(final Priority priority) {
		return this.isAllAppender || (this.threshold != null && this.threshold.toInt() == priority.toInt());
	}

	private final boolean isAllAppender;

}
