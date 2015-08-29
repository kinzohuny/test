package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.ModifyStatementDeclarator;
import com.jiuqi.dna.core.def.query.StatementDeclarator;
import com.jiuqi.dna.core.def.query.StoredProcedureDeclarator;
import com.jiuqi.dna.core.impl.PublishedDeclarator.CreateStep;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;

/**
 * 针对commands元素的收集器.需要特殊处理在旧有的注册成为command的存储过程定义.
 * 
 * @author houchunlei
 * 
 */
public final class MalformedPublishedCommandGather extends
		PublishedElementGatherer<PublishedDeclarator> {

	@Override
	protected PublishedDeclarator parseElement(SXElement element,
			BundleToken bundle) throws Throwable {
		return new PublishedDeclarator(bundle.loadClass(element.getAttribute(PublishedElementGatherer.xml_attr_class), StatementDeclarator.class));
	}

	static final Class<?> command_declarator_clz = ModifyStatementDeclarator.class;

	static final Class<?> procedure_declarator_clz = StoredProcedureDeclarator.class;

	@Override
	final void afterGatherElement(PublishedDeclarator pe, ResolveHelper helper) {
		CreateStep step = null;
		if (command_declarator_clz.isAssignableFrom(pe.clazz)) {
			step = PublishedDeclarator.command_create;
		} else if (procedure_declarator_clz.isAssignableFrom(pe.clazz)) {
			System.err.println("本地存储过程定义[" + pe.clazz.getName() + "]使用commands元素注册，期望使用procedure元素注册。");
			step = PublishedDeclarator.procedure_create;
		}
		if (step != null) {
			helper.regStartupEntry(step, pe);
			step.tryLoadScript(pe, helper);
		}
	}
}
