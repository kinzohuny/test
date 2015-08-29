package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.obja.StructDeclare;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDeclare;
import com.jiuqi.dna.core.impl.ModelDefineImpl;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.DataTypable;
import com.jiuqi.dna.core.type.DataType;

/**
 * ģ�Ͷ���ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface ModelDeclare extends ModelDefine, StructDeclare {
	public interface Helper {
		/**
		 * �½�����ʱģ�Ͷ��壨ϵͳ������־û���ע�ᣩ
		 */
		public ModelDeclare newTempModelDeclare(String author, String name,
				Class<?> moClass);

		/**
		 * ����ģ���½���ʱģ�Ͷ��壨ϵͳ������־û���ע�ᣩ
		 * 
		 * @param template
		 *            XMLģ�壬ModelDefine.Helper
		 * @param context
		 *            �����Ķ���
		 */
		public ModelDeclare newTempModelDeclare(SXElement template,
				Context context);

		public void ensurePrepared(ModelDefine model, Context context);
	}

	// TODO ���
	public final static Helper helper = new ModelDefineImpl.HelperImpl();

	/**
	 * ����ֶζ����б�
	 * 
	 * @return �����ֶζ����б�
	 */
	public ModifiableNamedElementContainer<? extends ModelFieldDeclare> getFields();

	public ModelFieldDeclare newField(String name, DataType type);

	public ModelFieldDeclare newField(FieldDefine sample);

	public ModelFieldDeclare newField(String name, DataTypable typable);

	/**
	 * ������Զ����б�
	 * 
	 * @return �������Զ����б�
	 */
	public ModifiableNamedElementContainer<? extends ModelPropertyDeclare> getProperties();

	public ModelPropertyDeclare newProperty(String name, DataType type);

	public ModelPropertyDeclare newProperty(String name, DataTypable typable);

	public ModelPropertyDeclare newProperty(ModelFieldDefine refField);

	/**
	 * ��ö��������б�
	 * 
	 * @return ���ض��������б�
	 */
	public ModifiableNamedElementContainer<? extends ModelActionDeclare> getActions();

	public ModelActionDeclare newAction(String name, Class<?> aoClass);

	public ModelActionDeclare newAction(String name);

	/**
	 * ��ù����������б�
	 * 
	 * @return ���ع����������б�
	 */
	public ModifiableNamedElementContainer<? extends ModelConstructorDeclare> getConstructors();

	public ModelConstructorDeclare newConstructor(String name, Class<?> aoClass);

	public ModelConstructorDeclare newConstructor(String name);

	/**
	 * ���Լ�������б�
	 * 
	 * @return ����Լ�������б�
	 */
	public ModifiableNamedElementContainer<? extends ModelConstraintDeclare> getConstraints();

	/**
	 * �½�����Լ��
	 * 
	 * @param name
	 *            Լ����
	 * @param messageFormat
	 *            ��Ϣ��ʽ���ı�
	 */
	public ModelConstraintDeclare newConstraint(String name,
			String messageFormat);

	/**
	 * ��ò�ѯ����
	 * 
	 * @return ���ز�ѯ���弯��
	 */
	public ModifiableNamedElementContainer<? extends MappingQueryStatementDeclare> getQueries();

	public MappingQueryStatementDeclare newQuery(String name);

	/**
	 * ���ģ��ʵ��Դ
	 */
	public NamedElementContainer<? extends ModelObjSourceDeclare> getSources();

	public ModelObjSourceDeclare newSource(String name);

	public ModelObjSourceDeclare newSource(String name, Class<?> aoClass);

	/**
	 * ��ò�ѯ����
	 * 
	 * @return ���ز�ѯ���弯��
	 */
	public ModifiableNamedElementContainer<? extends ModelReferenceDeclare> getReferences();

	public ModelReferenceDeclare newReference(String name, ModelDefine target);

	public ModifiableNamedElementContainer<? extends ModelDeclare> getNesteds();

	public ModelDeclare newNested(String name, Class<?> moClass);

}
