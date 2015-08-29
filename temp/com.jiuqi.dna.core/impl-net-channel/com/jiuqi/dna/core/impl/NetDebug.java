package com.jiuqi.dna.core.impl;

import java.util.Date;

class NetDebug {
	private static final String CLASS_NAME = NetDebug.class.getName();
	private static final int DEBUG_LEVEL;
	private static final int TRACE_CONNECT = 1;
	private static final int TRACE_EXCEPTION = 2;
	private static final int TRACE_FAULT = 4;
	private static final int TRACE_THREAD = 8;
	private static final int TRACE_INTERNAL = 16;
	private static final int TRACE_IO = 32;

	static {
		String value = System.getProperty("com.jiuqi.dna.debug.net.channel");
		if (value == null || value.length() == 0) {
			DEBUG_LEVEL = 0;
		} else {
			int l;
			try {
				l = Integer.parseInt(value);
			} catch (Throwable e) {
				l = 0;
				for (String s : value.split(",")) {
					s = s.trim();
					if (s.equalsIgnoreCase("conn")) {
						l |= TRACE_CONNECT;
					} else if (s.equalsIgnoreCase("exception")) {
						l |= TRACE_EXCEPTION;
					} else if (s.equalsIgnoreCase("fault")) {
						l |= TRACE_FAULT;
					} else if (s.equalsIgnoreCase("thread")) {
						l |= TRACE_THREAD;
					} else if (s.equalsIgnoreCase("internal")) {
						l |= TRACE_INTERNAL;
					} else if (s.equalsIgnoreCase("io")) {
						l |= TRACE_IO;
					}
				}
			}
			DEBUG_LEVEL = l;
		}
	}

	final static boolean TRACE_CONNECT() {
		return (DEBUG_LEVEL & TRACE_CONNECT) != 0;
	}

	final static boolean TRACE_EXCEPTION() {
		return (DEBUG_LEVEL & TRACE_EXCEPTION) != 0;
	}

	final static boolean TRACE_FAULT() {
		return (DEBUG_LEVEL & TRACE_FAULT) != 0;
	}

	final static boolean TRACE_THREAD() {
		return (DEBUG_LEVEL & TRACE_THREAD) != 0;
	}

	final static boolean TRACE_INTERNAL() {
		return (DEBUG_LEVEL & TRACE_INTERNAL) != 0;
	}

	final static boolean TRACE_IO() {
		return (DEBUG_LEVEL & TRACE_IO) != 0;
	}

	final static void trace(String msg, boolean timestamp, int stackDepth) {
		StringBuilder sb = new StringBuilder();
		if (timestamp) {
			sb.append(String.format("[%1$tT.%1$tL] %2$s %3$s\n", new Date(), Thread.currentThread(), msg));
		} else {
			sb.append(msg).append('\n');
		}
		if (stackDepth > 0) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			int i = 1;
			int c = trace.length;
			for (; i < c; i++) {
				if (!trace[i].getClassName().equals(CLASS_NAME)) {
					break;
				}
			}
			if (c > i + stackDepth) {
				c = i + stackDepth;
			}
			for (; i < c; i++) {
				sb.append('\t').append(trace[i]).append('\n');
			}
		}
		System.out.print(sb.toString());
	}

	final static void trace(String msg, int depth) {
		trace(msg, false, depth);
	}

	final static void trace(String msg) {
		trace(msg, 0);
	}

	final static String dataToStr(DataFragment fragment) {
		int pos = fragment.getPosition();
		fragment.setPosition(fragment.getAvailableOffset() + 4);
		StringBuilder sb = new StringBuilder();
		try {
			byte ctrlFlag = fragment.readByte();
			switch (ctrlFlag & NetChannelImpl.CTRL_FLAG_TYPE_MASK) {
			case NetChannelImpl.CTRL_FLAG_PACKAGE:
				sb.append("package[").append(fragment.readInt()).append("] package data");
				break;
			case NetChannelImpl.CTRL_FLAG_BREAK_RECEIVE:
				sb.append("package[").append(fragment.readInt()).append("] break receive");
				break;
			case NetChannelImpl.CTRL_FLAG_BREAK_SEND:
				sb.append("package[").append(fragment.readInt());
				switch (ctrlFlag & NetChannelImpl.CTRL_FLAG_SUBTYPE_MASK) {
				case 0:
					sb.append("] break send [ack ").append(fragment.readInt()).append(']');
					break;
				case NetChannelImpl.CTRL_FLAG_RESPONSE:
					sb.append("] ack of break send [").append(fragment.readInt()).append(']');
					break;
				}
				break;
			case NetChannelImpl.CTRL_FLAG_RESOLVED:
				sb.append("package[").append(fragment.readInt());
				switch (ctrlFlag & NetChannelImpl.CTRL_FLAG_SUBTYPE_MASK) {
				case 0:
					sb.append("] resolved [ack ").append(fragment.readInt()).append(']');
					break;
				case NetChannelImpl.CTRL_FLAG_RESPONSE:
					sb.append("] ack of resolved [").append(fragment.readInt()).append(']');
					break;
				}
				break;
			case NetChannelImpl.CTRL_FLAG_PACKAGE_STATE:
				switch (ctrlFlag & NetChannelImpl.CTRL_FLAG_SUBTYPE_MASK) {
				case 0:
					sb.append("request state [ack ").append(fragment.readInt()).append("] [package ");
					while (fragment.remain() > 0) {
						sb.append(' ').append(fragment.readInt());
					}
					sb.append(']');
					break;
				case NetChannelImpl.CTRL_FLAG_RESPONSE:
					sb.append("response state [generation ").append(fragment.readInt()).append("] [package ");
					while (fragment.remain() > 0) {
						sb.append(" ").append(fragment.readInt());
					}
					sb.append(']');
					break;
				}
				break;
			case NetChannelImpl.CTRL_FLAG_CLOSE:
				switch (ctrlFlag & NetChannelImpl.CTRL_FLAG_SUBTYPE_MASK) {
				case 0:
					sb.append("close");
					break;
				case NetChannelImpl.CTRL_FLAG_RESPONSE:
					sb.append("close cancel");
					break;
				}
				break;
			case NetChannelImpl.CTRL_FLAG_KEEP_ALIVE:
				sb.append("keep-alive");
				break;
			default:
				throw new IllegalStateException("无法识别的数据类型");
			}
		} finally {
			fragment.setPosition(pos);
		}
		return sb.toString();
	}
}
