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
 * �ռ��ģ��Ļ���
 * 
 * @author gaojingxin
 * 
 * @param <TParent>
 */
public abstract class ServiceBase<TContext extends Context> extends SpaceNode
		implements Bundleable {

	/**
	 * ����Bundle;
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
	 * ��ʼ���������������ظ÷�����ʼ��ģ��
	 * 
	 * @param context
	 */
	protected void init(Context context) throws Throwable {
	}

	/**
	 * �ͷŷ������������ظ÷����ͷ�ģ��
	 * 
	 * @param context
	 *            �����Ķ���
	 * @throws Throwable
	 */
	protected void dispose(Context context) throws Throwable {
	}

	/**
	 * �������������
	 * 
	 * @param context
	 *            ������
	 * @throws Throwable
	 */
	protected void resolveNativeDeclarator(Context context,
			NativeDeclaratorResolver resolver) throws Throwable {
	}

	/**
	 * �����ʼ��ʱ��Ҫ�õ��ĵ���
	 * 
	 * @param using
	 *            ��������������
	 */
	@Deprecated
	protected void initUsing(UsingDeclarator using) {

	}

	/**
	 * ��ó�ʼ�����ȼ��������ʼ�����������ȼ���С˳������
	 */
	protected float getPriority() {
		return 0.0f;
	}

	/**
	 * �¼�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TEvent>
	 *            �������¼�
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
	// ��ʾ�������һ���ķ���
	final static int METHODS_MASK = 1 << MAX_TASK_METHODS;
	static final int TASK_METHODS_MASK = -1 >> 32 - MAX_TASK_METHODS;

	private final static int getMethodMask(Enum<?> method) {
		if (method != null) {
			int ordinal = method.ordinal();
			if (ordinal > MAX_TASK_METHODS) {
				throw new IllegalArgumentException("����֧�ֳ���" + MAX_TASK_METHODS + "�ִ�����");
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
			throw new IllegalArgumentException("û����Ч����������");
		}
		if (c > 1) {
			mm |= METHODS_MASK;
		}
		return mm;
	}

	/**
	 * ��������ࡣ<br>
	 * ������Ա��Service��������ʵ����������ʱ��Ҫʵ��һ���޲����Ĺ��캯��������ϵͳ�޷���ȷʹ�ø���������
	 * ���齫���ô��벿��д��Service�У�����������ֻ����ʵ���������ķ�������<br>
	 * ǿ�ҽ��飺����ʮ�ֱ�Ҫ������ò�ҪΪһ����������д�����ֻ����һ�������ࡣ<br>
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TTask>
	 *            ��������
	 * @param <TMethod>
	 *            ����ķ�������
	 */
	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends ServiceInvokeeBase<TTask, TContext, None, None, None> {
		/**
		 * ׼�����񣬸�����һ����������ǰ�Ļ��ᡣ <br>
		 * ÿ�ε���Handleǰ����ø÷���������������ڸ÷�������������� <br>
		 * ����������������ڸ�����Ĵ������׼���ʹ����������׼���ʹ���Ҳ��ѭ��ͬ�Ļ���<br>
		 * ��prepare ����handle�з�������Service����ֱ�ӷ��ʣ�����ʹ��XXXService.this.YYY
		 * 
		 * @param context
		 *            ������
		 * @param task
		 *            ����
		 * @param method
		 *            ����Ĵ�����
		 */
		@Override
		protected void prepare(TContext context, TTask task) throws Throwable {
			// Ĭ�ϲ������κ���
		}

		/**
		 * �������񣬵�ĳ���������������󣬾ͽ���÷����� ��������Ҫʵ�ָ÷���������ɶԱ�����Ĵ��� ��prepare
		 * ����handle�з�������Service����ֱ�ӷ��ʣ�����ʹ��XXXService.this.YYY
		 * 
		 * @param context
		 *            ������
		 * @param task
		 *            ����
		 * @param method
		 *            ����Ĵ�����
		 */
		@Override
		protected abstract void handle(TContext context, TTask task)
				throws Throwable;

		/**
		 * ���캯��<br>
		 * 
		 * @param first
		 *            ��һ����Ҫ����ķ���
		 * @param otherMethods
		 *            ������Ҫ����ķ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
		 * ������ʼ
		 * 
		 * @param context
		 *            ������
		 * @return ����������״̬����
		 */
		protected abstract TBatchState beginBatch(TContext context)
				throws Throwable;

		/**
		 * ׼�����񣬸�����һ����������ǰ�Ļ��ᡣ <br>
		 * ÿ�ε���Handleǰ����ø÷���������������ڸ÷�������������� <br>
		 * ����������������ڸ�����Ĵ������׼���ʹ����������׼���ʹ���Ҳ��ѭ��ͬ�Ļ���<br>
		 * ��prepare ����handle�з�������Service����ֱ�ӷ��ʣ�����ʹ��XXXService.this.YYY
		 * 
		 * @param context
		 *            ������
		 * @param task
		 *            ����
		 * @param method
		 *            ����Ĵ�����
		 */
		@Override
		protected void prepare(TContext context, TTask task) throws Throwable {
			// Ĭ�ϲ������κ���
		}

		/**
		 * ��������������֮һ
		 * 
		 * @param context
		 *            ������
		 * @param task
		 *            �����������
		 * @param batchState
		 *            ������״̬����
		 */
		protected abstract boolean handle(TContext context, TTask task,
				TBatchState batchState) throws Throwable;

		/**
		 * ��������������������Դ
		 * 
		 * @param context
		 * @param batchState
		 *            ������״̬����
		 */
		protected void endBatch(TContext context, TBatchState batchState) {
		}

		/**
		 * ���캯��<br>
		 * 
		 * @param first
		 *            ��һ����Ҫ����ķ���
		 * @param otherMethods
		 *            ������Ҫ����ķ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ��
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 */
	protected abstract class ResultProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * ����������һ�ķ�������
		 * 
		 * @param context
		 *            ������
		 * @return ���ؽ��
		 * @throws Throwable
		 *             �����쳣
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
	 * ����ṩ��
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class OneKeyResultProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * ����������һ�ķ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @return ���ؽ��
		 * @throws Throwable
		 *             �����쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ��
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey1>
	 *            ��ѯƾ��1
	 * @param <TKey2>
	 *            ��ѯƾ��2
	 */
	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {

		/**
		 * ����������һ�ķ�������
		 * 
		 * @param context
		 *            ������
		 * @param key1
		 *            ��ѯƾ��1
		 * @param key2
		 *            ��ѯƾ��2
		 * @return ���ؽ��
		 * @throws Throwable
		 *             �����쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ��
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey1>
	 *            ��ѯƾ��1
	 * @param <TKey2>
	 *            ��ѯƾ��2
	 */
	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * ����������һ�ķ�������
		 * 
		 * @param context
		 *            ������
		 * @param key1
		 *            ��ѯƾ��1
		 * @param key2
		 *            ��ѯƾ��2
		 * @param key3
		 *            ��ѯƾ��3
		 * @return ���ؽ��
		 * @throws Throwable
		 *             �����쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 */
	protected abstract class ResultListProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class OneKeyResultListProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 */
	protected abstract class TreeNodeProvider<TResult> extends
			ServiceInvokeeBase<TResult, TContext, None, None, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
		 * @return resultTreeNode���������ṹ�еľ��Լ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceInvokeeBase<TResult, TContext, TKey, None, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
		 * @return resultTreeNode���������ṹ�еľ��Լ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, None> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
		 * @return resultTreeNode���������ṹ�еľ��Լ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ����ṩ�������ڸ���ĳ��������һ�������
	 * 
	 * @author gaojingxin
	 * 
	 * @param <TResult>
	 *            �������
	 * @param <TKey>
	 *            ��ѯƾ��
	 */
	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends ServiceInvokeeBase<TResult, TContext, TKey1, TKey2, TKey3> {
		/**
		 * ����ĳ����������һ�������
		 * 
		 * @param context
		 *            ������
		 * @param key
		 *            ��ѯƾ��
		 * @param resultList
		 *            �����
		 * @throws Throwable
		 *             ��������쳣
		 * @return resultTreeNode���������ṹ�еľ��Լ���
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
		 * ��ȡ�������
		 * 
		 * @param typeArgFinder
		 *            ģ����Ϣ
		 * @return ���ؿմ������ע��
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
	 * ��������ִ����
	 * 
	 * @author gaojingxin
	 * 
	 */
	protected abstract class CaseTester implements CaseTesterInstance {
		/**
		 * �������ش˷����ṩ�ò�������������
		 */
		public String getDescription() {
			return "";
		}

		/**
		 * �������ش˷����������������
		 */
		public String getName() {
			return this.getClass().getName();
		}

		/**
		 * ��������ı���
		 */
		public String getCode() {
			return this.code;
		}

		/**
		 * ��ȡ���ݿ����Ӷ���<br>
		 * �����Թرոö��󣬶���ʹ�øö��󴴽�������JDBC����Ҫ��ʱ�رպ��ͷţ�<br>
		 * ϵͳ������رպ��ͷ���ض���
		 * 
		 * @throws SQLException
		 */
		protected Connection getDBConnection(Context context)
				throws SQLException {
			return ContextImpl.toContext(context).getDBAdapter().testGetConnection();
		}

		/**
		 * ��������
		 * 
		 * @param context
		 *            ������
		 * @param testContext
		 *            ����������
		 * @param category
		 *            �������
		 */
		protected abstract void testCase(TContext context,
				TestContext testContext) throws Throwable;

		public CaseTester(String code) {
			this.code = code;
		}

		// /////////////////////////////////////////
		// ////// �ڲ�����
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
	 * ģ���״̬
	 */
	protected enum ServiceState {
		/**
		 * ����״̬
		 */
		CREATING,
		/**
		 * ����ע��
		 */
		REGISTERING,
		/**
		 * ע��ʧ��
		 */
		REGISTERERROR,
		/**
		 * ע�����
		 */
		REGISTERED,
		/**
		 * ��ʼ״̬
		 */
		INITIALIZING,
		/**
		 * ��ʼ������
		 */
		INITIALIZEERROR,
		/**
		 * ��ʼ�����
		 */
		INITIALIZED,
		/**
		 * ���ڼ���
		 */
		ACTIVING,
		/**
		 * ����ʱ����
		 */
		ACTIVEERROR,
		/**
		 * ��Ч״̬
		 */
		ACTIVED,
		/**
		 * ��ʼ����
		 */
		DISPOSING,
		/**
		 * �������
		 */
		DISPOSED,
	}

	/**
	 * ���ģ���״̬
	 * 
	 * @return ����״̬
	 */
	protected final ServiceState getState() {
		return this.state;
	}

	/**
	 * �����������еĵ�ǰģ����Ϣ����Դ��������Ҫ���أ��Ը��µ�ǰ��Դ������
	 */
	@Override
	SpaceNode updateContextSpace(ContextImpl<?, ?, ?> context) {
		SpaceNode occorAt = context.occorAt;
		context.occorAt = this.space;
		context.occorAtResourceService = null;
		return occorAt;
	}

	// ////////////////////////////////////////////////////////////////////////
	// ///////������ע�����
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * ����ע����
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
	 * ע���ڵ�ǰ��������Ч�����ĵ�����
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
	 * ģ���״̬
	 */
	ServiceState state = ServiceState.CREATING;

	/**
	 * ���Գ�ʼ��
	 * 
	 * @param context
	 *            ������
	 * @throws Throwable
	 *             �׳��쳣
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
			throw new IllegalStateException("���������״̬����");
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
//	// Ϊ���ݴ�eclipse�Ĺ���classPathԼ������EndPoint - > Object
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
//				// Ϊ���ݴ�eclipse�Ĺ���classPathԼ������EndPoint - > Object
//				((Endpoint) this.webServiceEndpoint).stop();
//			} catch (Throwable e) {
//				context.catcher.catchException(e, this.webServiceEndpoint);
//			} finally {
//				this.webServiceEndpoint = null;
//			}
//		}
//	}

	/**
	 * ��������
	 * 
	 * @param context
	 *            ������
	 * @throws Throwable
	 *             �׳��쳣
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
	 * ����������Դ����ĸ���Դ
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
