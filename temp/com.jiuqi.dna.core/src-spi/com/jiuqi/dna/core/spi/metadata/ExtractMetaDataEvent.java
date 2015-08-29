package com.jiuqi.dna.core.spi.metadata;

import java.io.OutputStream;
import java.util.HashMap;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.type.GUID;

/**
 * �ռ���ȡ������Ŀ�¼� <br>
 * 1. �����Լ������Ԫ���ݶ������Σ�<br>
 * 1.1. ����
 * <code>getMetaStream().newEntry("yourfolder/youentry1",RECVER,"XYZ")</code>
 * ��������Ŀ�����������ʹ�ø������л���������<br>
 * 1.2. ֱ��ʹ��
 * <code>getMetaStream().newEntry("yourfolder/youentry1",RECVER,"XYZ",xml)</code>
 * ��������Ŀ<br>
 * 2. �����ȡ����
 * 
 * @author gaojingxin
 * 
 */
public final class ExtractMetaDataEvent extends Event {
	private final MetaDataOutputStream metaStream;
	private final boolean extractAll;
	private final HashMap<GUID, Object> extractUserData = new HashMap<GUID, Object>();

	/**
	 * ���ԭ���ݴ����
	 */
	public final MetaDataOutputStream getMetaStream() {
		return this.metaStream;
	}

	/**
	 * ����Ƿ��ռ�ȫ�����������ǽ����·�����
	 */
	public final boolean extractAll() {
		return this.extractAll;
	}

	/**
	 * ��ȡmetaID��Ӧ��Ԫ���������ȡ�Զ�����Ϣ���п���Ϊ�ա�<br>
	 * �����ΪBoolean���жϿ���ʹ��Boolean.TRUE==getExtractUserData(metaID)
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
		throw new ClassCastException("�û�����ʵ�����ͣ�" + userData.getClass()
				+ "��Ҫ�����ͣ�" + userDataClass + "����");
	}

	/**
	 * ����Ƿ���Ҫ��ȡ��ӦmetaID��Ԫ����
	 */
	public final boolean needExtract(GUID metaID) {
		return this.extractUserData.get(metaID) != null;
	}

	/**
	 * �����Ƿ�Ҫ����ȡ��ӦmetaID��Ԫ����
	 */
	public final void setNeedExtract(GUID metaID) {
		this.setNeedExtract(metaID, Boolean.TRUE);
	}

	/**
	 * ����metaID��Ӧ��Ԫ���������ȡ�Զ�����Ϣ<br>
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
	 * ���췽���������ռ��·���ȫ������ ���¼�
	 * 
	 * @param out
	 *            Ŀ����
	 * @param isExtractAll
	 *            �Ƿ��ռ��·�����
	 */
	public ExtractMetaDataEvent(OutputStream out, boolean isExtractAll) {
		this.metaStream = new MetaDataOutputStream(out);
		this.extractAll = isExtractAll;
	}

	/**
	 * ���췽���������ռ��·��������¼�
	 * 
	 * @param out
	 *            Ŀ����
	 */
	public ExtractMetaDataEvent(OutputStream out) {
		this(out, false);
	}

}