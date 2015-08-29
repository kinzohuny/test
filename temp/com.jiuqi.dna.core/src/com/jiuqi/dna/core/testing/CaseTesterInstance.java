package com.jiuqi.dna.core.testing;

import com.jiuqi.dna.core.Context;

/**
 * ����������ʵ��<br>
 * ����ͨ��context.getList(CaseTesterInstance.class)����ȡ�б�,<br>
 * ��Ҫ����͹��˵���ָ�����˺ͱȽ���
 * 
 * @author gaojingxin
 * 
 */
public interface CaseTesterInstance {
	/**
	 * ����
	 */
	public String getCode();

	/**
	 * ����
	 */
	public String getName();

	/**
	 * ����
	 */
	public String getDescription();

	/**
	 * ���ò�������<br>
	 * ��ܻ�׼�������������Ȼ�����CaseTester.testCase����<br>
	 * 
	 * @param context
	 *            ������
	 * @param testContext
	 *            ���������ģ���Ҫ���Կ��ʵ�֣��÷�������ֱ�Ӵ��ݸ�CaseTester.testCase
	 */
	public void test(Context context, TestContext testContext) throws Throwable;
}
