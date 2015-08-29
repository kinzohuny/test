package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.Undigester;

/**
 * �����Ľṹ����ʵ��
 * 
 * @author gaojingxin
 * 
 */
public final class StaticStructDefineImpl extends StructDefineImpl {
	static final String type_name_prefix = "class:";

	@Override
	protected final String structTypeNamePrefix() {
		return type_name_prefix;
	}

	@Override
	public Class<?> getRegClass() {
		return this.soClass;
	}

	/**
	 * �������Ʋ�������
	 */
	public final static StaticStructDefineImpl tryParse(String fullName) {
		if (fullName.startsWith(type_name_prefix)) {
			return DataTypeBase.findStaticStructDefine(fullName);
		}
		return null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.STRUCT_H);
		super.digestType(digester);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.STRUCT_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException, StructDefineNotFoundException {
				return undigestType(undigester);
			}
		});
	}

	static class ETryLoadJavaFieldsAndPrepareAccessInfo extends
			RuntimeException {
		private static final long serialVersionUID = 1L;

		private ETryLoadJavaFieldsAndPrepareAccessInfo(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * ���췽��������һ��ʼ�ͽ��Լ����� assignerMap
	 */
	StaticStructDefineImpl(Class<?> soClass,
			Map<Class<?>, DataTypeInternal> dataTypeByClass,
			HashMap<String, StaticStructDefineImpl> staticStructDefineByName,
			boolean allJavaFields)
			throws ETryLoadJavaFieldsAndPrepareAccessInfo {
		super(soClass.getName(), soClass);
		final DataTypeInternal byClassSave = dataTypeByClass.put(soClass, this);// ��ֹ�ݹ���ѭ��
		try {
			if (!this.tryLoadJavaFields(allJavaFields) && this.fields.isEmpty()) {
				throw new UnsupportedOperationException("Java��\"" + soClass + "\"û����Ч�Ľṹ�ֶ�");
			}
			this.prepareAccessInfo();
		} catch (Throwable e) {
			this.clearAccessInfo();
			if (byClassSave == null) {
				dataTypeByClass.remove(soClass);
			} else {
				dataTypeByClass.put(soClass, byClassSave);
			}
			throw new ETryLoadJavaFieldsAndPrepareAccessInfo(e);
		}
		dataTypeByClass.remove(soClass);
		if (DataTypeBase.regDataType(this)) {
			staticStructDefineByName.put(this.getTypeName(), this);
		}
	}

	StaticStructDefineImpl(Class<?> soClass) {
		super(soClass.getName(), soClass);
		this.tryLoadJavaFields(true);
		this.prepareAccessInfo();
	}

	// //////////////////////////
	// //// XML
	// //////////////////////////
	static final String xml_element_struct = "struct";

	@Override
	public final String getXMLTagName() {
		return xml_element_struct;
	}
}