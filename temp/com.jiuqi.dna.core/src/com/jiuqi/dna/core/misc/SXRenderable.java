package com.jiuqi.dna.core.misc;

/**
 * ����Ⱦ��XML����Ľӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface SXRenderable {

	/**
	 * ���ص�ǰ�ڵ��XML�������
	 * 
	 * @return ���ص�ǰ�ڵ��XML�������
	 */
	public String getXMLTagName();

	/**
	 * ʵ�ָ÷���������д��XML
	 * 
	 * @param element
	 */
	public void render(SXElement element);
}
