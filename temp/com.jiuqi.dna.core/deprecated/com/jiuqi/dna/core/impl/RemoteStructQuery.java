/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteStructQuery.java
 * Date 2009-3-25
 */
package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.RefDataType.BoxingDataType;
import com.jiuqi.dna.core.type.DataType;

/**
 * Զ�̽ṹ�����ѯ��
 * 
 * @author LRJ
 * @version 1.0
 */
@StructClass
final class RemoteStructQuery implements RemoteRequest<RemoteQueryStubImpl> {

	/**
	 * �ṹ�����ժҪ��Ϣ��
	 */
	final StructSummary structSummary;

	SpaceNode occurAt;

	/**
	 * Զ�̽ṹ�����ѯ�Ĺ�������
	 * 
	 * @param structSummary
	 *            �ṹ�����ժҪ��Ϣ��
	 */
	RemoteStructQuery(StructSummary structSummary) {
		this.structSummary = structSummary;
	}

	public RemoteReturn execute(ContextImpl<?, ?, ?> context) throws Throwable {
		// Ϊ�˽���ɰ�Զ�̵��ò������ڷ����ʼ��ʱ����ͬ��Զ�̵��õ����⣬��ע�����´��롣
		// StructDefineImpl define = (StructDefineImpl) context.occorAt
		// .findNamedDefine(ModelDefine.class,
		// this.structSummary.defineName);
		// if (define == null) {
		StructDefineImpl define = null;
		if (this.occurAt != null) {
			Class<?> soClass = this.occurAt.site.application.tryLoadClass(this.structSummary.defineName);
			if (soClass != null) {
				DataType odt = DataTypeBase.dataTypeOfJavaClass(soClass);
				if (odt instanceof StructDefineImpl) {
					define = (StructDefineImpl) odt;
				} else if (odt instanceof BoxingDataType) {
					define = ((BoxingDataType) odt).forOldSerial;
				} else {
					throw new UnsupportedOperationException("unexpected data type: " + odt);
				}
			}
		}
		// }
		return new StructReturn(define);
	}

	public void writeTo(StructuredObjectSerializer serializer)
			throws IOException, StructDefineNotFoundException {
		serializer.serialize(this);
	}

	/**
	 * ��ȡԶ����������ݰ��Ĵ��롣
	 * 
	 * @return Զ����������ݰ��Ĵ��롣
	 */
	public final PacketCode getPacketCode() {
		return PacketCode.STRUCT_REQUEST;
	}

	public RemoteQueryStubImpl newStub(NetConnection netConnection) {
		if (netConnection == null) {
			throw new NullArgumentException("netConnection");
		}
		return new RemoteQueryStubImpl(netConnection, this);
	}
}
