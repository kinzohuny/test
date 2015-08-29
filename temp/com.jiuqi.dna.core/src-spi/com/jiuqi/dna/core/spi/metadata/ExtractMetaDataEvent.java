package com.jiuqi.dna.core.spi.metadata;

import java.io.OutputStream;
import java.util.HashMap;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.type.GUID;

/**
 * 收集提取参数条目事件 <br>
 * 1. 遍历自己管理的元数据对象，依次：<br>
 * 1.1. 调用
 * <code>getMetaStream().newEntry("yourfolder/youentry1",RECVER,"XYZ")</code>
 * 返回新条目的输出流。并使用该流序列化参数。或：<br>
 * 1.2. 直接使用
 * <code>getMetaStream().newEntry("yourfolder/youentry1",RECVER,"XYZ",xml)</code>
 * 创建新条目<br>
 * 2. 完成提取处理
 * 
 * @author gaojingxin
 * 
 */
public final class ExtractMetaDataEvent extends Event {
	private final MetaDataOutputStream metaStream;
	private final boolean extractAll;
	private final HashMap<GUID, Object> extractUserData = new HashMap<GUID, Object>();

	/**
	 * 获得原数据打包流
	 */
	public final MetaDataOutputStream getMetaStream() {
		return this.metaStream;
	}

	/**
	 * 获得是否收集全部参数，而非仅仅下发参数
	 */
	public final boolean extractAll() {
		return this.extractAll;
	}

	/**
	 * 获取metaID对应的元数据项的提取自定义信息，有可能为空。<br>
	 * 如果作为Boolean来判断可以使用Boolean.TRUE==getExtractUserData(metaID)
	 */
	@SuppressWarnings("unchecked")
	public final <TUserData> TUserData getExtractUserData(GUID metaID,
			Class<TUserData> userDataClass) {
		if (metaID == null) {
			throw new NullArgumentException("metaID");
		}
		if (userDataClass == null) {
			throw new NullArgumentException("userDataClass");
		}
		final Object userData = this.extractUserData.get(metaID);
		if (userData == null) {
			return null;
		}
		if (userDataClass.isInstance(userData)) {
			return (TUserData) userData;
		}
		throw new ClassCastException("用户数据实际类型：" + userData.getClass()
				+ "与要求类型：" + userDataClass + "不符");
	}

	/**
	 * 检测是否需要提取对应metaID的元数据
	 */
	public final boolean needExtract(GUID metaID) {
		return this.extractUserData.get(metaID) != null;
	}

	/**
	 * 设置是否要求提取对应metaID的元数据
	 */
	public final void setNeedExtract(GUID metaID) {
		this.setNeedExtract(metaID, Boolean.TRUE);
	}

	/**
	 * 设置metaID对应的元数据项的提取自定义信息<br>
	 */
	public final void setNeedExtract(GUID metaID, Object userData) {
		if (metaID == null) {
			throw new NullArgumentException("metaID");
		}
		if (userData == null) {
			this.extractUserData.remove(metaID);
		} else {
			this.extractUserData.put(metaID, userData);
		}
	}

	/**
	 * 构造方法，构造收集下发或全部参数 的事件
	 * 
	 * @param out
	 *            目标流
	 * @param isExtractAll
	 *            是否收集下发参数
	 */
	public ExtractMetaDataEvent(OutputStream out, boolean isExtractAll) {
		this.metaStream = new MetaDataOutputStream(out);
		this.extractAll = isExtractAll;
	}

	/**
	 * 构造方法，构造收集下发参数的事件
	 * 
	 * @param out
	 *            目标流
	 */
	public ExtractMetaDataEvent(OutputStream out) {
		this(out, false);
	}

}