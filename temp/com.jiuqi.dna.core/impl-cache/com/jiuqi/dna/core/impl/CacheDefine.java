package com.jiuqi.dna.core.impl;

import java.lang.reflect.Method;
import java.util.Comparator;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.DistCacheBarrierGatherer.Barrier;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.misc.HashUtil;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceService.WhenExists;
import com.jiuqi.dna.core.type.GUID;

public class CacheDefine<TFacade, TImplement extends TFacade, TKeysHolder> {

	enum PutPolicy {

		IGNORE,

		THROW_EXCEPTION,

		REPLACE
	}

	static final class WhenExistPolicyPutPolicyTranslator {

		static final PutPolicy toPutPolicy(final WhenExists policy) {
			switch (policy) {
			case IGNORE:
				return PutPolicy.IGNORE;
			case REPLACE:
				return PutPolicy.REPLACE;
			case EXCEPTION:
				return PutPolicy.THROW_EXCEPTION;
			default:
				throw new UnsupportedOperationException();
			}
		}

	}

	static abstract class Provider<TFacade, TImplement extends TFacade, TKeysHolder, TKeyValue1, TKeyValue2, TKeyValue3> {

		static final Method PROVIDEMETHOD_ZEROKEYVALUE;

		static final Method PROVIDEMETHOD_ONEKEYVALUE;

		static final Method PROVIDEMETHOD_TWOKEYVALUE;

		static final Method PROVIDEMETHOD_THREEKEYVALUE;

		static {
			PROVIDEMETHOD_ZEROKEYVALUE = Utils.getMethod(Provider.class, "provide", Context.class, ResourceInserter.class);
			PROVIDEMETHOD_ONEKEYVALUE = Utils.getMethod(Provider.class, "provide", Context.class, ResourceInserter.class, Object.class);
			PROVIDEMETHOD_TWOKEYVALUE = Utils.getMethod(Provider.class, "provide", Context.class, ResourceInserter.class, Object.class, Object.class);
			PROVIDEMETHOD_THREEKEYVALUE = Utils.getMethod(Provider.class, "provide", Context.class, ResourceInserter.class, Object.class, Object.class, Object.class);
		}

		Provider() {
			this.notHaveCustomProvideMethod = !this.haveCustomProvideMethod();
		}

		protected Object getKey1(final TKeysHolder keysHolder) {
			return null;
		}

		protected Object getKey2(final TKeysHolder keysHolder) {
			return null;
		}

		protected Object getKey3(final TKeysHolder keysHolder) {
			return null;
		}

		protected void provide(final Context context,
				final ResourceInserter<TFacade, TImplement, TKeysHolder> setter)
				throws Throwable {
		}

		protected void provide(
				final Context context,
				final ResourceInserter<TFacade, TImplement, TKeysHolder> setter,
				final TKeyValue1 keyValue) throws Throwable {
		}

		protected void provide(
				final Context context,
				final ResourceInserter<TFacade, TImplement, TKeysHolder> setter,
				final TKeyValue1 keyValue1, final TKeyValue2 keyValue2)
				throws Throwable {
		}

		protected void provide(
				final Context context,
				final ResourceInserter<TFacade, TImplement, TKeysHolder> setter,
				final TKeyValue1 keyValue1, final TKeyValue2 keyValue2,
				final TKeyValue3 keyValue3) throws Throwable {
		}

		@SuppressWarnings("unchecked")
		final void provideProxy(
				final Context context,
				final ResourceInserter<TFacade, TImplement, TKeysHolder> setter,
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3) throws Throwable {
			if (keyValue1 == null) {
				this.provide(context, setter);
			} else {
				if (keyValue2 == null) {
					this.provide(context, setter, (TKeyValue1) keyValue1);
				} else {
					if (keyValue3 == null) {
						this.provide(context, setter, (TKeyValue1) keyValue1, (TKeyValue2) keyValue2);
					} else {
						this.provide(context, setter, (TKeyValue1) keyValue1, (TKeyValue2) keyValue2, (TKeyValue3) keyValue3);
					}
				}
			}
		}

		String getAccessControlTitle(final TImplement value,
				final TKeysHolder keysHolder) {
			throw new UnsupportedOperationException();
		}

		abstract boolean equals(
				Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> other);

		abstract int getKeyValueCount();

		abstract boolean haveCustomProvideMethod();

