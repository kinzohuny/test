package com.jiuqi.dna.core.type;

/**
 * ����ת����
 * 
 * <p>
 * �������л��Ϳ�¡���ݶ��� ��ĳЩ�����޷��ﵽDNA���л�Ҫ��ʱ��ע��ר�еĶ���ת�����������л�������
 * 
 * @author gaojingxin
 * 
 */
public interface DataObjectTranslator<TSourceObject, TDelegateObject> {

	/**
	 * ��ȡ��ǰ�Զ������л����汾
	 */
	public short getVersion();

	/**
	 * ��С֧�ֵ����л��汾
	 */
	public short supportedVerionMin();

	/**
	 * �Ƿ�֧�ָ��ƶ���
	 */
	public boolean supportAssign();

	/**
	 * ��ȡԴ�����Ӧ�����ݴ������
	 * 
	 * <p>
	 * ���ɱ�ϵͳ���ܵĶ��󣨸���װ�����͡�ö�١����顢String��GUID��Class��
	 * DataType�Լ�ʵ����DataObjectTraslator �ĸ������ͣ��������󲿷ֵ�java.util�µ��������ͣ�
	 */
	public TDelegateObject toDelegateObject(TSourceObject obj);

	/**
	 * ȷ����ԭ��Ķ���ʵ������ԭ������Ϊȷ��ʵ���Լ���ԭ���������֡�
	 * 
	 * @param destHint
	 *            Ŀ�������ʾ��ʵ����Ӧ�ÿ����ڿ��ܵ���������øö�����ΪĿ�����
	 * @param delegate
	 *            ��Ӧ�����ݴ������
	 * @param version
	 *            ת�����汾��
	 * @param forSerial
	 *            �Ƿ����л����̵���
	 * @return ����ԭ�Ƕ����ʵ�����ѱ�������ԭ����ʹ��
	 */
	public TSourceObject resolveInstance(TSourceObject destHint,
			TDelegateObject delegate, short version, boolean forSerial);

	/**
	 * ��ԭ�ƶ�Ŀ����������
	 * 
	 * @param dest
	 *            �ƶ�Ŀ�����
	 * @param delegate
	 *            ��Ӧ�����ݴ������
	 * @param version
	 *            ת�����汾��
	 * @param forSerial
	 *            �Ƿ����л����̵���
	 */
	public void recoverData(TSourceObject dest, TDelegateObject delegate,
			short version, boolean forSerial);
}