package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;
import com.jiuqi.dna.core.type.Convert;

public class PublishedInfoGroupLGatherer extends
		PublishedElementGatherer<PublishedElement> {

	final static String xml_attr_path = "path";
	final static String xml_attr_language = "language";

	private final boolean parseLanguage(SXElement element) {
		element = element.getParent();
		if (element == null) {
			return false;
		}
		final String language = element.getAttribute(xml_attr_language, null);
		if (language == null || language.length() == 0) {
			return false;
		}
		this.localeKey = LocaleInterned.getLocaleKey(language);
		return true;
	}

	private final PublishedElement flag = new PublishedElement();
	private String infoGroupFullName;
	private int localeKey;
	private ArrayList<String> infoNameMessages = new ArrayList<String>();

	@Override
	protected final PublishedElement parseElement(SXElement element,
			BundleToken bundle) throws Throwable {

		if (!this.parseLanguage(element)) {
			return null;
		}
		final String path = element.getAttribute(xml_attr_path, null);
		if (path == null || path.length() == 0) {
			return null;
		}
		this.infoNameMessages.clear();
		final CsvReader reader = new CsvReader(bundle.getResource(path).openStream(), Convert.utf8);
		try {
			while (reader.readRecord()) {
				String infoName = reader.get(0);
				if (infoName == null || infoName.length() == 0) {
					continue;
				}
				String infoValue = reader.get(2);
				if (infoValue == null || infoValue.length() == 0) {
					continue;
				}
				this.infoNameMessages.add(infoName);
				this.infoNameMessages.add(infoValue);
			}
		} finally {
			reader.close();
		}
		this.infoGroupFullName = (path.endsWith(".lg") ? path.substring(0, path.length() - 3) : path).replace('/', '.');
		return this.infoNameMessages.isEmpty() ? null : this.flag;
	}

	@Override
	void afterGatherElement(PublishedElement pe, ResolveHelper helper) {
		pe.bundle = null;
		pe.space = null;
		final String[] infoNameMessages = this.infoNameMessages.toArray(new String[this.infoNameMessages.size()]);
		this.infoNameMessages.clear();
		helper.regInfoGroupLanguage(this.infoGroupFullName, this.localeKey, infoNameMessages);
	}
}
