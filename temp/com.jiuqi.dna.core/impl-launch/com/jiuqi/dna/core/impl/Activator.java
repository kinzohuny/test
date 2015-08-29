package com.jiuqi.dna.core.impl;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.spi.application.IApplicationLauncher;

public class Activator implements BundleActivator /*,ServiceTrackerCustomizer*/ {
//	private ServerServiceTracker serverServiceTracker;
	private static BundleContext context;
	static BundleContext getContext() {
		return context;
	}
//	private static Class httpServerClass;
//	public final static Application getDefaultApp() {
//		return ApplicationImpl.getDefaultApp();
//
//	}
	private ServiceRegistration<?> appLauncherRegistration;

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		

		if(Application.DEBUG_LAUNCH) {
			System.out.println("Enter DNA Core Activator.start");
		}
		
//		serverServiceTracker = new ServerServiceTracker(context);
//		serverServiceTracker.open();
//		ApplicationImpl.startApp(context);
		
		IApplicationLauncher appLauncher = new ApplicationLauncherImpl(context);

		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("com.jiuqi.dna.core.appLauncher", appLauncher.getClass().getName());
		props.put(Constants.SERVICE_DESCRIPTION, "DNA Application Launcher");
		appLauncherRegistration = context.registerService(IApplicationLauncher.class, appLauncher, props);
		

		if(Application.DEBUG_LAUNCH) {
			System.out.println("Exit DNA Core Activator.start");
		}
	}

	public void stop(BundleContext context) throws Exception {

		if(appLauncherRegistration!=null) {
			appLauncherRegistration.unregister();
			appLauncherRegistration = null;
		}
//		if(serverServiceTracker!=null)
//			serverServiceTracker.close();
//		ApplicationImpl.stopApp();
		Activator.context = null;
//		this.serverServiceTracker=null;
//		Activator.httpServerClass = null;
	}
//
//	public Object addingService(ServiceReference arg0) {
//		Object service = this.context.getService(arg0);
//		if (service instanceof IExtensionRegistry) {
//			IExtensionRegistry registry = (IExtensionRegistry) service;
//			IExtensionPoint point = registry.getExtensionPoint("com.jiuqi.dna.core.httpserver");
//			findHttpServer(point.getExtensions());
//		}
//		return null;
//	}
//	private void findHttpServer(IExtension[] es) {
//		if(es!=null){
//			find : {
//				for (int i = 0; i < es.length; i++) {
//					IExtension extension = es[i];
//					IConfigurationElement[] elements = extension.getConfigurationElements();
//					if (elements != null) {
//						for (int j = 0; j < elements.length; j++) {
//							Bundle[] bs = this.context.getBundles();
//							for(Bundle b : bs){
//								if(b.getSymbolicName().equals(elements[j].getAttribute("id"))){
//									try {
//										Activator.httpServerClass = b.loadClass(elements[j].getAttribute("class"));
//									} catch (Exception e) {
//										e.printStackTrace();
//									} 
//									break;
//								}
//							}
//							break find;
//						}
//					}
//				}
//				
//			}
//		}
//	}
//	public void modifiedService(ServiceReference arg0, Object arg1) {
//		
//	}
//
//	public void removedService(ServiceReference arg0, Object arg1) {
//		
//	}
//	public static Class getHttpServerClass(){
//		return Activator.httpServerClass;
//	}
}
