package com.jiuqi.dna.core.spi.publish;

import java.net.URL;

import com.jiuqi.dna.core.misc.MissingObjectException;

public interface BundleToken {
	public <T> Class<T> loadClass(String className, Class<T> baseClass)
			throws ClassNotFoundException;

	public URL getResource(String path) throws MissingObjectException;

	public URL findResource(String path);

	public String getName();

	/**
	 * 将当前BundleToken作为一个类加载器，加载类的方法与BundleToken没有实质的区别，不同的地方在于该ClassLoader可以提供给第三方的组件和框架使用。
	 * 
	 * @return 该Bundle的类加载器
	 */
	public ClassLoader asClassLoader();

	/**
	 * 将当前BundleToken结合一个Bridge作为一个类加载器。在该Bundle无法加载类或资源时，使用Bridge来加载类或资源。
	 * 
	 * @param bridge 作为备用的类加载器；在该Bundle无法加载类或资源时，使用Bridge来加载类或资源。
	 * @return 该Bundle与Bridge结合的类加载器
	 * @see BundleToken#asClassLoader()
	 */
	public ClassLoader asClassLoader(ClassLoader bridge);
}
