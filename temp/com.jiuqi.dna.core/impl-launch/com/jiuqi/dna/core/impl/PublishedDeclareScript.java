package com.jiuqi.dna.core.impl;

import java.net.URL;

import com.jiuqi.dna.core.def.DNASqlType;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;

/**
 * 声明脚本信息缓存
 * 
 * @author gaojingxin
 * 
 */
final class PublishedDeclareScript extends PublishedElement {
	final URL url;
	final String declareName;
	final DNASqlType type;

	PublishedDeclareScript(SXElement element, BundleToken bundle) {
		final String path = element.getString(xml_element_path);
		final int pathL;
		if (path == null || (pathL = path.length()) == 0) {
			throw new IllegalArgumentException("element.path");
		}
		final DNASqlType type = DNASqlType.typeOfResourcePath(path);
		if (type == null) {
			throw new MissingObjectException("不支持[" + path + "]类型的声明脚本");
		}
		this.url = bundle.getResource(path);
		this.type = type;
		final int pointAt = pathL - type.declareScriptPostfix.length() - 1;
		final int nameStart = path.lastIndexOf('/', pointAt - 1) + 1;
		this.declareName = path.substring(nameStart, pointAt);
	}

	final static String xml_element_path = "path";

}