		final boolean notHaveCustomProvideMethod;

	}

	static abstract class KeyDefine<TFacade, TImplement extends TFacade, TKeysHolder> {

		private KeyDefine(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider) {
			if (provider == null) {
				throw new NullArgumentException("provider");
			}
			this.provider = provider;
		}

		AccessControlDefine<TFacade, TImplement, TKeysHolder> asAccessControlDefine() {
			return null;
		}

		Object getKeyValue1(final TKeysHolder keysHolder) {
			return null;
		}

		Object getKeyValue2(final TKeysHolder keysHolder) {
			return null;
		}

		Object getKeyValue3(final TKeysHolder keysHolder) {
			return null;
		}

		abstract boolean equalKey(TKeysHolder keysHolder, Object key1,
				Object key2, Object key3);

		abstract boolean equalKeyClass(Class<?> key1Class, Class<?> key2Class,
				Class<?> key3Class);

		abstract boolean compareKeyValues(TKeysHolder keysHolder1,
				TKeysHolder keysHolder2);

		abstract int getKeysHashCode(TKeysHolder keysHolder);

		abstract Entry<TFacade, TImplement, TKeysHolder> newIndexEntry(
				CacheHolder<TFacade, TImplement, TKeysHolder> holder,
				Object keyValue1, Object keyValue2, Object keyValue3);

		/**
		 * 根据参数keys获取键值拼成字符串，用于调试
		 */
		public abstract String getKeyString(TKeysHolder keys);

		final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider;

		abstract Class<?> tryGetKey1Class();

		abstract Class<?> tryGetKey2Class();

		abstract Class<?> tryGetKey3Class();

		static abstract class Entry<TFacade, TImplement extends TFacade, TKeysHolder> {

			Entry(final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
				this.holder = holder;
				this.havePlaceHolder = false;
			}

			PlaceHolder asPlaceHolder() {
				return null;
			}

			abstract PlaceHolder newPlaceHolder(Object keyValue1,
					Object keyValue2, Object keyValue3);

			abstract boolean equalKeyValues(Object keyValue1, Object keyValue2,
					Object keyValue3);

			final CacheHolder<TFacade, TImplement, TKeysHolder> holder;

			volatile Entry<TFacade, TImplement, TKeysHolder> next;

			/**
			 * XXX 增加这个属性是为了控制索引项的可见性，以boolean表示只是就目前的其它实现而言较为简单的一种方式，<br>
			 * 但不是最好的方式，索引项应该有其独立于缓存项的状态标志，在日后的维护过程中，应重构这一部分。
			 */
			volatile boolean havePlaceHolder;

			abstract class PlaceHolder extends
					Entry<TFacade, TImplement, TKeysHolder> {

				PlaceHolder(
						final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
					super(holder);
				}

				@Override
				final PlaceHolder asPlaceHolder() {
					return this;
				}

				@Override
				final PlaceHolder newPlaceHolder(Object keyValue1,
						Object keyValue2, Object keyValue3) {
					throw new UnsupportedOperationException();
				}

				abstract Entry<TFacade, TImplement, TKeysHolder> getBaseEntry();

			}

		}

	}

