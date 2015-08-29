package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXRenderable;

/**
 * Ԫ���ݻ���
 * 
 * @author gaojingxin
 * 
 */
public abstract class MetaBase implements SXRenderable {
	/**
	 * ��������
	 */
	abstract String getDescription();

	/**
	 * ���ص�ǰ�ڵ��XML�������
	 * 
	 * @return ���ص�ǰ�ڵ��XML�������
	 */
	public abstract String getXMLTagName();

	/**
	 * �������ظ÷���������д��XML
	 * 
	 * @param element
	 *            ��ǰ�ڵ�Ԫ��
	 */
	public void render(SXElement element) {
	}

	/**
	 * ���ýڵ��XML���뵽ָ����Ԫ���ڲ�
	 * 
	 * @param parent
	 *            ���ڵ�
	 */
	final SXElement renderInto(SXElement parent) {
		final SXElement to = parent.append(this.getXMLTagName());
		this.render(to);
		return to;
	}

	MetaBase(SXElement element) {
	}

	MetaBase() {
	}

	MetaBase(MetaBase sample) {
	}
}