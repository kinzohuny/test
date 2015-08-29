package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.DataObjectTranslator;

public class DOT_SXElement implements DataObjectTranslator<SXElement, Object[]> {

	private enum Mark {
		/**
		 * 返回至上一层
		 */
		RETURN,
		/**
		 * SXElement 属性开始标记
		 */
		ATTRISTART,
		/**
		 * SXElement 属性结束标记
		 */
		ATTRIEND,
		/**
		 * SXElement 文本标记
		 */
		TEXT,
		/**
		 * SXElement CData标记
		 */
		CDATA
	}

	private static final short VERSION = 0x0100;

	/**
	 * SXElement序列化方法
	 * 
	 * @param root
	 * @param delegate
	 */
	private static final void toDelegateObject(final SXElement root,
			final ArrayList<Object> delegate) {
		writeElement(root, delegate);
		final SXElement firstChild = root.firstChild();
		if (firstChild != null) {
			toDelegateObject(firstChild, delegate);
		}
		final SXElement nextSibling = root.nextSibling();
		delegate.add(Mark.RETURN);
		if (nextSibling != null) {
			toDelegateObject(nextSibling, delegate);
		}
	}

	/**
	 * SXElement反序列化方法
	 * 
	 * @param root
	 * @param delegate
	 */
	private static final void recoverData(final SXElement root,
			final Object[] delegate) {
		int offset = readElement(root, delegate, 1);
		SXElement currentElement = root;
		for (;;) {
			final Object object = delegate[offset++];
			if (object == Mark.RETURN) {
				currentElement = currentElement.getParent();
				if (currentElement == null) {
					return;
				}
			} else if (object.getClass() == String.class) {
				currentElement = currentElement.append((String) object);
				offset = readElement(currentElement, delegate, offset);
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	private static final void writeElement(final SXElement element,
			final ArrayList<Object> delegate) {
		final String text = element.getText();
		final String cdata = element.getCDATA();
		final int count = element.getAttrCount();

		delegate.add(element.name);

		if (!"".equals(text)) {
			delegate.add(Mark.TEXT);
			delegate.add(text);
		}
		if (cdata != null) {
			delegate.add(Mark.CDATA);
			delegate.add(cdata);
		}
		if (count > 0) {
			delegate.add(Mark.ATTRISTART);
			for (int i = 0; i < count; i++) {
				delegate.add(element.getAttrName(i));
				delegate.add(element.getAttribute(i));
			}
			delegate.add(Mark.ATTRIEND);
		}
	}

	private static final int readElement(final SXElement elem,
			final Object[] delegate, int offset) {
		Object mark = delegate[offset++];
		if (mark == Mark.TEXT) {
			elem.setText((String) delegate[offset++]);
			mark = delegate[offset++];
		}
		if (mark == Mark.CDATA) {
			elem.setCDATA((String) delegate[offset++]);
			mark = delegate[offset++];
		}
		if (mark == Mark.ATTRISTART) {
			while (delegate[offset] != Mark.ATTRIEND) {
				elem.setAttrWithEmptyStr((String) delegate[offset++], (String) delegate[offset++]);
			}
			return offset + 1;
		} else {
			return offset - 1;
		}
	}

	private DOT_SXElement() {

	}

	public short getVersion() {
		return VERSION;
	}

	public void recoverData(final SXElement dest, final Object[] delegate,
			short version, boolean forSerial) {
		recoverData(dest, delegate);
	}

	public SXElement resolveInstance(final SXElement destHint,
			final Object[] delegate, short version, boolean forSerial) {
		return SXElement.newDoc();
	}

	public boolean supportAssign() {
		return false;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

	public Object[] toDelegateObject(final SXElement root) {
		final ArrayList<Object> list = new ArrayList<Object>();
		toDelegateObject(root, list);
		return list.toArray();
	}

}
