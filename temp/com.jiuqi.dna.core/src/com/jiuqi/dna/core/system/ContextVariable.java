package com.jiuqi.dna.core.system;

import java.util.Map.Entry;

import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.VariableValue;

/**
 * 上下文变量
 * 
 * <p>
 * 上下文变量包括全局、请求级别的系统参数。这些参数会在相应的级别影响系统行为。上下文变量的值根据配置或Java启动参数确定。
 * 部分上下文变量可以在请求级别修改其默认值。
 * 
 * <p>
 * 使用setNull方法来清空当前上下文的变量设值，从而使用全局的系统默认值。
 * 
 * @author houchunlei
 * 
 */
public interface ContextVariable extends NamedDefine, VariableValue {

	/**
	 * 可接受的值
	 * 
	 * <p>
	 * 使用ReadableValue提供，根据类型读取相应的值。
	 * 
	 * @return
	 */
	public Iterable<Entry<ReadableValue, String>> acceptedValues();

	/**
	 * 参数是否允许在上下文级别修改
	 * 
	 * @return
	 */
	public boolean modifiable();

	/**
	 * 控制台打印执行的DML语句
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.debug.sql.dml
	 * 
	 * <p>
	 * boolean类型，默认为false。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface DebugSqlDML extends ContextVariable {
	}

	/**
	 * 控制台打印执行的DDL语句
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.debug.sql.ddl
	 * 
	 * <p>
	 * boolean类型，默认为false。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface DebugSqlDDL extends ContextVariable {
	}

	/**
	 * 控制台打印执行的SQL语句及消耗的时间
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.debug.sql.duration
	 * 
	 * <p>
	 * boolean类型，默认为false。
	 * 
	 * <p>
	 * 隐含了控制台输出DDL和DML。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface DebugSqlDuration extends ContextVariable {
	}

	/**
	 * 控制台打印SQL的绑定变量
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.debug.sql.param
	 * 
	 * <p>
	 * boolean类型，默认为false。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface DebugSqlParam extends ContextVariable {
	}

	/**
	 * 严格检查表达式的使用域。非严格模式下，语句的对象树可能非法，克隆操作可能错误。
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.strict-expr-domain
	 * 
	 * <p>
	 * boolean类型。默认false。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface StrictExprDomain extends ContextVariable {
	}

	/**
	 * 严格的赋值类型检查，包括：Insert、Update语句的赋值，以及ORM的字段映射。
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.strict-assign-type
	 * 
	 * <p>
	 * int类型。
	 * 
	 * <p>
	 * 0，不检查；1，检查并打印信息；2，检查并打印异常栈；3，检查并抛出异常。
	 * 
	 * <p>
	 * 默认2。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface StrictAssignType extends ContextVariable {
	}

	/**
	 * 严格要求Null表达式的使用范围，即不能用作函数的输入参数，及各种谓词运算。只能用作insert和update的赋值，及select输出。
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.strict-null-usage
	 * 
	 * <p>
	 * int类型。
	 * 
	 * <p>
	 * 0，不检查；1，检查并打印信息；2，检查并打印异常栈；3，检查并抛出异常。
	 * 
	 * <p>
	 * 默认2。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface StrictNullUsage extends ContextVariable {
	}

	/**
	 * 严格检查比较运算（=、>、<等）各运算体的类型是否匹配。
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.strict-compare-datatype
	 * 
	 * <p>
	 * int类型。
	 * 
	 * <p>
	 * 0，不检查；1，检查并打印信息；2，检查并打印异常栈；3，检查并抛出异常。
	 * 
	 * <p>
	 * 默认1。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface StrictCompareDataType extends ContextVariable {
	}

	/**
	 * 当使用Oracle数据源在生成SQL语句时，尽量使用Oracle的本地函数，而避免使用自定义函数。
	 * 
	 * <p>
	 * JVM参数：com.jiuqi.dna.oracle-using-native-func
	 * 
	 * <p>
	 * boolean类型，默认为false。
	 * 
	 * <p>
	 * 设值为true能避免某些极端情况下的性能问题，SQL语句可读性降低。
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface OracleUsingNativeFunc extends ContextVariable {
	}

	/**
	 * 更新和删除语句有表内连接，是否优化语句，省略where和exist语句主表的join。
	 * 
	 * boolean类型，默认为false。
	 * 
	 */
	public static interface OptimizeModifySqlOmitTargetSource extends
			ContextVariable {
	}
}