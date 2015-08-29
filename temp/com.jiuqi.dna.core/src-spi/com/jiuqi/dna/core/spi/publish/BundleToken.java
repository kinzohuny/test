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
	 * ����ǰBundleToken��Ϊһ�����������������ķ�����BundleTokenû��ʵ�ʵ����𣬲�ͬ�ĵط����ڸ�ClassLoader�����ṩ��������������Ϳ��ʹ�á�
	 * 
	 * @return ��Bundle���������
	 */
	public ClassLoader asClassLoader();

	/**
	 * ����ǰBundleToken���һ��Bridge��Ϊһ������������ڸ�Bundle�޷����������Դʱ��ʹ��Bridge�����������Դ��
	 * 
	 * @param bridge ��Ϊ���õ�����������ڸ�Bundle�޷����������Դʱ��ʹ��Bridge�����������Դ��
	 * @return ��Bundle��Bridge��ϵ��������
	 * @see BundleToken#asClassLoader()
	 */
	public ClassLoader asClassLoader(ClassLoader bridge);
}
