package com.jiuqi.dna.core.impl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import sun.misc.Unsafe;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.type.GUID;

/**
 * 助理类
 * 
 * @author gaojingxin
 * 
 */
@SuppressWarnings("unchecked")
public final class Utils {
	final static String noneNull(String text) {
		return text == null || text.length() == 0 ? "" : text;
	}

	// //////////////////////////////////////////////////////////////////////////
	// ////////////////////

	// //////////////////以下是内部方法/////////////////////////////////////////////////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////////
	static Map<String, String> parseNameValues(String nameValues,
			boolean namelow) {
		Map<String, String> map = new HashMap<String, String>(0);
		if (nameValues == null || nameValues.length() == 0) {
			return map;
		}
		int l = nameValues.length();
		int start = 0;
		do {
			int end = nameValues.indexOf(';', start);
			if (end < 0) {
				end = l;
			}
			for (int index = start; index < end; index++) {
				char c = nameValues.charAt(index);
				if (c == '=') {
					String name = nameValues.substring(start, index).trim();
					if (namelow) {
						name = name.toLowerCase();
					}
					String value = nameValues.substring(index + 1, end).trim();
					map.put(name, value);
					break;
				}
			}
			start = end + 1;
		} while (start < l);
		return map;
	}

	static final Class<?>[] getParameterTypes(Constructor<?> constructor) {
		if (Unsf.unsafe != null && Utils.constructorParameterTypesOffset != ILLEGAL_OFFSET) {
			return (Class<?>[]) Unsf.unsafe.getObject(constructor, Utils.constructorParameterTypesOffset);
		} else {
			return constructor.getParameterTypes();
		}
	}

	static final Class<?>[] getParameterTypes(Method method) {
		if (Unsf.unsafe != null && Utils.methodParameterTypesOffset != ILLEGAL_OFFSET) {
			return (Class<?>[]) Unsf.unsafe.getObject(method, Utils.methodParameterTypesOffset);
		} else {
			return method.getParameterTypes();
		}
	}

	final static Lock NOLOCK = new Lock() {

		public void lock() {
		}

		public void lockInterruptibly() throws InterruptedException {
		}

		public Condition newCondition() {
			throw new UnsupportedOperationException();
		}

		public boolean tryLock() {
			return false;
		}

		public boolean tryLock(long time, TimeUnit unit)
				throws InterruptedException {
			return false;
		}

		public void unlock() {
		}
	};

	static final Constructor<?>[] getConstructorParamCountOrder(Class<?> clazz) {
		Constructor<?>[] result = clazz.getDeclaredConstructors();
		switch (result.length) {
		case 1:
			return result;
		case 2:
			int cc1 = Utils.getParameterTypes(result[0]).length;
			int cc2 = Utils.getParameterTypes(result[1]).length;
			if (cc1 < cc2) {
				Constructor<?> c = result[0];
				result[0] = result[1];
				result[1] = c;
			}
			return result;
		}
		int[] pcounts = new int[result.length];
		int count = result.length;
		for (int i = 0; i < count; i++) {
			pcounts[i] = Utils.getParameterTypes(result[i]).length;
		}
		for (int i = 0; i < count; i++) {
			int pc = pcounts[i];
			int pc2 = pc;
			int maxi = i;
			for (int j = i + 1; j < count; j++) {
				int c = pcounts[j];
				if (pc < c) {
					pc = c;
					maxi = j;
				}
			}
			if (maxi != i) {
				pcounts[maxi] = pc2;
				pcounts[i] = pc;
				Constructor<?> c = result[maxi];
				result[maxi] = result[i];
				result[i] = c;
			}
		}
		return result;
	}

	public interface ObjectAccessor<TObject, TFieldType> {
		public boolean isReadable();

		public boolean isWritable();

		public TFieldType get(TObject obj);

		public void set(TObject obj, TFieldType value);

		public boolean CAS(TObject obj, TFieldType compareTo, TFieldType set);
	}

	final static Task._Accessor taskAccessor = Task._Accessor.get();

