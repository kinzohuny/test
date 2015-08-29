package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.da.SQLFuncSpec;
import com.jiuqi.dna.core.impl.DeclaratorBase;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.type.DataType;

/**
 * 用户定义函数的声明器
 * 
 * <p>
 * 用户定义函数的名称会被自动转成大写注册到站点的根目录中。函数名称且不能与系统默认的函数名称重复。
 * <p>
 * 函数的返回类型可以为：
 * <ul>
 * <li>TypeFactory.STRING
 * <li>TypeFactory.BYTES
 * <li>TypeFactory.INT
 * <li>TypeFactory.LONG
 * <li>TypeFactory.DOUBLE
 * <li>TypeFactory.DATE
 * </ul>
 * <p>
 * 构造方法isNonDeterministic用于指定函数的确定性，即：在参数不变的情况下，函数的返回值是否总是不变。例如，getdate()，
 * new_recid
 * ()即为返回值不确定的函数。不指定的情况下，该参数为false。框架不会也不可能检查函数的确定性，需要用户正确的声明其确定性。不正确的指示函数的确定性
 * ，会导致使用该函数的insert或update语句执行错误。
 * 
 * <p>
 * 在使用当前声明器定义用户函数时，必须提供同名称的函数构建脚本，即使用据库各自语法的创建函数的sql脚本。例如有声明器
 * <code>UF_MyFunc</code>
 * ，则针对Oracle上，需要提供想应的sql脚本"UF_MyFunc.oracle"，或"UF_MyFunc.oracle10"
 * ；针对DB2的扩展名为"db2"；针对SQL
 * Server的扩展名为"sqlserver"，"sqlserver9"，"sqlserver2005"；针对MySQL的扩展名为"mysql"。
 * 
 * <p>
 * 在DB2上，用户函数的名称指函数的调用名称，而不是specific名。
 * 
 * <p>
 * 可以使用<code>Context<code>的<code>get</code>方法获取指定名称大用户定义函数。
 * 
 * <blockquote>
 * 
 * <pre>
 * UserFunctionDefine uf = context.get(UserFunction.class, &quot;大写名称&quot;);
 * </pre>
 * 
 * </blockquote>
 * 
 * @author houchunlei
 * 
 */
public abstract class UserFunctionDeclarator extends DeclaratorBase {

	public final UserFunctionDeclare function;

	@Override
	public final UserFunctionDefine getDefine() {
		return this.function;
	}

	public UserFunctionDeclarator(String name, DataType returns) {
		this(name, returns, false);
	}

	public UserFunctionDeclarator(String name, DataType returns,
			boolean isNonDeterministic) {
		super(false);
		if (name == null || name.length() == 0) {
			throw new NullPointerException("用户定义函数名称为空。");
		}
		if (SQLFuncSpec.contains(name)) {
			throw new IllegalArgumentException("[" + name
					+ "]已经作为系统函数名称，不允许注册同名的用户定义函数。");
		}
		this.function = new UserFunctionImpl(name, returns, isNonDeterministic,
				this);
	}

	@Override
	protected final Class<?>[] getDefineIntfRegClasses() {
		return intf_classes;
	}

	static final Class<?>[] intf_classes = { UserFunctionDefine.class };

}
