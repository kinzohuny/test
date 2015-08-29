package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.da.SQLFuncSpec.ArgumentSpec;
import com.jiuqi.dna.core.type.DataType;

public final class ScalarFuncArgumentSpecConstants {

	static final ArgumentSpec date = new ArgumentSpec("date", "���ڱ��ʽ", DateType.TYPE);

	static final ArgumentSpec interval = new ArgumentSpec("interval", "ʱ������", LongType.TYPE);

	static final ArgumentSpec startdate = new ArgumentSpec("startdate", "��ʼ����", DateType.TYPE);

	static final ArgumentSpec enddate = new ArgumentSpec("enddate", "��������", DateType.TYPE);

	static final ArgumentSpec radians = new ArgumentSpec("radians", "����", DoubleType.TYPE);

	static final ArgumentSpec base = new ArgumentSpec("base", "����", DoubleType.TYPE);

	static final ArgumentSpec power = new ArgumentSpec("power", "ָ��", DoubleType.TYPE);

	static final ArgumentSpec number = new ArgumentSpec("number", "��ֵ", DoubleType.TYPE);

	static final ArgumentSpec string = new ArgumentSpec("str", "�ַ���", StringType.TYPE) {

		@Override
		public boolean accept(DataType type) {
			return type.getRootType() == StringType.TYPE && !type.isLOB() || type == NullType.TYPE;
		}
	};

	static final ArgumentSpec clob = new ArgumentSpec("clob", "���ַ���", StringType.TYPE) {

		@Override
		public boolean accept(DataType type) {
			return type == TextDBType.TYPE || type == NTextDBType.TYPE || type == NullType.TYPE;
		}
	};

	static final ArgumentSpec bytes = new ArgumentSpec("bytes", "�����ƴ�", BytesType.TYPE) {

		@Override
		public boolean accept(DataType type) {
			return type.getRootType() == BytesType.TYPE && !type.isLOB() || type == NullType.TYPE;
		}
	};

	static final ArgumentSpec blob = new ArgumentSpec("blob", "������ƴ�", BytesType.TYPE) {

		@Override
		public boolean accept(DataType type) {
			if (type == BlobDBType.TYPE || type == NullType.TYPE) {
				return true;
			}
			return false;
		}
	};

	static final ArgumentSpec search = new ArgumentSpec("search", "�����ַ���", StringType.TYPE);

	static final ArgumentSpec start_position = new ArgumentSpec("start_position", "��ʼλ��", LongType.TYPE);

	static final ArgumentSpec substr_length = new ArgumentSpec("length", "��ȡ����", LongType.TYPE);

	static final ArgumentSpec scale = new ArgumentSpec("scale", "��ȡ����", LongType.TYPE);

	static final ArgumentSpec replace = new ArgumentSpec("replace", "�滻�ַ���", StringType.TYPE);

	static final ArgumentSpec pad_length = new ArgumentSpec("length", "���󳤶�", LongType.TYPE);

	static final ArgumentSpec pad_str = new ArgumentSpec("replace", "����ַ����ַ���", StringType.TYPE);

}
