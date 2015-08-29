package com.jiuqi.dna.core.internal.db.monitor;

import java.util.Iterator;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.table.TableDeclare;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

final class VariationStruct {

	/**
	 * ʱ���ֶΣ�����������ʱ��
	 */
	static final String VAR_DATE = "VAR_DATE";
	/**
	 * �����ֶΣ���������������ɾ�������͡�ֵΪ��I��U��D��
	 */
	static final String VAR_OPERATION = "VAR_OPERATION";
	/**
	 * ����汾�ֶΣ���ʶһ������������������ݿ������󲻻����á�
	 */
	static final String VAR_VERSION = "VAR_VERSION";

	static final DataType VAR_DATE_TYPE = TypeFactory.DATE;
	static final DataType VAR_OPERATION_TYPE = TypeFactory.NVARCHAR(2);
	static final DataType VAR_VERSION_TYPE = TypeFactory.LONG;

	static final TableDefineImpl build(Context context,
			VariationMonitorImpl monitor, TableDefineImpl target) {
		TableDefineImpl variation = (TableDefineImpl) context.get(
				TableDeclare.class, monitor.variation);
		Iterator<TableFieldDefineImpl> it = variation.getFields()
				.removableIterator();
		while (it.hasNext()) {
			TableFieldDefineImpl field = it.next();
			if (!field.isRECID() && !field.isRECVER()) {
				it.remove();
			}
		}
		variation.newField(VAR_DATE, VAR_DATE_TYPE);
		variation.newField(VAR_OPERATION, VAR_OPERATION_TYPE);
		variation.newField(VAR_VERSION, VAR_VERSION_TYPE);
		for (VariationMonitorFieldImpl vmf : monitor.watches) {
			buildWatch(target, variation, vmf);
		}
		return variation;
	}

	private static final void buildWatch(TableDefineImpl target,
			TableDefineImpl variation, VariationMonitorFieldImpl vmf) {
		final TableFieldDefineImpl f = target.fields.find(vmf.watchFN);
		if (f == null) {
			throw new IllegalStateException("�����������ı仯�������ʱ���󣺼����ֶ�[" + vmf.watchFN + "]�����ڡ�");
		}
		final DataType type = f.getType();
		variation.newField(vmf.oldValueFN, type);
		variation.newField(vmf.newValueFN, type);
	}
}