package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.MetaElement;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDefine;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * ģ�Ͷ���ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface ModelDefine extends MetaElement, StructDefine {
	/**
	 * ��ģ�ͱ����XMLģ��
	 * 
	 * @param toElement
	 *            ��ģ�Ͷ���������Ľڵ�
	 */
	public void render(SXElement toElement);

	/**
	 * ��ȡģ��ʵ���������
	 * 
	 * @return ����ģ��ʵ��������
	 */
	public Class<?> getMOClass();

	/**
	 * ����ֶζ����б�
	 * 
	 * @return �����ֶζ����б�
	 */
	public NamedElementContainer<? extends ModelFieldDefine> getFields();

	/**
	 * ������Զ����б�
	 * 
	 * @return �������Զ����б�
	 */
	public NamedElementContainer<? extends ModelPropertyDefine> getProperties();

	/**
	 * ��ö��������б�
	 * 
	 * @return ���ض��������б�
	 */
	public NamedElementContainer<? extends ModelActionDefine> getActions();

	/**
	 * ��ù����������б�
	 * 
	 * @return ���ع����������б�
	 */
	public NamedElementContainer<? extends ModelConstructorDefine> getConstructors();

	/**
	 * ���Լ�������б�
	 * 
	 * @return ����Լ�������б�
	 */
	public NamedElementContainer<? extends ModelConstraintDefine> getConstraints();

	/**
	 * ��ò�ѯ����
	 * 
	 * @return ���ز�ѯ���弯��
	 */
	public NamedElementContainer<? extends MappingQueryStatementDefine> getQueries();

	/**
	 * ��òο�ģ�ͼ���
	 * 
	 * @return ���زο�ģ�ͼ���
	 */
	public NamedElementContainer<? extends ModelReferenceDefine> getReferences();

	/**
	 * ���ģ��ʵ��Դ
	 */
	public NamedElementContainer<? extends ModelObjSourceDefine> getSources();

	/**
	 * ���Ƕ��ģ�͵ļ���
	 */
	public NamedElementContainer<? extends ModelDefine> getNesteds();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////
	/**
	 * �����յ�ģ��ʵ������
	 */
	public Object newMO();
}
