/**
 * 
 */
package com.jiuqi.dna.core.impl;

import java.lang.reflect.Modifier;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.ws.impl.WSContextHolder;
import com.jiuqi.dna.core.ws.impl.WebServiceBinding;

/**
 * @author linfangchao
 *
 */
public class WSServiceBinding<TContext extends Context> extends ServiceBinding<TContext> {

	
	// 为了容错eclipse的工程classPath约束，将EndPoint - > Object
	private Object webServiceEndpoint;
	private ServiceBase<TContext> service;
	private WebServiceBinding binding;
	
	public WSServiceBinding(ServiceBase<TContext> service) {
		this.service = service;
		this.binding = new DNAWebServiceBinding(service);
	}
	
	public ServiceBase<TContext> getService() {
		return service;
	}

	public boolean bind() {
		if(binding==null)
			return false;
		if (!service.site.shared || this.webServiceEndpoint != null) {
			return true;
		}
		final Class<?> thisClass = this.getClass();
		if ((thisClass.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
			return true;
		}
		this.webServiceEndpoint = binding.instance(service.getClass());
		return true;
	}

	public boolean unbind() {
		if (this.webServiceEndpoint == null ||  binding==null)
			return true;
		this.webServiceEndpoint = binding.release(webServiceEndpoint);
		return true;
	}

}



class DNAWebServiceBinding extends WebServiceBinding {
	

	private ServiceBase<?> service;
	
	public DNAWebServiceBinding(ServiceBase<?> service) {
		this.service = service;
	}
	
//	@Override
	protected Object getTarget() {
		return service;
	}

	@Override
	protected boolean isIgnore(Class<?> type) {
		if (type == Context.class
				|| (type == ResourceContext.class && (service instanceof ResourceServiceBase))) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean isContext(Class<?> clazz) {
		return clazz == Context.class || clazz == ResourceContext.class;
	}

	@Override
	protected WSContextHolder createContextHolder() {
		return new DNAWSContextHolder(service);
	}

	@Override
	protected RuntimeException tryThrowException(Throwable throwable) {
		return Utils.tryThrowException(throwable);
	}
	
}

/**
 * WebService调用的DNA会话以及上下文信息
 */
 final class DNAWSContextHolder extends WSContextHolder {

	private ContextImpl<?, ?, ?> context;
	private SessionImpl session;

	public final ContextImpl<?, ?, ?> getContext() {
		return this.context;
	}

	public final void dispose(Throwable e) {
		if (this.context != null) {
			try {
				this.context.exception(e);
				this.context.endContextInvoke(this.context.occorAt, 0f, e != null ? -1f : 1f, (short) 0);
				this.context.dispose();
				this.context = null;
			} finally {
				this.session.dispose(0);
				this.session = null;
			}
		}
	}

	DNAWSContextHolder(SpaceNode occerAt) {
		final SessionImpl session = occerAt.site.application.sessionManager.newSession(SessionKind.REMOTE, BuildInUser.anonym, null, null);
		try {
			session.setHeartbeatTimeoutSec(0);
			session.setSessionTimeoutMinutes(0);
			final ContextImpl<?, ?, ?> context = session.newContext(occerAt, ContextKind.TRANSIENT);
			try {
				context.setNextStep(1);
				context.beginContextInvoke();
			} catch (Throwable e) {
				context.dispose();
				throw e;
			}
			this.context = context;
		} catch (Throwable e) {
			session.dispose(0);
			throw Utils.tryThrowException(e);
		}
		this.session = session;
	}
}