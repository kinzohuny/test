package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.da.SQLFuncSpec;
import com.jiuqi.dna.core.impl.DeclaratorBase;
import com.jiuqi.dna.core.impl.UserFunctionImpl;
import com.jiuqi.dna.core.type.DataType;

/**
 * �û����庯����������
 * 
 * <p>
 * �û����庯�������ƻᱻ�Զ�ת�ɴ�дע�ᵽվ��ĸ�Ŀ¼�С����������Ҳ�����ϵͳĬ�ϵĺ��������ظ���
 * <p>
 * �����ķ������Ϳ���Ϊ��
 * <ul>
 * <li>TypeFactory.STRING
 * <li>TypeFactory.BYTES
 * <li>TypeFactory.INT
 * <li>TypeFactory.LONG
 * <li>TypeFactory.DOUBLE
 * <li>TypeFactory.DATE
 * </ul>
 * <p>
 * ���췽��isNonDeterministic����ָ��������ȷ���ԣ������ڲ������������£������ķ���ֵ�Ƿ����ǲ��䡣���磬getdate()��
 * new_recid
 * ()��Ϊ����ֵ��ȷ���ĺ�������ָ��������£��ò���Ϊfalse����ܲ���Ҳ�����ܼ�麯����ȷ���ԣ���Ҫ�û���ȷ��������ȷ���ԡ�����ȷ��ָʾ������ȷ����
 * ���ᵼ��ʹ�øú�����insert��update���ִ�д���
 * 
 * <p>
 * ��ʹ�õ�ǰ�����������û�����ʱ�������ṩͬ���Ƶĺ��������ű�����ʹ�þݿ�����﷨�Ĵ���������sql�ű���������������
 * <code>UF_MyFunc</code>
 * �������Oracle�ϣ���Ҫ�ṩ��Ӧ��sql�ű�"UF_MyFunc.oracle"����"UF_MyFunc.oracle10"
 * �����DB2����չ��Ϊ"db2"�����SQL
 * Server����չ��Ϊ"sqlserver"��"sqlserver9"��"sqlserver2005"�����MySQL����չ��Ϊ"mysql"��
 * 
 * <p>
 * ��DB2�ϣ��û�����������ָ�����ĵ������ƣ�������specific����
 * 
 * <p>
 * ����ʹ��<code>Context<code>��<code>get</code>������ȡָ�����ƴ��û����庯����
 * 
 * <blockquote>
 * 
 * <pre>
 * UserFunctionDefine uf = context.get(UserFunction.class, &quot;��д����&quot;);
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
			throw new NullPointerException("�û����庯������Ϊ�ա�");
		}
		if (SQLFuncSpec.contains(name)) {
			throw new IllegalArgumentException("[" + name
					+ "]�Ѿ���Ϊϵͳ�������ƣ�������ע��ͬ�����û����庯����");
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
