package com.jiuqi.dna.core.impl;

//import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
//import java.util.ArrayList;
import java.util.List;

//import javax.jws.WebService;
//import javax.xml.ws.Endpoint;
//
//import org.apache.cxf.BusFactory;
//import org.apache.cxf.jaxws.EndpointImpl;
//import org.apache.cxf.jaxws.JAXWSMethodInvoker;
//import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
//import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
//import org.apache.cxf.message.Exchange;
//import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.TreeNode;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceRef;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.TypeArgFinder;
import com.jiuqi.dna.core.model.ModelService;
//import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.service.NativeDeclaratorResolver;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Service;
import com.jiuqi.dna.core.service.UsingDeclarator;
import com.jiuqi.dna.core.spi.publish.Bundleable;
import com.jiuqi.dna.core.testing.CaseTesterInstance;
import com.jiuqi.dna.core.testing.TestContext;

/**
 * 空间和模块的基类
 * 
 * @author gaojingxin
 * 
 * @param <TParent>
 */
public abstract class ServiceBase<TContext extends Context> extends SpaceNode
		implements Bundleable {

	/**
	 * 所属Bundle;
	 */
	BundleStub bundle;
	final String title;
	ServiceBinding<?>[] bindings;

	public String getTitle() {
		return this.title;
	}

	protected ServiceBase(String title) {
		if (title == null || title.length() == 0) {
			throw new NullArgumentException("title");
		}
		this.title = title;
		this.bindings = ServiceBinding.createDefaults(this);
	}

	public final BundleStub getBundle() {
		return this.bundle;
	}

	/**
	 * 初始化方法，子类重载该方法初始化模块
	 * 
	 * @param context
	 */
	protected void init(Context context) throws Throwable {
	}

	/**
	 * 释放方法，子类重载该方法释放模块
	 * 
	 * @param context
	 *            上下文对象
	 * @throws Throwable
	 */
	protected void dispose(Context context) throws Throwable {
	}

	/**
	 * 解决本地声明器
	 * 
	 * @param context
	 *            上下文
	 * @throws Throwable
	 */
	protected void resolveNativeDeclarator(Context context,
			NativeDeclaratorResolver resolver) throws Throwable {
	}

	/**
	 * 定义初始化时需要用到的调用
	 * 
	 * @param using
	 *            调用依赖声明器
	 */
	@Deprecated
	protected void initUsing(UsingDeclarator using) {

	}

	/**
	 * 获得初始化优先级，服务初始化将按照优先级大小顺序启动
	 */
	protected float getPriority() {
		return 0.0f;
	}

	/**
	 * 事件监听器
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TEvent>
	 *            监听的事件
	 */
	protected abstract class EventListener<TEvent extends Event> extends
			EventListenerBase<TEvent, TContext, None, None, None> {

		protected EventListener(float priority) {
			super(priority);
		}

		@Override
		protected abstract void occur(TContext context, TEvent event)
				throws Throwable;

		@Override
		final boolean accept(Object key1, Object key2, Object key3) {
			return true;
		}

		@Override
		final boolean match(Class<?> key1Class1, Class<?> key2Class2,
				Class<?> key3Class3, int mask) {
			return mask == MASK_EVENT && key1Class1 == null && key2Class2 == null && key3Class3 == null;
		}

		@Override
		ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	protected abstract class OneKeyEventListener<TEvent extends Event, TKey1>
			extends EventListenerBase<TEvent, TContext, TKey1, None, None> {

		final Class<?> key1Class;

		protected OneKeyEventListener(float priority) {
			super(priority);
			this.key1Class = TypeArgFinder.get(this.getClass(), OneKeyEventListener.class, 1);
		}

		@Override
		final boolean match(Class<?> key1Class1, Class<?> key2Class2,
				Class<?> key3Class3, int mask) {
			return mask == MASK_EVENT && key1Class1 == this.key1Class && key2Class2 == null && key3Class3 == null;
		}

		@SuppressWarnings("unchecked")
		@Override
		final boolean accept(Object key1, Object key2, Object key3) {
			return this.accept((TKey1) key1);
		}

		protected abstract boolean accept(TKey1 key1);

		@Override
		ServiceBase<?> getService() {
			return ServiceBase.this;
		}

		@Override
		protected abstract void occur(TContext context, TEvent event)
				throws Throwable;
	}

	static final int MAX_TASK_METHODS = 25;
	// 表示处理多于一个的方法
	final static int METHODS_MASK = 1 << MAX_TASK_METHODS;
	static final int TASK_METHODS_MASK = -1 >> 32 - MAX_TASK_METHODS;

	private final static int getMethodMask(Enum<?> method) {
		if (method != null) {
			int ordinal = method.ordinal();
			if (ordinal > MAX_TASK_METHODS) {
				throw new IllegalArgumentException("任务不支持超过" + MAX_TASK_METHODS + "种处理方法");
			}
			return 1 << ordinal;
		}
		return 0;
	}

	static final int getMethodsMask(Enum<?> first, Enum<?>[] others) {
		int c = 0;
		int mm = getMethodMask(first);
		if (mm != 0) {
			c++;
		}
		if (others != null) {
			for (int i = 0; i < others.length; i++) {
				int m = getMethodMask(others[i]);
				if (m != 0) {
					mm |= m;
					c++;
				}
			}
		}
		if (c == 0) {
			throw new IllegalArgumentException("没有有效的任务处理方法");
		}
		if (c > 1) {
			mm |= METHODS_MASK;
		}
		return mm;
	}

	/**
	 * 任务处理基类。<br>
	 * 开发人员在Service的子类中实现任务处理类时需要实现一个无参数的构造函数，否则系统无法正确使用该任务处理类
	 * 建议将公用代码部分写在Service中，而任务处理类只负责实现任务具体的方法处理<br>
	 * 强烈建议：除非十分必要否则最好不要为一个任务的所有处理方法只定义一个处理类。<br>
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TTask>
	 *            任务类型
	 * @param <TMethod>
	 *            任务的方法类型
	 */
	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends ServiceInvokeeBase<TTask, TContext, None, None, None> {
		/**
		 * 准备任务，给任务一个子任务处理前的机会。 <br>
		 * 每次调用Handle前会调用该方法，处理类可以在该方法中添加子任务。 <br>
		 * 添加完的子任务会先于父任务的处理进行准备和处理，子任务的准备和处理也遵循相同的机制<br>
		 * 在prepare 或者handle中访问所述Service可以直接访问，或者使用XXXService.this.YYY
		 * 
		 * @param context
		 *            上下文
		 * @param task
		 *            任务
		 * @param method
		 *            任务的处理方法
		 */
		@Override
		protected void prepare(TContext context, TTask task) throws Throwable {
			// 默认不处理任何事
		}

		/**
		 * 处理任务，当某任务的子任务处理完后，就进入该方法。 处理器需要实现该方法，以完成对本任务的处理。 在prepare
		 * 或者handle中访问所述Service可以直接访问，或者使用XXXService.this.YYY
		 * 
		 * @param context
		 *            上下文
		 * @param task
		 *            任务
		 * @param method
		 *            任务的处理方法
		 */
		@Override
		protected abstract void handle(TContext context, TTask task)
				throws Throwable;

		/**
		 * 构造函数<br>
		 * 
		 * @param first
		 *            第一个需要处理的方法
		 * @param otherMethods
		 *            余下需要处理的方法
		 */
		protected TaskMethodHandler(TMethod first, TMethod[] otherMethods) {
			this.taskClass = TypeArgFinder.get(this.getClass(), TaskMethodHandler.class, 0);
			this.handleableMethodsMask = getMethodsMask(first, otherMethods);
		}

		// ///////////////////////////////////////////////////
		final int handleableMethodsMask;
		final Class<?> taskClass;

		@Override
		final boolean match(Class<?> key1Class1, Class<?> key2Class2,
				Class<?> key3Class3, int mask) {
			return (mask & MASKS_MASK) == MASK_TASK && (mask & this.handleableMethodsMask) != 0;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.taskClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	protected abstract class BatchTaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>, TBatchState>
			extends ServiceInvokeeBase<TTask, TContext, None, None, None> {
		/**
		 * 批处理开始
		 * 
		 * @param context
		 *            上下文
		 * @return 返回批处理状态对象
		 */
		protected abstract TBatchState beginBatch(TContext context)
				throws Throwable;

		/**
		 * 准备任务，给任务一个子任务处理前的机会。 <br>
		 * 每次调用Handle前会调用该方法，处理类可以在该方法中添加子任务。 <br>
		 * 添加完的子任务会先于父任务的处理进行准备和处理，子任务的准备和处理也遵循相同的机制<br>
		 * 在prepare 或者handle中访问所述Service可以直接访问，或者使用XXXService.this.YYY
		 * 
		 * @param context
		 *            上下文
		 * @param task
		 *            任务
		 * @param method
		 *            任务的处理方法
		 */
		@Override
		protected void prepare(TContext context, TTask task) throws Throwable {
			// 默认不处理任何事
		}

		/**
		 * 处理批处理其中之一
		 * 
		 * @param context
		 *            上下文
		 * @param task
		 *            待处理的任务
		 * @param batchState
		 *            批处理状态对象
		 */
		protected abstract boolean handle(TContext context, TTask task,
				TBatchState batchState) throws Throwable;

		/**
		 * 批处理结束，回收相关资源
		 * 
		 * @param context
		 * @param batchState
		 *            批处理状态对象
		 */
		protected void endBatch(TContext context, TBatchState batchState) {
		}

		/**
		 * 构造函数<br>
		 * 
		 * @param first
		 *            第一个需要处理的方法
		 * @param otherMethods
		 *            余下需要处理的方法
		 */
		protected BatchTaskMethodHandler(TMethod first, TMethod[] otherMethods) {
			this.taskClass = TypeArgFinder.get(this.getClass(), TaskMethodHandler.class, 0);
			this.handleableMethodsMask = getMethodsMask(first, otherMethods);
		}

		// ///////////////////////////////////////////////////
		final int handleableMethodsMask;
		final Class<?> taskClass;

		@Override
		final boolean match(Class<?> key1Class1, Class<?> key2Class2,
				Class<?> key3Class3, int mask) {
			return (mask & MASKS_MASK) == MASK_TASK && (mask & this.handleableMethodsMask) != 0;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.taskClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 结果提供器
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 */
	protected abstract class ResultProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * 根据条件单一的返回数据
		 * 
		 * @param context
		 *            上下文
		 * @return 返回结果
		 * @throws Throwable
		 *             错误异常
		 */
		@Override
		protected abstract TResult provide(TContext context) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;

		protected ResultProvider() {
			this.resultClass = TypeArgFinder.get(this.getClass(), ResultProvider.class, 0);
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_RESULT && key1Class == null;
		}

		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 结果提供器
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * 根据条件单一的返回数据
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @return 返回结果
		 * @throws Throwable
		 *             错误异常
		 */
		@Override
		protected abstract TResult provide(TContext context, TKey key)
				throws Throwable;

		final Class<?> resultClass;
		final Class<?> key1Class;

		protected OneKeyResultProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), OneKeyResultProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_RESULT && this.key1Class == key1Class && key2Class == null && key3Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey1>
	 *            查询凭据1
	 * @param <TKey2>
	 *            查询凭据2
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {

		/**
		 * 根据条件单一的返回数据
		 * 
		 * @param context
		 *            上下文
		 * @param key1
		 *            查询凭据1
		 * @param key2
		 *            查询凭据2
		 * @return 返回结果
		 * @throws Throwable
		 *             错误异常
		 */
		@Override
		protected abstract TResult provide(TContext context, TKey1 key1,
				TKey2 key2) throws Throwable;

		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;

		protected TwoKeyResultProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), TwoKeyResultProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_RESULT && this.key1Class == key1Class && this.key2Class == key2Class && key3Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey1>
	 *            查询凭据1
	 * @param <TKey2>
	 *            查询凭据2
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * 根据条件单一的返回数据
		 * 
		 * @param context
		 *            上下文
		 * @param key1
		 *            查询凭据1
		 * @param key2
		 *            查询凭据2
		 * @param key3
		 *            查询凭据3
		 * @return 返回结果
		 * @throws Throwable
		 *             错误异常
		 */
		@Override
		protected abstract TResult provide(TContext context, TKey1 key1,
				TKey2 key2, TKey3 key3) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;
		final Class<?> key3Class;

		protected ThreeKeyResultProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), ThreeKeyResultProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
			this.key3Class = types[3];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_RESULT && this.key1Class == key1Class && this.key2Class == key2Class && this.key3Class == key3Class;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 */
	protected abstract class ResultListProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 */
		@Override
		protected abstract void provide(TContext context,
				List<TResult> resultList) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;

		protected ResultListProvider() {
			this.resultClass = TypeArgFinder.get(this.getClass(), ResultListProvider.class, 0);

		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_LIST && key1Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 */
		@Override
		protected abstract void provide(TContext context, TKey key,
				List<TResult> resultList) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;

		protected OneKeyResultListProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), OneKeyResultListProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_LIST && this.key1Class == key1Class && key2Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 */
		@Override
		protected abstract void provide(TContext context, TKey1 key1,
				TKey2 key2, List<TResult> resultList) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;

		protected TwoKeyResultListProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), TwoKeyResultListProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_LIST && this.key1Class == key1Class && this.key2Class == key2Class && key3Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 */
		@Override
		protected abstract void provide(TContext context, TKey1 key1,
				TKey2 key2, TKey3 key3, List<TResult> resultList)
				throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;
		final Class<?> key3Class;

		protected ThreeKeyResultListProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), ThreeKeyResultListProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
			this.key3Class = types[3];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_LIST && this.key1Class == key1Class && this.key2Class == key2Class && this.key3Class == key3Class;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 */
	protected abstract class TreeNodeProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 * @return resultTreeNode在整个树结构中的绝对级次
		 */
		@Override
		protected abstract int provide(TContext context,
				TreeNode<TResult> resultTreeNode) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;

		protected TreeNodeProvider() {
			this.resultClass = TypeArgFinder.get(this.getClass(), TreeNodeProvider.class, 0);
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_TREE && key1Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 * @return resultTreeNode在整个树结构中的绝对级次
		 */
		@Override
		protected abstract int provide(TContext context, TKey key,
				TreeNode<TResult> resultTreeNode) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;

		protected OneKeyTreeNodeProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), OneKeyTreeNodeProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];

		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_TREE && this.key1Class == key1Class && key2Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 * @return resultTreeNode在整个树结构中的绝对级次
		 */
		@Override
		protected abstract int provide(TContext context, TKey1 key1,
				TKey2 key2, TreeNode<TResult> resultTreeNode) throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;

		protected TwoKeyTreeNodeProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), TwoKeyTreeNodeProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_TREE && this.key1Class == key1Class && this.key2Class == key2Class && key3Class == null;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 结果提供器，用于根据某条件返回一个结果集
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            结果类型
	 * @param <TKey>
	 *            查询凭据
	 */
	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * 根据某个条件返回一个结果集
		 * 
		 * @param context
		 *            上下文
		 * @param key
		 *            查询凭据
		 * @param resultList
		 *            结果集
		 * @throws Throwable
		 *             任务错误异常
		 * @return resultTreeNode在整个树结构中的绝对级次
		 */
		@Override
		protected abstract int provide(TContext context, TKey1 key1,
				TKey2 key2, TKey3 key3, TreeNode<TResult> resultTreeNode)
				throws Throwable;

		// ////////////////////////////
		final Class<?> resultClass;
		final Class<?> key1Class;
		final Class<?> key2Class;
		final Class<?> key3Class;

		protected ThreeKeyTreeNodeProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(), ThreeKeyTreeNodeProvider.class);
			this.resultClass = types[0];
			this.key1Class = types[1];
			this.key2Class = types[2];
			this.key3Class = types[3];

		}

		@Override
		final boolean match(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class, int mask) {
			return mask == MASK_TREE && this.key1Class == key1Class && this.key2Class == key2Class && this.key3Class == key3Class;
		}

		/**
		 * 获取标底类型
		 * 
		 * @param typeArgFinder
		 *            模块信息
		 * @return 返回空代表忽略注册
		 */
		@Override
		final Class<?> getTargetClass() {
			return this.resultClass;
		}

		@Override
		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}

	}

	/**
	 * 测试用例执行器
	 * 
	 * @author gaojingxin
	 * 
	 */
	protected abstract class CaseTester implements CaseTesterInstance {
		/**
		 * 子类重载此方法提供用测试用例的描述
		 */
		public String getDescription() {
			return "";
		}

		/**
		 * 子类重载此方法获得用例的名称
		 */
		public String getName() {
			return this.getClass().getName();
		}

		/**
		 * 获得用例的编码
		 */
		public String getCode() {
			return this.code;
		}

		/**
		 * 获取数据库连接对象，<br>
		 * 不可以关闭该对象，对于使用该对象创建的其他JDBC对象要及时关闭和释放，<br>
		 * 系统不负责关闭和释放相关对象。
		 * 
		 * @throws SQLException
		 */
		protected Connection getDBConnection(Context context)
				throws SQLException {
			return ContextImpl.toContext(context).getDBAdapter().testGetConnection();
		}

		/**
		 * 测试用例
		 * 
		 * @param context
		 *            上下文
		 * @param testContext
		 *            测试向下文
		 * @param category
		 *            用例类别
		 */
		protected abstract void testCase(TContext context,
				TestContext testContext) throws Throwable;

		public CaseTester(String code) {
			this.code = code;
		}

		// /////////////////////////////////////////
		// ////// 内部方法
		// /////////////////////////////////////////
		public final void test(Context context, TestContext testContext)
				throws Throwable {
			ContextImpl<?, ?, ?> cntxt = ContextImpl.toContext(context);
			cntxt.testCase(testContext, this);
		}

		final String code;

		final ServiceBase<?> getService() {
			return ServiceBase.this;
		}
	}

	/**
	 * 模块的状态
	 */
	protected enum ServiceState {
		/**
		 * 创建状态
		 */
		CREATING,
		/**
		 * 正在注册
		 */
		REGISTERING,
		/**
		 * 注册失败
		 */
		REGISTERERROR,
		/**
		 * 注册完成
		 */
		REGISTERED,
		/**
		 * 初始状态
		 */
		INITIALIZING,
		/**
		 * 初始化出错
		 */
		INITIALIZEERROR,
		/**
		 * 初始化完成
		 */
		INITIALIZED,
		/**
		 * 正在激活
		 */
		ACTIVING,
		/**
		 * 激活时出错
		 */
		ACTIVEERROR,
		/**
		 * 有效状态
		 */
		ACTIVED,
		/**
		 * 开始销毁
		 */
		DISPOSING,
		/**
		 * 销毁完毕
		 */
		DISPOSED,
	}

	/**
	 * 获得模块的状态
	 * 
	 * @return 返回状态
	 */
	protected final ServiceState getState() {
		return this.state;
	}

	/**
	 * 更新上下文中的当前模块信息，资源管理器需要重载，以更新当前资源管理器
	 */
	@Override
	SpaceNode updateContextSpace(ContextImpl<?, ?, ?> context) {
		SpaceNode occorAt = context.occorAt;
		context.occorAt = this.space;
		context.occorAtResourceService = null;
		return occorAt;
	}

	// ////////////////////////////////////////////////////////////////////////
	// ///////调用器注册相关
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * 子类注册用
	 */
	void afterRegInvokees(Publish.Mode servicePublishMode,
			ExceptionCatcher catcher) {
	}

	boolean tryRegDeclaredClasses(Class<?> serviceClass,
			Class<?> declaredClass, Publish.Mode servicePublishMode,
			ExceptionCatcher catcher) {
		if (ServiceInvokeeBase.class.isAssignableFrom(declaredClass)) {
			Publish.Mode publishMode = Publish.Mode.getMode(declaredClass, null);
			if (publishMode == null) {
				return false;
			} else if (publishMode == Publish.Mode.DEFAULT) {
				publishMode = servicePublishMode;
			}
			try {
				final ServiceInvokeeBase<?, ?, ?, ?, ?> invokee = (ServiceInvokeeBase<?, ?, ?, ?, ?>) this.newObjectInNode(declaredClass, null, null);
				invokee.publishMode = publishMode;
				final Class<?> targetClass = invokee.getTargetClass();
				if (targetClass != null) {
					this.space.regInvokee(targetClass, invokee, catcher);
					return true;
				}
			} catch (Throwable e) {
				catcher.catchException(e, this);
				this.state = ServiceState.REGISTERERROR;
			}
		}
		return false;
	}

	final static Class<?>[] endServiceClasses = { ServiceBase.class, ModelService.class, Service.class, ResourceService.class };

	private static final boolean validServiceClass(Class<?> serviceClass) {
		for (Class<?> endServiceClass : endServiceClasses) {
			if (serviceClass == endServiceClass) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 注册在当前服务中有效声明的调用器
	 * 
	 */
	final void regInvokees(Publish.Mode servicePublishMode,
			ExceptionCatcher catcher) {
		if (this.state == ServiceState.CREATING) {
			this.state = ServiceState.REGISTERING;
		}
		for (Class<?> serviceClass = this.getClass(); ServiceBase.validServiceClass(serviceClass); serviceClass = serviceClass.getSuperclass()) {
			Class<?>[] declaredClasses = serviceClass.getDeclaredClasses();
			for (int i = 0; i < declaredClasses.length; i++) {
				Class<?> declaredClass = declaredClasses[i];
				if ((declaredClass.getModifiers() & Modifier.ABSTRACT) != 0) {
					continue;
				}
				this.tryRegDeclaredClasses(serviceClass, declaredClass, servicePublishMode, catcher);
			}
		}
		this.afterRegInvokees(servicePublishMode, catcher);
		if (this.state == ServiceState.REGISTERING) {
			this.state = ServiceState.REGISTERED;
		}
	}

	final void allocCaseTesters(List<ServiceBase<?>.CaseTester> list) {
		for (Class<?> serviceClass = this.getClass(); ServiceBase.validServiceClass(serviceClass); serviceClass = serviceClass.getSuperclass()) {
			Class<?>[] declaredClasses = serviceClass.getDeclaredClasses();
			for (int i = 0; i < declaredClasses.length; i++) {
				Class<?> declaredClass = declaredClasses[i];
				if ((declaredClass.getModifiers() & Modifier.ABSTRACT) != 0) {
					continue;
				}
				if (CaseTester.class.isAssignableFrom(declaredClass)) {
					try {
						CaseTester tester = (CaseTester) this.newObjectInNode(declaredClass, null, null);
						list.add(tester);
					} catch (Throwable e) {
					}
				}
			}
		}
	}

	/**
	 * 模块的状态
	 */
	ServiceState state = ServiceState.CREATING;

	/**
	 * 尝试初始化
	 * 
	 * @param context
	 *            上下文
	 * @throws Throwable
	 *             抛出异常
	 */
	final boolean tryInit(ContextImpl<?, ?, ?> context) throws Throwable {
		if (this.state == ServiceState.REGISTERED) {
			this.state = ServiceState.INITIALIZING;
			try {
				context.initService(this);
				this.state = ServiceState.INITIALIZED;
			} catch (Throwable e) {
				this.state = ServiceState.INITIALIZEERROR;
				throw e;
			}
//			this.tryPublishWebService(context);
			if(bindings!=null) {
				for(int i=0; i<bindings.length; i++) {
					if(bindings[i]==null)
						continue;
					bindings[i].bind();
				}
			}
			return true;
		} else if (this.state == ServiceState.INITIALIZING) {
			throw new IllegalStateException("服务的启动状态错误");
		}
		return false;
	}
//
//	private final Object createEndpoint(String address) {
//		final JaxWsServerFactoryBean jwsfb = new JaxWsServerFactoryBean(new JaxWsServiceFactoryBean() {
//			private final boolean inResourceService = ServiceBase.this instanceof ResourceServiceBase<?, ?, ?>;
//
//			private boolean ignoreParam(Method method, int index) {
//				final Class<?> type = index == -1 ? method.getReturnType() : method.getParameterTypes()[index];
//				if (type == Context.class || (type == ResourceContext.class && this.inResourceService)) {
//					return true;
//				}
//				return false;
//			}
//
//			@Override
//			protected boolean isInParam(Method method, int index) {
//				if (this.ignoreParam(method, index)) {
//					return false;
//				}
//				return super.isInParam(method, index);
//			}
//
//			@Override
//			protected boolean isOutParam(Method method, int index) {
//				if (this.ignoreParam(method, index)) {
//					return false;
//				}
//				return super.isOutParam(method, index);
//			}
//		});
//
//		final EndpointImpl cxfEp = new EndpointImpl(BusFactory.getThreadDefaultBus(), this, jwsfb);
//		cxfEp.setInvoker(new JAXWSMethodInvoker(this) {
//			private ArrayList<Object> insertParams(ArrayList<Object> newParams,
//					Object[] paramArray, int pIndex, Object internalParam) {
//				if (newParams == null) {
//					newParams = new ArrayList<Object>();
//					for (int i = 0; i < pIndex; i++) {
//						newParams.add(paramArray[i]);
//					}
//				}
//				newParams.add(internalParam);
//				return newParams;
//			}
//
//			@Override
//			protected final Object performInvocation(Exchange exchange,
//					Object serviceObject, Method method, Object[] paramArray)
//					throws Exception {
//				Throwable ee = null;
//				DNAWSContextHolder contextHolder = null;
//				try {
//					final Class<?>[] paramTypes = method.getParameterTypes();
//					int pIndex = 0;
//					ArrayList<Object> newParams = null;
//					for (Class<?> pt : paramTypes) {
//						if (pt == org.apache.cxf.message.Exchange.class) {
//							newParams = this.insertParams(newParams, paramArray, pIndex, exchange);
//						} else if (pt == Context.class || pt == ResourceContext.class) {
//							if (contextHolder == null) {
//								contextHolder = new DNAWSContextHolder(ServiceBase.this);
//							}
//							newParams = this.insertParams(newParams, paramArray, pIndex, contextHolder.getContext());
//						} else if (newParams != null) {
//							newParams.add(paramArray[pIndex]);
//						}
//						pIndex++;
//					}
//					if (newParams != null) {
//						paramArray = newParams.toArray(new Object[newParams.size()]);
//					}
//					return method.invoke(serviceObject, paramArray);
//				} catch (Throwable e) {
//					throw Utils.tryThrowException(ee = e);
//				} finally {
//					if (contextHolder != null) {
//						contextHolder.dispose(ee);
//					}
//				}
//			}
//		});
//		cxfEp.publish(address);
//		return cxfEp;
//	}
//
//	// 为了容错eclipse的工程classPath约束，将EndPoint - > Object
//	private Object webServiceEndpoint;
//
//	private synchronized final void tryPublishWebService(
//			ContextImpl<?, ?, ?> context) {
//		if (!this.site.shared || this.webServiceEndpoint != null) {
//			return;
//		}
//		final Class<?> thisClass = this.getClass();
//		if ((thisClass.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
//			return;
//		}
//		final WebService wsAnnotation = thisClass.getAnnotation(WebService.class);
//		if (wsAnnotation == null) {
//			return;
//		}
//		try {
//			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//			try {
//				Thread.currentThread().setContextClassLoader(CXFNonSpringServlet.class.getClassLoader());
//				final String wsName = wsAnnotation.name();
//				final String address = "/".concat(wsName != null && wsName.length() > 0 ? wsName : thisClass.getSimpleName());
//				this.webServiceEndpoint = this.createEndpoint(address);
//			} finally {
//				Thread.currentThread().setContextClassLoader(classLoader);
//			}
//		} catch (Throwable e) {
//			context.catcher.catchException(e, this);
//		}
//	}
//
//	private synchronized final void tryStopWebService(
//			ContextImpl<?, ?, ?> context) {
//		if (this.webServiceEndpoint != null) {
//			try {
//				// 为了容错eclipse的工程classPath约束，将EndPoint - > Object
//				((Endpoint) this.webServiceEndpoint).stop();
//			} catch (Throwable e) {
//				context.catcher.catchException(e, this.webServiceEndpoint);
//			} finally {
//				this.webServiceEndpoint = null;
//			}
//		}
//	}

	/**
	 * 尝试销毁
	 * 
	 * @param context
	 *            上下文
	 * @throws Throwable
	 *             抛出异常
	 */
	@Override
	void doDispose(ContextImpl<?, ?, ?> context) {
		switch (this.state) {
		case DISPOSING:
		case DISPOSED:
			return;
		}
		this.state = ServiceState.DISPOSING;
		try {
//			this.tryStopWebService(context);
			if(bindings!=null) {
				for(int i=bindings.length-1; i>=0; i--) {
					if(bindings[i]==null)
						continue;
					bindings[i].unbind();
				}
			}
			context.disposeService(this);
		} catch (Throwable e) {
			context.catcher.catchException(e, this);
		} finally {
			this.state = ServiceState.DISPOSED;
		}
	}

	/**
	 * 尝试设置资源服务的父资源
	 */
	boolean trySetOwnerResourceService(
			ResourceServiceBase<?, ?, ?> ownerResourceService) {
		return false;
	}

	@Override
	final DataSourceRef tryGetDataSourceRef() {
		return this.space.tryGetDataSourceRef();
	}
}
