package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.jiuqi.dna.core.exception.NullArgumentException;

public final class DNALogManagerInternal {

	private static String LOG_FORMAT = "Log Time: %d%nLevel: %p%n%m%n%n";
	private static final String ALL_LOGFILENAME = "_all.log";
	private static final String DEBUG_LOGFILENAME = "_debug.log";
	private static final String INFO_LOGFILENAME = "_info.log";
	private static final String WARN_LOGFILENAME = "_warn.log";
	private static final String ERROR_LOGFILENAME = "_error.log";
	private static final String FATAL_LOGFILENAME = "_fatal.log";
	private static boolean DEBUG_LEVEL_DEBUG;
	private static boolean DEBUG_LEVEL_INFO;
	private static boolean DEBUG_LEVEL_WARN;
	private static boolean DEBUG_LEVEL_ERROR;
	private static boolean DEBUG_LEVEL_FATAL;

	private static final HashMap<String, DNALogger> LOGGER_MAP = new HashMap<String, DNALogger>();

	static {
		for (String p : System.getProperty("com.jiuqi.dna.debug.level", "all").toLowerCase().split(",")) {
			p = p.trim();
			if (p.equals("all")) {
				DEBUG_LEVEL_DEBUG = true;
				DEBUG_LEVEL_INFO = true;
				DEBUG_LEVEL_WARN = true;
				DEBUG_LEVEL_ERROR = true;
				DEBUG_LEVEL_FATAL = true;
				break;
			} else if (p.equals("debug")) {
				DEBUG_LEVEL_DEBUG = true;
			} else if (p.equals("info")) {
				DEBUG_LEVEL_INFO = true;
			} else if (p.equals("warn")) {
				DEBUG_LEVEL_WARN = true;
			} else if (p.equals("error")) {
				DEBUG_LEVEL_ERROR = true;
			} else if (p.equals("fatal")) {
				DEBUG_LEVEL_FATAL = true;
			}
		}
	}

	static final String[] dealWithCategory(final String category) {
		final ArrayList<Integer> indexs = new ArrayList<Integer>();
		final char[] chars = category.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '/') {
				indexs.add(i);
			}
		}
		final int indexsLength = indexs.size();
		final String[] superiorCategory = new String[indexsLength];
		for (int i = 0; i < indexsLength; i++) {
			superiorCategory[i] = category.substring(0, indexs.get(i));
		}
		return superiorCategory;
	}

	private static final DNALogger ensureLogger(String category) {
		DNALogger logger = LOGGER_MAP.get(category);
		if (logger != null) {
			return logger;
		}
		final Logger fourJLogger = LogManager.getLogger(category);
		try {
			addAppender(fourJLogger, category, Level.ALL, ALL_LOGFILENAME);
			if (DEBUG_LEVEL_FATAL) {
				addAppender(fourJLogger, category, Level.FATAL, FATAL_LOGFILENAME);
			}
			if (DEBUG_LEVEL_ERROR) {
				addAppender(fourJLogger, category, Level.ERROR, ERROR_LOGFILENAME);
			}
			if (DEBUG_LEVEL_WARN) {
				addAppender(fourJLogger, category, Level.WARN, WARN_LOGFILENAME);
			}
			if (DEBUG_LEVEL_INFO) {
				addAppender(fourJLogger, category, Level.INFO, INFO_LOGFILENAME);
			}
			if (DEBUG_LEVEL_DEBUG) {
				addAppender(fourJLogger, category, Level.DEBUG, DEBUG_LOGFILENAME);
			}
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		logger = new DNALogger(fourJLogger);
		LOGGER_MAP.put(category, logger);
		category = getSuperiorCategoryOf(category);
		if (category != null) {
			logger.Superior = ensureLogger(category);
		}
		return logger;
	}

	public static final DNALogger getLogger(final String category) {
		if (category == null) {
			throw new NullArgumentException("category");
		}
		synchronized (LOGGER_MAP) {
			return ensureLogger(category);
		}
	}

	private static final Object lock = new Object();
	private static volatile String LOG_ROOT_DIRECTORY;

	private static final String getLogRootDirectory() {
		if (LOG_ROOT_DIRECTORY != null) {
			return LOG_ROOT_DIRECTORY;
		}
		synchronized (lock) {
			if (LOG_ROOT_DIRECTORY != null) {
				return LOG_ROOT_DIRECTORY;
			}
			LOG_ROOT_DIRECTORY = ApplicationImpl.getDefaultApp().getDNARoot().toString() + "/logs";
			return LOG_ROOT_DIRECTORY;
		}
	}

	private static final void addAppender(Logger logger, String category,
			Level level, String logFileName) throws IOException {
		// 类别的最后一节加上对应日志的文件的后缀
		String fileName = category.substring(category.lastIndexOf('/') + 1) + logFileName;
		String filePath = getLogRootDirectory() + "/" + category + "/" + fileName;
		DNARollingFileAppender appender = new DNARollingFileAppender(null, filePath, level == Level.ALL);
		appender.setName(category);
		appender.setThreshold(level);
		appender.setAppend(true);
		appender.setMaxFileSize("1MB");
		appender.setMaxBackupIndex(100);
		appender.setLayout(new PatternLayout(LOG_FORMAT));
		appender.setImmediateFlush(true);
		logger.addAppender(appender);
		if (ApplicationImpl.IN_DEBUG_MODE && level != Level.ALL) {
			ConsoleAppender ca = new DNAConsoleAppender(new PatternLayout(LOG_FORMAT));
			ca.setName(category);
			ca.setThreshold(level);
			ca.setImmediateFlush(true);
			logger.addAppender(ca);
		}
	}

	/**
	 * @return 返回null表示没有上级
	 */
	private static final String getSuperiorCategoryOf(final String category) {
		int lastIndex = category.lastIndexOf('/');
		return lastIndex < 0 ? null : category.substring(0, lastIndex);
	}

	private DNALogManagerInternal() {
		// to do nothing
	}
}