	static final class KeyDefineOfZeroValue<TFacade, TImplement extends TFacade, TKeysHolder>
			extends KeyDefine<TFacade, TImplement, TKeysHolder> {

		KeyDefineOfZeroValue(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider) {
			super(provider);
		}

		@Override
		final boolean equalKey(final TKeysHolder keysHolder, final Object key1,
				final Object key2, final Object key3) {
			return true;
		}

		@Override
		final boolean equalKeyClass(final Class<?> key1Class,
				final Class<?> key2Class, final Class<?> key3Class) {
			return true;
		}

		@Override
		final boolean compareKeyValues(final TKeysHolder keysHolder1,
				final TKeysHolder keysHolder2) {
			return true;
		}

		@Override
		final int getKeysHashCode(final TKeysHolder keysHolder) {
			return 0;
		}

		@Override
		final Entry<TFacade, TImplement, TKeysHolder> newIndexEntry(
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3) {
			return new Entry0<TFacade, TImplement, TKeysHolder>(holder);
		}

		@Override
		public String getKeyString(TKeysHolder keys) {
			return "";
		}

		@Override
		final Class<?> tryGetKey1Class() {
			return null;
		}

		@Override
		final Class<?> tryGetKey2Class() {
			return null;
		}

		@Override
		final Class<?> tryGetKey3Class() {
			return null;
		}

		private static class Entry0<TFacade, TImplement extends TFacade, TKeysHolder>
				extends Entry<TFacade, TImplement, TKeysHolder> {

			private Entry0(
					final CacheHolder<TFacade, TImplement, TKeysHolder> item) {
				super(item);
			}

			@Override
			public final int hashCode() {
				return 0;
			}

			@Override
			final PlaceHolder newPlaceHolder(final Object keyValue1,
					final Object keyValue2, final Object keyValue3) {
				return new PlaceHolder0(this, super.holder);
			}

			@Override
			final boolean equalKeyValues(final Object keyValue1,
					final Object keyValue2, final Object keyValue3) {
				return true;
			}

			private final class PlaceHolder0 extends
					Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder {

				private PlaceHolder0(
						final Entry0<TFacade, TImplement, TKeysHolder> entry0,
						final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
					entry0.super(holder);
				}

				@Override
				public final int hashCode() {
					return 0;
				}

				@Override
				final Entry<TFacade, TImplement, TKeysHolder> getBaseEntry() {
					return Entry0.this;
				}

				@Override
				final boolean equalKeyValues(final Object keyValue1,
						final Object keyValue2, final Object keyValue3) {
					return true;
				}

			}

		}

	}

	static class KeyDefineOfOneValue<TFacade, TImplement extends TFacade, TKeysHolder>
			extends KeyDefine<TFacade, TImplement, TKeysHolder> {

		KeyDefineOfOneValue(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider,
				final Class<?> value1Class) {
			super(provider);
			this.value1Class = value1Class;
		}

		@Override
		final Object getKeyValue1(final TKeysHolder keysHolder) {
			Object key1 = this.provider.getKey1(keysHolder);
			if (key1 == null) {
				throw new NullPointerException("资源提供器[" + this.provider + "]返回键值key1为空，keysHolder为[" + keysHolder + "]");
			}
			return key1;
		}

		@Override
		final boolean equalKey(final TKeysHolder keysHolder, final Object key1,
				final Object key2, final Object key3) {
			return key1.equals(this.getKeyValue1(keysHolder));
		}

		@Override
		final boolean equalKeyClass(final Class<?> key1Class,
				final Class<?> key2Class, final Class<?> key3Class) {
			return this.value1Class == key1Class && key2Class == null && key3Class == null;
		}

		@Override
		final boolean compareKeyValues(final TKeysHolder keysHolder1,
				final TKeysHolder keysHolder2) {
			return this.getKeyValue1(keysHolder1).equals(this.getKeyValue1(keysHolder2));
		}

		@Override
		final int getKeysHashCode(final TKeysHolder keysHolder) {
			return HashUtil.hash(this.getKeyValue1(keysHolder));
		}

		@Override
		final Entry<TFacade, TImplement, TKeysHolder> newIndexEntry(
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3) {
			return new Entry1<TFacade, TImplement, TKeysHolder>(holder, keyValue1);
		}

		@Override
		public String getKeyString(TKeysHolder keys) {
			return "" + this.getKeyValue1(keys);
		}

		private final Class<?> value1Class;

		@Override
		final Class<?> tryGetKey1Class() {
			return this.value1Class;
		}

		@Override
		final Class<?> tryGetKey2Class() {
			return null;
		}

		@Override
		final Class<?> tryGetKey3Class() {
			return null;
		}

		private static class Entry1<TFacade, TImplement extends TFacade, TKeysHolder>
				extends Entry<TFacade, TImplement, TKeysHolder> {

			private Entry1(
					final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
					final Object keyValue) {
				super(holder);
				this.keyValue = keyValue;
			}

			@Override
			public final int hashCode() {
				return HashUtil.hash(this.keyValue);
			}

			@Override
			final PlaceHolder newPlaceHolder(final Object keyValue1,
					final Object keyValue2, final Object keyValue3) {
				return new PlaceHolder1(this, super.holder, keyValue1);
			}

			@Override
			final boolean equalKeyValues(final Object keyValue1,
					final Object keyValue2, final Object keyValue3) {
				return this.keyValue.equals(keyValue1);
			}

			private final Object keyValue;

			private final class PlaceHolder1 extends
					Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder {

				private PlaceHolder1(
						final Entry1<TFacade, TImplement, TKeysHolder> entry1,
						final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
						final Object keyValue) {
					entry1.super(holder);
					this.keyValue = keyValue;
				}

				@Override
				public final int hashCode() {
					return HashUtil.hash(this.keyValue);
				}

				@Override
				final Entry<TFacade, TImplement, TKeysHolder> getBaseEntry() {
					return Entry1.this;
				}

				private final Object keyValue;

				@Override
				final boolean equalKeyValues(final Object keyValue1,
						final Object keyValue2, final Object keyValue3) {
					return this.keyValue.equals(keyValue1);
				}

			}

		}

	}

