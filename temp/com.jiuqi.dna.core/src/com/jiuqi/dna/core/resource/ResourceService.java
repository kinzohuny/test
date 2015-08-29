package com.jiuqi.dna.core.resource;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.TreeNode;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.ResourceServiceBase;
import com.jiuqi.dna.core.impl.ServiceBase;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.GUID;

public abstract class ResourceService<TFacade, TImplement extends TFacade, TKeysHolder>
		extends ResourceServiceBase<TFacade, TImplement, TKeysHolder> {

	protected static final ResourceKind DEFAULT_RESOURCEKIND;

	protected static final ResourceToken<Object> MISSTOKEN;

	static {
		DEFAULT_RESOURCEKIND = ResourceKind.SINGLETON_IN_SITE;
		MISSTOKEN = new ResourceToken<Object>() {

			public final Object getCategory() {
				return null;
			}

			public final Class<Object> getFacadeClass() {
				throw new UnsupportedOperationException();
			}

			public final ResourceKind getKind() {
				throw new UnsupportedOperationException();
			}

			public final Object getFacade() throws DisposedException {
				throw new UnsupportedOperationException();
			}

			public final Object tryGetFacade() {
				return null;
			}

			public final ResourceToken<Object> getParent() {
				return null;
			}

			public final ResourceTokenLink<Object> getChildren() {
				return null;
			}

			@Deprecated
			public final <TSubFacade> ResourceTokenLink<TSubFacade> getSubTokens(
					final Class<TSubFacade> subTokenFacadeClass)
					throws IllegalArgumentException {
				throw new UnsupportedOperationException();
			}

			@Deprecated
			public final <TSuperFacade> ResourceToken<TSuperFacade> getSuperToken(
					final Class<TSuperFacade> superTokenFacadeClass)
					throws IllegalArgumentException {
				throw new UnsupportedOperationException();
			}

		};
	}

	protected ResourceService(final String title) {
		super(title, DEFAULT_RESOURCEKIND);
	}

	protected ResourceService(final String title, final ResourceKind kind) {
		super(title, kind);
	}

	/**
	 * 获得初始化优先级，服务初始化将按照优先级大小顺序启动
	 */
	@Override
	protected float getPriority() {
		return 0.0f;
	}

	@Override
	public final String getTitle() {
		return super.getTitle();
	}

	@Override
	protected void initResources(Context context,
			ResourceInserter<TFacade, TImplement, TKeysHolder> initializer)
			throws Throwable {
	}

	@Override
	protected void initResourceReferences(
			Context context,
			ResourceReferencePutter<TFacade, TImplement, TKeysHolder> initializer) {
	}

	@Override
	protected void disposeResource(TImplement value, TKeysHolder keysHolder,
			ExceptionCatcher catcher) throws Throwable {
	}

	@Override
	protected boolean defaultAccept(final TImplement item) {
		return true;
	}

	@Override
	protected int defaultSortCompare(final TImplement a, final TImplement b) {
		return 0;
	}

	@Override
	protected final void registerCategory(Object category, String title) {
		super.registerCategory(category, title);
	}

	@Override
	protected final void unRegisterCategory(Object category) {
		super.unRegisterCategory(category);
	}

	@Override
	protected void beforeAccessAuthorityResource(Context context) {

	}

	@Override
	protected void endAccessAuthorityResource(Context context) {

	}

	@Override
	protected Object extractSerialUserData(TImplement impl, TKeysHolder keys) {
		return null;
	}

	@Override
	protected void restoreSerialUserData(Object userData, TImplement impl,
			TKeysHolder keys, ObjectQuerier querier) {
	}

	public enum WhenExists {

		EXCEPTION,

		REPLACE,

		IGNORE

	}

	public interface OperationMap<TResourceFacade, TMapToResourceFacade> {

		public void map(
				Enum<? extends Operation<? super TResourceFacade>> operation,
				Enum<? extends Operation<? super TMapToResourceFacade>> mapToOperation);

	}

	protected abstract class AuthorizableResourceProvider<TOperationEnum extends Enum<? extends Operation<? super TFacade>>>
			extends
			ResourceServiceBase<TFacade, TImplement, TKeysHolder>.AuthorizableResourceProvider<TOperationEnum> {

		protected AuthorizableResourceProvider() {
			super(null, false);
		}

		protected AuthorizableResourceProvider(final boolean looseAuthPolicy) {
			super(null, looseAuthPolicy);
		}

		protected AuthorizableResourceProvider(final GUID resourceCategoryID) {
			this(resourceCategoryID, false);
		}

		protected AuthorizableResourceProvider(final GUID resourceCategoryID,
				final boolean looseAuthPolicy) {
			super(resourceCategoryID, looseAuthPolicy);
			if (resourceCategoryID == null) {
				throw new NullArgumentException("defaultCategoryID");
			}
		}

		@Override
		protected abstract GUID getKey1(TKeysHolder keys);

		@Override
		protected abstract String getResourceTitle(TImplement resource,
				TKeysHolder keysHolder);

	}

	protected abstract class SingletonResourceProvider
			extends
			ResourceServiceBase<TFacade, TImplement, TKeysHolder>.SingletonResourceProvider {

	}

	protected abstract class OneKeyResourceProvider<TKey>
			extends
			ResourceServiceBase<TFacade, TImplement, TKeysHolder>.OneKeyResourceProvider<TKey> {

		@Override
		protected abstract TKey getKey1(TKeysHolder keysHolder);

	}

	protected abstract class TwoKeyResourceProvider<TKey1, TKey2>
			extends
			ResourceServiceBase<TFacade, TImplement, TKeysHolder>.TwoKeyResourceProvider<TKey1, TKey2> {

		@Override
		protected abstract TKey1 getKey1(TKeysHolder keysHolder);

		@Override
		protected abstract TKey2 getKey2(TKeysHolder keysHolder);

	}

	protected abstract class ThreeKeyResourceProvider<TKey1, TKey2, TKey3>
			extends
			ResourceServiceBase<TFacade, TImplement, TKeysHolder>.ThreeKeyResourceProvider<TKey1, TKey2, TKey3> {

		@Override
		protected abstract TKey1 getKey1(TKeysHolder keysHolder);

		@Override
		protected abstract TKey2 getKey2(TKeysHolder keysHolder);

		@Override
		protected abstract TKey3 getKey3(TKeysHolder keysHolder);

	}

	protected abstract class ResourceReference<TRefFacade>
			extends
			ResourceServiceBase<TFacade, TImplement, TKeysHolder>.ResourceReference<TRefFacade> {

		@Override
		protected void authMapOperation(
				final OperationMap<TFacade, TRefFacade> operationMap) {
		}

	}

	protected abstract class ReferredByResource<TReferredByFacade>
			extends
			ResourceServiceBase<TFacade, TImplement, TKeysHolder>.ReferredByResource<TReferredByFacade> {

		@Override
		protected void authMapOperation(
				final OperationMap<TReferredByFacade, TFacade> operationMap) {
		}
	}

	protected abstract class EventListener<TEvent extends Event>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.EventListener<TEvent> {

		protected EventListener() {
			super(0f);
		}

		protected EventListener(float priority) {
			super(priority);
		}

		@Override
		protected abstract void occur(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TEvent event) throws Throwable;
	}

	protected abstract class OneKeyEventListener<TEvent extends Event, TKey1>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.OneKeyEventListener<TEvent, TKey1> {

		protected OneKeyEventListener() {
			super(0f);
		}

		protected OneKeyEventListener(float priority) {
			super(priority);
		}

		@Override
		protected abstract boolean accept(TKey1 key1);

		@Override
		protected abstract void occur(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TEvent event) throws Throwable;
	}

	protected abstract class TaskMethodHandler<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.TaskMethodHandler<TTask, TMethod> {

		protected TaskMethodHandler(TMethod first, TMethod... otherMethods) {
			super(first, otherMethods);
		}

		@Override
		protected abstract void handle(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TTask task) throws Throwable;

	}

	protected abstract class SimpleTaskMethodHandler<TTask extends SimpleTask>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.TaskMethodHandler<TTask, None> {

		protected SimpleTaskMethodHandler() {
			super(None.NONE, null);
		}

		@Override
		protected abstract void handle(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TTask task) throws Throwable;

	}

	protected abstract class ResultProvider<TResult>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.ResultProvider<TResult> {

		@Override
		protected abstract TResult provide(
				ResourceContext<TFacade, TImplement, TKeysHolder> context)
				throws Throwable;

	}

	protected abstract class ResultListProvider<TResult>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.ResultListProvider<TResult> {

		@Override
		protected abstract void provide(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				List<TResult> resultList) throws Throwable;

	}

	protected abstract class TreeNodeProvider<TResult> extends
			ServiceBase<Context>.TreeNodeProvider<TResult> {

		@Override
		protected abstract int provide(Context context,
				TreeNode<TResult> resultTreeNode) throws Throwable;

	}

	protected abstract class OneKeyResultProvider<TResult, TKey>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.OneKeyResultProvider<TResult, TKey> {

		@Override
		protected abstract TResult provide(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TKey key) throws Throwable;

	}

	protected abstract class OneKeyResultListProvider<TResult, TKey>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.OneKeyResultListProvider<TResult, TKey> {

		@Override
		protected abstract void provide(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TKey key, List<TResult> resultList) throws Throwable;

	}

	protected abstract class OneKeyTreeNodeProvider<TResult, TKey> extends
			ServiceBase<Context>.OneKeyTreeNodeProvider<TResult, TKey> {

		@Override
		protected abstract int provide(Context context, TKey key,
				TreeNode<TResult> resultTreeNode) throws Throwable;

	}

	protected abstract class TwoKeyResultProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.TwoKeyResultProvider<TResult, TKey1, TKey2> {

		@Override
		protected abstract TResult provide(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TKey1 key1, TKey2 key2) throws Throwable;

	}

	protected abstract class TwoKeyResultListProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.TwoKeyResultListProvider<TResult, TKey1, TKey2> {

		@Override
		protected abstract void provide(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TKey1 key1, TKey2 key2, List<TResult> resultList)
				throws Throwable;

	}

	protected abstract class TwoKeyTreeNodeProvider<TResult, TKey1, TKey2>
			extends
			ServiceBase<Context>.TwoKeyTreeNodeProvider<TResult, TKey1, TKey2> {

		@Override
		protected abstract int provide(Context context, TKey1 key, TKey2 key2,
				TreeNode<TResult> resultTreeNode) throws Throwable;

	}

	protected abstract class ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.ThreeKeyResultProvider<TResult, TKey1, TKey2, TKey3> {

		@Override
		protected abstract TResult provide(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TKey1 key1, TKey2 key2, TKey3 key3) throws Throwable;

	}

	protected abstract class ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.ThreeKeyResultListProvider<TResult, TKey1, TKey2, TKey3> {

		@Override
		protected abstract void provide(
				ResourceContext<TFacade, TImplement, TKeysHolder> context,
				TKey1 key1, TKey2 key2, TKey3 key3, List<TResult> resultList)
				throws Throwable;

	}

	protected abstract class ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3>
			extends
			ServiceBase<Context>.ThreeKeyTreeNodeProvider<TResult, TKey1, TKey2, TKey3> {

		@Override
		protected abstract int provide(Context context, TKey1 key, TKey2 key2,
				TKey3 key3, TreeNode<TResult> resultTreeNode) throws Throwable;

	}

	protected abstract class CaseTester
			extends
			ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>>.CaseTester {

		protected CaseTester(final String code) {
			super(code);
		}

		@Override
		public final String getCode() {
			return super.getCode();
		}
	}
}