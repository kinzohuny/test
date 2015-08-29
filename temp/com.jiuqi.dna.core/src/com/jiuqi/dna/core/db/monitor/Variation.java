package com.jiuqi.dna.core.db.monitor;

import java.sql.Timestamp;

import com.jiuqi.dna.core.da.DataManipulation;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

/**
 * �仯��
 * 
 * @author houchunlei
 * 
 */
public interface Variation {

	/**
	 * �仯����Ψһ��ʶ
	 * 
	 * @return
	 */
	public GUID id();

	/**
	 * �仯��������ʱ��
	 * 
	 * <p>
	 * ָʾDML����������ʱ�䣬���������ύ��ʱ�䡣��ͬ�ı仯���汾���䷢��ʱ����ܴ���΢С�Ĳ�ֵ��
	 * 
	 * @return
	 */
	public Timestamp instant();

	/**
	 * �����Ĳ���
	 * 
	 * @return
	 */
	public DataManipulation operation();

	/**
	 * �仯���İ汾
	 * 
	 * <p>
	 * ��һ���仯�������У����ܰ����������汾�ı仯�����ɼ����������þ�����
	 * 
	 * @return
	 */
	public VariationVersion version();

	/**
	 * �仯���ֶε�����
	 * 
	 * @return
	 */
	public int size();

	/**
	 * ��ȡ��ֵ
	 * 
	 * @param field
	 *            MonitorField��˳��ţ���0��ʼ��
	 * @return
	 */
	public ReadableValue oldValue(int field);

	/**
	 * ��ȡ��ֵ
	 * 
	 * @param field
	 * @return
	 */
	public ReadableValue oldValue(VariationMonitorField field);

	/**
	 * ��ȡ��ֵ
	 * 
	 * @param field
	 * @return
	 */
	public ReadableValue oldValue(TableFieldDefine field);

	/**
	 * ��ȡ��ֵ
	 * 
	 * @param field
	 *            MonitorField��˳��ţ���0��ʼ��
	 * @return
	 */
	public ReadableValue newValue(int field);

	/**
	 * ��ȡ��ֵ
	 * 
	 * @param field
	 * @return
	 */
	public ReadableValue newValue(VariationMonitorField field);

	/**
	 * ��ȡ��ֵ
	 * 
	 * @param field
	 * @return
	 */
	public ReadableValue newValue(TableFieldDefine field);
}