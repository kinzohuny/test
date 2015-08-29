package com.jiuqi.dna.core.impl;

import java.util.Collection;
import java.util.Iterator;

import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.exception.NamedDefineExistingException;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 命名定义的容器
 * 
 * <p>
 * 根据是否区分大小写而行为不同
 * 
 * @author gaojingxin
 * 
 * @param <TDefine>
 */
public class NamedDefineContainerImpl<TDefine extends NamedDefineImpl> extends
		MetaBaseContainerImpl<TDefine> implements
		ModifiableNamedElementContainer<TDefine> {

	private static final long serialVersionUID = 8003688456795448671L;

	public NamedDefineContainerImpl() {
		this(true, null);
	}

	public NamedDefineContainerImpl(boolean caseSensitive) {
		this(caseSensitive, null);
	}

	public NamedDefineContainerImpl(boolean caseSensitive,
			ContainerListener listener) {
		super(listener);
		this.map = new StringKeyMap<TDefine>(caseSensitive);
	}

	private final StringKeyMap<TDefine> map;

	@Override
	public final boolean add(TDefine define) {
		this.map.put(define.name, define, true);
		return super.add(define);
	}

	@Override
	public final void add(int index, TDefine define) {
		this.map.put(define.name, define, true);
		super.add(index, define);
	}

	@Override
	public final void addAll(TDefine[] defines) {
		for (TDefine define : defines) {
			if (this.contains(define)) {
				throw new NamedDefineExistingException(define);
			}
		}
		super.addAll(defines);
		for (TDefine define : defines) {
			this.map.put(define.name, define, true);
		}
	}

	@Override
	public final boolean addAll(Collection<? extends TDefine> c) {
		for (TDefine define : c) {
			if (this.contains(define)) {
				throw new NamedDefineExistingException(define);
			}
		}
		boolean r = super.addAll(c);
		for (TDefine define : c) {
			this.map.put(define.name, define, true);
		}
		return r;
	}

	@Override
	public final boolean addAll(int index, Collection<? extends TDefine> c) {
		for (TDefine define : c) {
			if (this.contains(define)) {
				throw new NamedDefineExistingException(define);
			}
		}
		boolean r = super.addAll(index, c);
		for (TDefine define : c) {
			this.map.put(define.name, define, false);
		}
		return r;
	}

	@Override
	public final TDefine set(int index, TDefine define) {
		if (define == null) {
			throw new NullArgumentException("命名定义");
		}
		TDefine old = super.set(index, define);
		if (old != define) {
			this.map.remove(old.name, true);
			try {
				this.map.put(define.name, define, true);
			} catch (NamedDefineExistingException e) {
				super.set(index, old);
				this.map.put(old.name, old);
				throw e;
			}
		}
		return old;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean remove(Object o) {
		if (o == null) {
			throw new NullArgumentException("命名定义");
		}
		if (super.remove(o)) {
			this.map.remove(((TDefine) o).name);
			return true;
		}
		return false;
	}

	@Override
	public final TDefine remove(int index) {
		TDefine define = super.remove(index);
		this.map.remove(define.name);
		return define;
	}

	@Override
	public final boolean removeAll(Collection<?> c) {
		boolean modified = false;
		Iterator<TDefine> e = super.removableIterator();
		while (e.hasNext()) {
			TDefine d = e.next();
			if (c.contains(d)) {
				e.remove();
				this.map.remove(d.name);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	protected final void removeRange(int fromIndex, int toIndex) {
		for (int i = fromIndex; i <= toIndex; i++) {
			this.map.remove(this.get(i).name);
		}
		super.removeRange(fromIndex, toIndex);
	}

	@Override
	public final void clear() {
		super.clear();
		this.map.clear();
	}

	public final TDefine find(String name) {
		if (this.size() == 0) {
			return null;
		}
		return this.map.find(name);
	}

	public final TDefine get(String name) throws MissingDefineException {
		TDefine define = this.find(name);
		if (define == null) {
			throw new MissingDefineException("无法找到名称为[" + name + "]的元数据。");
		}
		return define;
	}

	int indexOfName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		for (int i = 0, c = this.size(); i < c; i++) {
			TDefine define = this.get(i);
			if (name.equals(define.name)) {
				return i;
			}
		}
		return -1;
	}

	int indexOfName(String nameIn, int nameStart, int nameLen) {
		if (nameIn == null) {
			throw new NullPointerException();
		}
		for (int i = 0, c = this.size(); i < c; i++) {
			String name = this.get(i).name;
			if (name.length() == nameLen && nameIn.regionMatches(nameStart, name, 0, nameLen)) {
				return i;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean contains(Object o) {
		try {
			return this.contains((TDefine) o);
		} catch (ClassCastException e) {
			return false;
		}
	}

	final boolean contains(String name) {
		return this.map.containsKey(name);
	}

	final boolean contains(TDefine define) {
		return this.map.containsKey(define.name);
	}

}
