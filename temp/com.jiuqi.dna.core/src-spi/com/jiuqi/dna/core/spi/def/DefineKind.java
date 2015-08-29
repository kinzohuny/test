package com.jiuqi.dna.core.spi.def;

/**
 * �������� <br>
 * Context ��صĵ���:<br>
 * ��ģ�Ͷ��塷:<br>
 * ��ȡģ�Ͷ����������б�:<br>
 * <code>contex.getList(ModelDefine.class,DefineKind.DESIGN);</code><br>
 * ͨ�����ߺ����ֻ��ģ�Ͷ���Ŀɱ༭����: <br>
 * <code>contex.get(ModelDeclare.class,"name");</code><br>
 * ������ģ��ʱʹ�� <br>
 * <code>contex.get(ModelDeclare.class,"name",moClass);</code> <br>
 * ͨ�����ߺ����ֻ��ģ�Ͷ��������ʱ: <br>
 * <code>contex.get(ModelDefine.class,"name");</code> <br>
 * �����塷:<br>
 * ͨ�����ߺ����ֻ�ñ���Ŀɱ༭���������û���򴴽��µĶ���: <br>
 * <code>contex.get(TableDeclare.class,"name");</code><br>
 * ͨ�����ߺ����ֻ�ñ��������ʱ: <br>
 * <code>contex.get(TableDefine.class,"name");</code><br>
 * 
 * @author gaojingxin
 * 
 */
public enum DefineKind {
	/**
	 * �����ڶ���
	 */
	RUNTIME,
	/**
	 * ����ڶ���
	 */
	DESIGN

}