	static final class KeyDefineOfTwoValue<TFacade, TImplement extends TFacade, TKeysHolder>
			extends KeyDefine<TFacade, TImplement, TKeysHolder> {

		KeyDefineOfTwoValue(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider,
				final Class<?> value1Class, final Class<?> value2Class) {
			super(provider);
			this.value1Class = value1Class;
			this.value2Class = value2Class;
		}

		@Override
		final Object getKeyValue1(final TKeysHolder keysHolder) {
			Object key1 = this.provider.getKey1(keysHolder);
			if (key1 == null) {
				throw new NullPointerException("资源提供器[" + this.provider + "]返回的键值key1为空，keysHolder为[" + keysHolder + "]");
			}
			return key1;
		}

		@Override
		final Object getKeyValue2(final TKeysHolder keysHolder) {
			Object key2 = this.provider.getKey2(keysHolder);
			if (key2 == null) {
				throw new NullPointerException("资源提供器[" + this.provider + "]返回的键值key2为空，keysHolder为[" + keysHolder + "]");
			}
			return key2;
		}

		@Override
		final boolean equalKey(final TKeysHolder keysHolder, final Object key1,
				final Object key2, final Object key3) {
			return key1.equals(this.getKeyValue1(keysHolder)) && key2.equals(this.getKeyValue2(keysHolder));
		}

		@Override
		final boolean equalKeyClass(final Class<?> key1Class,
				final Class<?> key2Class, final Class<?> key3Class) {
			return this.value1Class == key1Class && this.value2Class == key2Class && key3Class == null;
		}

		@Override
		final boolean compareKeyValues(final TKeysHolder keysHolder1,
				final TKeysHolder keysHolder2) {
			return (this.getKeyValue1(keysHolder1).equals(this.getKeyValue1(keysHolder2))) && (this.getKeyValue2(keysHolder1).equals(this.getKeyValue2(keysHolder2)));
		}

		@Override
		final int getKeysHashCode(final TKeysHolder keysHolder) {
			return HashUtil.hash(this.getKeyValue1(keysHolder), this.getKeyValue2(keysHolder));
		}

		@Override
		final Entry<TFacade, TImplement, TKeysHolder> newIndexEntry(
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3) {
			return new Entry2<TFacade, TImplement, TKeysHolder>(holder, keyValue1, keyValue2);
		}

		@Override
		public String getKeyString(TKeysHolder keys) {
			return "" + this.getKeyValue1(keys) + ", " + this.getKeyValue2(keys);
		}

		private final Class<?> value1Class;

		private final Class<?> value2Class;

		@Override
		final Class<?> tryGetKey1Class() {
			return this.value1Class;
		}

		@Override
		final Class<?> tryGetKey2Class() {
			return this.value2Class;
		}

		@Override
		final Class<?> tryGetKey3Class() {
			return null;
		}

		private static class Entry2<TFacade, TImplement extends TFacade, TKeysHolder>
				extends Entry<TFacade, TImplement, TKeysHolder> {

			private Entry2(
					final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
					final Object keyValue1, final Object keyValue2) {
				super(holder);
				this.keyValue1 = keyValue1;
				this.keyValue2 = keyValue2;
			}

			@Override
			public final int hashCode() {
				return HashUtil.hash(this.keyValue1, this.keyValue2);
			}

			@Override
			final PlaceHolder newPlaceHolder(final Object keyValue1,
					final Object keyValue2, final Object keyValue3) {
				return new PlaceHolder2(this, super.holder, keyValue1, keyValue2);
			}

			@Override
			final boolean equalKeyValues(final Object keyValue1,
					final Object keyValue2, final Object keyValue3) {
				return this.keyValue1.equals(keyValue1) && this.keyValue2.equals(keyValue2);
			}

			private final Object keyValue1;

			private final Object keyValue2;

			private final class PlaceHolder2 extends
					Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder {

				private PlaceHolder2(
						final Entry2<TFacade, TImplement, TKeysHolder> entry2,
						final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
						final Object keyValue1, final Object keyValue2) {
					entry2.super(holder);
					this.keyValue1 = keyValue1;
					this.keyValue2 = keyValue2;
				}

				@Override
				public final int hashCode() {
					return HashUtil.hash(this.keyValue1, this.keyValue2);
				}

				@Override
				final Entry<TFacade, TImplement, TKeysHolder> getBaseEntry() {
					return Entry2.this;
				}

				private final Object keyValue1;

				private final Object keyValue2;

				@Override
				final boolean equalKeyValues(final Object keyValue1,
						final Object keyValue2, final Object keyValue3) {
					return this.keyValue1.equals(keyValue1) && this.keyValue2.equals(keyValue2);
				}
			}
		}

	}

