package com.jiuqi.dna.core.da;

import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.obja.DynamicObject;
import com.jiuqi.dna.core.def.query.StatementDefine;

/**
 * ���ݿ�����
 * 
 * <p>
 * ���������ݿ���Դ�Ŀ����õķ��ʽӿڡ������Ҫ���ִ��ͬһ��䣬����˶����ִ��Ч������ֱ�ӵ���DBAdapter�ĸ�������
 * 
 * @author gaojingxin
 * 
 */
public interface DBCommand {

	/**
	 * ������䶨��
	 */
	public StatementDefine getStatement();

	/**
	 * ��ȡ��������
	 * 
	 * <p>
	 * ����������һ�����ڴ�Ų���ֵ�Ķ�̬����
	 * 
	 * @return ���ز�������
	 */
	public DynamicObject getArgumentsObj();

	/**
	 * ���ò���ֵ
	 * 
	 * <p>
	 * ˳��������䶨��Ĳ�����ֵ���������ֵ���ඨ��Ĳ��ֽ������ԡ�
	 * 
	 * @param argValues
	 *            ����ֵ
	 */
	public void setArgumentValues(Object... argValues);

	/**
	 * ����ָ��������ֵ
	 * 
	 * @param argIndex
	 *            ��������ţ���0��ʼ
	 * @param argValue
	 *            ����ֵ
	 */
	public void setArgumentValue(int argIndex, Object argValue);

	/**
	 * ����ָ��������ֵ
	 * 
	 * @param arg
	 *            ��������
	 * @param argValue
	 *            ����ֵ
	 */
	public void setArgumentValue(ArgumentDefine arg, Object argValue);

	/**
	 * ִ����䣬�����ص�Ӱ������
	 * 
	 * <p>
	 * ����ֵ��Ŀ���߼���Ϊ�������ʱ�ķ���������
	 * 
	 * @return ����ִ�е�Ӱ�����ĸ���
	 */
	public int executeUpdate();

	/**
	 * ִ�в�ѯ��װ�ؽ����
	 * 
	 * <p>
	 * ��ѯ�����һ����װ�뵽��¼���С�����ѯ�����н϶�ʱ������ʹ�ô����޶��Ĳ�ѯ���ߵ���������Ĳ�ѯ�ӿڡ�
	 * 
	 * @return ��ѯ��¼��
	 */
	public RecordSet executeQuery();

	/**
	 * ִ�д����޶��Ĳ�ѯ��װ�ؼ�¼��
	 * 
	 * @param offset
	 *            ��ָ��ƫ������ʼװ�ؽ���С��ӵ�1�п�ʼ������ƫ����Ϊ0��
	 * @param rowCount
	 *            װ�ص�������(�������)��
	 * @return ��ѯ��¼��
	 */
	public RecordSet executeQueryLimit(long offset, long rowCount);

	/**
	 * ִ�в�ѯ��ʹ��ָ���������������
	 * 
	 * <p>
	 * ���Ὣ�����һ����װ���ڴ棬���ڷ����нϴ�Ĳ�ѯ��
	 * 
	 * @param action
	 *            ��ѯ����ı�������
	 */
	public void iterateQuery(RecordIterateAction action);

	/**
	 * ִ�д����޶��Ĳ�ѯ��ʹ��ָ���������������
	 * 
	 * @param action
	 *            ��ѯ����ı�������
	 * @param offset
	 *            ��ָ��ƫ������ʼ��������У��ӵ�1�п�ʼ������ƫ����Ϊ0��
	 * @param rowCount
	 *            ������������(�������)
	 */
	public void iterateQueryLimit(RecordIterateAction action, long offset,
			long rowCount);

	/**
	 * ִ�в�ѯ
	 * 
	 * @return ���ؽ����һ�е�һ�е�ֵ
	 */
	public Object executeScalar();

	/**
	 * ���ز�ѯ���������
	 * 
	 * @return
	 */
	public int rowCountOf();

	/**
	 * ���ز�ѯ���������
	 * 
	 * @return
	 */
	public long rowCountOfL();

	/**
	 * ִ�д洢����
	 * 
	 * @return û�ж������������򷵻س���Ϊ0�����飬���᷵��NULL��
	 */
	public RecordSet[] executeProcedure();

	/**
	 * �ͷ����ݿ�������ص����ݿ���Դ
	 * 
	 * <p>
	 * ���ø÷������ᵼ�¶��󲻿��ã�ֻ����ʱ�ͷ����ݿ���Դ��<br>
	 * ������ʹ�ø�������󣬻������һ��ʹ�ú�Զʱ�����ø÷��������Ż����ݿ����ӡ�<br>
	 * ʹ�����ǿ�ҽ�����ã���û�бط���finally���С�<br>
	 */
	void unuse();

	/**
	 * ��ȡ����ֵ
	 * 
	 * <p>
	 * ��Ҫ���ڻ�ȡ�洢���̵Ĵ���������
	 * 
	 * @param index
	 * @return
	 */
	Object getArgumentValue(int index);

	/**
	 * ��ȡ����ֵ
	 * 
	 * <p>
	 * ��Ҫ���ڻ�ȡ�洢���̵Ĵ���������
	 * 
	 * @param arg
	 * @return
	 */
	Object getArgumentValue(ArgumentDefine arg);
}