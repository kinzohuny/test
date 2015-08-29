package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.type.DataObjectTranslator;

@SuppressWarnings("unchecked")
public class DOT_TreeNodeImpl implements
		DataObjectTranslator<TreeNodeImpl, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_TreeNodeImpl() {

	}

	public final boolean supportAssign() {
		return false;
	}

	private static void tdo(TreeNodeImpl node, ArrayList<Object> delegate) {
		delegate.add(node.getElement());
		final int c = node.getChildCount();
		if (c > 0) {
			delegate.add(Mark.INDENT);
			for (int i = 0; i < c; i++) {
				tdo(node.getChild(i), delegate);
			}
			delegate.add(Mark.UNINDENT);
		}
	}

	public final Object[] toDelegateObject(TreeNodeImpl root) {
		final ArrayList<Object> list = new ArrayList<Object>();
		if (root.getClass() == TreeNodeRoot.class) {
			list.add(Mark.ROOT);
			list.add(((TreeNodeRoot) root).getAbsoluteLevel());
		}
		tdo(root, list);
		return list.toArray();
	}

	/**
	 * 缩进标记
	 * 
	 * @author gaojingxin
	 * 
	 */
	private enum Mark {
		ROOT,
		/**
		 * 层次加深
		 */
		INDENT,
		/**
		 * 层次减少
		 */
		UNINDENT,
	}

	public short getVersion() {
		return VERSION;
	}

	public final TreeNodeImpl resolveInstance(TreeNodeImpl destHint,
			Object[] delegate, short version, boolean forSerial) {
		if (delegate[0] == Mark.ROOT) {
			if (destHint == null || destHint.getClass() != TreeNodeRoot.class) {
				destHint = new TreeNodeRoot(null, 0);
			}
			((TreeNodeRoot) destHint).absoluteLevel = (Integer) delegate[1];
		} else if (destHint == null || destHint.getClass() != TreeNodeImpl.class) {
			destHint = new TreeNodeImpl(null, null);
		}
		return destHint;
	}

	private static int rd(TreeNodeImpl parent, Object[] delegate, int offset) {
		int childIndex = 0;
		int childIndexH = parent.getChildCount() - 1;
		TreeNodeImpl node;
		Object o = delegate[offset++];
		if (childIndex <= childIndexH) {
			node = parent.getChild(childIndex);
			node.setElement(o);
		} else {
			node = parent.append(o);
		}
		for (;;) {
			o = delegate[offset++];
			if (o == Mark.INDENT) {
				offset = rd(node, delegate, offset);
			} else if (o == Mark.UNINDENT) {
				while (childIndexH > childIndex) {
					parent.remove(childIndexH--);
				}
				return offset;
			} else {
				if (++childIndex <= childIndexH) {
					node = parent.getChild(childIndex);
					node.setElement(o);
				} else {
					node = parent.append(o);
				}
			}
		}
	}

	public void recoverData(TreeNodeImpl dest, Object[] delegate,
			short version, boolean forSerial) {
		int offset;
		Object o = delegate[0];
		if (o == Mark.ROOT) {
			offset = 3;
			o = delegate[2];
		} else {
			offset = 1;
		}
		dest.setElement(o);
		if (offset < delegate.length) {
			o = delegate[offset++];
			if (o != Mark.INDENT) {
				throw new IllegalArgumentException("delegate 结构有误");
			}
			rd(dest, delegate, offset);
		} else {
			dest.clear();
		}
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