	static final class KeyDefineOfThreeValue<TFacade, TImplement extends TFacade, TKeysHolder>
			extends KeyDefine<TFacade, TImplement, TKeysHolder> {

		KeyDefineOfThreeValue(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider,
				final Class<?> value1Class, final Class<?> value2Class,
				final Class<?> value3Class) {
			super(provider);
			this.value1Class = value1Class;
			this.value2Class = value2Class;
			this.value3Class = value3Class;
		}

		@Override
		final Object getKeyValue1(final TKeysHolder keysHolder) {
			Object key1 = this.provider.getKey1(keysHolder);
			if (key1 == null) {
				throw new NullPointerException("资源提供器[" + this.provider + "]返回的键值key1为空，keysHolder为[" + keysHolder + "]");
			}
			return key1;
		}

		@Override
		final Object getKeyValue2(final TKeysHolder keysHolder) {
			Object key2 = this.provider.getKey2(keysHolder);
			if (key2 == null) {
				throw new NullPointerException("资源提供器[" + this.provider + "]返回的键值key2为空，keysHolder为[" + keysHolder + "]");
			}
			return key2;
		}

		@Override
		final Object getKeyValue3(final TKeysHolder keysHolder) {
			Object key3 = this.provider.getKey3(keysHolder);
			if (key3 == null) {
				throw new NullPointerException("资源提供器[" + this.provider + "]返回的键值key3为空，keysHolder为[" + keysHolder + "]");
			}
			return key3;
		}

		@Override
		final boolean equalKey(final TKeysHolder keysHolder, final Object key1,
				final Object key2, final Object key3) {
			return key1.equals(this.getKeyValue1(keysHolder)) && key2.equals(this.getKeyValue2(keysHolder)) && key3.equals(this.getKeyValue3(keysHolder));
		}

		@Override
		final boolean equalKeyClass(final Class<?> key1Class,
				final Class<?> key2Class, final Class<?> key3Class) {
			return this.value1Class == key1Class && this.value2Class == key2Class && this.value3Class == key3Class;
		}

		@Override
		final boolean compareKeyValues(final TKeysHolder keysHolder1,
				final TKeysHolder keysHolder2) {
			return (this.getKeyValue1(keysHolder1).equals(this.getKeyValue1(keysHolder2))) && (this.getKeyValue2(keysHolder1).equals(this.getKeyValue2(keysHolder2))) && (this.getKeyValue3(keysHolder1).equals(this.getKeyValue3(keysHolder2)));
		}

		@Override
		final int getKeysHashCode(final TKeysHolder keysHolder) {
			return HashUtil.hash(this.getKeyValue1(keysHolder), this.getKeyValue2(keysHolder), this.getKeyValue3(keysHolder));
		}

		@Override
		final Entry<TFacade, TImplement, TKeysHolder> newIndexEntry(
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3) {
			return new Entry3<TFacade, TImplement, TKeysHolder>(holder, keyValue1, keyValue2, keyValue3);
		}

		@Override
		public String getKeyString(TKeysHolder keys) {
			return "" + this.getKeyValue1(keys) + ", " + this.getKeyValue2(keys) + ", " + this.getKeyValue3(keys);
		}

		private final Class<?> value1Class;

		private final Class<?> value2Class;

		private final Class<?> value3Class;

		@Override
		final Class<?> tryGetKey1Class() {
			return this.value1Class;
		}

		@Override
		final Class<?> tryGetKey2Class() {
			return this.value2Class;
		}

		@Override
		final Class<?> tryGetKey3Class() {
			return this.value3Class;
		}

		private static class Entry3<TFacade, TImplement extends TFacade, TKeysHolder>
				extends Entry<TFacade, TImplement, TKeysHolder> {

			private Entry3(
					final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
					final Object keyValue1, final Object keyValue2,
					final Object keyValue3) {
				super(holder);
				this.keyValue1 = keyValue1;
				this.keyValue2 = keyValue2;
				this.keyValue3 = keyValue3;
			}

			@Override
			public final int hashCode() {
				return HashUtil.hash(this.keyValue1, this.keyValue2, this.keyValue3);
			}

			@Override
			final PlaceHolder newPlaceHolder(final Object keyValue1,
					final Object keyValue2, final Object keyValue3) {
				return new PlaceHolder3(this, super.holder, keyValue1, keyValue2, keyValue3);
			}

			@Override
			final boolean equalKeyValues(final Object keyValue1,
					final Object keyValue2, final Object keyValue3) {
				return this.keyValue1.equals(keyValue1) && this.keyValue2.equals(keyValue2) && this.keyValue3.equals(keyValue3);
			}

			private final Object keyValue1;

			private final Object keyValue2;

			private final Object keyValue3;

			private final class PlaceHolder3 extends
					Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder {

				private PlaceHolder3(
						final Entry3<TFacade, TImplement, TKeysHolder> entry3,
						final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
						final Object keyValue1, final Object keyValue2,
						final Object keyValue3) {
					entry3.super(holder);
					this.keyValue1 = keyValue1;
					this.keyValue2 = keyValue2;
					this.keyValue3 = keyValue3;
				}

				@Override
				public final int hashCode() {
					return HashUtil.hash(this.keyValue1, this.keyValue2, this.keyValue3);
				}

				@Override
				final Entry<TFacade, TImplement, TKeysHolder> getBaseEntry() {
					return Entry3.this;
				}

				private final Object keyValue1;

				private final Object keyValue2;

				private final Object keyValue3;

				@Override
				final boolean equalKeyValues(final Object keyValue1,
						final Object keyValue2, final Object keyValue3) {
					return this.keyValue1.equals(keyValue1) && this.keyValue2.equals(keyValue2) && this.keyValue3.equals(keyValue3);
				}

			}

		}

	}

