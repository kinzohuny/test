package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.NSerializer.NSerializerFactory;

final class ReliableRSINodeToken {

	ReliableRSINodeToken(final int indexInCluster,
			final short higthestSerializeVersion,
			final short higthestHandlerVersion) {
		this.indexInCluster = indexInCluster;
		this.serializerFactory = NSerializer.getRemoteCompatibleFactory(higthestSerializeVersion);
		this.handler = ReliableRSIHandler.getRemoteCompatibleHandler(higthestHandlerVersion);
		this.higthestSerializeVersion = String.valueOf(higthestSerializeVersion);
		this.higthestHandlerVersion = String.valueOf(higthestHandlerVersion);
	}

	final int indexInCluster;

	final String higthestSerializeVersion;

	final String higthestHandlerVersion;

	final ReliableRSIHandler handler;

	final NSerializerFactory serializerFactory;

}
