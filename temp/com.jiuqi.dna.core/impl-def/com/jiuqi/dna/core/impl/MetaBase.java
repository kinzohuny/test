package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXRenderable;

/**
 * 元数据基类
 * 
 * @author gaojingxin
 * 
 */
public abstract class MetaBase implements SXRenderable {
	/**
	 * 返回描述
	 */
	abstract String getDescription();

	/**
	 * 返回当前节点的XML标记名称
	 * 
	 * @return 返回当前节点的XML标记名称
	 */
	public abstract String getXMLTagName();

	/**
	 * 子类重载该方法将定义写入XML
	 * 
	 * @param element
	 *            当前节点元素
	 */
	public void render(SXElement element) {
	}

	/**
	 * 将该节点的XML插入到指定的元素内部
	 * 
	 * @param parent
	 *            父节点
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