	static final class AccessControlDefine<TFacade, TImplement extends TFacade, TKeysHolder>
			extends KeyDefineOfOneValue<TFacade, TImplement, TKeysHolder> {

		AccessControlDefine(
				final Provider<TFacade, TImplement, TKeysHolder, ?, ?, ?> provider,
				final boolean defaultAuthority, final Operation<?>[] operations) {
			super(provider, GUID.class);
			this.defaultAuthority = defaultAuthority;
			this.operations = operations;
			this.operationEntrys = new OperationEntry[operations.length];
			for (int index = 0, endIndex = operations.length; index < endIndex; index++) {
				this.operationEntrys[index] = new OperationEntry(operations[index]);
			}
		}

		@Override
		final AccessControlDefine<TFacade, TImplement, TKeysHolder> asAccessControlDefine() {
			return this;
		}

		final boolean defaultAuthority;

		final Operation<?>[] operations;

		final OperationEntry[] operationEntrys;

	}

	static final class ReferenceDefine<TReferenceFacade> {

		ReferenceDefine(final CacheDefine<?, ?, ?> ownCacheDefine,
				final CacheDefine<?, ?, ?> referenceCacheDefine,
				final Operation<?>[] operationMap,
				final Filter<? super TReferenceFacade> defaultFilter,
				final Comparator<? super TReferenceFacade> defaultComparator) {
			this.referenceCacheDefine = referenceCacheDefine;
			if (operationMap == null) {
				this.operationMap = null;
			} else {
				if (!ownCacheDefine.isAccessControlDefine() || !referenceCacheDefine.isAccessControlDefine()) {
					throw new UnsupportedOperationException("只能定义访问控制对象对访问控制的对象的操作映射。");
				}
				final OperationEntry[] referenceOperationEntrys = referenceCacheDefine.accessControlDefine.operationEntrys;
				this.operationMap = new OperationEntry[operationMap.length];
				for (int index = 0, endIndex = operationMap.length; index < endIndex; index++) {
					final Operation<?> mapOperation = operationMap[index];
					if (mapOperation == null) {
						this.operationMap[index] = null;
					} else {
						this.operationMap[index] = OperationEntry.operationEntryOf(mapOperation, referenceOperationEntrys);
					}
				}
			}
			this.defaultFilter = defaultFilter;
			this.defaultComparator = defaultComparator;
		}

