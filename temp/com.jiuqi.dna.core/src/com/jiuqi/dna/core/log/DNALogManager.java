package com.jiuqi.dna.core.log;

import com.jiuqi.dna.core.impl.DNALogManagerInternal;

public final class DNALogManager {

	/**
	 * ��ȡ��־��¼��
	 * 
	 * @param category
	 *            ��־�ļ�¼�ı�ʶ����ʽΪ��"/"�ֿ��ı�ʶ�����У���ʶ��ֻ������ĸ�����֡��»��߹��ɡ��磺XX/XX/XX
	 * @return ���ض�Ӧ����־��¼��
	 */
	public static final Logger getLogger(final String category) {
		return DNALogManagerInternal.getLogger(category);
	}

}
