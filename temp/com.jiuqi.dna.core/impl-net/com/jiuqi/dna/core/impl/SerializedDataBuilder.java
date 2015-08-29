package com.jiuqi.dna.core.impl;

/**
 * ���л����ݹ�����
 * 
 * @author gaojingxin
 * 
 */
public class SerializedDataBuilder<TAttachment extends NSerializerFactoryProvider>
		implements DataFragmentBuilder<TAttachment> {
	protected Object objectToSerialize;
	public final byte packageType;
	private NSerializer serializer;

	public SerializedDataBuilder(Object objectToSerialize, byte packageType) {
		this.objectToSerialize = objectToSerialize;
		this.packageType = packageType;
	}

	public final boolean tryResetPackage(TAttachment attachment) {
		if (this.serializer != null) {
			this.serializer.reset();
		}
		return true;
	}

	public final boolean buildFragment(DataOutputFragment fragment,
			TAttachment attachment) throws Throwable {
		if (this.serializer == null) {
			this.serializer = attachment.getNSerializerFactory().newNSerializer();
		}
		if (this.serializer.isSerialized()) {
			this.writeHead(fragment, attachment);
			// д�����л����汾��Ϣ
			fragment.writeShort(this.serializer.getVersion());
			// ��ʼ���л�
			return this.serializer.serializeStart(this.objectToSerialize, fragment);
		}
		return this.serializer.serializeRest(fragment);
	}

	protected void writeHead(DataOutputFragment fragment, TAttachment attachment) {
		fragment.writeByte(this.packageType);
	}

	public void onFragmentOutError(TAttachment attachment) {
		this.objectToSerialize = null;// help GC
	}

	public void onFragmentOutFinished(TAttachment attachment) {
		this.objectToSerialize = null;// help GC
	}
}
