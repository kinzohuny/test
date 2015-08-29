package com.jiuqi.util;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Bundle������
 * @author huangkaibin
 *
 */
public class BundleUtils {

	/**
	 * ָ��һ��eclipse���̻�jar����·������ȡbundle id
	 * @param path eclipse���̻�jar����·��
	 * @return ���ָ����eclipse���̻�jar����OSGi��bundle���򷵻�bundle id�����򷵻�<code>null</code>
	 */
	public static String getBundleId(String path) {
		return getBundleId(new File(path));
	}

	/**
	 * ָ��һ��eclipse���̻�jar����·��File����ȡbundle id
	 * @param path eclipse���̻�jar����·��File
	 * @return ���ָ����eclipse���̻�jar����OSGi��bundle���򷵻�bundle id�����򷵻�<code>null</code>
	 */
	public static String getBundleId(File path) {
		Manifest manifest = ManifestUtil.getManifest(path);
		if (manifest == null) {
			return null;
		}
		Attributes attrs = manifest.getMainAttributes();
		String bundleId = ManifestUtil.getAttributeValue(attrs,
				ManifestConstants.BUNDLE_SYMBOLIC_NAME);
		return bundleId;
	}

	/**
	 * ָ��һ��eclipse���̻�jar����·�����ж��Ƿ�Ϊbundle
	 * @param path eclipse���̻�jar����·��
	 * @return ���ָ����eclipse���̻�jar����OSGi��bundle���򷵻�<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean isBundle(String path) {
		return isBundle(new File(path));
	}

	/**
	 * ָ��һ��eclipse���̻�jar����·�����ж��Ƿ�Ϊbundle
	 * @param path eclipse���̻�jar����·��
	 * @return ���ָ����eclipse���̻�jar����OSGi��bundle���򷵻�<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean isBundle(File path) {
		String id = getBundleId(path);
		return id != null && !"".equals(id);
	}

	/**
	 * ָ��һ��eclipse���̻�jar����·�����ж��Ƿ�Ϊhost bundle
	 * @param path eclipse���̻�jar����·��
	 * @return ���ָ����eclipse���̻�jar����host bundle���򷵻�<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean isHostBundle(String path) {
		return isHostBundle(new File(path));
	}

	/**
	 * ָ��һ��eclipse���̻�jar����·�����ж��Ƿ�Ϊhost bundle
	 * @param path eclipse���̻�jar����·��
	 * @return ���ָ����eclipse���̻�jar����host bundle���򷵻�<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean isHostBundle(File path) {
		Manifest manifest = ManifestUtil.getManifest(path);
		if (manifest == null) {
			return false;
		}
		Attributes attrs = manifest.getMainAttributes();
		String bundleId = ManifestUtil.getAttributeValue(attrs,
				ManifestConstants.BUNDLE_SYMBOLIC_NAME);
		if (bundleId == null) {
			return false;
		}
		String bundleLocalization = ManifestUtil.getAttributeValue(attrs,
				ManifestConstants.BUNDLE_LOCALIZATION);
		return bundleLocalization == null
				|| "plugin".equalsIgnoreCase(bundleLocalization);
	}

	/**
	 * ָ��һ��eclipse���̻�jar����·�����ж��Ƿ�Ϊfragment bundle
	 * @param path eclipse���̻�jar����·��
	 * @return ���ָ����eclipse���̻�jar����fragment bundle���򷵻�<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean isFragmentBundle(String path) {
		return isFragmentBundle(new File(path));
	}

	/**
	 * ָ��һ��eclipse���̻�jar����·�����ж��Ƿ�Ϊfragment bundle
	 * @param path eclipse���̻�jar����·��
	 * @return ���ָ����eclipse���̻�jar����fragment bundle���򷵻�<tt>true</tt>�����򷵻�<tt>false</tt>
	 */
	public static boolean isFragmentBundle(File path) {
		Manifest manifest = ManifestUtil.getManifest(path);
		if (manifest == null) {
			return false;
		}
		Attributes attrs = manifest.getMainAttributes();
		String bundleId = ManifestUtil.getAttributeValue(attrs,
				ManifestConstants.BUNDLE_SYMBOLIC_NAME);
		if (bundleId == null) {
			return false;
		}
		String fragmentHost = ManifestUtil.getAttributeValue(attrs,
				ManifestConstants.FRAGMENT_HOST);
		if (fragmentHost != null) {
			return true;
		}
		String bundleLocalization = ManifestUtil.getAttributeValue(attrs,
				ManifestConstants.BUNDLE_LOCALIZATION);
		return "fragment".equalsIgnoreCase(bundleLocalization);
	}

}
