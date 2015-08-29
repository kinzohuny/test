package com.jiuqi.util;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Bundle工具类
 * @author huangkaibin
 *
 */
public class BundleUtils {

	/**
	 * 指定一个eclipse工程或jar包的路径，获取bundle id
	 * @param path eclipse工程或jar包的路径
	 * @return 如果指定的eclipse工程或jar包是OSGi的bundle，则返回bundle id，否则返回<code>null</code>
	 */
	public static String getBundleId(String path) {
		return getBundleId(new File(path));
	}

	/**
	 * 指定一个eclipse工程或jar包的路径File，获取bundle id
	 * @param path eclipse工程或jar包的路径File
	 * @return 如果指定的eclipse工程或jar包是OSGi的bundle，则返回bundle id，否则返回<code>null</code>
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
	 * 指定一个eclipse工程或jar包的路径，判断是否为bundle
	 * @param path eclipse工程或jar包的路径
	 * @return 如果指定的eclipse工程或jar包是OSGi的bundle，则返回<tt>true</tt>，否则返回<tt>false</tt>
	 */
	public static boolean isBundle(String path) {
		return isBundle(new File(path));
	}

	/**
	 * 指定一个eclipse工程或jar包的路径，判断是否为bundle
	 * @param path eclipse工程或jar包的路径
	 * @return 如果指定的eclipse工程或jar包是OSGi的bundle，则返回<tt>true</tt>，否则返回<tt>false</tt>
	 */
	public static boolean isBundle(File path) {
		String id = getBundleId(path);
		return id != null && !"".equals(id);
	}

	/**
	 * 指定一个eclipse工程或jar包的路径，判断是否为host bundle
	 * @param path eclipse工程或jar包的路径
	 * @return 如果指定的eclipse工程或jar包是host bundle，则返回<tt>true</tt>，否则返回<tt>false</tt>
	 */
	public static boolean isHostBundle(String path) {
		return isHostBundle(new File(path));
	}

	/**
	 * 指定一个eclipse工程或jar包的路径，判断是否为host bundle
	 * @param path eclipse工程或jar包的路径
	 * @return 如果指定的eclipse工程或jar包是host bundle，则返回<tt>true</tt>，否则返回<tt>false</tt>
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
	 * 指定一个eclipse工程或jar包的路径，判断是否为fragment bundle
	 * @param path eclipse工程或jar包的路径
	 * @return 如果指定的eclipse工程或jar包是fragment bundle，则返回<tt>true</tt>，否则返回<tt>false</tt>
	 */
	public static boolean isFragmentBundle(String path) {
		return isFragmentBundle(new File(path));
	}

	/**
	 * 指定一个eclipse工程或jar包的路径，判断是否为fragment bundle
	 * @param path eclipse工程或jar包的路径
	 * @return 如果指定的eclipse工程或jar包是fragment bundle，则返回<tt>true</tt>，否则返回<tt>false</tt>
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
