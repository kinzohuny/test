package com.jiuqi.dna.core.impl;

import static com.jiuqi.dna.core.impl.BooleanConstExpr.FALSE;
import static com.jiuqi.dna.core.impl.BooleanConstExpr.TRUE;
import static com.jiuqi.dna.core.impl.IntConstExpr.ONE;
import static com.jiuqi.dna.core.impl.IntConstExpr.THREE;
import static com.jiuqi.dna.core.impl.IntConstExpr.TWO;
import static com.jiuqi.dna.core.impl.IntConstExpr.ZERO_INT;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.system.ContextVariable;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

/**
 * 环境变量
 * 
 * @author houchunlei
 * 
 */
public abstract class ContextVariableIntl extends NamedDefineImpl implements
		ContextVariable {

	public final boolean modifiable() {
		return this.modifiable;
	}

	/**
	 * @param name
	 *            参数名称，唯一
	 * @param description
	 *            参数介绍
	 * @param initialValue
	 *            默认值
	 * @param getSystemValue
	 *            是否读取Java虚拟机参数替代代码指定的默认值
	 * @param modifiable
	 *            是否运行期可修改
	 * @param acceptedValues
	 *            可接受的值
	 */
	private ContextVariableIntl(String name, String description,
			ReadableValue initialValue, boolean getSystemValue,
			boolean modifiable, AcceptedValue... acceptedValues) {
		super(name);
		this.setDescription(description);
		this.initial = getSystemValue ? getSystemValue(name, initialValue.getType(), initialValue) : initialValue;
		this.modifiable = modifiable;
		if (acceptedValues == null) {
			throw new IllegalArgumentException("未定义参数[" + name + "]的接受值。");
		}
		for (AcceptedValue acceptedValue : acceptedValues) {
			if (this.acceptedValues.put(acceptedValue.value, acceptedValue.description) != null) {
				throw new IllegalArgumentException("重复定义了参数[" + name + "]的接受值[" + acceptedValue.value + "]。");
			}
		}
	}

	private static final ReadableValue getSystemValue(String name,
			DataType type, ReadableValue defaultValue) {
		final String prop = System.getProperty(name);
		final Object value = prop != null && prop.length() > 0 ? prop : defaultValue;
		return type.detect(ConstExpr.parser, value);
	}

	private static final AcceptedValue $(ReadableValue value, String description) {
		return new AcceptedValue(value, description);
	}

	static final class AcceptedValue {

		final ReadableValue value;

		final String description;

		AcceptedValue(ReadableValue value, String description) {
			this.value = value;
			this.description = description;
		}

		static final AcceptedValue[] ON_OFF = new AcceptedValue[] { $(TRUE, "开启"), $(FALSE, "关闭") };

		static final AcceptedValue[] FOUR_STATE = new AcceptedValue[] { $(ZERO_INT, "不检查"), $(ONE, "检查并打印简单信息"), $(TWO, "检查并打印栈信息"), $(THREE, "检查并抛出异常") };
	}

	public final ReadableValue initial;
	public final boolean modifiable;
	private final ThreadLocal<ReadableValue> local = new ThreadLocal<ReadableValue>();
	private final LinkedHashMap<ReadableValue, String> acceptedValues = new LinkedHashMap<ReadableValue, String>();

	public final Iterable<Entry<ReadableValue, String>> acceptedValues() {
		return this.acceptedValues.entrySet();
	}

	@Override
	public final String getXMLTagName() {
		throw new UnsupportedOperationException();
	}

	public final boolean isNull() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.isNull();
		}
		return this.initial.isNull();
	}

	public final Object getObject() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getObject();
		}
		return this.initial.getObject();
	}

	public final boolean getBoolean() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getBoolean();
		}
		return this.initial.getBoolean();
	}

	public final char getChar() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getChar();
		}
		return this.initial.getChar();
	}

	public final byte getByte() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getByte();
		}
		return this.initial.getByte();
	}

	public final short getShort() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getShort();
		}
		return this.initial.getShort();
	}

	public final int getInt() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getInt();
		}
		return this.initial.getInt();
	}

	public final long getLong() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getLong();
		}
		return this.initial.getLong();
	}

	public final long getDate() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getDate();
		}
		return this.initial.getDate();
	}

	public final float getFloat() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getFloat();
		}
		return this.initial.getFloat();
	}

	public final double getDouble() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getDouble();
		}
		return this.initial.getDouble();
	}

	public final byte[] getBytes() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getBytes();
		}
		return this.initial.getBytes();
	}

	public final String getString() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getString();
		}
		return this.initial.getString();
	}

	public final GUID getGUID() {
		ReadableValue local = this.local.get();
		if (local != null) {
			return local.getGUID();
		}
		return this.initial.getGUID();
	}

	public final DataType getType() {
		return this.initial.getType();
	}

	private final void checkModifiable() {
		if (!this.modifiable) {
			throw new UnsupportedOperationException("上下文参数[" + this.name + "]不允许修改。");
		}
	}

	private final void setValue0(Object value) {
		this.checkModifiable();
		ReadableValue setValue = this.initial.getType().detect(ConstExpr.parser, value);
		this.checkAcceptValue(setValue);
		this.local.set(setValue);
	}

	final void checkAcceptValue(ReadableValue value) {
		if (this.acceptedValues.get(value) == null) {
			throw new UnsupportedOperationException("参数[" + this.name + "]的设值[" + value.toString() + "]，不是有效值。");
		}
	}

	public final void setNull() {
		this.checkModifiable();
		this.local.remove();
	}

	public final void setObject(Object value) {
		this.setValue0(value);
	}

	public final void setValue(ReadableValue value) {
		this.setValue0(value.getObject());
	}

	public final void setBoolean(boolean value) {
		this.setValue0(value);
	}

	public final void setChar(char value) {
		this.setValue0(value);
	}

	public final void setShort(short value) {
		this.setValue0(value);
	}

	public final void setInt(int value) {
		this.setValue0(value);
	}

	public final void setLong(long value) {
		this.setValue0(value);
	}

	public final void setDate(long value) {
		this.setValue0(value);
	}

	public final void setFloat(float value) {
		this.setValue0(value);
	}

	public final void setDouble(double value) {
		this.setValue0(value);
	}

	public final void setString(String value) {
		this.setValue0(value);
	}

	public final void setByte(byte value) {
		this.setValue0(value);
	}

	public final void setBytes(byte[] value) {
		this.setValue0(value);
	}

	public final void setGUID(GUID value) {
		this.setValue0(value);
	}

	public static final void reg(ContextImpl<?, ?, ?> context) {
		for (Field field : ContextVariableIntl.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && ContextVariableIntl.class.isAssignableFrom(field.getType())) {
				try {
					final ContextVariableIntl impl = (ContextVariableIntl) field.get(ContextVariableIntl.class);
					final Class<?>[] intfs = impl.getClass().getInterfaces();
					if (intfs.length > 0) {
						for (Class<?> intf : intfs) {
							if (ContextVariable.class.isAssignableFrom(intf)) {
								impl.regToSite(context, intf);
								break;
							}
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	final void regToSite(ContextImpl<?, ?, ?> context, Class<?> targetClass) {
		context.occorAt.site.regNamedDefineToSpace(ContextVariable.class, this, context.catcher);
		context.occorAt.site.regInvokee(targetClass, new SpaceElementBroker<ContextVariableIntl>(this, Publish.Mode.PUBLIC), context.catcher);
		if (this.modifiable) {
			modifiableVariables.add(this);
		}
	}

	private static final ArrayList<ContextVariableIntl> modifiableVariables = new ArrayList<ContextVariableIntl>();

	public static final class DebugSqlDDLImpl extends ContextVariableIntl
			implements DebugSqlDDL {

		public static final String NAME = "com.jiuqi.dna.debug.sql.ddl";
		public static final String DESC = "控制台打印输出DDL。";

		private DebugSqlDDLImpl() {
			super(NAME, DESC, FALSE, true, true, AcceptedValue.ON_OFF);
		}
	}

	public static final DebugSqlDDLImpl DEBUG_DDL = new DebugSqlDDLImpl();

	public static final boolean isDebugDDL() {
		return DEBUG_DDL.getBoolean();
	}

	public static final class DebugSqlDMLImpl extends ContextVariableIntl
			implements DebugSqlDML {

		public static final String NAME = "com.jiuqi.dna.debug.sql.dml";
		public static final String DESC = "控制台打印输出DML。";

		private DebugSqlDMLImpl() {
			super(NAME, DESC, FALSE, true, true, AcceptedValue.ON_OFF);
		}
	}

	public static final DebugSqlDMLImpl DEBUG_DML = new DebugSqlDMLImpl();

	public static final boolean isDebugDML() {
		return DEBUG_DML.getBoolean();
	}

	public static final class DebugSqlDurationImpl extends ContextVariableIntl
			implements DebugSqlDuration {

		public static final String NAME = "com.jiuqi.dna.debug.sql.duration";
		public static final String DESC = "控制台调试输出SQL语句的执行时间。隐含了控制台输出DDL和DML。";

		private DebugSqlDurationImpl() {
			super(NAME, DESC, FALSE, true, true, AcceptedValue.ON_OFF);
		}
	}

	public static final DebugSqlDurationImpl DEBUG_SQL_DURATION = new DebugSqlDurationImpl();

	public static final boolean isDebugSqlDuration() {
		return DEBUG_SQL_DURATION.getBoolean();
	}

	public static final class DebugSqlParamImpl extends ContextVariableIntl
			implements DebugSqlParam {

		public static final String NAME = "com.jiuqi.dna.debug.sql.param";
		public static final String DESC = "控制台调试输出SQL语句的绑定变量赋值。";

		private DebugSqlParamImpl() {
			super(NAME, DESC, FALSE, true, true, AcceptedValue.ON_OFF);
		}
	}

	public static final DebugSqlParamImpl DEBUG_SQL_PARAM = new DebugSqlParamImpl();

	public static final boolean isDebugSqlParam() {
		return DEBUG_SQL_PARAM.getBoolean();
	}

	public static final class StrictExprDomainImpl extends ContextVariableIntl
			implements StrictExprDomain {

		public static final String NAME = "com.jiuqi.dna.strict-expr-domain";
		public static final String DESC = "严格检查表达式的使用域。非严格模式下，语句的对象树可能非法，克隆操作可能错误。";

		private StrictExprDomainImpl() {
			super(NAME, DESC, FALSE, true, true, AcceptedValue.ON_OFF);
		}
	}

	public static final StrictExprDomainImpl STRICT_EXPR_DOMAIN = new StrictExprDomainImpl();

	public static final boolean isStrictExprDomain() {
		return STRICT_EXPR_DOMAIN.getBoolean();
	}

	public static final class StrictAssignTypeImpl extends ContextVariableIntl
			implements StrictAssignType {

		public static final String NAME = "com.jiuqi.dna.strict-assign-type";
		public static final String DESC = "严格的赋值类型检查，包括：Insert、Update语句的赋值，以及ORM的字段映射。";

		private StrictAssignTypeImpl() {
			super(NAME, DESC, TWO, true, true, AcceptedValue.FOUR_STATE);
		}
	}

	public static final StrictAssignTypeImpl STRICT_ASSIGN_TYPE = new StrictAssignTypeImpl();

	public static final int isStrictAssignType() {
		return STRICT_ASSIGN_TYPE.getInt();
	}

	public static final class StrictNullUsageImpl extends ContextVariableIntl
			implements StrictNullUsage {

		public static final String NAME = "com.jiuqi.dna.strict-null-usage";
		public static final String DESC = "严格要求Null表达式的使用范围，即不能用作函数的输入参数，及各种谓词运算。只能用作insert和update的赋值，及select输出。";

		private StrictNullUsageImpl() {
			super(NAME, DESC, TWO, true, true, AcceptedValue.FOUR_STATE);
		}
	}

	public static final StrictNullUsageImpl STRICT_NULL_USAGE = new StrictNullUsageImpl();

	public static final int isStrictNullUsage() {
		return STRICT_NULL_USAGE.getInt();
	}

	public static final class StrictCompareDataTypeImpl extends
			ContextVariableIntl implements StrictCompareDataType {

		public static final String NAME = "com.jiuqi.dna.strict-compare-datatype";
		public static final String DESC = "比较运算（=、>、<等），严格检查运算类型是否匹配。";

		private StrictCompareDataTypeImpl() {
			super(NAME, DESC, ONE, true, true, AcceptedValue.FOUR_STATE);
		}
	}

	public static final StrictCompareDataTypeImpl STRICT_COMPARE_DATATYPE = new StrictCompareDataTypeImpl();

	public static final int isStrictCompareDatatype() {
		return STRICT_COMPARE_DATATYPE.getInt();
	}

	public static final class OracleUsingNativeFuncImpl extends
			ContextVariableIntl implements OracleUsingNativeFunc {

		public static final String NAME = "com.jiuqi.dna.oracle-using-native-func";
		public static final String DESC = "当使用Oracle数据源在生成SQL语句时，尽量使用Oracle的本地函数，而避免使用自定义函数。（设值为true能避免某些极端情况下的性能问题，SQL语句可读性降低。）";

		private OracleUsingNativeFuncImpl() {
			super(NAME, DESC, FALSE, true, true, AcceptedValue.ON_OFF);
		}
	}

	public static final OracleUsingNativeFuncImpl ORACLE_USING_NATIVE_FUNC = new OracleUsingNativeFuncImpl();

	public static final boolean isOracleUsingNativeFunc() {
		return ORACLE_USING_NATIVE_FUNC.getBoolean();
	}

	public static final void clearContextVariable() {
		for (int i = 0; i < modifiableVariables.size(); i++) {
			modifiableVariables.get(i).setNull();
		}
	}

	public static final class OptimizeModifySqlOmitTargetSourceImpl extends
			ContextVariableIntl implements OptimizeModifySqlOmitTargetSource {

		public static final String NAME = "com.jiuqi.dna.optimize-modify-sql-omit-target-source";
		public static final String DESC = "更新和删除语句有表内连接，生成语句where条件和exist的join语句里，可以省略更新的表，优化性能。（设值为true，如果剩下join语句使用更新表的引用，oracle数据库会报错）";

		private OptimizeModifySqlOmitTargetSourceImpl() {
			super(NAME, DESC, FALSE, false, true, AcceptedValue.ON_OFF);
		}
	}

	public static final OptimizeModifySqlOmitTargetSourceImpl OPTIMIZE_MODIFY_SQL_OMIT_TARGET_SOURCE = new OptimizeModifySqlOmitTargetSourceImpl();

	private static final boolean booleanOf(String key, boolean defaultValue) {
		final String value = System.getProperty(key);
		if (value != null && value.length() > 0) {
			return Boolean.valueOf(value);
		}
		return defaultValue;
	}

	private static final int intOf(String key, int defaultValue) {
		final String value = System.getProperty(key);
		if (value != null && value.length() > 0) {
			return Integer.valueOf(value);
		}
		return defaultValue;
	}

	/**
	 * 禁止subselect使用orderby子句，将抛出异常，否则只打印警告。
	 */
	public static final boolean FORBIDE_SUBSELECT_ORDERBY = booleanOf("com.jiuqi.dna.forbid-subselect-orderby", false);

	/**
	 * 禁止使用cube类型的groupby子句， 将抛出异常，否则只打印警告。
	 */
	public static final boolean FORBIDE_CUBE_GROUPBY = booleanOf("com.jiuqi.dna.forbid-cube-groupby", true);

	/**
	 * 禁止使用grouping函数，将抛出异常，否则只打印警告。
	 */
	public static final boolean FORBID_GROUPING = booleanOf("com.jiuqi.dna.forbid-grouping", false);

	/**
	 * 内联视图独立的，不能使用任何上级的关系引用。如果为false，在db2，mysql上将会存在兼容问题。
	 */
	public static final boolean derived_query_standalone = booleanOf("com.jiuqi.dna.derived-query-standalone", true);

	public static final int ORM_PER_BYIDS_DELETE = intOf("com.jiuqi.dna.orm-per-byrecids-delete", 10);

	public static final boolean CHECK_IMP_FILE = booleanOf("com.jiuqi.dna.check-imp-file", true);

	public static final boolean PRINT_SYNC_TABLE = booleanOf("com.jiuqi.dna.print-sync-table", false);

	public static final boolean PRINT_INIT_SERVICE = booleanOf("com.jiuqi.dna.print-init-service", false);

	public static final boolean UNSAFE_FORCE_SKIP_DB_SYNC = booleanOf("com.jiuqi.dna._unsafe-force-skip-db-sync_", false);

	public static boolean ENABLE_CACHE_MODIFY_EVENT;
}