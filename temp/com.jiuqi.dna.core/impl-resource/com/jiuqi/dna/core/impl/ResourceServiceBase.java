package com.jiuqi.dna.core.impl;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.CacheDefine.AccessControlDefine;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefine;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefineOfOneValue;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefineOfThreeValue;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefineOfTwoValue;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefineOfZeroValue;
import com.jiuqi.dna.core.impl.CacheDefine.Provider;
import com.jiuqi.dna.core.impl.CacheDefine.PutPolicy;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.TypeArgFinder;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceQuerier;
import com.jiuqi.dna.core.resource.ResourceReferencePutter;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.resource.ResourceService.OperationMap;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.UsingDeclarator;
import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.type.GUID;

@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
public abstract class ResourceServiceBase<TFacade, TImplement extends TFacade, TKeysHolder>
		extends ServiceBase<ResourceContext<TFacade, TImplement, TKeysHolder>> {

	final KeyDefine<TFacade, TImplement, TKeysHolder>[] getKeyDefines() {
		final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?>[] providers = this.providers;
		if (providers == null || providers.length == 0) {
			return new KeyDefine[] { new KeyDefineOfZeroValue<TFacade, TImplement, TKeysHolder>(new SingletonResourceProvider() {

				@Override
				protected final void provide(
						final Context context,
						final ResourceInserter<TFacade, TImplement, TKeysHolder> setter)
						throws Throwable {
				}

			}) };
		} else {
			final int providerCount = providers.length;
			final KeyDefine[] keyDefines = new KeyDefine[providerCount];
			for (int index = 0, endIndex = providerCount; index < endIndex; index++) {
				final Provider provider = providers[index];
				if (provider == this.authorizableProvider) {
					keyDefines[index] = new AccessControlDefine<TFacade, TImplement, TKeysHolder>(this.authorizableProvider, this.authorizableProvider.defaultAuthority, this.authorizableProvider.getOperations());
				} else {
					switch (provider.getKeyValueCount()) {
					case 0:
						keyDefines[index] = new KeyDefineOfZeroValue<TFacade, TImplement, TKeysHolder>(provider);
						continue;
					case 1:
						keyDefines[index] = new KeyDefineOfOneValue<TFacade, TImplement, TKeysHolder>(provider, ((OneKeyResourceProvider) provider).value1Class);
						continue;
					case 2:
						final TwoKeyResourceProvider twoKeyProvider = (TwoKeyResourceProvider) provider;
						keyDefines[index] = new KeyDefineOfTwoValue<TFacade, TImplement, TKeysHolder>(provider, twoKeyProvider.value1Class, twoKeyProvider.value2Class);
						continue;
					case 3:
						final ThreeKeyResourceProvider threeKeyProvider = (ThreeKeyResourceProvider) provider;
						keyDefines[index] = new KeyDefineOfThreeValue<TFacade, TImplement, TKeysHolder>(provider, threeKeyProvider.value1Class, threeKeyProvider.value2Class, threeKeyProvider.value3Class);
						continue;
					default:
						throw new UnsupportedOperationException();
					}
				}
			}
			return keyDefines;
		}
	}

	final CacheDefine.ReferenceDefine<?>[] getReferenceDefines(
			final CacheDefine<TFacade, TImplement, TKeysHolder> cacheDefine) {
		final ReferenceDefine<?>[] referenceDefines = this.referenceDefines;
		if (referenceDefines == null) {
			return CacheDefine.EMPTY_REFERENCEDEFINES;
		} else {
			final int referenceDefineCount = referenceDefines.length;
			final Cache cache = cacheDefine.ownCache;
			final OperationEntry[] operationEntrys = cacheDefine.isAccessControlDefine() ? cacheDefine.accessControlDefine.operationEntrys : null;
			final CacheDefine.ReferenceDefine<?>[] cacheReferenceDefines = new CacheDefine.ReferenceDefine[referenceDefineCount];
			for (int index = 0, endIndex = referenceDefineCount; index < endIndex; index++) {
				final ReferenceDefine<?> referenceDefine = referenceDefines[index];
				final Operation<?>[] operationMap;
				if (operationEntrys == null) {
					operationMap = null;
				} else {
					operationMap = referenceDefine.getOperationMap(operationEntrys);
				}
				cacheReferenceDefines[index] = new CacheDefine.ReferenceDefine(cacheDefine, cache.getDefine(referenceDefine.referenceFacadeClass), operationMap, referenceDefine.getDefaultFilter(), referenceDefine.getDefaultComparator());
			}
			return cacheReferenceDefines;
		}
	}

	final Filter<? super TImplement> getDefaultFilter() {
		if (Utils.overridden(DEFAULTACCEPT_METHOD, this.getClass(), new Class<?>[] { ResourceService.class })) {
			return new Filter<TImplement>() {
				public final boolean accept(final TImplement item) {
					return ResourceServiceBase.this.defaultAccept(item);
				}
			};
		}
		return null;
	}

	final Comparator<? super TImplement> getDefaultComparator() {
		if (Utils.overridden(DEFAULTCOMPARE_METHOD, this.getClass(), new Class<?>[] { ResourceService.class })) {
			return new Comparator<TImplement>() {
				public final int compare(final TImplement o1,
						final TImplement o2) {
					return ResourceServiceBase.this.defaultSortCompare(o1, o2);
				}
			};
		}
		return null;
	}

	final void initializeClusterGroup(
			final CacheGroup<TFacade, TImplement, TKeysHolder> group,
			final Transaction transaction) throws Throwable {
		// DIST
		Filter<TFacade> filter = group.define.facadeClass == DistCacheFilterImpl.class ? null : this.getDistFilter(transaction.getCurrentContext());
		this.initResources(transaction.getCurrentContext(), new ClusterGroupInitializer(group, transaction, filter));
	}

	final void initializeNoneClusterGroup(
			final CacheGroup<TFacade, TImplement, TKeysHolder> group,
			final Transaction transaction) throws Throwable {
		// DIST
		Filter<TFacade> filter = group.define.facadeClass == DistCacheFilterImpl.class ? null : this.getDistFilter(transaction.getCurrentContext());
		this.initResources(transaction.getCurrentContext(), new NoneClusterGroupInitializer(group, filter));
	}

	// DIST
	private final Filter<TFacade> getDistFilter(ResourceQuerier querier) {
		final DistCacheFilterImpl fitler = querier.find(DistCacheFilterImpl.class, this.facadeClass);
		return fitler != null ? (Filter<TFacade>) (fitler.inner) : null;
	}

	// DIST
	final boolean isFilterExcluded(ResourceQuerier querier, TFacade facade) {
		if (this.facadeClass == DistCacheFilterImpl.class) {
			return false;
		}
		final Filter<TFacade> filter = this.getDistFilter(querier);
		return filter != null && !filter.accept(facade);
	}

	private static final Method DEFAULTACCEPT_METHOD;

	private static final Method DEFAULTCOMPARE_METHOD;

	private static final Method REFERENCE_DEFAULTACCEPT_METHOD;

	private static final Method REFERENCE_DEFAULTCOMPARE_METHOD;

	private static final Method BEFOREACCESSAUTHORITYRESOURCE_METHOD;

	private static final Method ENDACCESSAUTHORITYRESOURCE_METHOD;

	static {
		DEFAULTACCEPT_METHOD = Utils.getMethod(ResourceServiceBase.class, "defaultAccept", Object.class);
		DEFAULTCOMPARE_METHOD = Utils.getMethod(ResourceServiceBase.class, "defaultSortCompare", Object.class, Object.class);
		REFERENCE_DEFAULTACCEPT_METHOD = Utils.getMethod(ResourceServiceBase.ReferenceDefine.class, "accept", Object.class);
		REFERENCE_DEFAULTCOMPARE_METHOD = Utils.getMethod(ResourceServiceBase.ReferenceDefine.class, "compare", Object.class, Object.class);
		BEFOREACCESSAUTHORITYRESOURCE_METHOD = Utils.getMethod(ResourceServiceBase.class, "beforeAccessAuthorityResource", Context.class);
		ENDACCESSAUTHORITYRESOURCE_METHOD = Utils.getMethod(ResourceServiceBase.class, "endAccessAuthorityResource", Context.class);
	}

	protected ResourceServiceBase(final String title, final ResourceKind kind) {
		super(title);
		if (Cache.IN_DEBUG_MODE) {
			Cache.printInformation(kind + ": " + this.title + "(" + this.getClass().getName() + ")");
		}
		final Class<?>[] parameterClasses = TypeArgFinder.get(this.getClass(), ResourceServiceBase.class);
		this.facadeClass = parameterClasses[0];
		this.implementClass = parameterClasses[1];

		if (kind == ResourceKind.SINGLETON_IN_CLUSTER) {
			if (Site.clusterResourceModes != null && Site.clusterResourceModes.size() > 0 && Site.clusterResourceModes.get(this.facadeClass.getName()) != null && Site.clusterResourceModes.get(this.facadeClass.getName()).isQuirk()) { // 增加策略判断逻辑
				this.kind = ResourceKind.SINGLETON_IN_SITE;
				this.quirkMode = true;
			} else {
				this.kind = kind;
				this.quirkMode = false;
			}
		} else {
			this.kind = kind;
			this.quirkMode = false;
		}

		this.needExecuteBAARMethod = Utils.overridden(BEFOREACCESSAUTHORITYRESOURCE_METHOD, this.getClass(), new Class<?>[] { ResourceService.class });
		this.needExecuteEAARMethod = Utils.overridden(ENDACCESSAUTHORITYRESOURCE_METHOD, this.getClass(), new Class<?>[] { ResourceService.class });
	}

	protected abstract void initResources(Context context,
			ResourceInserter<TFacade, TImplement, TKeysHolder> initializer)
			throws Throwable;

	protected void initResourceReferences(
			Context context,
			ResourceReferencePutter<TFacade, TImplement, TKeysHolder> initializer) {
		// do nothing
	}

	protected void disposeResource(final TImplement value,
			final TKeysHolder keysHolder, final ExceptionCatcher catcher)
			throws Throwable {
		// do nothing
	}

	protected void beforeAccessAuthorityResource(final Context context) {
		// do nothing
	}

	protected void endAccessAuthorityResource(final Context context) {
		// do nothing
	}

	protected Object extractSerialUserData(final TImplement impl,
			final TKeysHolder keys) {
		return null;
	}

	protected void restoreSerialUserData(Object userData,
			final TImplement impl, final TKeysHolder keys, ObjectQuerier querier) {
		// do nothing
	}

	final Object internalExtractSerialUserData(final TImplement impl,
			final TKeysHolder keys) {
		try {
			return this.extractSerialUserData(impl, keys);
		} catch (Throwable e) {
			StringBuilder sb = new StringBuilder();
			sb.append("集群：资源服务[").append(this.getClass().getName()).append("]自定义序列化方法发生异常，正在处理的对象拥有以下键值：");
			for (CacheDefine.KeyDefine<TFacade, TImplement, TKeysHolder> kd : this.getCacheDefine().keyDefines) {
				try {
					String keyStr = kd.getKeyString(keys);
					sb.append("\r\n\t[").append(keyStr).append("]");
				} catch (Throwable ex) {
				}
			}
			String msg = sb.toString();
			Logger logger = DNALogManager.getLogger("core/cluster");
			logger.logFatal(null, msg, e, false);
			System.err.println(msg);
			e.printStackTrace();
		}
		return null;
	}

	final void internalRestoreSerialUserData(Object userData,
			final TImplement impl, final TKeysHolder keys, ObjectQuerier querier) {
		try {
			this.restoreSerialUserData(userData, impl, keys, querier);
		} catch (Throwable e) {
			StringBuilder sb = new StringBuilder();
			sb.append("集群：资源服务[").append(this.getClass().getName()).append("]自定义反序列化方法发生异常，正在处理的对象拥有以下键值：");
			for (CacheDefine.KeyDefine<TFacade, TImplement, TKeysHolder> kd : this.getCacheDefine().keyDefines) {
				try {
					String keyStr = kd.getKeyString(keys);
					sb.append("\r\n\t[").append(keyStr).append("]");
				} catch (Throwable ex) {
				}
			}
			String msg = sb.toString();
			Logger logger = DNALogManager.getLogger("core/cluster");
			logger.logFatal(null, msg, e, false);
			throw Utils.tryThrowException(e);
		}
	}

	protected boolean defaultAccept(final TImplement item) {
		throw new UnsupportedOperationException();
	}

	protected int defaultSortCompare(final TImplement a, final TImplement b) {
		throw new UnsupportedOperationException();
	}

	protected void registerCategory(final Object category, final String title) {
		if (title == null) {
			throw new NullArgumentException("title");
		}
		final Transaction transaction = this.site.newTransaction(TransactionKind.CACHE_INIT, null);
		transaction.directReplicate = true; // 恶心
		transaction.bindCurrentThread();
		boolean commit = true;
		try {
			super.site.cache.localCreateGroup(category, this.getCacheDefine(), title, transaction);
		} catch (Throwable e) {
			commit = false;
			transaction.getExceptionCatcher().catchException(e, null);
		} finally {
			try {
				transaction.finish(commit);
			} finally {
				transaction.dispose();
			}
		}
	}

	protected void unRegisterCategory(final Object category) {
		// DIST
		this.cacheDefine.checkModfiable(category);
		final Transaction transaction = this.site.newTransaction(TransactionKind.CACHE_INIT, null);
		transaction.directReplicate = true; // 恶心
		boolean commit = true;
		try {
			transaction.bindCurrentThread();
			try {
				super.site.cache.localRemoveGroup(this.getCacheDefine(), category, transaction);
			} catch (Throwable e) {
				commit = false;
				transaction.getExceptionCatcher().catchException(e, null);
			}
		} finally {
			try {
				transaction.finish(commit);
			} finally {
				transaction.dispose();
			}
		}
	}

	protected void initResourcesUsing(final UsingDeclarator using) {
		// do nothing
	}

	void ensureCacheDefine(final Cache cache) {
		if (this.cacheDefine == null) {
			synchronized (this) {
				if (this.cacheDefine == null) {
					this.cacheDefine = new CacheDefine<TFacade, TImplement, TKeysHolder>(cache, this);
				}
			}
		}
	}

	final CacheDefine<TFacade, TImplement, TKeysHolder> getCacheDefine() {
		if (this.cacheDefine == null) {
			throw new RuntimeException("外观类型为[" + this.facadeClass + "]缓存定义未初始化成功。");
		} else {
			return this.cacheDefine;
		}
	}

	final void ensureBuiltCacheDefine() {
		if (this.cacheDefine == null) {
			throw new RuntimeException("外观类型为[" + this.facadeClass + "]缓存定义未初始化成功。");
		}
	}

	final void callBeforeAccessAuthorityResource(
			final ContextImpl<?, ?, ?> context) {
		if (this.needExecuteBAARMethod) {
			context.beforeAccessAuthorityResource(this);
		}
	}

	final void callEndAccessAuthorityResource(final ContextImpl<?, ?, ?> context) {
		if (this.needExecuteEAARMethod) {
			context.endAccessAuthorityResource(this);
		}
	}

	// ==========================================================================

	@Override
	final SpaceNode updateContextSpace(final ContextImpl<?, ?, ?> context) {
		final SpaceNode occorAt = context.occorAt;
		context.occorAt = this;
		context.occorAtResourceService = (ResourceServiceBase) this;
		return occorAt;
	}

	@Override
	final boolean tryRegDeclaredClasses(final Class<?> serviceClass,
			final Class<?> declaredClass, Publish.Mode servicePublishMode,
			final ExceptionCatcher catcher) {
		if (super.tryRegDeclaredClasses(serviceClass, declaredClass, servicePublishMode, catcher)) {
			return true;
		}
		if (Provider.class.isAssignableFrom(declaredClass)) {
			try {
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider = (Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?>) (this.newObjectInNode(declaredClass, null, null));
				checkRepeat: {
					if (this.providers == null) {
						this.providers = new Provider[] { provider };
					} else {
						for (int index = 0, endIndex = this.providers.length; index < endIndex; index++) {
							Provider existProvider = this.providers[index];
							if (existProvider.equals(provider)) {
								final String providerName;
								if (AuthorizableResourceProvider.class.isAssignableFrom(declaredClass) && !AuthorizableResourceProvider.class.isAssignableFrom(existProvider.getClass())) {
									this.providers[index] = provider;
									this.authorizableProvider = (AuthorizableResourceProvider) provider;
									providerName = existProvider.getClass().getName();
								} else {
									providerName = provider.getClass().getName();
								}
								if (Application.IN_DEBUG_MODE) {
									Cache.printWarningMessage("资源服务:[" + this.getClass().getName() + "]重复定义了资源提供器:[" + providerName + "]");
								}
								break checkRepeat;
							}
						}
						int oldLength = this.providers.length;
						Provider[] newProviders = new Provider[oldLength + 1];
						System.arraycopy(this.providers, 0, newProviders, 0, oldLength);
						newProviders[oldLength] = provider;
						this.providers = newProviders;
					}
					if (AuthorizableResourceProvider.class.isAssignableFrom(declaredClass)) {
						this.authorizableProvider = (AuthorizableResourceProvider) provider;
					}
				}
				return true;
			} catch (Exception e) {
				catcher.catchException(e, this);
				this.state = ServiceBase.ServiceState.REGISTERERROR;
			}
		} else if (ReferenceDefine.class.isAssignableFrom(declaredClass)) {
			try {
				final ReferenceDefine<?> referenceDefine = (ReferenceDefine<?>) this.newObjectInNode(declaredClass, null, null);
				final Class<?> holderFacadeClass = referenceDefine.getHolderFacadeClass();
				final ResourceServiceBase holderResourceService = this.space.findResourceService(holderFacadeClass, InvokeeQueryMode.IN_SITE);
				if (holderResourceService == null) {
					throw new IllegalArgumentException("找不到外观类型为" + holderFacadeClass + "的资源服务");
				} else {
					holderResourceService.addReferenceDefine(referenceDefine);
				}
				return true;
			} catch (Exception e) {
				catcher.catchException(e, this);
				this.state = ServiceBase.ServiceState.REGISTERERROR;
			}
		}
		return false;
	}

	@Override
	final void doDispose(final ContextImpl<?, ?, ?> context) {
		switch (super.state) {
		case DISPOSING:
		case DISPOSED:
			return;
		}
		super.doDispose(context);
	}

	private final void addReferenceDefine(
			final ReferenceDefine<?> referenceDefine) {
		if (this.referenceDefines == null) {
			this.referenceDefines = new ReferenceDefine[] { referenceDefine };
		} else {
			checkRepeat: {
				for (ReferenceDefine existReferenceDefine : this.referenceDefines) {
					if (existReferenceDefine.referenceFacadeClass == referenceDefine.referenceFacadeClass) {
						System.err.println("警告：资源服务:[" + this.getClass().getName() + "]重复定义了资源引用:[" + existReferenceDefine.referenceFacadeClass + "]");
						break checkRepeat;
					}
				}
				int oldLength = this.referenceDefines.length;
				ReferenceDefine[] newReferenceDefines = new ReferenceDefine[oldLength + 1];
				System.arraycopy(this.referenceDefines, 0, newReferenceDefines, 0, oldLength);
				newReferenceDefines[oldLength] = referenceDefine;
				this.referenceDefines = newReferenceDefines;
			}
		}
	}

	final ResourceKind kind;

	final boolean quirkMode;

	final Class<?> facadeClass;

	final Class<?> implementClass;

	volatile CacheDefine<TFacade, TImplement, TKeysHolder> cacheDefine;

	private final boolean needExecuteBAARMethod;

	private final boolean needExecuteEAARMethod;

	private AuthorizableResourceProvider<?> authorizableProvider;

	private Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?>[] providers;

	private ReferenceDefine<?>[] referenceDefines;

	private static final class OperationMapImplement<TResourceFacade, TMapToResourceFacade>
			extends
			HashMap<Enum<? extends Operation<? super TResourceFacade>>, Enum<? extends Operation<? super TMapToResourceFacade>>>
			implements OperationMap<TResourceFacade, TMapToResourceFacade> {

		private static final long serialVersionUID = -7941497552280617881L;

		public OperationMapImplement() {
			// do nothing
		}

		public final void map(
				final Enum<? extends Operation<? super TResourceFacade>> operation,
				final Enum<? extends Operation<? super TMapToResourceFacade>> mapToOperation) {
			if (operation == null) {
				throw new NullArgumentException("operation");
			}
			if (mapToOperation == null) {
				throw new NullArgumentException("mapToOperation");
			}
			if (this.put(operation, mapToOperation) != null) {
				throw new IllegalArgumentException(operation + "重复映射");
			}
		}

	}

	protected abstract class SingletonResourceProvider extends
			Provider<TFacade, TImplement, TKeysHolder, None, None, None> {

		protected SingletonResourceProvider() {
			// do nothing
		}

		@Override
		protected void provide(Context context,
				ResourceInserter<TFacade, TImplement, TKeysHolder> setter)
				throws Throwable {
			// do nothing
		}

		@Override
		final boolean equals(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> other) {
			return true;
		}

		@Override
		final int getKeyValueCount() {
			return 0;
		}

		@Override
		final boolean haveCustomProvideMethod() {
			return Utils.overridden(Provider.PROVIDEMETHOD_ZEROKEYVALUE, this.getClass(), new Class<?>[] { SingletonResourceProvider.class });
		}

	}

	protected abstract class OneKeyResourceProvider<TKey> extends
			Provider<TFacade, TImplement, TKeysHolder, TKey, None, None> {

		protected OneKeyResourceProvider() {
			final Class<?>[] types = TypeArgFinder.get(this.getClass(), OneKeyResourceProvider.class);
			this.value1Class = types[0];
		}

		@Override
		protected abstract TKey getKey1(TKeysHolder keysHolder);

		@Override
		protected void provide(Context context,
				ResourceInserter<TFacade, TImplement, TKeysHolder> setter,
				TKey key) throws Throwable {
			// do nothing
		}

		@Override
		final boolean equals(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> other) {
			if (other instanceof OneKeyResourceProvider) {
				return this.value1Class == ((OneKeyResourceProvider) other).value1Class;
			} else {
				return other.getKeyValueCount() == 0;
			}
		}

		@Override
		final int getKeyValueCount() {
			return 1;
		}

		@Override
		final boolean haveCustomProvideMethod() {
			return Utils.overridden(Provider.PROVIDEMETHOD_ONEKEYVALUE, this.getClass(), new Class<?>[] { OneKeyResourceProvider.class });
		}

		final Class<?> value1Class;

	}

	protected abstract class TwoKeyResourceProvider<TKey1, TKey2> extends
			Provider<TFacade, TImplement, TKeysHolder, TKey1, TKey2, None> {

		protected TwoKeyResourceProvider() {
			final Class<?>[] types = TypeArgFinder.get(this.getClass(), TwoKeyResourceProvider.class);
			this.value1Class = types[0];
			this.value2Class = types[1];
		}

		@Override
		protected abstract TKey1 getKey1(TKeysHolder keysHolder);

		@Override
		protected abstract TKey2 getKey2(TKeysHolder keysHolder);

		@Override
		protected void provide(Context context,
				ResourceInserter<TFacade, TImplement, TKeysHolder> setter,
				TKey1 key1, TKey2 key2) throws Throwable {
			// do nothing
		}

		@Override
		final boolean equals(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> other) {
			if (other instanceof TwoKeyResourceProvider) {
				final TwoKeyResourceProvider twoKeyProvider = (TwoKeyResourceProvider) other;
				return this.value1Class == twoKeyProvider.value1Class && this.value2Class == twoKeyProvider.value2Class;
			} else {
				return other.getKeyValueCount() == 0;
			}
		}

		@Override
		final int getKeyValueCount() {
			return 2;
		}

		@Override
		final boolean haveCustomProvideMethod() {
			return Utils.overridden(Provider.PROVIDEMETHOD_TWOKEYVALUE, this.getClass(), new Class<?>[] { TwoKeyResourceProvider.class });
		}

		final Class<?> value1Class;

		final Class<?> value2Class;

	}

	protected abstract class ThreeKeyResourceProvider<TKey1, TKey2, TKey3>
			extends
			Provider<TFacade, TImplement, TKeysHolder, TKey1, TKey2, TKey3> {

		protected ThreeKeyResourceProvider() {
			final Class<?>[] types = TypeArgFinder.get(this.getClass(), ThreeKeyResourceProvider.class);
			this.value1Class = types[0];
			this.value2Class = types[1];
			this.value3Class = types[2];
		}

		@Override
		protected abstract TKey1 getKey1(TKeysHolder keysHolder);

		@Override
		protected abstract TKey2 getKey2(TKeysHolder keysHolder);

		@Override
		protected abstract TKey3 getKey3(TKeysHolder keysHolder);

		@Override
		protected void provide(Context context,
				ResourceInserter<TFacade, TImplement, TKeysHolder> setter,
				TKey1 key1, TKey2 key2, TKey3 key3) throws Throwable {
			// do nothing
		}

		@Override
		final boolean equals(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> other) {
			if (other instanceof ThreeKeyResourceProvider) {
				final ThreeKeyResourceProvider threeKeyProvider = (ThreeKeyResourceProvider) other;
				return this.value1Class == threeKeyProvider.value1Class && this.value2Class == threeKeyProvider.value2Class && this.value3Class == threeKeyProvider.value3Class;
			} else {
				return other.getKeyValueCount() == 0;
			}
		}

		@Override
		final int getKeyValueCount() {
			return 3;
		}

		@Override
		final boolean haveCustomProvideMethod() {
			return Utils.overridden(Provider.PROVIDEMETHOD_THREEKEYVALUE, this.getClass(), new Class<?>[] { ThreeKeyResourceProvider.class });
		}

		final Class<?> value1Class;

		final Class<?> value2Class;

		final Class<?> value3Class;

	}

	protected abstract class AuthorizableResourceProvider<TOperationEnum extends Enum<? extends Operation<? super TFacade>>>
			extends OneKeyResourceProvider<GUID> {

		protected AuthorizableResourceProvider(final GUID resourceCategoryID,
				final boolean looseAuthPolicy) {
			this.defaultAuthority = looseAuthPolicy;
		}

		@Override
		final String getAccessControlTitle(final TImplement value,
				final TKeysHolder keysHolder) {
			return this.getResourceTitle(value, keysHolder);
		}

		@Override
		protected abstract GUID getKey1(TKeysHolder keysHolder);

		protected abstract String getResourceTitle(TImplement resource,
				TKeysHolder keysHolder);

		final Operation<?>[] getOperations() {

			final Class<?> operationClass = TypeArgFinder.get(this.getClass(), AuthorizableResourceProvider.class, 0);
			final Object[] operationEnum = operationClass.getEnumConstants();
			final Operation<?>[] operations = new Operation<?>[operationEnum.length];
			for (int index = 0, endIndex = operationEnum.length; index < endIndex; index++) {
				operations[index] = (Operation<?>) (operationEnum[index]);
			}
			return operations;
		}

		final boolean defaultAuthority;

	}

	abstract class ReferenceDefine<TReferenceFacade> {

		ReferenceDefine() {
			this.referenceFacadeClass = this.getReferenceFacadeClass();
		}

		protected boolean accept(final TReferenceFacade item) {
			return true;
		}

		protected int compare(final TReferenceFacade a, final TReferenceFacade b) {
			return 0;
		}

		abstract Class<?> getHolderFacadeClass();

		abstract Class<?> getReferenceFacadeClass();

		abstract Operation<?>[] getOperationMap(OperationEntry[] operations);

		final Filter<? super TReferenceFacade> getDefaultFilter() {
			if (Utils.overridden(REFERENCE_DEFAULTACCEPT_METHOD, this.getClass(), null)) {
				return new Filter<TReferenceFacade>() {

					public final boolean accept(final TReferenceFacade item) {
						return ReferenceDefine.this.accept(item);
					}

				};
			} else {
				return null;
			}
		}

		final Comparator<? super TReferenceFacade> getDefaultComparator() {
			if (Utils.overridden(REFERENCE_DEFAULTCOMPARE_METHOD, this.getClass(), null)) {
				return new Comparator<TReferenceFacade>() {

					public final int compare(final TReferenceFacade o1,
							final TReferenceFacade o2) {
						return ReferenceDefine.this.compare(o1, o2);
					}

				};
			} else {
				return null;
			}
		}

		final Class<?> referenceFacadeClass;

	}

	protected abstract class ResourceReference<TReferenceFacade> extends
			ReferenceDefine<TReferenceFacade> {

		protected abstract void authMapOperation(
				final OperationMap<TFacade, TReferenceFacade> operationMap);

		@Override
		final Class<?> getHolderFacadeClass() {
			return ResourceServiceBase.this.facadeClass;
		}

		@Override
		final Class<?> getReferenceFacadeClass() {
			return TypeArgFinder.get(this.getClass(), ReferenceDefine.class, 0);
		}

		@Override
		final Operation<?>[] getOperationMap(final OperationEntry[] operations) {
			final OperationMapImplement<TFacade, TReferenceFacade> map = new OperationMapImplement<TFacade, TReferenceFacade>();
			this.authMapOperation(map);
			if (map.isEmpty()) {
				return null;
			} else {
				final Operation<?>[] operationMap = new Operation<?>[operations.length];
				for (int index = 0, endIndex = operations.length; index < endIndex; index++) {
					operationMap[index] = (Operation<?>) map.get(operations[index].operation);
				}
				return operationMap;
			}
		}

	}

	protected abstract class ReferredByResource<TReferredByFacade> extends
			ReferenceDefine<TReferredByFacade> {

		protected abstract void authMapOperation(
				OperationMap<TReferredByFacade, TFacade> operationMap);

		@Override
		final Class<?> getHolderFacadeClass() {
			return TypeArgFinder.get(this.getClass(), ReferenceDefine.class, 0);
		}

		@Override
		final Class<?> getReferenceFacadeClass() {
			return ResourceServiceBase.this.facadeClass;
		}

		@Override
		final Operation<?>[] getOperationMap(final OperationEntry[] operations) {
			final OperationMapImplement<TReferredByFacade, TFacade> map = new OperationMapImplement<TReferredByFacade, TFacade>();
			this.authMapOperation(map);
			if (map.isEmpty()) {
				return null;
			} else {
				final Operation<?>[] operationMap = new Operation<?>[operations.length];
				for (int index = 0, endIndex = operations.length; index < endIndex; index++) {
					operationMap[index] = (Operation<?>) map.get(operations[index].operation);
				}
				return operationMap;
			}
		}
	}

	private static final <TObject> boolean isFilterExclude(
			Filter<TObject> filter, TObject object) {
		return filter != null && !filter.accept(object);
	}

	private final class ClusterGroupInitializer implements
			ResourceInserter<TFacade, TImplement, TKeysHolder> {

		private ClusterGroupInitializer(
				final CacheGroup<TFacade, TImplement, TKeysHolder> group,
				final Transaction transaction, Filter<TFacade> filter) {
			this.group = group;
			this.transaction = transaction;
			this.filter = filter;// DIST
		}

		public final Object getCategory() {
			return CacheGroupSpace.isPreservedSpaceIdentifier(this.group.ownSpace.identifier) ? None.NONE : this.group.ownSpace.identifier;
		}

		public final ResourceToken<TFacade> putResource(
				final TImplement resource) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			// DIST
			if (isFilterExclude(this.filter, resource)) {
				return null;
			}
			return this.group.localCreateHolderWhenInitialize(resource, (TKeysHolder) resource, this.transaction);
		}

		public final ResourceToken<TFacade> putResource(
				final TImplement resource, final TKeysHolder keys) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			if (keys == null) {
				throw new NullArgumentException("keys");
			}// DIST
			if (isFilterExclude(this.filter, resource)) {
				return null;
			}
			return this.group.localCreateHolderWhenInitialize(resource, keys, this.transaction);
		}

		public final ResourceToken<TFacade> putResource(
				final ResourceToken<TFacade> treeParent,
				final TImplement resource) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			// DIST
			if (isFilterExclude(this.filter, resource)) {
				return null;
			}
			final CacheHolder<TFacade, TImplement, TKeysHolder> item = this.group.localCreateHolderWhenInitialize(resource, (TKeysHolder) resource, this.transaction);
			final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.group.getBindTree();
			tree.localCreateNodeWhenInitialize(treeParent == null ? null : (CacheHolder<TFacade, TImplement, TKeysHolder>) treeParent, item);
			return item;
		}

		public final ResourceToken<TFacade> putResource(
				final ResourceToken<TFacade> treeParent,
				final TImplement resource, final TKeysHolder keys) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			if (keys == null) {
				throw new NullArgumentException("keys");
			}
			// DIST
			if (isFilterExclude(this.filter, resource)) {
				return null;
			}
			final CacheHolder<TFacade, TImplement, TKeysHolder> item = this.group.localCreateHolderWhenInitialize(resource, keys, this.transaction);
			final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.group.getBindTree();
			tree.localCreateNodeWhenInitialize(treeParent == null ? null : (CacheHolder<TFacade, TImplement, TKeysHolder>) treeParent, item);
			return item;
		}

		public final void putResource(final ResourceToken<TFacade> treeParent,
				final ResourceToken<TFacade> child) {
			if (child == null) {
				throw new NullArgumentException("child");
			}
			final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.group.getBindTree();
			tree.localCreateNodeWhenInitialize(treeParent == null ? null : (CacheHolder<TFacade, TImplement, TKeysHolder>) treeParent, (CacheHolder<TFacade, TImplement, TKeysHolder>) child);
		}

		public final <THolderFacade> void putResourceReference(
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryCreateReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <THolderFacade> void putResourceReferenceBy(
				final ResourceToken<TFacade> holder,
				final ResourceToken<THolderFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryCreateReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <THolderFacade> void removeResourceReference(
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <TReferenceFacade> void removeResourceReferenceBy(
				final ResourceToken<TFacade> holder,
				final ResourceToken<TReferenceFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <THolderFacade> void removeResourceReference(
				final Operation<? super TFacade> operation,
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <TReferenceFacade> void removeResourceReferenceBy(
				final Operation<? super TReferenceFacade> operation,
				final ResourceToken<TFacade> holder,
				final ResourceToken<TReferenceFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		@Deprecated
		public final <TOwnerFacade> ResourceToken<TOwnerFacade> getOwnerResource(
				Class<TOwnerFacade> ownerFacadeClass) {
			throw new UnsupportedOperationException();
		}

		private final CacheGroup<TFacade, TImplement, TKeysHolder> group;

		private final Transaction transaction;

		private final Filter<TFacade> filter; // DIST

	}

	private final class NoneClusterGroupInitializer implements
			ResourceInserter<TFacade, TImplement, TKeysHolder> {

		// DIST
		private NoneClusterGroupInitializer(
				final CacheGroup<TFacade, TImplement, TKeysHolder> group,
				Filter<TFacade> filter) {
			this.group = group;
			this.filter = filter;
		}

		public final Object getCategory() {
			return CacheGroupSpace.isPreservedSpaceIdentifier(this.group.ownSpace.identifier) ? None.NONE : this.group.ownSpace.identifier;
		}

		public final ResourceToken<TFacade> putResource(
				final TImplement resource) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			// DIST
			if (isFilterExclude(this.filter, resource)) {
				return null;
			}
			return this.group.localCreateHolderAndCommit(resource, (TKeysHolder) resource);
		}

		public final ResourceToken<TFacade> putResource(
				final TImplement resource, final TKeysHolder keys) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			if (keys == null) {
				throw new NullArgumentException("keys");
			}
			// DIST
			if (isFilterExclude(this.filter, resource)) {
				return null;
			}
			return this.group.localCreateHolderAndCommit(resource, keys);
		}

		public final ResourceToken<TFacade> putResource(
				final ResourceToken<TFacade> treeParent,
				final TImplement resource) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			// DIST
			if (isFilterExclude(this.filter, resource)) {
				return null;
			}
			final CacheHolder<TFacade, TImplement, TKeysHolder> item = this.group.localCreateHolderAndCommit(resource, (TKeysHolder) resource);
			final CacheTree tree = this.group.getBindTree();
			tree.localCreateNodeAndCommit(treeParent == null ? null : (CacheHolder<?, ?, ?>) treeParent, item);
			return item;
		}

		public final ResourceToken<TFacade> putResource(
				final ResourceToken<TFacade> treeParent,
				final TImplement resource, final TKeysHolder keys) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			if (keys == null) {
				throw new NullArgumentException("keys");
			}
			// DIST
			if (isFilterExclude(this.filter, resource)) {
				return null;
			}
			final CacheHolder<TFacade, TImplement, TKeysHolder> item = this.group.localCreateHolderAndCommit(resource, keys);
			final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.group.getBindTree();
			tree.localCreateNodeAndCommit(treeParent == null ? null : (CacheHolder<TFacade, TImplement, TKeysHolder>) treeParent, item);
			return item;
		}

		public final void putResource(final ResourceToken<TFacade> treeParent,
				final ResourceToken<TFacade> child) {
			if (child == null) {
				throw new NullArgumentException("child");
			}
			final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.group.getBindTree();
			tree.localCreateNodeAndCommit(treeParent == null ? null : (CacheHolder<TFacade, TImplement, TKeysHolder>) treeParent, (CacheHolder<TFacade, TImplement, TKeysHolder>) child);
		}

		public final <THolderFacade> void putResourceReference(
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localCreateReferenceAndCommit((CacheHolder<?, ?, ?>) reference);
		}

		public final <THolderFacade> void putResourceReferenceBy(
				final ResourceToken<TFacade> holder,
				final ResourceToken<THolderFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localCreateReferenceAndCommit((CacheHolder<?, ?, ?>) reference);
		}

		public final <THolderFacade> void removeResourceReference(
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).removeReferenceAndCommit((CacheHolder<?, ?, ?>) reference);
		}

		public final <TReferenceFacade> void removeResourceReferenceBy(
				final ResourceToken<TFacade> holder,
				final ResourceToken<TReferenceFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).removeReferenceAndCommit((CacheHolder<?, ?, ?>) reference);
		}

		public final <THolderFacade> void removeResourceReference(
				final Operation<? super TFacade> operation,
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).removeReferenceAndCommit((CacheHolder<?, ?, ?>) reference);
		}

		public final <TReferenceFacade> void removeResourceReferenceBy(
				final Operation<? super TReferenceFacade> operation,
				final ResourceToken<TFacade> holder,
				final ResourceToken<TReferenceFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).removeReferenceAndCommit((CacheHolder<?, ?, ?>) reference);
		}

		@Deprecated
		public final <TOwnerFacade> ResourceToken<TOwnerFacade> getOwnerResource(
				Class<TOwnerFacade> ownerFacadeClass) {
			throw new UnsupportedOperationException();
		}

		private final CacheGroup<TFacade, TImplement, TKeysHolder> group;
		private final Filter<TFacade> filter; // DIST
	}

	final class HolderSetter implements
			ResourceInserter<TFacade, TImplement, TKeysHolder> {

		HolderSetter(final CacheGroup<TFacade, TImplement, TKeysHolder> group,
				final Transaction transaction) {
			this.group = group;
			this.transaction = transaction;
		}

		public final Object getCategory() {
			return CacheGroupSpace.isPreservedSpaceIdentifier(this.group.ownSpace.identifier) ? None.NONE : this.group.ownSpace.identifier;
		}

		public final ResourceToken<TFacade> putResource(
				final TImplement resource) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			return this.group.localTryCreateHolder(resource, (TKeysHolder) resource, PutPolicy.REPLACE, this.transaction);
		}

		public final ResourceToken<TFacade> putResource(
				final TImplement resource, final TKeysHolder keys) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			if (keys == null) {
				throw new NullArgumentException("keys");
			}
			return this.group.localTryCreateHolder(resource, keys, PutPolicy.REPLACE, this.transaction);
		}

		public final ResourceToken<TFacade> putResource(
				final ResourceToken<TFacade> treeParent,
				final TImplement resource) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			final CacheHolder<TFacade, TImplement, TKeysHolder> item = this.group.localTryCreateHolder(resource, (TKeysHolder) resource, PutPolicy.REPLACE, this.transaction);
			final CacheTree tree = this.group.getBindTree();
			tree.localTryCreateNode(treeParent == null ? null : (CacheHolder<?, ?, ?>) treeParent, item, this.transaction);
			return item;
		}

		public final ResourceToken<TFacade> putResource(
				final ResourceToken<TFacade> treeParent,
				final TImplement resource, final TKeysHolder keys) {
			if (resource == null) {
				throw new NullArgumentException("resource");
			}
			if (keys == null) {
				throw new NullArgumentException("keys");
			}
			final CacheHolder<TFacade, TImplement, TKeysHolder> item = this.group.localTryCreateHolder(resource, keys, PutPolicy.REPLACE, this.transaction);
			final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.group.getBindTree();
			tree.localTryCreateNode(treeParent == null ? null : (CacheHolder<TFacade, TImplement, TKeysHolder>) treeParent, item, this.transaction);
			return item;
		}

		public final void putResource(final ResourceToken<TFacade> treeParent,
				final ResourceToken<TFacade> child) {
			if (child == null) {
				throw new NullArgumentException("child");
			}
			final CacheTree<TFacade, TImplement, TKeysHolder> tree = this.group.getBindTree();
			tree.localTryCreateNode(treeParent == null ? null : (CacheHolder<TFacade, TImplement, TKeysHolder>) treeParent, (CacheHolder<TFacade, TImplement, TKeysHolder>) child, this.transaction);
		}

		public final <THolderFacade> void putResourceReference(
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryCreateReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <THolderFacade> void putResourceReferenceBy(
				final ResourceToken<TFacade> holder,
				final ResourceToken<THolderFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryCreateReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <THolderFacade> void removeResourceReference(
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <TReferenceFacade> void removeResourceReferenceBy(
				final ResourceToken<TFacade> holder,
				final ResourceToken<TReferenceFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <THolderFacade> void removeResourceReference(
				final Operation<? super TFacade> operation,
				final ResourceToken<THolderFacade> holder,
				final ResourceToken<TFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		public final <TReferenceFacade> void removeResourceReferenceBy(
				final Operation<? super TReferenceFacade> operation,
				final ResourceToken<TFacade> holder,
				final ResourceToken<TReferenceFacade> reference) {
			if (holder == null) {
				throw new NullArgumentException("holder");
			}
			if (reference == null) {
				throw new NullArgumentException("reference");
			}
			((CacheHolder<?, ?, ?>) holder).localTryRemoveReference((CacheHolder<?, ?, ?>) reference, this.transaction);
		}

		@Deprecated
		public final <TOwnerFacade> ResourceToken<TOwnerFacade> getOwnerResource(
				Class<TOwnerFacade> ownerFacadeClass) {
			throw new UnsupportedOperationException();
		}

		private final CacheGroup<TFacade, TImplement, TKeysHolder> group;

		private final Transaction transaction;

	}

	final AuthorizableResourceProvider<?> getAuthorizableProvider() {
		return this.authorizableProvider;
	}

}