		final boolean isAccessControlReferenceDefine() {
			return this.operationMap != null;
		}

		final CacheDefine<?, ?, ?> referenceCacheDefine;

		final OperationEntry[] operationMap;

		final Filter<? super TReferenceFacade> defaultFilter;

		final Comparator<? super TReferenceFacade> defaultComparator;

	}

	static final ReferenceDefine<?>[] EMPTY_REFERENCEDEFINES;

	static {
		EMPTY_REFERENCEDEFINES = new ReferenceDefine<?>[0];
	}

	CacheDefine(
			final Cache ownCache,
			final ResourceServiceBase<TFacade, TImplement, TKeysHolder> resourceService) {
		this.ownCache = ownCache;
		this.resourceService = resourceService;
		this.title = resourceService.title;
		this.facadeClass = resourceService.facadeClass;
		try {
			StaticStructDefineImpl structDefine = DataTypeBase.getStaticStructDefine(resourceService.implementClass);
			if (!structDefine.isStructClass) {
				String msg = "资源服务警告：资源服务[" + this.resourceService.getClass().getName() + "]实现类[" + structDefine.name + "]缺少@StructClass修饰";
				Logger logger = DNALogManager.getLogger("core/service");
				logger.logWarn(null, msg, false);
			}
			this.implementStruct = structDefine;
		} catch (Throwable e) {
			throw Utils.tryThrowException(new UnsupportedOperationException("缓存定义的实现类型的结构无法支持内存事务", e));
		}
		this.kind = resourceService.kind;
		this.quirkMode = resourceService.quirkMode;
		this.keyDefines = resourceService.getKeyDefines();
		AccessControlDefine<TFacade, TImplement, TKeysHolder> accessControlDefine = null;
		for (KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine : this.keyDefines) {
			if ((accessControlDefine = keyDefine.asAccessControlDefine()) != null) {
				break;
			}
		}
		this.accessControlDefine = accessControlDefine;
		this.defaultFilter = resourceService.getDefaultFilter();
		this.defaultComparator = resourceService.getDefaultComparator();
		this.GUIDIdentifier = GUID.MD5Of(this.facadeClass.getName());
		ownCache.defineContainer.registDefine(this);
		this.calculateModifiable();
	}

	private final void calculateModifiable() {
		final DistributedEnvironment env = this.ownCache.site.application.distenv;
		if (env == null) {
			this.barrier = null;
		} else if (env.param) {
			this.barrier = null;
		} else if (DistCacheBarrierGatherer.map.containsKey(this.facadeClass.getName())) {
			this.barrier = DistCacheBarrierGatherer.map.get(this.facadeClass.getName());
		} else if (this.kind.inCluster) {
			this.barrier = DistCacheBarrierGatherer.FORBIDDEN;
		} else {
			this.barrier = null;
		}
	}

	@Override
	public final String toString() {
		return "FacadeClass:[" + this.facadeClass.getName() + "]\n" + "Scope:[" + this.kind.name() + "]\n" + "IsAccessControlDefine:[" + this.isAccessControlDefine() + "]";
	}

	CacheGroup<TFacade, TImplement, TKeysHolder> newGroup(
			final CacheGroupSpace ownSpace, final String title,
			final Long fixLongIdentifier, final Byte fixInitializeState,
			final Throwable initializeException) {
		return new CacheGroup<TFacade, TImplement, TKeysHolder>(ownSpace, this, title, fixLongIdentifier, fixInitializeState, initializeException);
	}

	final boolean isAccessControlDefine() {
		return this.accessControlDefine != null;
	}

	final ReferenceDefine<?> getReferenceDefine(
			final Class<?> referenceFacadeClass) {
		this.ensureInitializedReferenceDefine();
		for (ReferenceDefine<?> referenceDefine : this.referenceDefines) {
			if (referenceFacadeClass == referenceDefine.referenceCacheDefine.facadeClass) {
				return referenceDefine;
			}
		}
		throw new RuntimeException("没有定义缓存引用关系。[" + referenceFacadeClass + "]");
	}

