package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.ModifiableContainer;
import com.jiuqi.dna.core.misc.SafeItrList;

/**
 * ÈİÆ÷ÊµÏÖÀà
 * 
 * @author gaojingxin
 * 
 * @param <TDefine>
 */
public class ContainerImpl<TDefine> extends SafeItrList<TDefine> implements
		ModifiableContainer<TDefine> {

	private static final long serialVersionUID = 1L;

	/**
	 * ÈİÆ÷¼àÌıÆ÷
	 */
	final ContainerListener listener;

	public ContainerImpl(ContainerListener listener) {
		super(0);
		this.listener = listener;
	}

	public ContainerImpl() {
		super(0);
		this.listener = null;
	}

	public final void move(int from, int to) {
		if (this.listener != null) {
			this.listener.beforeMoving(this, from, to);
		}
		if (to < 0 || this.size() <= to) {
			throw new IndexOutOfBoundsException("Index: " + to + ", Size: " + this.size());
		}
		if (to < from) {
			TDefine save = this.get(from);
			for (int i = to;; i++) {
				if (i < from) {
					TDefine save2 = super.get(i);
					super.set(i, save);
					save = save2;
				} else {
					super.set(i, save);
					break;
				}
			}
		} else if (from < to) {
			TDefine save = this.get(from);
			for (int i = to;; i--) {
				if (i > from) {
					TDefine save2 = super.get(i);
					super.set(i, save);
					save = save2;
				} else {
					super.set(i, save);
					break;
				}
			}
		}
	}

	@Override
	public TDefine remove(int index) {
		if (this.listener != null) {
			this.listener.beforeRemoving(this, index);
		}
		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		if (this.listener != null) {
			this.listener.beforeRemoving(this, o);
		}
		return super.remove(o);
	}

	@Override
	public void clear() {
		if (this.listener != null) {
			this.listener.beforeClearing(this);
		}
		super.clear();
	}
}
