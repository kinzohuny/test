package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.def.model.ModelActionDeclare;
import com.jiuqi.dna.core.def.model.ModelInvokeStage;
import com.jiuqi.dna.core.impl.ModelServiceBase.ModelActionHandler;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.model.ModelService;

/**
 * 模型动作定义实现
 * 
 * @author gaojingxin
 * 
 */
final class ModelActionDefineImpl extends ModelInvokeDefineImpl implements
		ModelActionDeclare, ScriptCompilable {

	private ScriptImpl script;

	public final void tryCompileScript(ContextImpl<?, ?, ?> context) {
		if (this.script != null) {
			this.script.prepareAsAction(context, this);
		}
	}

	public final ScriptImpl getScript() {
		if (this.script == null) {
			this.script = new ScriptImpl(this.owner);
		}
		return this.script;
	}

	public ModelActionDefineImpl(ModelDefineImpl owner, String name,
			Class<?> aoClass) {
		super(owner, name, aoClass);
	}

	// ////////////////////////////
	// /// 模型访问
	// ////////////////////////////
	@SuppressWarnings("unchecked")
	private ModelActionHandler handler;

	@SuppressWarnings("unchecked")
	final void internalSetHandler(ModelActionHandler handler) {
		if (handler != null && handler != this.handler) {
			this.owner.checkModelServiceMO(handler);
			if (handler.aoClass != None.class && !handler.aoClass.isAssignableFrom(this.getAOClass())) {
				throw new UnsupportedOperationException("模型动作执行器与模型动作定义的参数不符");
			}
		}
		this.handler = handler;

	}

	@SuppressWarnings("unchecked")
	final void internalExecute(ContextImpl<?, ?, ?> context, Object mo,
			Object ao, ModelInvokeDefineImpl trigger, Object triggerAO,
			Object value, ModelInvokeStage stage) {
		if (context == null || mo == null || stage == null) {
			throw new NullPointerException();
		}
		if (stage == ModelInvokeStage.DOING) {
			this.arguments.checkSO(ao);
		}
		super.callBeforeInspects(context, mo, ao, value);
		try {
			if ((this.script == null || !this.script.executeScriptAsAction(context, mo, ao, trigger, triggerAO, value, this)) && this.handler != null) {
				SpaceNode occorAtSave = this.handler.getService().updateContextSpace(context);
				try {
					this.handler.doAction(context, mo, ao, this, trigger, triggerAO, value, stage);
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				} finally {
					occorAtSave.updateContextSpace(context);
				}
			}
			super.callAfterInspects(context, mo, ao, value);
		} finally {
			super.callFinallyInspects(context, mo, ao, value);
		}
	}

	public final void execute(Context context, Object mo) {
		this.internalExecute(ContextImpl.toContext(context), mo, None.NONE, null, null, null, ModelInvokeStage.DOING);
	}

	public final void execute(Context context, Object mo, Object ao) {
		this.internalExecute(ContextImpl.toContext(context), mo, ao, null, null, null, ModelInvokeStage.DOING);
	}

	@SuppressWarnings("unchecked")
	public final ModelService<?>.ModelActionHandler<?> setHandler(
			ModelService<?>.ModelActionHandler<?> handler) {
		ModelActionHandler old = this.handler;
		this.internalSetHandler(handler);
		return (ModelService.ModelActionHandler) old;
	}

	// ////////////////////////////
	// /// XML
	// ////////////////////////////
	static final String xml_element_action = "action";
	static final String xml_attr_handler = "handler";

	@Override
	public final String getXMLTagName() {
		return xml_element_action;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		if (this.script != null) {
			this.script.tryRender(element);
		}
		if (this.handler != null) {
			element.setAttribute(xml_attr_handler, this.handler.getClass().getName());
		}
	}

	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		SXElement scriptE = element.firstChild(ScriptImpl.xml_element_script);
		if (scriptE != null) {
			this.getScript().merge(scriptE, helper);
		}
		String handlerClassName = element.getAttribute(xml_attr_handler, null);
		if (handlerClassName != null && handlerClassName.length() > 0) {
			this.handler = helper.querier.find(ModelActionHandler.class, handlerClassName);
		}

	}
}
