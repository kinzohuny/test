package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.util.HashSet;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.TypeArgFinder;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.DataObjectTranslator;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.TypeDetector;

/**
 * 对象类型基类
 * 
 * @author gaojingxin
 * 
 */
public abstract class ObjectDataTypeBase extends DataTypeBase implements
		ObjectDataTypeInternal {

	@SuppressWarnings("unchecked")
	private DataObjectTranslator translator;

	public final DataObjectTranslator<?, ?> getDataObjectTranslator() {
		return this.translator;
	}

	public final DataObjectTranslator<?, ?> registerDataObjectTranslator(
			final DataObjectTranslator<?, ?> serializer) {
		final DataObjectTranslator<?, ?> old = this.translator;
		this.translator = serializer;
		return old;
	}

	ObjectDataTypeBase(Class<?> javaClass) {
		super(javaClass);
	}

	public boolean isInstance(Object obj) {
		return this.javaClass.isInstance(obj);
	}

	public final Object assignNoCheckSrcD(DynObj dynSrc, Object dest,
			OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		throw new UnsupportedOperationException("无效的调用");
	}

	@SuppressWarnings("unchecked")
	public Object assignNoCheckSrc(Object src, Object destHint,
			OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		final DataObjectTranslator translator = this.translator;
		if (translator != null && translator.supportAssign()) {
			final DataTranslatorHelper helper = objaContext.newDataTranslatorHelper(src, translator);
			final Object srcDelegate = translator.toDelegateObject(src);
			final Object destDelegateHint;
			if (destHint != null && this.javaClass == destHint.getClass()) {
				destDelegateHint = translator.toDelegateObject(destHint);
			} else {
				destDelegateHint = null;
			}
			final Object destDelegate = ((ObjectDataTypeInternal) DataTypeBase.dataTypeOfJavaObj(srcDelegate)).assignNoCheckSrc(srcDelegate, destDelegateHint, objaContext, helper);
			return helper.translate(destDelegate, destHint);
		} else {
			return src;
		}
	}

	public AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		if (another instanceof ObjectDataType) {
			Class<?> anotherJavaClass = ((ObjectDataType) another).getJavaClass();
			if (anotherJavaClass == this.javaClass) {
				return AssignCapability.SAME;
			}
			if (this.javaClass.isAssignableFrom(anotherJavaClass)) {
				return AssignCapability.IMPLICIT;
			}
			if (anotherJavaClass.isAssignableFrom(this.javaClass)) {
				return AssignCapability.EXPLICIT;
			}
		}
		return AssignCapability.NO;
	}

	@Override
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inObject(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	// ///////////////////////////////////////
	// Serialization

	public boolean supportSerialization() {
		return false;
	}

	public void writeObjectData(InternalSerializer serializer, Object obj)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException("incomplete");
	}

	public Object readObjectData(InternalDeserializer deserializer)
			throws IOException, StructDefineNotFoundException {
		throw new UnsupportedOperationException("incomplete");
	}

	static final String xml_element_types = "types";
	static final String xml_element_type = "type";
	static final String xml_attr_class = "class";
	static final String xml_attr_struct = "struct";
	static final String xml_value_struct_try = "try";
	static final String xml_value_struct_reject = "false";
	static final String xml_value_struct_strict = "true";
	static final String xml_value_struct_force = "force";
	static final String xml_attr_translator = "translator";

	@SuppressWarnings("unchecked")
	final static void loadCustomType(BundleStub bundle, SXElement typeE)
			throws Throwable {
		final Class<?> customClass = bundle.loadClass(typeE.getAttribute(xml_attr_class), null);
		if (customClass.isPrimitive()) {
			throw new UnsupportedOperationException("不支持自定义原始类型：" + customClass);
		}
		final String translatorClassName = typeE.getAttribute(xml_attr_translator);
		final DataObjectTranslator<?, ?> translator;
		if (translatorClassName != null && translatorClassName.length() != 0) {
			final Class<? extends DataObjectTranslator> translatorClass = bundle.loadClass(typeE.getAttribute(xml_attr_translator), DataObjectTranslator.class);
			final Class<?> sourceClass = TypeArgFinder.find(translatorClass, DataObjectTranslator.class, 0);
			if (sourceClass != customClass) {
				throw new UnsupportedOperationException("数据对象转换器类型不符：对象类型：\"" + customClass + "\"，转换器类型\"" + translatorClass + "<" + sourceClass + ",?>\"");
			}
			try {

				translator = Utils.publicAccessibleObject(translatorClass.getDeclaredConstructor()).newInstance();
			} catch (ExceptionInInitializerError e) {
				throw e.getException();
			}
		} else {
			translator = null;
		}
		final ObjectDataTypeInternal odti;
		final DataType dt;
		final String struct = typeE.getAttribute(xml_attr_struct, null);
		if (xml_value_struct_strict.equals(struct)) {
			dt = odti = getStaticStructDefine(customClass, false);
		} else if (xml_value_struct_force.equals(struct)) {
			dt = odti = getStaticStructDefine(customClass, true);
		} else if (xml_value_struct_reject.equals(struct)) {
			dt = dataTypeOfJavaClass(customClass, true);
			if (dt instanceof ObjectDataTypeInternal) {
				odti = (ObjectDataTypeInternal) dt;
			} else {
				odti = null;
			}
		} else {
			dt = dataTypeOfJavaClass(customClass, false);
			if (dt instanceof ObjectDataTypeInternal) {
				odti = (ObjectDataTypeInternal) dt;
			} else {
				odti = null;
			}
		}
		if (translator != null) {
			if (odti == null) {
				throw new UnsupportedOperationException("类型\"" + dt + "\"不支持自定义转换器");
			}
			final DataObjectTranslator<?, ?> old = odti.registerDataObjectTranslator(translator);
			if (old != null) {
				if (old.getClass() != translator.getClass()) {
					System.err.println("自定义对象类型警告：类型\"" + odti + "\"将自定义转换器\"" + old + "\"替换为\"" + translator + "\"");
				} else {
					System.err.println("自定义对象类型警告：类型\"" + odti + "\"重复设定自定义转换器\"");
				}
			}
		}
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////

	static final HashSet<Class<?>> unserializableClasses = new HashSet<Class<?>>();

	public boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		if (this.translator != null) {
			return serializer.writeCustomSerializeDataObject(object, this, this.translator);
		} else {
			boolean ok = serializer.writeUnserializable();
			if (ok && Cache.IN_DEBUG_MODE) {
				final boolean notPrinted;
				synchronized (unserializableClasses) {
					notPrinted = unserializableClasses.add(object.getClass());
				}
				if (notPrinted) {
					Cache.printWarningMessage("类型: " + object.getClass() + "不支持序列化");
				}
			}
			return ok;
		}
	}

}
