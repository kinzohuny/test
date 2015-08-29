package com.jiuqi.dna.core.impl;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;

final class DNAConsoleAppender extends ConsoleAppender {
	DNAConsoleAppender(Layout layout) throws IOException {
		super(layout);
	}

	@Override
	public final boolean isAsSevereAsThreshold(Priority priority) {
		return this.threshold != null && this.threshold.equals(priority);
	}
}