package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.da.SQLFuncSpec.ArgumentSpec;
import com.jiuqi.dna.core.type.DataType;

public final class ScalarFuncArgumentSpecConstants {

	static final ArgumentSpec date = new ArgumentSpec("date", "日期表达式", DateType.TYPE);

	static final ArgumentSpec interval = new ArgumentSpec("interval", "时间间隔数", LongType.TYPE);

	static final ArgumentSpec startdate = new ArgumentSpec("startdate", "开始日期", DateType.TYPE);

	static final ArgumentSpec enddate = new ArgumentSpec("enddate", "结束日期", DateType.TYPE);

	static final ArgumentSpec radians = new ArgumentSpec("radians", "弧度", DoubleType.TYPE);

	static final ArgumentSpec base = new ArgumentSpec("base", "底数", DoubleType.TYPE);

	static final ArgumentSpec power = new ArgumentSpec("power", "指数", DoubleType.TYPE);

	static final ArgumentSpec number = new ArgumentSpec("number", "数值", DoubleType.TYPE);

	static final ArgumentSpec string = new ArgumentSpec("str", "字符串", StringType.TYPE) {

		@Override
		public boolean accept(DataType type) {
			return type.getRootType() == StringType.TYPE && !type.isLOB() || type == NullType.TYPE;
		}
	};

	static final ArgumentSpec clob = new ArgumentSpec("clob", "大字符串", StringType.TYPE) {

		@Override
		public boolean accept(DataType type) {
			return type == TextDBType.TYPE || type == NTextDBType.TYPE || type == NullType.TYPE;
		}
	};

	static final ArgumentSpec bytes = new ArgumentSpec("bytes", "二进制串", BytesType.TYPE) {

		@Override
		public boolean accept(DataType type) {
			return type.getRootType() == BytesType.TYPE && !type.isLOB() || type == NullType.TYPE;
		}
	};

	static final ArgumentSpec blob = new ArgumentSpec("blob", "大二进制串", BytesType.TYPE) {

		@Override
		public boolean accept(DataType type) {
			if (type == BlobDBType.TYPE || type == NullType.TYPE) {
				return true;
			}
			return false;
		}
	};

	static final ArgumentSpec search = new ArgumentSpec("search", "搜索字符串", StringType.TYPE);

	static final ArgumentSpec start_position = new ArgumentSpec("start_position", "开始位置", LongType.TYPE);

	static final ArgumentSpec substr_length = new ArgumentSpec("length", "截取长度", LongType.TYPE);

	static final ArgumentSpec scale = new ArgumentSpec("scale", "截取精度", LongType.TYPE);

	static final ArgumentSpec replace = new ArgumentSpec("replace", "替换字符串", StringType.TYPE);

	static final ArgumentSpec pad_length = new ArgumentSpec("length", "填充后长度", LongType.TYPE);

	static final ArgumentSpec pad_str = new ArgumentSpec("replace", "填充字符串字符串", StringType.TYPE);

}
