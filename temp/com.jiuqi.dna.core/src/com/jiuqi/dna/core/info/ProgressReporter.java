package com.jiuqi.dna.core.info;

import com.jiuqi.dna.core.LifeHandle;
import com.jiuqi.dna.core.exception.AbortException;

/**
 * 
 * @author gaojingxin
 * 
 */
public interface ProgressReporter extends LifeHandle {
	/**
	 * Ӧ����һ���Ĳ�������������һ���Ĳ���,<br>
	 * ������0f��getRestPartialProgress()֮�������<br>
	 * ���������getRestPartialProgress()����getRestPartialProgress()���ò���<br>
	 * 
	 * 
	 * @param step
	 *            ��һ���Ĳ���
	 */
	public float setNextStep(float nextStep);

	/**
	 * �����һ���Ĳ���
	 */
	public float getNextStep();

	/**
	 * Ӧ����һ���Ĳ�������������һ��֮��ֲ�������Ҫ�ִ��λ��,<br>
	 * ������getPartialProgress()+getNextStep()��1f֮�������<br>
	 * ���С��getPartialProgress()+getNextStep()����Ƚ���getPartialProgress()+
	 * getNextStep()��<br>
	 * �������1f�������Ϊ1f<br>
	 * 
	 * @param nextProgress
	 *            ��һ����ﵽ�ľֲ�����
	 */
	public float setNextPartialProgress(float nextProgress);

	/**
	 * ��õ�ǰ����ʣ��Ľ���
	 */
	public float getRestPartialProgress();

	/**
	 * ��õ�ǰ����Ľ���
	 * 
	 * @return ���ص�ǰ�����Ĵ���Ľ���
	 */
	public float getPartialProgress();

	/**
	 * �����þֲ�����<br>
	 * ������getPartialProgress()+getNextStep()��1f֮�������<br>
	 * ���С��getPartialProgress()+getNextStep()����Ƚ���getPartialProgress()+
	 * getNextStep()��<br>
	 * �������1f�������Ϊ1f<br>
	 * 
	 * @param progress
	 *            �ﵽ�ľֲ�����
	 */
	public float setPartialProgress(float progress);

	/**
	 * ���ȫ��������ܽ���
	 * 
	 * @return ���ص�ǰ��������ܽ���
	 */
	public float getTotalProgress();

	/**
	 * ��õ�ǰ������ռ��������������ı�����
	 * 
	 * @return ��ǰ������ռ��������������ı�����
	 */
	public float getPartialProgressQuotiety();

	/**
	 * ����Ƿ����ȡ��
	 */
	public boolean isCanceling();

	/**
	 * ����Ƿ�ȡ���������ֹ�����׳�InterruptedException�쳣
	 */
	public void throwIfCanceling();

	/**
	 * ��ֹ��ǰ������������������
	 */
	public void abort() throws AbortException;

}
