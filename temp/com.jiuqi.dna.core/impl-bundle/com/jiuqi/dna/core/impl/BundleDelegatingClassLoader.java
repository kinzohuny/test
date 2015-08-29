package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.framework.Bundle;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * ClassLoader backed by an OSGi bundle. Provides the ability to use a separate
 * class loader as fall back.
 * 
 * Contains facilities for tracing class loading behaviour so that issues can be
 * easily resolved.
 * 
 * 
 * @author Adrian Colyer
 * @author Andy Piper
 * @author Costin Leau
 * @author linfangchao
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class BundleDelegatingClassLoader extends ClassLoader {

	private static final String NULL_STRING = "null";

	/**
	 * Returns the bundle name and symbolic name - useful when logging bundle
	 * info.
	 * 
	 * @param bundle
	 *            OSGi bundle (can be null)
	 * @return the bundle name and symbolic name
	 */
	public static String nullSafeNameAndSymName(Bundle bundle) {
		if (bundle == null)
			return NULL_STRING;

		Dictionary dict = bundle.getHeaders();

		if (dict == null)
			return NULL_STRING;

		StringBuffer buf = new StringBuffer();
		String name = (String) dict.get(org.osgi.framework.Constants.BUNDLE_NAME);
		if (name == null)
			buf.append(NULL_STRING);
		else
			buf.append(name);
		buf.append(" (");
		String sname = (String) dict.get(org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME);

		if (sname == null)
			buf.append(NULL_STRING);
		else
			buf.append(sname);

		buf.append(")");

		return buf.toString();
	}

	private final ClassLoader bridge;

	private final Bundle backingBundle;

	/**
	 * Factory method for creating a class loader over the given bundle.
	 * 
	 * @param aBundle
	 *            bundle to use for class loading and resource acquisition
	 * @return class loader adapter over the given bundle
	 */
	public static BundleDelegatingClassLoader createBundleClassLoaderFor(
			Bundle aBundle) {
		return createBundleClassLoaderFor(aBundle, null);
	}

	/**
	 * Factory method for creating a class loader over the given bundle and with
	 * a given class loader as fall-back. In case the bundle cannot find a class
	 * or locate a resource, the given class loader will be used as fall back.
	 * 
	 * @param bundle
	 *            bundle used for class loading and resource acquisition
	 * @param bridge
	 *            class loader used as fall back in case the bundle cannot load
	 *            a class or find a resource. Can be <code>null</code>
	 * @return class loader adapter over the given bundle and class loader
	 */
	public static BundleDelegatingClassLoader createBundleClassLoaderFor(
			final Bundle bundle, final ClassLoader bridge) {
		return (BundleDelegatingClassLoader) AccessController.doPrivileged(new PrivilegedAction() {

			public Object run() {
				return new BundleDelegatingClassLoader(bundle, bridge);
			}
		});
	}

	/**
	 * Private constructor.
	 * 
	 * Constructs a new <code>BundleDelegatingClassLoader</code> instance.
	 * 
	 * @param bundle
	 * @param bridgeLoader
	 */
	protected BundleDelegatingClassLoader(Bundle bundle,
			ClassLoader bridgeLoader) {
		super(null);
		if (bundle == null)
			throw new NullArgumentException("bundle should be non-null");
		this.backingBundle = bundle;
		this.bridge = bridgeLoader;
	}

	protected Class findClass(String name) throws ClassNotFoundException {
		try {
			return this.backingBundle.loadClass(name);
		} catch (ClassNotFoundException cnfe) {
			throw new ClassNotFoundException(name + " not found from bundle [" + backingBundle.getSymbolicName() + "]", cnfe);
		} catch (NoClassDefFoundError ncdfe) {
			NoClassDefFoundError e = new NoClassDefFoundError(name + " not found from bundle [" + nullSafeNameAndSymName(backingBundle) + "]");
			e.initCause(ncdfe);
			throw e;
		}
	}

	protected URL findResource(String name) {
		return this.backingBundle.getResource(name);
	}

	protected Enumeration findResources(String name) throws IOException {
		return this.backingBundle.getResources(name);
	}

	public URL getResource(String name) {
		URL resource = findResource(name);
		if (bridge != null && resource == null) {
			resource = bridge.getResource(name);
		}
		return resource;
	}

	protected Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class clazz = null;
		try {
			clazz = findClass(name);
		} catch (ClassNotFoundException cnfe) {
			if (bridge != null)
				clazz = bridge.loadClass(name);
			else
				throw cnfe;
		}
		if (resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}

	public String toString() {
		return "BundleDelegatingClassLoader for [" + nullSafeNameAndSymName(backingBundle) + "]";
	}

	/**
	 * Returns the bundle to which this class loader delegates calls to.
	 * 
	 * @return the backing bundle
	 */
	public Bundle getBundle() {
		return backingBundle;
	}

}
