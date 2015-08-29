package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.MetaElementTemplateParams;
import com.jiuqi.dna.core.impl.DeclaratorBase;
import com.jiuqi.dna.core.impl.ModelDefineImpl;
import com.jiuqi.dna.core.misc.TypeArgFinder;

/**
 * Ä£ÐÍÉùÃ÷Æ÷
 * 
 * @author gaojingxin
 * 
 */
public abstract class ModelDeclarator<TMO> extends DeclaratorBase {

	public ModelDeclarator(MetaElementTemplateParams params) {
		super(true);
		Class<?> moClass = TypeArgFinder.find(this.getClass(),
		        ModelDeclarator.class, 0);
		if (moClass == null) {
			moClass = params.getParam(Class.class);
		}
		this.model = new ModelDefineImpl(params.getName(), moClass, this);
	}

	public ModelDeclarator(String name) {
		super(true);
		this.model = new ModelDefineImpl(name, TypeArgFinder.get(this
		        .getClass(), ModelDeclarator.class, 0), this);
	}

	@Override
	public final ModelDefine getDefine() {
		return this.model;
	}

	protected final ModelDeclare model;
	// //////////////////////////////////////////////////
	private final static Class<?>[] intf_classes = { ModelDefine.class };

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return intf_classes;
	}
}
