package com.jiuqi.dna.core.impl;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import com.jiuqi.dna.core.Filter;

public final class NameUtl {

	private NameUtl() {
	}

	public static final String buildIdentityName(String name, int maxlen,
			Filter<String> filter) {
		if (name == null || name.length() == 0) {
			throw new NullPointerException();
		}
		maxlen = maxlen < 0 ? 0 : maxlen;
		String base;
		int sequence;
		int length = name.length();
		int underline = name.lastIndexOf('_');
		if (underline < 0) {
			base = name;
			sequence = 1;
		} else if (underline == length - 1) {
			base = name.substring(0, underline);
			sequence = 1;
		} else {
			try {
				sequence = Integer.parseInt(name.substring(underline + 1)) + 1;
				base = name.substring(0, underline);
			} catch (NumberFormatException e) {
				base = name;
				sequence = 1;
			}
		}
		do {
			name = NameUtl.buildName(base, sequence++, maxlen);
		} while (filter.accept(name));
		return name;
	}

	/**
	 * 字符串使用指定字符集的字节长度.
	 * 
	 * @param s
	 * @param cs
	 * @return
	 */
	public static final int length(String s, Charset cs) {
		try {
			CharsetEncoder cse = cs.newEncoder();
			return cse.encode(CharBuffer.wrap(s)).limit();
		} catch (CharacterCodingException e) {
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * 根据base,序号以及最大长度,重新构建名称
	 * 
	 * @param base
	 * @param order
	 * @param maxlen
	 * @return
	 */
	private static final String buildName(String base, int order, int maxlen) {
		if (maxlen == 0) {
			return base + '_' + order;
		} else {
			int numlen = NameUtl.stringSizeOfInt(order);
			if (numlen + 2 > maxlen) {
				throw new IllegalArgumentException("新字符串的最大长度过小");
			}
			int length = base.length();
			int newlen = length + 1 + numlen;
			if (newlen > maxlen) {
				base = base.substring(0, length - newlen + maxlen);
				return base + '_' + order;
			} else {
				return base + '_' + order;
			}
		}
	}

	private static final String build(String base, int seq, Charset cs,
			int maxlen, Filter<String> filter) {
		final int baselen = base.length();
		// byte length of each char
		final int[] charslen = new int[baselen];
		final CharsetEncoder cse = cs.newEncoder();
		int seqlen = NameUtl.stringSizeOfInt(seq);
		// TODO opt allocate length
		CharBuffer cb = CharBuffer.allocate(baselen + 1 + seqlen + 30);
		ByteBuffer bb = ByteBuffer.allocate((int) (baselen * cse.maxBytesPerChar()));
		if (maxlen - 1 - seqlen <= 0) {
			throw new IllegalArgumentException();
		}
		int i = 0;
		for (; i < baselen; i++) {
			final char c = base.charAt(i);
			cb.limit(i + 1);
			cb.put(i, c);
			cb.position(i);
			int before = bb.position();
			// encode each char into byte buffer
			cse.encode(cb, bb, false);
			int after = bb.position();
			charslen[i] = after - before;
			if (after + 1 + seqlen < maxlen) {
			} else if (after + 1 + seqlen == maxlen) {
				i++;
				break;
			} else if (i > 0) {
				break;
			} else {
				throw new IllegalArgumentException();
			}
		}
		int using = bb.position();
		// i represent '_' index, or valid base string length.
		String build;
		boolean first = true;
		do {
			if (first) {
				cb.limit(i + 1 + seqlen);
				cb.position(i);
				cb.append('_');
				cb.append(String.valueOf(seq));
				cb.position(0);
				build = cb.toString();
				first = false;
			} else {
				seq++;
				if (NameUtl.stringSizeOfInt(seq) == seqlen) {
					// seq length not changed
					cb.position(i + 1);
					cb.append(String.valueOf(seq));
					cb.position(0);
					build = cb.toString();
				} else if (NameUtl.stringSizeOfInt(seq) + 1 + using <= maxlen) {
					// seq length changed but still less than max length
					seqlen = NameUtl.stringSizeOfInt(seq);
					cb.limit(i + 1 + seqlen);
					cb.position(i + 1);
					cb.append(String.valueOf(seq));
					cb.position(0);
					build = cb.toString();
				} else {
					seqlen = NameUtl.stringSizeOfInt(seq);
					i--;
					using -= charslen[i];
					cb.position(i);
					cb.limit(i + 1 + seqlen);
					cb.append('_');
					cb.append(String.valueOf(seq));
					cb.position(0);
					build = cb.toString();
				}
			}
		} while (filter != null && filter.accept(build));
		return build;
	}

	/**
	 * 重命名,生成以下划线加自增序号结尾的新字符串.
	 * 
	 * @param name
	 *            原始名称,如果以下划线加数字结尾,则继续递增顺序.
	 * @param cs
	 *            使用的字符集
	 * @param mbl
	 *            最大字节长度
	 * @param filter
	 *            新名称过滤器.新名称使用满足使用该过滤器返回false.
	 * @return
	 */
	public static final String build(String name, Charset cs, int maxlen,
			Filter<String> filter) {
		final int nl = name.length();
		String base;
		int seq;
		int underline = name.lastIndexOf('_');
		if (underline < 0) {
			base = name;
			seq = 1;
		} else if (underline == nl - 1) {
			base = name.substring(0, underline);
			seq = 1;
		} else {
			try {
				seq = Integer.parseInt(name.substring(underline + 1)) + 1;
				base = name.substring(0, underline);
			} catch (NumberFormatException e) {
				base = name;
				seq = 1;
			}
		}
		return build(base, seq, cs, maxlen, filter);
	}

	public static final int stringSizeOfInt(int x) {
		for (int i = 0;; i++) {
			if (x <= NameUtl.sizeTable[i]) {
				return i + 1;
			}
		}
	}

	private final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };

	public static final int stringSizeOfLong(long x) {
		for (int i = 0;; i++) {
			if (x <= NameUtl.longTable[i]) {
				return i + 1;
			}
		}
	}

	private final static long[] longTable = { 9L, 99L, 999L, 9999L, 99999L, 999999L, 9999999L, 99999999L, 999999999L, 9999999999L, 99999999999L, 999999999999L, 9999999999999L, 99999999999999L, 999999999999999L, 9999999999999999L, 99999999999999999L, 999999999999999999L, Long.MAX_VALUE };
}
