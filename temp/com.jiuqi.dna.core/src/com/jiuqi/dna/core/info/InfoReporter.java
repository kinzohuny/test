package com.jiuqi.dna.core.info;

import com.jiuqi.dna.core.def.info.ErrorInfoDefine;
import com.jiuqi.dna.core.def.info.HintInfoDefine;
import com.jiuqi.dna.core.def.info.ProcessInfoDefine;
import com.jiuqi.dna.core.def.info.WarningInfoDefine;

/**
 * ��Ϣ����ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface InfoReporter extends ProgressReporter {
	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine infoDefine);

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1);

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2);

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2, Object param3);

	/**
	 * ������ʾ��Ϣ
	 */
	public void reportHint(HintInfoDefine info, Object param1, Object param2,
			Object param3, Object... others);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine infoDefine);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2, Object param3);

	/**
	 * ���������Ϣ
	 */
	public void reportError(ErrorInfoDefine info, Object param1, Object param2,
			Object param3, Object... others);

	/**
	 * ���澯����Ϣ
	 */
	public void reportWarning(WarningInfoDefine infoDefine);

	/**
	 * ���澯����Ϣ
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1);

	/**
	 * ���澯����Ϣ
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1,
			Object param2);

	/**
	 * ���澯����Ϣ
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1,
			Object param2, Object param3);

	/**
	 * ����Done��Ϣ
	 */
	public void reportWarning(WarningInfoDefine info, Object param1,
			Object param2, Object param3, Object... others);

	/**
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine);

	/**
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1);

	/**
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2);

	/**
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3);

	/**
	 * ʼĳ�����̣�������Ϣ���涼�����ڸù��̡�<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others);

	/**
	 * ��ɱ�����Ϣ<br>
	 * ���̵Ŀ�ʼ������������ʹ�ü�:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void endProcess();
}
