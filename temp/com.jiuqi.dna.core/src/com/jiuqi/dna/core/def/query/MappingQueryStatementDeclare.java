package com.jiuqi.dna.core.def.query;

/**
 * Ӱ���ѯ���壬���Զ���ģ�������ݿ�����ݵ�Ӱ���ϵ�Ĳ�ѯ
 * 
 * @author gaojingxin
 * 
 */
public interface MappingQueryStatementDeclare extends
		MappingQueryStatementDefine, QueryStatementDeclare {

	/**
	 * �����Ƿ��Զ���ʵ���ֶ�
	 * 
	 * <p>
	 * ������Ϊ�Զ���ʱ,ϵͳ�������δ�󶨵�ʵ���ֶ�,������󶨵���ͬ���Ƶı��ֶ���
	 * 
	 * @param isAutoBind
	 */
	public void setAutoBind(boolean isAutoBind);

}
