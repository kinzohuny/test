package com.jiuqi.dna.core.da;

import java.text.Format;

import com.jiuqi.dna.core.def.query.QueryColumnDefine;
import com.jiuqi.dna.core.type.ReadableValue;

/**
 * ֻ��������ֶ�
 * 
 * @author houchunlei
 * 
 */
public interface ReadOnlyRecordSetField extends ReadableValue {

	/**
	 * ��ö�Ӧ�Ĳ�ѯ�ж���
	 */
	public QueryColumnDefine getDefine();

	/**
	 * ��ȡ�ֶ�������Sqlװ�ؽ����ʱ�п���������
	 */
	public String getName();

	/**
	 * ��ȡ��ʽ������
	 */
	public Format getFormat();

	/**
	 * ���ø�ʽ������
	 */
	public void setFormat(Format format);

	/**
	 * ��ʽ�����
	 */
	public String formatText();

	/**
	 * ������ʽ���ı�
	 */
	public void parseText(String text);
}