	final static class ObjectCompareAndSetterUnsafeImpl<TObject, TFieldType>
			implements ObjectAccessor<TObject, TFieldType> {
		final long fieldOffset;
		final boolean readonly;

		public final boolean isReadable() {
			return true;
		}

		public final boolean isWritable() {
			return !this.readonly;
		}

		ObjectCompareAndSetterUnsafeImpl(Field field, boolean readonly) {
			this.fieldOffset = Unsf.unsafe.objectFieldOffset(field);
			this.readonly = readonly;
		}

		public final boolean CAS(TObject obj, TFieldType compareTo,
				TFieldType set) {
			if (this.readonly) {
				throw new IllegalStateException("只读CAS访问器");
			}
			return Unsf.unsafe.compareAndSwapObject(obj, this.fieldOffset, compareTo, set);
		}

		public TFieldType get(TObject obj) {
			return (TFieldType) Unsf.unsafe.getObject(obj, this.fieldOffset);
		}

		public void set(TObject obj, TFieldType value) {
			if (this.readonly) {
				throw new IllegalStateException("只读访问器");
			}
			Unsf.unsafe.putObject(obj, this.fieldOffset, value);
		}

	}

	final static class ObjectCompareAndSetterImpl<TObject, TFieldType>
			implements ObjectAccessor<TObject, TFieldType>,
			PrivilegedAction<Object> {
		final Field field;
		final boolean readonly;

		public final boolean isReadable() {
			return true;
		}

		public final boolean isWritable() {
			return !this.readonly;
		}

		ObjectCompareAndSetterImpl(Field field, boolean readonly) {
			this.field = field;
			this.readonly = readonly;
			AccessController.doPrivileged(this);
		}

		public Object run() {
			this.field.setAccessible(true);
			return null;
		}

		public boolean CAS(TObject obj, TFieldType compareTo, TFieldType set) {
			if (this.readonly) {
				throw new IllegalStateException("只读访CAS问器");
			}
			synchronized (obj) {
				try {
					if (this.field.get(obj) == compareTo) {
						this.field.set(obj, set);
						return true;
					}
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				}
				return false;
			}
		}

		public TFieldType get(TObject obj) {
			try {
				return (TFieldType) this.field.get(obj);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}

		public void set(TObject obj, TFieldType value) {
			try {
				this.field.set(obj, value);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	private final static class ObjectCompareAndSetterMissingFieldImpl<TObject, TFieldType>
			implements ObjectAccessor<TObject, TFieldType> {
		public final boolean isReadable() {
			return false;
		}

		public final boolean isWritable() {
			return false;
		}

		public boolean CAS(TObject obj, TFieldType compareTo, TFieldType set) {
			throw new UnsupportedOperationException("field missing");
		}

		public TFieldType get(TObject obj) {
			throw new UnsupportedOperationException("field missing");
		}

		public void set(TObject obj, TFieldType value) {
			throw new UnsupportedOperationException("field missing");
		}
	}

	@SuppressWarnings("rawtypes")
	private static final ObjectCompareAndSetterMissingFieldImpl fieldMissing = new ObjectCompareAndSetterMissingFieldImpl();

	public final static <TObject, TFieldType> ObjectAccessor<TObject, TFieldType> newObjectAccessor(
			Class<TObject> objClass, Class<TFieldType> fieldType,
			String fieldName) {
		if (objClass == null) {
			throw new NullPointerException();
		}
		Field field;
		try {
			field = objClass.getDeclaredField(fieldName);
		} catch (Throwable e) {
			return Utils.fieldMissing;
		}
		return Unsf.unsafe != null ? new ObjectCompareAndSetterUnsafeImpl<TObject, TFieldType>(field, false) : new ObjectCompareAndSetterImpl<TObject, TFieldType>(field, false);
	}

	/**
	 * Unsafe引用
	 */
	public static final RuntimeException tryThrowException(Throwable e) {
		if (Unsf.unsafe != null) {
			Unsf.unsafe.throwException(e);
		}
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}

	static final long objectFieldOffset(Field field) {
		if (Unsf.unsafe != null) {
			return Unsf.unsafe.objectFieldOffset(field);
		}
		throw new UnsupportedOperationException();
	}

	static <TAccessibleObject extends AccessibleObject> TAccessibleObject publicAccessibleObject(
			final TAccessibleObject ao) {
		if (Utils.accessibleObjectOverrideOffset == ILLEGAL_OFFSET) {
			AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					ao.setAccessible(true);
					return null;
				}
			});
		} else {
			Unsf.unsafe.putBoolean(ao, Utils.accessibleObjectOverrideOffset, true);

		}
		return ao;
	}

	static boolean hasMethod(String method, Class<?> inClass,
			Class<?>[] parameterTypes, Class<?>[] ignoreClasses) {
		Class<?> declareClass;
		try {
			declareClass = inClass.getDeclaredMethod(method, parameterTypes).getDeclaringClass();
		} catch (Throwable e) {
			return false;
		}
		if (ignoreClasses != null) {
			for (int i = 0; i < ignoreClasses.length; i++) {
				if (declareClass == ignoreClasses[i]) {
					return false;
				}
			}
		}
		return true;
	}

	static Method getMethod(Class<?> clazz, String methodName,
			Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 该方法检查指定的类是否重写或实现了指定的方法。<br/>
	 * 假设声明指定的方法的类或接口叫作“声明类”。那么如果该方法返回真（<code>true</code>）， 则表明指定的类肯定不是声明类。
	 * 还表明指定的类重写或实现了指定的方法，或者指定的类的父类（同时也不是声明类）重写或实现了指定的方法。
	 */
	static boolean overridden(final Method superClassMethod, Class<?> clazz,
			Class<?>[] ignores) {
		if (superClassMethod == null) {
			return false;
		}
		if (clazz == null) {
			throw new NullArgumentException("clazz");
		}
		final Class<?> superClass = superClassMethod.getDeclaringClass();
		if (!superClass.isAssignableFrom(clazz)) {
			return false;
		}
		final Class<?>[] parameterTypes = Utils.getParameterTypes(superClassMethod);
		final String methodName = superClassMethod.getName();
		for (;;) {
			try {
				return clazz.getDeclaredMethod(methodName, parameterTypes) != null;
			} catch (NoSuchMethodException e) {
				clazz = clazz.getSuperclass(); // continue
				if (clazz == superClass || clazz == null || clazz == Object.class) {
					return false;
				}
				if (ignores != null) {
					for (Class<?> ignore : ignores) {
						if (clazz == ignore) {
							return false;
						}
					}
				}
			}
		}
	}

	final static Object[] emptyObjectArray = {};
	final static long[] emptyLongArray = {};
	final static GUID[] emptyGUIDArray = {};

	public static String fastString(char[] chars) {
		final int charCount = chars.length;
		if (charCount == 0) {
			return "";
		}
		return new String(chars);
		// if (Utils.stringCountOffset != ILLEGAL_OFFSET
		// && stringValueOffset != ILLEGAL_OFFSET) {
		// try {
		// String s = (String) Unsf.unsafe.allocateInstance(String.class);
		// Unsf.unsafe.putInt(s, Utils.stringCountOffset, charCount);
		// Unsf.unsafe.putObject(s, Utils.stringValueOffset, chars);
		// return s;
		// } catch (InstantiationException e) {
		// return new String(chars);
		// }
		// } else {
		// return new String(chars);
		// }
	}

	// //////////////////////////////////////////////////////////////////////////
	// ////////////////////
	// //////////////////以下是内部方法/////////////////////////////////////////////////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////////
	// ////////////////////
	static final long ILLEGAL_OFFSET = Unsafe.INVALID_FIELD_OFFSET;

	static long tryGetFieldOffset(Class<?> clazz, String fieldName) {
		if (Unsf.unsafe != null) {
			try {
				return Unsf.unsafe.objectFieldOffset(clazz.getDeclaredField(fieldName));
			} catch (Throwable e) {
				return ILLEGAL_OFFSET;
			}
		}
		return ILLEGAL_OFFSET;
	}

	static {
		accessibleObjectOverrideOffset = tryGetFieldOffset(AccessibleObject.class, "override");
		constructorParameterTypesOffset = tryGetFieldOffset(Constructor.class, "parameterTypes");
		methodParameterTypesOffset = tryGetFieldOffset(Method.class, "parameterTypes");
		stringValueOffset = tryGetFieldOffset(String.class, "value");
		stringCountOffset = tryGetFieldOffset(String.class, "count");
		stringOffsetOffset = tryGetFieldOffset(String.class, "offset");
	}

	private static final long accessibleObjectOverrideOffset;
	private static final long constructorParameterTypesOffset;
	private static final long methodParameterTypesOffset;

	static final long stringValueOffset;
	static final long stringCountOffset;
	static final long stringOffsetOffset;

	private static char[] emptychars = new char[0];

	static char[] stringGetCharArray(String s) {
		if (s == null) {
			throw new NullArgumentException("s");
		}
		final int count = s.length();
		if (count == 0) {
			return emptychars;
		}
		final int offset = Unsf.unsafe.getInt(s, Utils.stringOffsetOffset);
		char[] value = (char[]) Unsf.unsafe.getObject(s, Utils.stringValueOffset);
		if (offset == 0 && value.length == count) {
			return value;
		}
		final char[] copyChars = new char[count];
		System.arraycopy(value, offset, copyChars, 0, count);
		return copyChars;
	}

	private Utils() {
	}

	public static NotImplementedException notImplemented() {
		return new NotImplementedException();
	}

	public static final boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
}