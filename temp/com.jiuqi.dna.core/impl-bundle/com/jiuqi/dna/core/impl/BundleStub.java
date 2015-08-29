package com.jiuqi.dna.core.impl;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.jiuqi.dna.core.impl.Utils.ObjectAccessor;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXElementBuilder;
import com.jiuqi.dna.core.spi.publish.BundleToken;

/**
 * Bundle存根
 * 
 * @author gaojingxin
 * 
 */
public final class BundleStub implements BundleToken {

	public final String name;
	public final Version version;
	public final ApplicationImpl application;

	public final String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	private final Bundle bundle;

	final boolean sameBundle(Bundle bundle) {
		return this.bundle == bundle;
	}

	final BundleContext getBundleContext() {
		return this.getHost().getBundleContext();
	}

	/**
	 * 发布元素的信息
	 */
	final SXElement dna;

	/**
	 * 下一个同名但版本低的bundle
	 */
	BundleStub next;

	final int gatherElement(Site site, ResolveHelper helper) {
		int count = 0;
		if (this.dna != null) {
			final Map<String, PublishedElementGathererGroup> gathererGroupMap = this.application.gathererGroupMap;
			for (SXElement publish = this.dna.firstChild(Site.xml_element_publish); publish != null; publish = publish.nextSibling(Site.xml_element_publish)) {
				for (SXElement group = publish.firstChild(); group != null; group = group.nextSibling()) {
					final PublishedElementGathererGroup gathererGroup = gathererGroupMap.get(group.name);
					if (gathererGroup != null) {
						for (SXElement element = group.firstChild(); element != null; element = element.nextSibling()) {
							count += gathererGroup.gatherElement(site, this, element, helper);
						}
					}
				}
			}
		}
		return count;
	}

	private volatile Bundle host;

	@SuppressWarnings({ "unchecked" })
	private final static Bundle getHost(Bundle bundle) {
		Class<?> bundleClass = bundle.getClass();
		if (bundleClass.getName().equals("org.eclipse.osgi.framework.internal.core.BundleFragment")) {
			if (BundleFragment_hosts_getter == null) {
				synchronized (BundleStub.class) {
					if (BundleFragment_hosts_getter == null) {
						BundleFragment_hosts_getter = Utils.newObjectAccessor((Class) bundleClass, Object.class, "hosts");
					}
				}
			}
			Object hosts = BundleFragment_hosts_getter.get(bundle);
			if ((hosts == null) || (Array.getLength(hosts) == 0)) {
				return null;
			}
			Object hostProxy = Array.get(hosts, 0);
			if (hostProxy == null) {
				return null;
			} else if (hostProxy instanceof Bundle) {
				return (Bundle) hostProxy;
			}
			if (BundleLoaderProxy_bundle_getter == null) {
				synchronized (BundleStub.class) {
					if (BundleLoaderProxy_bundle_getter == null) {
						BundleLoaderProxy_bundle_getter = Utils.newObjectAccessor((Class) hostProxy.getClass(), Bundle.class, "bundle");
					}
				}
			}
			return BundleLoaderProxy_bundle_getter.isReadable() ? BundleLoaderProxy_bundle_getter.get(hostProxy) : null;
		} else {
			return bundle;
		}
	}

	private final Bundle getHost() {
		Bundle host = this.host;
		if (host != null) {
			return host;
		}
		synchronized (this) {
			host = this.host;
			if (host == null) {
				this.host = host = BundleStub.getHost(this.bundle);
			}
		}
		if (host == null) {
			throw new UnsupportedOperationException("host of bundle[" + this.name + "] not found");
		}
		return host;
	}

	@SuppressWarnings("unchecked")
	public final <T> Class<T> loadClass(String className, Class<T> baseClass)
			throws ClassNotFoundException {
		Class<T> clazz;
		try {
			clazz = (Class<T>) this.getHost().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("class in bundle[" + this.name + "] not found: " + className, e);
		} catch (UnsupportedClassVersionError e) {
			throw new UnsupportedClassVersionError("class in bundle[" + this.name + "] has unsupported class version: " + className);
		}
		if ((baseClass != null) && (baseClass != Object.class) && !baseClass.isAssignableFrom(clazz)) {
			throw new ClassNotFoundException("class " + className + " in bundle[" + this.name + "] is not the sub class of " + baseClass);
		}
		return clazz;
	}

	private ClassLoader classLoader;

	public ClassLoader asClassLoader() {
		if (classLoader == null) {
			synchronized (this) {
				if (classLoader == null) {
					classLoader = BundleDelegatingClassLoader.createBundleClassLoaderFor(bundle);
				}
			}
		}
		return classLoader;
	}

	public ClassLoader asClassLoader(ClassLoader bridge) {
		if (bridge == null)
			return asClassLoader();
		return BundleDelegatingClassLoader.createBundleClassLoaderFor(bundle, bridge);
	}

	public final URL findResource(String path) {
		return this.getHost().getResource(path);
	}

	public final URL getResource(String path) throws MissingObjectException {
		final URL resource = this.getHost().getResource(path);
		if (resource == null) {
			throw new MissingObjectException("bundle[" + this.name + "]未找到资源[" + path + "]");
		}
		return resource;
	}

	final static String entry_file_dna = "/dna.xml";
	final static String xml_element_dna = "dna";
	final static String xml_element_factory = "factory";
	final static String bundle_version = "Bundle-Version";

	private static volatile ObjectAccessor<Object, Object> BundleFragment_hosts_getter;
	private static volatile ObjectAccessor<Object, Bundle> BundleLoaderProxy_bundle_getter;

	BundleStub(Bundle bundle, SXElementBuilder sxBuilder,
			ApplicationImpl application) {
		if (bundle == null || sxBuilder == null || application == null) {
			throw new NullPointerException();
		}
		this.application = application;
		this.bundle = bundle;
		this.name = bundle.getSymbolicName();
		this.version = ManifestParser.parseVersion((String) bundle.getHeaders().get(BundleStub.bundle_version));
		URL url = bundle.getEntry(BundleStub.entry_file_dna);
		SXElement dna = null;
		if (url != null) {
			try {
				try {
					dna = sxBuilder.build(url).firstChild(BundleStub.xml_element_dna);
				} catch (Throwable e) {
					throw new RuntimeException("解析dna.xml错误：" + bundle.getSymbolicName(), e);
				}
				if (dna != null) {
					application.loadBaseConfigs(this, dna);
				}
			} catch (Throwable e) {
				application.catcher.catchException(e, this);
			}
		}
		this.dna = dna;
	}

}
