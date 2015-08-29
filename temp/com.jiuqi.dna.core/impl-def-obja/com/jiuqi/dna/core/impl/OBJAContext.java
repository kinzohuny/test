package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.HashUtil;
import com.jiuqi.dna.core.type.DataObjectTranslator;
import com.jiuqi.dna.core.type.DataType;

/**
 * 对象访问上下文
 * 
 * @author gaojingxin
 * 
 */
final class OBJAContext {

	private OBJMapEntry[] table;
	private int size;

	private void helpGC() {
		if (this.size > 0) {
			for (int i = 0, c = this.table.length; i < c; i++) {
				OBJMapEntry e = this.table[i];
				if (e != null) {
					this.table[i] = null;
					do {
						final OBJMapEntry next = e.next;
						e.helpGC();
						e = next;
					} while (e != null);
				}
			}
		}
	}

	private OBJAContext() {
	}

	final OBJMapEntry valueHolder = new OBJMapEntry(0, null, null, null);

	static class OBJMapEntry {
		public final int hash;
		public Object src;
		public Object dest;

		final void helpGC() {
			this.src = null;
			this.dest = null;
		}

		public OBJMapEntry next;

		OBJMapEntry(int hash, Object src, Object dest, OBJMapEntry next) {
			this.hash = hash;
			this.src = src;
			this.dest = dest;
			this.next = next;
		}
	}

	static class CloneDataTranslatorHelper<TSourceObject, TDelegateObject>
			extends DataTranslatorHelper<TSourceObject, TDelegateObject> {

		private OBJMapEntry entry;

		CloneDataTranslatorHelper(
				DataObjectTranslator<TSourceObject, TDelegateObject> translator,
				OBJMapEntry entry) {
			super(translator);
			entry.dest = this;
			this.entry = entry;
		}

		@Override
		protected final void destInstanceResolved(TSourceObject dest) {
			this.entry.dest = dest;
			this.entry = null;
		}

		@Override
		protected final boolean forSerial() {
			return false;
		}
	}

	final <TSourceObject, TDelegateObject> CloneDataTranslatorHelper<TSourceObject, TDelegateObject> newDataTranslatorHelper(
			Object src,
			DataObjectTranslator<TSourceObject, TDelegateObject> translator) {
		return new CloneDataTranslatorHelper<TSourceObject, TDelegateObject>(translator, this.putRef(src, null));
	}

	final OBJMapEntry putRef(Object src, Object dest) {
		int oldLen;
		if (this.size == 0) {
			this.table = new OBJMapEntry[oldLen = 16];
		} else {
			oldLen = this.table.length;
		}
		int index;
		if (++this.size > oldLen * 0.75) {
			int newLen = oldLen * 2;
			OBJMapEntry[] newTable = new OBJMapEntry[newLen];
			for (int j = 0; j < oldLen; j++) {
				for (OBJMapEntry e = this.table[j], next; e != null; e = next) {
					index = e.hash & (newLen - 1);
					next = e.next;
					e.next = newTable[index];
					newTable[index] = e;
				}
			}
			this.table = newTable;
			oldLen = newLen;
		}
		int hash = HashUtil.identityHash(src);
		index = hash & (oldLen - 1);
		return this.table[index] = new OBJMapEntry(hash, src, dest, this.table[index]);
	}

	final Object find(Object src) {
		if (this.size > 0) {
			for (OBJMapEntry e = this.table[HashUtil.identityHash(src) & (this.table.length - 1)]; e != null; e = e.next) {
				if (e.src == src) {
					return e.dest;
				}
			}
		}
		return null;
	}

	final Object doAssign(Object srcObj, Object destHint, DataType typeHint) {
		final ObjectDataTypeInternal objectDataType;
		final DynObj dynSrcObj;
		resolveAssigner: {
			Class<?> srcObjClass = srcObj.getClass();
			if (typeHint instanceof StructDefineImpl) {
				StructDefineImpl define = (StructDefineImpl) typeHint;
				if (define.soClass == srcObjClass) {
					if (define.isDynObj) {
						dynSrcObj = (DynObj) srcObj;
						if (dynSrcObj.define != define && dynSrcObj.define != null) {
							objectDataType = dynSrcObj.define;
							break resolveAssigner;
						}
					} else {
						dynSrcObj = null;
					}
					objectDataType = define;
					break resolveAssigner;
				}
			}
			if (srcObj instanceof DynObj) {
				dynSrcObj = (DynObj) srcObj;
				objectDataType = dynSrcObj.define != null ? dynSrcObj.define : (ObjectDataTypeInternal) DataTypeBase.dataTypeOfJavaClass(srcObjClass);
				break resolveAssigner;
			} else {
				dynSrcObj = null;
				objectDataType = (ObjectDataTypeInternal) DataTypeBase.dataTypeOfJavaClass(srcObjClass);
			}
		}
		if (dynSrcObj != null) {
			return objectDataType.assignNoCheckSrcD(dynSrcObj, destHint, this, null);
		} else {
			return objectDataType.assignNoCheckSrc(srcObj, destHint, this, null);
		}
	}

	@SuppressWarnings("unchecked")
	public static final <TObject> TObject clone(TObject src, TObject tryReuse,
			DataType typeHint) {
		if (src == null || src == tryReuse) {
			return src;
		}
		final OBJAContext objAc = new OBJAContext();
		final TObject cloned = (TObject) objAc.doAssign(src, tryReuse, typeHint);
		objAc.helpGC();
		return cloned;
	}

	public static final <TObject> TObject clone(TObject src) {
		return clone(src, null, null);
	}
}
