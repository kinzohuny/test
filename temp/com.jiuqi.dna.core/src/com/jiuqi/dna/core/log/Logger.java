package com.jiuqi.dna.core.log;

import com.jiuqi.dna.core.Context;

/**
 * ��־��¼
 * 
 * @author hanfei
 * 
 */
public interface Logger {

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param massage
	 *            ��־��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logFatal(Context context, Object message, boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param throwable
	 *            �쳣��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logFatal(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logFatal(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param massage
	 *            ��־��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logError(Context context, Object message, boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param throwable
	 *            �쳣��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logError(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logError(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param massage
	 *            ��־��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logWarn(Context context, Object message, boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param throwable
	 *            �쳣��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logWarn(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logWarn(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param massage
	 *            ��־��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logInfo(Context context, Object message, boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param throwable
	 *            �쳣��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logInfo(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logInfo(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param massage
	 *            ��־��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logDebug(Context context, Object message, boolean isForwardTell);

	/**
	 * ��¼��־
	 * 
	 * @param context
	 *            ������
	 * @param throwable
	 *            �쳣��Ϣ
	 * @param isForwardTell
	 *            �Ƿ��¼�������ϼ�Ŀ¼��Ӧ����־�ļ���
	 */
	public void logDebug(Context context, Throwable throwable,
			boolean isForwardTell);

	public void logDebug(Context context, Object message, Throwable throwable,
			boolean isForwardTell);

}
