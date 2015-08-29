package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.def.arg.ArgumentableDeclare;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDefine;
import com.jiuqi.dna.core.model.ModelService;

/**
 * ģ��ʵ��Դ���壬���Է���ģ��ʵ���б�Ķ���
 * 
 * @author gaojingxin
 * 
 */
public interface ModelObjSourceDeclare extends ModelObjSourceDefine,
        NamedDeclare, ArgumentableDeclare {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDeclare getOwner();

	/**
	 * �������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDeclare getScript();

	/**
	 * ���ȡʵ������Ľű�
	 */
	public ScriptDeclare getMOCountOfScript();

	/**
	 * ����ģ��ʵ��Դ�ṩ����<br>
	 * 
	 * @return ���ؾɵ�ʵ��Դ�ṩ��
	 */
	public ModelService<?>.ModelObjProvider<?> setProvider(
	        ModelService<?>.ModelObjProvider<?> provider);

	public MappingQueryStatementDefine setMappingQueryRef(MappingQueryStatementDefine ref);
}
