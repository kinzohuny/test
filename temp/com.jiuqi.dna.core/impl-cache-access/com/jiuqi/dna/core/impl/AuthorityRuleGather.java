/**
 * 
 */
package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.auth.AuthorityRule;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;
import com.jiuqi.dna.core.spi.publish.NamedFactory;
import com.jiuqi.dna.core.spi.publish.NamedFactoryElement;

/**
 * @author yangduanxue
 * 
 */
final class AuthorityRuleGather extends
		NamedFactory<AuthorityRule<?>, NamedFactoryElement> {

	private static final Map<Class<?>, List<AuthorityRule<?>>> rules = new HashMap<Class<?>, List<AuthorityRule<?>>>();

	public static final List<AuthorityRule<?>> getRules(Class<?> facadeClass) {
		return rules.get(facadeClass);
	}

	public static final Set<Class<?>> getRuleGroups() {
		return rules.keySet();
	}

	@Override
	protected AuthorityRule<?> doNewElement(Context context,
			NamedFactoryElement meta, Object... adArgs) {
		return null;
	}

	@Override
	protected NamedFactoryElement parseElement(SXElement element,
			BundleToken bundle) throws Throwable {
		AuthorityRoleElement ruleElement = new AuthorityRoleElement(element, bundle);
		AuthorityRule<?> rule = ruleElement.clazz.newInstance();
		Class<?> facade = rule.getFacadeClass();
		List<AuthorityRule<?>> rls = rules.get(facade);
		if (rls == null) {
			rls = new ArrayList<AuthorityRule<?>>();
			rules.put(facade, rls);
		}
		rls.add(rule);
		return ruleElement;
	}

	static final class AuthorityRoleElement extends NamedFactoryElement {

		public static final String xml_attr_class = "class";
		public Class<AuthorityRule> clazz;

		public AuthorityRoleElement(SXElement element, BundleToken bundle)
				throws ClassNotFoundException {
			super(element.getAttribute(xml_attr_class));
			this.clazz = bundle.loadClass(element.getAttribute(xml_attr_class), AuthorityRule.class);
		}
	}
}