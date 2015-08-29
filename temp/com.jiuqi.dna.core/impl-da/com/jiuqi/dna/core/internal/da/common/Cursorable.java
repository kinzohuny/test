package com.jiuqi.dna.core.internal.da.common;

/**
 * ���α��ƶ���
 * 
 * @author houchunlei
 */
public interface Cursorable {

	/**
	 * �α��ƶ�����һ��֮ǰ
	 */
	public void beforeFirst();

	/**
	 * �α��ƶ������һ��֮��
	 */
	public void afterLast();

	/**
	 * �Ƿ��ڵ�һ��֮ǰ
	 * 
	 * @return
	 */
	public boolean isBeforeFirst();

	/**
	 * �Ƿ������һ��֮��
	 * 
	 * @return
	 */
	public boolean isAfterLast();

	/**
	 * �α��ƶ�����һ��
	 * 
	 * @return �Ƿ���Ч��¼��
	 */
	public boolean first();

	/**
	 * �α��ƶ������һ��
	 * 
	 * @return �Ƿ���Ч��¼��
	 */
	public boolean last();

	/**
	 * �Ƿ��һ��
	 * 
	 * @return
	 */
	public boolean isFirst();

	/**
	 * �Ƿ����һ��
	 * 
	 * @return
	 */
	public boolean isLast();

	/**
	 * �α����һ��
	 * 
	 * @return �Ƿ���Ч��
	 */
	public boolean next();

	/**
	 * �α�ǰ��һ��
	 * 
	 * @return �Ƿ���Ч��
	 */
	public boolean previous();

	/**
	 * ����ƶ��α�
	 * 
	 * @param rows
	 * @return �Ƿ���Ч��
	 */
	public boolean relative(int rows);

	/**
	 * �����ƶ��α�
	 * 
	 * @param row
	 * @return �Ƿ���Ч��
	 */
	public boolean absolute(int row);
}