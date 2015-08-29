package com.jiuqi.dna.core.service;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.impl.ServiceBase;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.invoke.Task;

/**
 * 模块基类
 * 
 * @author gaojingxin
 * 
 */
public abstract class Service extends ServiceBase<Context> {

	/**
	 * 获得服务的标题
	 */
	@Override
	public final String getTitle() {
		return super.getTitle();
	}

	/**
	 * 构造方法，提供给子类指定服务的标题所用<br>
	 * 例如：
	 * 
	 * <pre>
	 * class MyService extends Service {
	 * 	MyService() {
	 * 		super(&quot;我的服务&quot;);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param title
	 *            指定服务的标题，作为框架监控和管理这些服务时该服务易读的标识。
	 */
	protected Service(String title) {
		super(title);
	}

	/**
	 * 获得初始化优先级，服务初始化将按照优先级大小顺序启动
	 */
	@Override
	protected float getPriority() {
		return 0.0f;
	}

	/**
	 * 事件监听器
	 */
	protected abstract class EventListener<TEvent extends Event> extends
			ServiceBase<Context>.EventListener<TEvent> {

		/**
		 * 构造函数
		 * 
		 * 默认的执行优先级
		 */
		protected EventListener() {
			super(0f);
		}

		/**
		 * 构造函数
		 * 
		 * @param priority
		 *            执行优先级，越小的越先执行
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
	 * 任务处理基类。
	 * 
	 * <p>
	 * 开发人员在Service的子类中实现任务处理类时需要实现一个无参数的构造函数，否则系统无法正确使用该任务处理类
	 * 建议将公用代码部分写在Service中，而任务处理类只负责实现任务具体的方法处理<br>
	 * 强烈建议：除非十分必要否则最好不要为一个任务的所有处理方法只定义一个处理类。<br>
	 */
	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends ServiceBase<Context>.TaskMethodHandler<TTask, TMethod> {

		protected TaskMethodHandler(TMethod first, TMethod... otherMethods) {
			super(first, otherMethods);
		}
	}

	/**
	 * 简单任务处理器基类
	 * 
	 * @author houchunlei
	 * 
	 * @param <TTask>
	 *            任务类型
	 */
	protected abstract class SimpleTaskMethodHandler<TTask extends SimpleTask>
			extends ServiceBase<Context>.TaskMethodHandler<TTask, None> {

		protected SimpleTaskMethodHandler() {
			super(None.NONE, null);
		}
	}

	/**
	 * 单例结果提供器
	 * 
	 * @author houchunlei
	 * 
	 * @param <TResult>
	 *            结果类型
	 */
	protected abstract class ResultProvider<TResult> extends
			ServiceBase<Context>.ResultProvider<TResult> {
	}

	/**
	 * 单键结果提供器
	 * 
	 * @author houchunlei
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            键类型
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyResultProvider<TResult, TKey> {
	}

	/**
	 * 双键结果提供器
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
	 * 三键结果提供器
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
	 * 结果集（列表形式）提供器，用于返回一个结果集。
	 */
	protected abstract class ResultListProvider<TResult> extends
			ServiceBase<Context>.ResultListProvider<TResult> {
	}

	/**
	 * 单键结果集（列表形式）提供器，用于根据指定的条件返回一个结果集。
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyResultListProvider<TResult, TKey> {
	}

	/**
	 * 双键结果集（列表形式）提供器，用于根据指定的条件返回一个结果集。
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {
	}

	/**
	 * 三键结果集（列表形式）提供器，用于根据指定的条件返回一个结果集
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3> {
	}

	/**
	 * 树结构提供器，用于返回一个树结构
	 */
	protected abstract class TreeNodeProvider<TResult> extends
			ServiceBase<Context>.TreeNodeProvider<TResult> {
	}

	/**
	 * 单键树结构提供器，用于根据指定的条件返回一个树结构
	 */
	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyTreeNodeProvider<TResult, TKey> {
	}

	/**
	 * 双键树结构提供器，用于根据指定的条件返回一个树结构
	 */
	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyTreeNodeProvider<TResult, TKey1, TKey2> {
	}

	/**
	 * 三键树结构提供器，用于根据指定的条件返回一个树结构
	 */
	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3> {
	}

	/**
	 * 测试用例执行器
	 * 
	 * @author gaojingxin
	 * 
	 */
	protected abstract class CaseTester extends ServiceBase<Context>.CaseTester {
		/**
		 * 构造方法
		 * 
		 * @param order
		 *            测试用例的顺序号,便于执行时确定先后顺序
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