	final ReferenceDefine<?> findReferenceDefine(
			final Class<?> referenceFacadeClass) {
		this.ensureInitializedReferenceDefine();
		for (ReferenceDefine<?> referenceDefine : this.referenceDefines) {
			if (referenceFacadeClass == referenceDefine.referenceCacheDefine.facadeClass) {
				return referenceDefine;
			}
		}
		return null;
	}

	final ReferenceDefine<?>[] getAccessControlReferenceDefines() {
		this.ensureInitializedReferenceDefine();
		return this.accessControlReferenceDefines;
	}

	final OperationEntry tryGetMappingOperationEntry(
			final OperationEntry operation, final CacheDefine<?, ?, ?> define) {
		this.ensureInitializedReferenceDefine();
		if (this.accessControlReferenceDefines != null) {
			for (int index = 0, endIndex = this.accessControlReferenceDefines.length; index < endIndex; index++) {
				final ReferenceDefine<?> referenceDefine = this.accessControlReferenceDefines[index];
				if (referenceDefine.referenceCacheDefine == define) {
					return referenceDefine.operationMap[operation.index];
				}
			}
		}
		return null;
		// return this.accessControlReferenceDefines == null ? null
		// : this.accessControlReferenceDefines.operationMap[operation.index];
		// for (ReferenceDefine<?> referenceDefine : this.referenceDefines) {
		// if (define == referenceDefine.referenceCacheDefine) {
		// return referenceDefine.isAccessControlReferenceDefine() ?
		// referenceDefine.operationMap[operation.index]
		// : null;
		// }
		// }
		// return null;
	}

	private final void ensureInitializedReferenceDefine() {
		if (this.referenceDefines == null) {
			synchronized (this) {
				if (this.referenceDefines == null) {
					final ReferenceDefine<?>[] referenctDefines = this.resourceService.getReferenceDefines(this);
					if (this.isAccessControlDefine()) {
						for (ReferenceDefine<?> referenceDefine : referenctDefines) {
							if (referenceDefine.isAccessControlReferenceDefine()) {
								if (this.accessControlReferenceDefines == null) {
									this.accessControlReferenceDefines = new ReferenceDefine<?>[] { referenceDefine };
								} else {
									final int oldCount = this.accessControlReferenceDefines.length;
									final ReferenceDefine<?>[] newACReferenceDefine = new ReferenceDefine<?>[oldCount + 1];
									System.arraycopy(this.accessControlReferenceDefines, 0, newACReferenceDefine, 0, oldCount);
									newACReferenceDefine[oldCount] = referenceDefine;
								}
							}
						}
					} else {
						for (ReferenceDefine<?> referenceDefine : referenctDefines) {
							if (referenceDefine.isAccessControlReferenceDefine()) {
								throw new IllegalArgumentException("不能定义非访问控制对象的访问控制引用。");
							}
						}
					}
					this.referenceDefines = referenctDefines;
				}
			}
		}
	}

	/**
	 * CacheDefine的唯一标识，该标识只与资源服务的类型相关，所以在集群中是一致的，不需要同步
	 */
	final GUID GUIDIdentifier;

	final Cache ownCache;

	final ResourceServiceBase<TFacade, TImplement, TKeysHolder> resourceService;

	final String title;

	final Class<?> facadeClass;

	final StructDefineImpl implementStruct;

	final ResourceKind kind;

	final boolean quirkMode;

	final KeyDefine<TFacade, TImplement, TKeysHolder>[] keyDefines;

	final AccessControlDefine<TFacade, TImplement, TKeysHolder> accessControlDefine;

	final Filter<? super TImplement> defaultFilter;

	final Comparator<? super TImplement> defaultComparator;

	private volatile ReferenceDefine<?>[] referenceDefines;

	private volatile ReferenceDefine<?> accessControlReferenceDefines[];

	volatile CacheDefine<?, ?, ?> nextInMapByGUIDIdentifier;

	volatile CacheDefine<?, ?, ?> nextInMapByFacadeClass;

	Barrier barrier;

	// DIST
	final void checkModfiable(Object spaceIdentifier) {
		if (this.barrier == null) {
			return;
		}
		if (this.barrier.isModifiable(spaceIdentifier)) {
			return;
		}
		throw new UnsupportedOperationException("Forbidden in Distributed DNA");
	}
}