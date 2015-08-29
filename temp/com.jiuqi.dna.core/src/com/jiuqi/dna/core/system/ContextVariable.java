package com.jiuqi.dna.core.system;

import java.util.Map.Entry;

import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.type.ReadableValue;
import com.jiuqi.dna.core.type.VariableValue;

/**
 * �����ı���
 * 
 * <p>
 * �����ı�������ȫ�֡����󼶱��ϵͳ��������Щ����������Ӧ�ļ���Ӱ��ϵͳ��Ϊ�������ı�����ֵ�������û�Java��������ȷ����
 * ���������ı������������󼶱��޸���Ĭ��ֵ��
 * 
 * <p>
 * ʹ��setNull��������յ�ǰ�����ĵı�����ֵ���Ӷ�ʹ��ȫ�ֵ�ϵͳĬ��ֵ��
 * 
 * @author houchunlei
 * 
 */
public interface ContextVariable extends NamedDefine, VariableValue {

	/**
	 * �ɽ��ܵ�ֵ
	 * 
	 * <p>
	 * ʹ��ReadableValue�ṩ���������Ͷ�ȡ��Ӧ��ֵ��
	 * 
	 * @return
	 */
	public Iterable<Entry<ReadableValue, String>> acceptedValues();

	/**
	 * �����Ƿ������������ļ����޸�
	 * 
	 * @return
	 */
	public boolean modifiable();

	/**
	 * ����̨��ӡִ�е�DML���
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.debug.sql.dml
	 * 
	 * <p>
	 * boolean���ͣ�Ĭ��Ϊfalse��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface DebugSqlDML extends ContextVariable {
	}

	/**
	 * ����̨��ӡִ�е�DDL���
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.debug.sql.ddl
	 * 
	 * <p>
	 * boolean���ͣ�Ĭ��Ϊfalse��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface DebugSqlDDL extends ContextVariable {
	}

	/**
	 * ����̨��ӡִ�е�SQL��估���ĵ�ʱ��
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.debug.sql.duration
	 * 
	 * <p>
	 * boolean���ͣ�Ĭ��Ϊfalse��
	 * 
	 * <p>
	 * �����˿���̨���DDL��DML��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface DebugSqlDuration extends ContextVariable {
	}

	/**
	 * ����̨��ӡSQL�İ󶨱���
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.debug.sql.param
	 * 
	 * <p>
	 * boolean���ͣ�Ĭ��Ϊfalse��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface DebugSqlParam extends ContextVariable {
	}

	/**
	 * �ϸ�����ʽ��ʹ���򡣷��ϸ�ģʽ�£����Ķ��������ܷǷ�����¡�������ܴ���
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.strict-expr-domain
	 * 
	 * <p>
	 * boolean���͡�Ĭ��false��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface StrictExprDomain extends ContextVariable {
	}

	/**
	 * �ϸ�ĸ�ֵ���ͼ�飬������Insert��Update���ĸ�ֵ���Լ�ORM���ֶ�ӳ�䡣
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.strict-assign-type
	 * 
	 * <p>
	 * int���͡�
	 * 
	 * <p>
	 * 0������飻1����鲢��ӡ��Ϣ��2����鲢��ӡ�쳣ջ��3����鲢�׳��쳣��
	 * 
	 * <p>
	 * Ĭ��2��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface StrictAssignType extends ContextVariable {
	}

	/**
	 * �ϸ�Ҫ��Null���ʽ��ʹ�÷�Χ���������������������������������ν�����㡣ֻ������insert��update�ĸ�ֵ����select�����
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.strict-null-usage
	 * 
	 * <p>
	 * int���͡�
	 * 
	 * <p>
	 * 0������飻1����鲢��ӡ��Ϣ��2����鲢��ӡ�쳣ջ��3����鲢�׳��쳣��
	 * 
	 * <p>
	 * Ĭ��2��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface StrictNullUsage extends ContextVariable {
	}

	/**
	 * �ϸ���Ƚ����㣨=��>��<�ȣ���������������Ƿ�ƥ�䡣
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.strict-compare-datatype
	 * 
	 * <p>
	 * int���͡�
	 * 
	 * <p>
	 * 0������飻1����鲢��ӡ��Ϣ��2����鲢��ӡ�쳣ջ��3����鲢�׳��쳣��
	 * 
	 * <p>
	 * Ĭ��1��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface StrictCompareDataType extends ContextVariable {
	}

	/**
	 * ��ʹ��Oracle����Դ������SQL���ʱ������ʹ��Oracle�ı��غ�����������ʹ���Զ��庯����
	 * 
	 * <p>
	 * JVM������com.jiuqi.dna.oracle-using-native-func
	 * 
	 * <p>
	 * boolean���ͣ�Ĭ��Ϊfalse��
	 * 
	 * <p>
	 * ��ֵΪtrue�ܱ���ĳЩ��������µ��������⣬SQL���ɶ��Խ��͡�
	 * 
	 * @author houchunlei
	 * 
	 */
	public static interface OracleUsingNativeFunc extends ContextVariable {
	}

	/**
	 * ���º�ɾ������б������ӣ��Ƿ��Ż���䣬ʡ��where��exist��������join��
	 * 
	 * boolean���ͣ�Ĭ��Ϊfalse��
	 * 
	 */
	public static interface OptimizeModifySqlOmitTargetSource extends
			ContextVariable {
	}
}