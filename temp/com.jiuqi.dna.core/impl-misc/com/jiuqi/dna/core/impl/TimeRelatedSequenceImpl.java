package com.jiuqi.dna.core.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.jiuqi.dna.core.TimeRelatedSequence;

/**
 * ����������<br>
 * ���и�ʽ��<br>
 * 1. 63~29��35bit��ʱ�����䣬�������������ȷ��Ϊ32���롣 �������������ʱ����Ӱ��ʱ�����䣬�����ʱʹ�ó�ǰ��ʱ��<br>
 * 2. 28~5��24bit���������䣬��ʱ��������º������������´���������������������ʱ������ʱ�������λ��<br>
 * ����32�������þ�24λ�������������������ᷢ��,���㷢��Ҳ����Ӱ�����е�Ψһ�ԣ�<br>
 * 3. 4~0��5bit����Ⱥ�ڵ����ţ�Ϊ�˱��ⲻͬ��Ⱥ�ڵ㣨���32������������ͬ�����к�
 * 
 * @author gaojingxin
 * 
 */
public class TimeRelatedSequenceImpl extends AtomicLong implements
		TimeRelatedSequence {
	final static int hash(long seq) {
		return (int) ((seq >>> (TIME_ZOOM_SHIFT + 4)) ^ (seq >>> 4));
	}

	private final static long serialVersionUID = 1L;
	/**
	 * ʱ������24λ������64-24=40λ��ʾ��������Լ35��Ż�����ظ�
	 */
	static final int TIME_ZOOM_SHIFT = 24;
	/**
	 * 2009-1-1��ʱ��
	 */
	private final static long TIME_2009_1_1 = 0x11f2d6afc00L;
	/**
	 * ����ʱ�䣬���������Ʊ��
	 */
	private final static long TIME_CIRCLE = (TIME_2009_1_1 << TIME_ZOOM_SHIFT) >>> TIME_ZOOM_SHIFT;
	/**
	 * ������ʱ��ʧ��ʱ��
	 */
	private final static long TIME_LOST = TIME_2009_1_1 - TIME_CIRCLE;
	/**
	 * ���ƺ������ʱ��
	 */
	private final static long TIME_LOST2 = TIME_LOST + (1L << (64 - TIME_ZOOM_SHIFT));
	/**
	 * ���Ƚضϵ�(1024/128)=8����,����β����5λ
	 */
	private final static long TIME_PRECISION_MASK = -1L << NetClusterImpl.NODE_INDEX_LEN;
	/**
	 * �������������λ
	 */
	private final static long SEQ_INC_STEP = 1L << NetClusterImpl.NODE_INDEX_LEN;
	/**
	 * ���nλ��ʾ��ID
	 */
	private final int clusterIndex;

	public TimeRelatedSequenceImpl(int clusterIndex) {
		if (clusterIndex < 0 || NetClusterImpl.MAX_NODE_INDEX < clusterIndex) {
			throw new IllegalArgumentException("clusterIndex(" + clusterIndex + ") ������[0.." + NetClusterImpl.MAX_NODE_INDEX + "]���䷶Χ��!");
		}
		this.clusterIndex = clusterIndex;
		this.set(this.nextTimeFirst());
	}

	private final long nextTimeFirst() {
		return ((System.currentTimeMillis() & TIME_PRECISION_MASK) << TIME_ZOOM_SHIFT) + this.clusterIndex;
	}

	public final long last() {
		return super.get();
	}

	public final long next() {
		long nextTimeFirst = this.nextTimeFirst();
		long last, next;
		// no lock no wait
		do {
			last = super.get();
			next = last + SEQ_INC_STEP;
			if (next < nextTimeFirst) {
				next = nextTimeFirst;
			}
		} while (!super.compareAndSet(last, next));
		return next;
	}

	public final static long timeOf(long seq) {
		long timePart = ((seq >>> TIME_ZOOM_SHIFT) & TIME_PRECISION_MASK);
		if (timePart > TIME_CIRCLE) {
			return timePart + TIME_LOST;
		} else {
			return timePart + TIME_LOST2;
		}
	}

	static class HelperImpl implements Helper {
		public final long timeOf(long seq) {
			return TimeRelatedSequenceImpl.timeOf(seq);
		}
	}

	public static final HelperImpl helper = new HelperImpl();
}
