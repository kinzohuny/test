package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.situation.Situation;
import com.jiuqi.dna.core.spi.publish.SpaceToken;

/**
 * Situation���ڲ���̽ӿڣ��ṩ��������ʹ��
 * 
 * @author gaojingxin
 * 
 */
public interface SituationSPI extends Situation {
	/**
	 * �½��Ӿ���
	 * 
	 * @param space
	 *            �����Ŀռ�λ��
	 * @return ���ش������Ӿ���
	 */
	public SituationSPI newSubSituation(SpaceToken space);

	/**
	 * ָ���������쳣�����ں�������ع�
	 * 
	 * @param e
	 */
	public void exception(Throwable e);

	/**
	 * �ύ��ع�����,�ͷ����ݿ���Դ���ڴ�������Դ
	 * 
	 * @return ����֮ǰ���쳣����
	 */
	public Throwable resolveTrans();

	/**
	 * �رյ�ǰ����
	 */
	public void close();
}
