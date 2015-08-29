/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * @author yangduanxue
 * 
 */
final class BundleClassPath extends MetaBase {

	final String path;

	BundleClassPath(String path) {
		if (path == null || path.length() == 0) {
			throw new NullArgumentException("path");
		}
		this.path = path;
	}

	// ///////////////////////////////
	// ///////////XML
	// ///////////////////////////////
	private final static String xml_element_bundle_classpath = "bundle-classpath";

	@Override
	final String getDescription() {
		return "BundleClassPath";
	}

	@Override
	public final String getXMLTagName() {
		return xml_element_bundle_classpath;
	}
}
