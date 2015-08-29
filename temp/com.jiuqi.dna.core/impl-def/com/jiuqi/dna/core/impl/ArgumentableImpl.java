package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.arg.ArgumentableDeclare;
import com.jiuqi.dna.core.def.obja.DynamicObject;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.type.DataTypable;
import com.jiuqi.dna.core.type.DataType;

/**
 * 有参数类型的实现基类
 * 
 * @author gaojingxin
 * 
 */
abstract class ArgumentableImpl extends NamedDefineImpl implements
		ArgumentableDeclare, Prepareble {

	public final Class<?> getAOClass() {
		return this.arguments.soClass;
	}

	public final Object newAO() {
		return this.arguments.newInitedSO();
	}

	public final Object newAO(Object... args) {
		return this.arguments.valuesAsSo(args);
	}

	final StructDefineImpl arguments;
	final boolean ownArguments;

	public final StructFieldDefineImpl getArgument(String name) {
		return this.arguments.fields.get(name);
	}

	ArgumentableImpl(String name, Class<?> soClass) {
		super(name);
		this.arguments = new ArgumentsDefine(soClass);
		this.ownArguments = true;
	}

	ArgumentableImpl(String name, StructDefineImpl argumentsRef) {
		super(name);
		if (argumentsRef == null) {
			throw new NullPointerException();
		}
		this.arguments = argumentsRef;
		this.ownArguments = false;
	}

	private final void checkArgumentModifiable() {
		if (!this.ownArguments) {
			throw new UnsupportedOperationException("该对象引用其他对象的参数，不支持自定义参数");
		}
	}

	/**
	 * 新增参数
	 */
	public final StructFieldDefineImpl newArgument(String name, DataType type) {
		this.checkArgumentModifiable();
		return this.arguments.newField(name, type);
	}

	/**
	 * 新增参数
	 */
	public final StructFieldDefineImpl newArgument(String name,
			DataTypable typable) {
		this.checkArgumentModifiable();
		return this.arguments.newField(name, typable);
	}

	/**
	 * 新增参数
	 */
	public final StructFieldDefineImpl newArgument(FieldDefine sample) {
		this.checkArgumentModifiable();
		return this.arguments.newField(sample);
	}

	public final NamedDefineContainerImpl<StructFieldDefineImpl> getArguments() {
		return this.arguments.fields;
	}

	/**
	 * 子类重载此方法准备定义的各种内部需要
	 * 
	 * @param context
	 */
	public void doPrepare(ContextImpl<?, ?, ?> context) throws Throwable {
		if (this.ownArguments) {
			this.arguments.prepareAccessInfo();
		}
	}

	private volatile boolean prepared;

	public final boolean isPrepared() {
		return this.prepared;
	}

	public final void ensurePrepared(ContextImpl<?, ?, ?> context,
			boolean rePrepared) {
		if (rePrepared || !this.prepared) {
			try {
				synchronized (this) {
					if (rePrepared || !this.prepared) {
						this.doPrepare(context);
						this.prepared = true;
					}
				}
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	// ///////////////////////////
	// //// XML
	// //////////////////////////
	final static String xml_attr_aoClass = "aoClass";
	final static String xml_element_arguments = "arguments";
	final static String xml_element_argument = "argument";

	final static Class<?> getAOClass(SXMergeHelper helper, SXElement element) {
		String className = element.getAttribute(ArgumentableImpl.xml_attr_aoClass, null);
		if ((className == null) || (className.length() == 0) || className.equals(None.class.getName())) {
			return None.class;
		} else if (className.equals(DynamicObject.class.getName())) {
			return DynamicObject.class;
		} else {
			return helper.querier.get(Class.class, className);
		}
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		if (this.ownArguments && (this.arguments.soClass != None.class)) {
			element.setAttribute(ArgumentableImpl.xml_attr_aoClass, this.arguments.soClass.getName());
			if (!this.arguments.fields.isEmpty()) {
				this.arguments.fields.renderInto(element, ArgumentableImpl.xml_element_arguments, ArgumentableImpl.xml_element_argument, 0);
			}
		}
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		if (this.ownArguments) {
			SXElement args = element.firstChild(ArgumentableImpl.xml_element_arguments);
			if (args != null) {
				this.arguments.mergeFields(args, helper, ArgumentableImpl.xml_element_argument);
			}
		}
	}

}
