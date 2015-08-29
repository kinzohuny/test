package com.jiuqi.dna.core.service;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.impl.ServiceBase;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.invoke.Task;

/**
 * ģ�����
 * 
 * @author gaojingxin
 * 
 */
public abstract class Service extends ServiceBase<Context> {

	/**
	 * ��÷���ı���
	 */
	@Override
	public final String getTitle() {
		return super.getTitle();
	}

	/**
	 * ���췽�����ṩ������ָ������ı�������<br>
	 * ���磺
	 * 
	 * <pre>
	 * class MyService extends Service {
	 * 	MyService() {
	 * 		super(&quot;�ҵķ���&quot;);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param title
	 *            ָ������ı��⣬��Ϊ��ܼ�غ͹�����Щ����ʱ�÷����׶��ı�ʶ��
	 */
	protected Service(String title) {
		super(title);
	}

	/**
	 * ��ó�ʼ�����ȼ��������ʼ�����������ȼ���С˳������
	 */
	@Override
	protected float getPriority() {
		return 0.0f;
	}

	/**
	 * �¼�������
	 */
	protected abstract class EventListener<TEvent extends Event> extends
			ServiceBase<Context>.EventListener<TEvent> {

		/**
		 * ���캯��
		 * 
		 * Ĭ�ϵ�ִ�����ȼ�
		 */
		protected EventListener() {
			super(0f);
		}

		/**
		 * ���캯��
		 * 
		 * @param priority
		 *            ִ�����ȼ���ԽС��Խ��ִ��
		 */
		protected EventListener(float priority) {
			super(priority);
		}

		@Override
		protected abstract void occur(Context context, TEvent event)
				throws Throwable;
	}

	protected abstract class OneKeyEventListener<TEvent extends Event, TKey1>
			extends ServiceBase<Context>.OneKeyEventListener<TEvent, TKey1> {

		protected OneKeyEventListener() {
			super(0f);
		}

		protected OneKeyEventListener(float priority) {
			super(priority);
		}

		@Override
		protected abstract boolean accept(TKey1 key1);

		@Override
		protected abstract void occur(Context context, TEvent event)
				throws Throwable;
	}

	/**
	 * ��������ࡣ
	 * 
	 * <p>
	 * ������Ա��Service��������ʵ����������ʱ��Ҫʵ��һ���޲����Ĺ��캯��������ϵͳ�޷���ȷʹ�ø���������
	 * ���齫���ô��벿��д��Service�У�����������ֻ����ʵ���������ķ�������<br>
	 * ǿ�ҽ��飺����ʮ�ֱ�Ҫ������ò�ҪΪһ����������д�����ֻ����һ�������ࡣ<br>
	 */
	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends ServiceBase<Context>.TaskMethodHandler<TTask, TMethod> {

		protected TaskMethodHandler(TMethod first, TMethod... otherMethods) {
			super(first, otherMethods);
		}
	}

	/**
	 * ��������������
	 * 
	 * @author houchunlei
	 * 
	 * @param <TTask>
	 *            ��������
	 */
	protected abstract class SimpleTaskMethodHandler<TTask extends SimpleTask>
			extends ServiceBase<Context>.TaskMethodHandler<TTask, None> {

		protected SimpleTaskMethodHandler() {
			super(None.NONE, null);
		}
	}

	/**
	 * ��������ṩ��
	 * 
	 * @author houchunlei
	 * 
	 * @param <TResult>
	 *            �������
	 */
	protected abstract class ResultProvider<TResult> extends
			ServiceBase<Context>.ResultProvider<TResult> {
	}

	/**
	 * ��������ṩ��
	 * 
	 * @author houchunlei
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ������
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyResultProvider<TResult, TKey> {
	}

	/**
	 * ˫������ṩ��
	 * 
	 * @author houchunlei
	 * 
	 * @param <TResult>
	 * @param <TKey1>
	 * @param <TKey2>
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyResultProvider<TResult, TKey1, TKey2> {
	}

	/**
	 * ��������ṩ��
	 * 
	 * @author houchunlei
	 * 
	 * @param <TResult>
	 * @param <TKey1>
	 * @param <TKey2>
	 * @param <TKey3>
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3> {
		// nothing to do.
	}

	/**
	 * ��������б���ʽ���ṩ�������ڷ���һ���������
	 */
	protected abstract class ResultListProvider<TResult> extends
			ServiceBase<Context>.ResultListProvider<TResult> {
	}

	/**
	 * ������������б���ʽ���ṩ�������ڸ���ָ������������һ���������
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyResultListProvider<TResult, TKey> {
	}

	/**
	 * ˫����������б���ʽ���ṩ�������ڸ���ָ������������һ���������
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {
	}

	/**
	 * ������������б���ʽ���ṩ�������ڸ���ָ������������һ�������
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3> {
	}

	/**
	 * ���ṹ�ṩ�������ڷ���һ�����ṹ
	 */
	protected abstract class TreeNodeProvider<TResult> extends
			ServiceBase<Context>.TreeNodeProvider<TResult> {
	}

	/**
	 * �������ṹ�ṩ�������ڸ���ָ������������һ�����ṹ
	 */
	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyTreeNodeProvider<TResult, TKey> {
	}

	/**
	 * ˫�����ṹ�ṩ�������ڸ���ָ������������һ�����ṹ
	 */
	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyTreeNodeProvider<TResult, TKey1, TKey2> {
	}

	/**
	 * �������ṹ�ṩ�������ڸ���ָ������������һ�����ṹ
	 */
	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3> {
	}

	/**
	 * ��������ִ����
	 * 
	 * @author gaojingxin
	 * 
	 */
	protected abstract class CaseTester extends ServiceBase<Context>.CaseTester {
		/**
		 * ���췽��
		 * 
		 * @param order
		 *            ����������˳���,����ִ��ʱȷ���Ⱥ�˳��
		 */
		public CaseTester(String code) {
			super(code);
		}

		@Override
		public final String getCode() {
			return super.getCode();
		}
	}
}