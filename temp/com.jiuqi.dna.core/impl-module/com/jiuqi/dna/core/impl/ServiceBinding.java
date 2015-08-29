/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.log.DNALogManager;

/**
 * @author linfangchao
 *
 */
public abstract class ServiceBinding<TContext extends Context> {
	

	private static boolean[] failed = new boolean[] { false };
	
	@SuppressWarnings("unchecked")
	public static final <TContext extends Context> 
		ServiceBinding<TContext>[] createDefaults(ServiceBase<TContext> service) {

		return new ServiceBinding[] {
				createWSBinding(service, 0)	
		};
	}
	
	public static final <TContext extends Context> 
		ServiceBinding<TContext> newNull(ServiceBase<TContext> service, String mark) {
		return new NullServiceBinding<TContext>(service, mark);
	}
	
	private static final <TContext extends Context> ServiceBinding<TContext> createWSBinding(ServiceBase<TContext> service, int index) {
		if(!failed[index]) {
			try {
				return new WSServiceBinding<TContext>(service);
			} catch (NoClassDefFoundError e) {
				DNALogManager.getLogger("core/ws").logError(null, 
						"支持Web Service所需的插件（com.jiuqi.dna.core.ws）未启用。",
						false);
				failed[index] = true;
			}
		}
		return newNull(service, "WebServiceBinding");
	}
	
	public abstract ServiceBase<TContext> getService();
	
	public abstract boolean bind();
	
	public abstract boolean unbind();
}

class NullServiceBinding<TContext extends Context> extends ServiceBinding<TContext> {
	
	private ServiceBase<TContext> service;
	private String mark;
	
	public NullServiceBinding(ServiceBase<TContext> service, String mark) {
		this.service = service;
		this.mark = mark;
	}
	
	public String getMark() {
		return mark;
	}

	@Override
	public ServiceBase<TContext> getService() {
		return service;
	}

	@Override
	public boolean bind() {
		return true;
	}

	@Override
	public boolean unbind() {
		return true;
	}
	
}
