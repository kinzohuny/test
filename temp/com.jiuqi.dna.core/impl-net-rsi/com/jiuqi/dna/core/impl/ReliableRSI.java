package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;

interface ReliableRSI {

	static final String LISTEN_PATH = "dna_core/rrsi";

	static final int DATAFRAGMENT_CAPACITY = 0x1000;

	static final String HTTP_HEADER_HANDLER_VERSION = "RRSI_HV";

	static final String HTTP_HEADER_HIGTHEST_HANDLER_VERSION = "RRSI_HHV";

	static final String HTTP_HEADER_HIGTHEST_SERIALIZE_VERSION = "RRSI_HSV";

	static final String HTTP_HEADER_ACTION = "RRSI_ACTION";

	static final String ACTION_GETINFOMATION = "GETINFO";

	static final String ACTION_INVOKE = "INVOKE";

	static final String REQUEST_ATTR_SERIALIZE_VERSION = "RRSI_RDSV";

	/**
	 * 第一个请求数据片断的头部结构： <li>ControlFlag 控制标记 [1Byte]</li> <li>PackageID
	 * 包ID[4Byte]</li> <li>PackageType 包类型 [1Byte]</li> <li>RequestType 请求类型
	 * [1Byte]</li> <li>RequestID 请求ID [4Byte]</li> <li>RemoteSessionID 远程会话ID
	 * [8Byte]</li> <li>SerializeVersion 序列化版本 [2Byte]</li> <br>
	 * 请求数据片断的头部结构： <li>ControlFlag 控制标记 [1Byte]</li> <li>PackageID 包ID [4Byte]</li>
	 * <br>
	 * 第一个应答数据片断的头部结构： <li>ControlFlag 控制标记 [1Byte]</li> <li>PackageID 包ID
	 * [4Byte]</li> <li>PackageType 包类型 [1Byte]</li> <li>RequestType 请求类型
	 * [1Byte]</li> <li>RequestID 请求ID [4Byte]</li> <li>RemoteSessionID 远程会话ID
	 * [8Byte]</li> <li>Progress 进度 [4Byte]</li> <li>
	 * SerializeVersion 序列化版本 [2Byte]</li> <br>
	 * 应答数据片断的头部结构： <li>ControlFlag 控制标记 [1Byte]</li> <li>PackageID 包ID [4Byte]</li>
	 * 
	 */
	static final class DataFragmentReader {

		DataFragmentReader(final InputStream is) {
			this.is = is;
		}

		final SafeDataFragmentImpl getNextDataFragment() throws IOException {
			final SafeDataFragmentImpl dataFragment = new SafeDataFragmentImpl(DATAFRAGMENT_CAPACITY);
			final int availableOffset = dataFragment.getAvailableOffset();
			final byte[] buffer = dataFragment.getBytes();
			final InputStream is = this.is;
			int len;
			int position = availableOffset;
			int remain = 4;
			do {
				if ((len = is.read(buffer, position, remain)) > 0) {
					position += len;
					remain -= len;
				} else if (remain > 0) {
					throw new IOException("异常结束的输入流。");
				}
			} while (remain > 0);
			remain = dataFragment.readInt();
			while (remain > 0) {
				if ((len = is.read(buffer, position, remain)) > 0) {
					position += len;
					remain -= len;
				} else if (remain > 0) {
					throw new IOException("异常结束的输入流。");
				}
			}
			dataFragment.limit(position);
			dataFragment.setPosition(availableOffset + 4);
			return dataFragment;
		}

		private final InputStream is;

	}

}
