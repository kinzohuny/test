package com.jiuqi.dna.core.spi.setl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.GUID;

/**
 * SETL�ⲿ�����ӿڡ�<br>
 * �ýӿ������¼�����;��
 * <ul>
 * <li>�ṩ��������</li>
 * <li>�ṩĿ�����Ϣ���Ƿ񸡶���ά���ֶΡ�ά��ֵ��ת������</li>
 * <li>�ṩĿ����¼�Ĺ�����</li>
 * <li>�ṩд�����󣬿��ø���������ָ����ȡִ�й��̵Ĳ���</li> �ýӿ�Ӧ��ָ����ȡ����/��ȡ�������ù��ܵĿ�����Աʵ�֡�
 */
public interface SETLExternalHelper {
	public SETLSAXParseReporter getReporter();

	/**
	 * ��ȡĿ����ά����Ϣ
	 */
	public SETLTargetDimProvider[] getDimProviders(Context context,
			TableDefine table);

	/**
	 * ��ȡд��
	 */
	public SETLWriteLock getWriteLock();

	/**
	 * ��ȡĿ�����ݹ��������ɷ���null<br>
	 * ע�⣬ָ����ȡ�㷨���ܻ�ʹ�ö���̶߳�Ŀ���table������ȡ��ÿ���̶߳�����ø÷�������ȡһ���������� ������ʵ�ֹ�����ʱӦ��ע���̰߳�ȫ���⡣
	 */
	public SETLTargetFilter createTargetFilter(Context context,
			TableDefine table);

	/**
	 * ��ȡһ������ֵ��ָʾĿ����Ƿ�Ϊ������
	 */
	public boolean isFloatTable(Context context, TableDefine table);

	/**
	 * ��ȡ�Զ����Ŀ���ֶ��б���Щ�ֶβ���ָ����ȡӳ���Ŀ�꣬���ǿ�����Ŀ�����ݹ��������޸ġ�
	 */
	public TableFieldDefine[] getCustomTargetFields(TableDefine table);

	/**
	 * ʵ���ʶ����
	 * 
	 * @author gaojingxin
	 * 
	 */
	public interface EntityPaths {
		public void put(String path);
	}

	/**
	 * �������ñ��ö�Ӧ�����ð�����
	 */
	public SETLEntityRefHelper getEntityRefHelper(Context contex,
			TableDefine refTable);

	/**
	 * �����ṩά��ֵ�Ľӿ�
	 */
	public interface SETLTargetDimProvider {
		/**
		 * ��ȡά���ֶ�
		 */
		public TableFieldDefine getField();

		/**
		 * ��ȡά��ֵ�����͡������������ά���ֶ����Ͳ���ͬ��getConverter�������뷵��һ���ܹ���ά��ֵת�����ֶ�ֵ��ת����ʵ����
		 */
		public SETLTargetDimValueType getValueType();

		/**
		 * ��ȡĿ���ά��ֵ-�ֶ�ֵת���������Է���null��
		 */
		public SETLValueConverter getConverter();

		/**
		 * ����ҵ�񷽰���GUID��ȡά��ֵ������ά��ֵ��ӵ�������
		 */
		public void getValues(Context context, GUID slnID,
				SETLTargetDimValueSet set);

		/**
		 * ��ȡ��������
		 */
		public SETLTargetIndexType getIndexType();
	}

	/**
	 * Ŀ�������ֵ������
	 */
	public enum SETLTargetDimValueType {
		/**
		 * ���ͣ���������
		 */
		LONG(Long.class, false),
		/**
		 * �ַ�����
		 */
		STRING(String.class, false),
		/**
		 * GUID
		 */
		GUID(GUID.class, false),
		/**
		 * �������䣬����������
		 */
		LONG_RANGE(Long.class, true);

		private final Class<?> javaClass;
		private final boolean range;

		SETLTargetDimValueType(Class<?> javaClass, boolean range) {
			this.javaClass = javaClass;
			this.range = range;
		}

		public Class<?> getJavaClass() {
			return this.javaClass;
		}

		public boolean isRange() {
			return this.range;
		}
	}

	/**
	 * Ŀ���ά��ֵ��������ʽ
	 */
	public enum SETLTargetIndexType {
		/**
		 * ��ϣ����
		 */
		HASH,
		/**
		 * ��������
		 */
		ORDINAL
	}

	public interface SETLValueConverter {
		public Object convertFrom(GUID slnID, Object value);
	}

	/**
	 * Ŀ����������ڶ�Ŀ������ά�Ⱥ������н��й��ˣ���ȷ����Щ��Ҫ������<br>
	 * ͨ�����ڹ̶�����˵ȷ��ά��ֵ������£�ֻ��һ�У����ڸ�������˵��ȷ��ά���Ͽ��ܰ������С�<br/>
	 * ָ����ȡ�㷨���ڸ���Ŀ���ʱ����shouldUpdateDim������ȷ��ָ��ά�ȵ������Ƿ�Ӧ�ø��£�<br/>
	 * <li>����÷�������true����ָ����ȡ�㷨����shouldUpdateRow��ȷ��ָ��ά���µ�ÿһ���Ƿ�Ӧ�ø��¡�</li> <li>
	 * ����÷�������false���򲻸���ָ��ά���µ��У����Ҳ���ָ��ά���µ��е���shouldUpdateRow������</li>
	 */
	public interface SETLTargetFilter {
		/**
		 * ��Ŀ�����ݵ�ά��ֵ���й��ˣ�����true��ʾ��Ŀ��ά�Ƚ��и��£�����false��ʾ�����¡�<br/>
		 * ����r����ά���ֶε�ֵ�����ܲ����������ֶε�ֵ���������޸��ֶ�ֵ��
		 */
		public boolean shouldUpdateDim(Context context, SETLTargetRecord r);

		/**
		 * ��Ŀ�������н��й��ˣ�����true��ʾ���¸��У�����false��ʾ�����¸��С�<br/>
		 * �����޸�Ŀ�����ݵķ�ά���ֶΡ�
		 */
		public boolean shouldUpdateRow(Context context, SETLTargetRecord r);

		public void unuse();
	}

	public interface SETLTargetRecord {
		Object getField(String name);

		void setField(String name, Object value);
	}
}
