package com.jiuqi.dna.core.spi.setl;

import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * XML�������󱨸���
 * 
 * @author niuhaifeng
 * 
 */
public interface SETLSAXParseReporter {
	/**
	 * ����ָ��Ӱ����ʽ������Ϣ
	 * 
	 * @param field
	 *            ������ص�ָ���ֶ�
	 * @param message
	 *            ������Ϣ
	 */
	public void reportError(TableFieldDefine field, String message);

	/**
	 * ���������Ϣ(�����������)
	 * 
	 * @param funcName
	 *            ������صĺ�������
	 * @param section
	 *            ������صĺ������岿�ֱ�ʶ
	 * @param message
	 *            ������Ϣ
	 */
	public void reportError(String funcName, Section section, String message);

	public enum Section {
		/**
		 * ��������
		 */
		NAME("��������"),
		/**
		 * ��Դ��
		 */
		SOURCE("��Դ��"),
		/**
		 * �ھ�ƥ�������
		 */
		CALIBER("�ھ�ƥ�������"),
		/**
		 * ������
		 */
		RETURN("������"),
		/**
		 * ά��ƥ��
		 */
		DIMS("ά��ƥ��"),
		/**
		 * ������ƥ������
		 */
		MATCHING("������ƥ������"),
		/**
		 * ˵��
		 */
		DESCRIPTION("˵��");

		public final String title;

		Section(String title) {
			this.title = title;
		}
	}
}
