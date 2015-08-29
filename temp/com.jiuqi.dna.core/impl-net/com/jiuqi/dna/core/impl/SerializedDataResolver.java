package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.NUnserializer.ObjectTypeQuerier;

/**
 * ���л����ݻ�ԭ��
 * 
 * @author gaojingxin
 * 
 * @param <TAttachment>
 */
public abstract class SerializedDataResolver<TAttachment> implements
		DataFragmentResolver<TAttachment> {
	private NUnserializer unserializer;
	private final ObjectTypeQuerier objectTypeQuerier;
	private final Object destHint;

	public SerializedDataResolver(ObjectTypeQuerier objectTypeQuerier,
			Object destHint) {
		this.objectTypeQuerier = objectTypeQuerier;
		this.destHint = destHint;
	}

	public SerializedDataResolver(ObjectTypeQuerier objectTypeQuerier) {
		this.objectTypeQuerier = objectTypeQuerier;
		this.destHint = null;
	}

	protected boolean readHead(DataInputFragment fragment,
			TAttachment attachment) {
		return false;
	}

	protected abstract void finishUnserialze(Object unserialzedObject,
			TAttachment attachment);

	public void onFragmentInFailed(TAttachment attachment) throws Throwable {
	}

	public final boolean resolveFragment(DataInputFragment fragment,
			TAttachment attachment) throws Throwable {
		final boolean finished;
		if (this.unserializer == null) {// ��һ��Ƭ��
			if (this.readHead(fragment, attachment)) {
				return true;
			}
			this.unserializer = NUnserializer.newUnserializer(fragment.readShort(), this.objectTypeQuerier);
			finished = this.unserializer.unserializeStart(fragment, this.destHint);
		} else {
			finished = this.unserializer.unserializeRest(fragment);
		}
		if (finished) {
			this.finishUnserialze(this.unserializer.getUnserialzedObject(), attachment);
		}
		return finished;
	}
}
