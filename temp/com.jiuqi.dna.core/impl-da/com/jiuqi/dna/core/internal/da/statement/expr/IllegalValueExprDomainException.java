package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.def.exp.SelectColumnRefExpr;
import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;
import com.jiuqi.dna.core.def.query.SubQueryExpression;

/**
 * ��䶨��ı��ʽʹ����Ƿ�
 * 
 * <p>
 * core2.5��������䶨��ı��ʽʹ�����顣�ü�����ͨ�����������رա�
 * 
 * @see com.jiuqi.dna.core.system.ContextVariable.StrictExprDomain
 * 
 * @since core2.5
 * 
 * @author houchunlei
 * 
 */
public final class IllegalValueExprDomainException extends RuntimeException {

	private static final long serialVersionUID = -1720255180362049350L;

	public IllegalValueExprDomainException(SelectColumnRefExpr columnRef) {
		super("��ѯ������[" + columnRef.toString() + "]��ʹ�������.");
	}

	public IllegalValueExprDomainException(TableFieldRefExpr fieldRef) {
		super("�ֶ�����[" + fieldRef.toString() + "]��ʹ�������.");
	}

	public IllegalValueExprDomainException(SubQueryExpression expr) {
		super("�Ӳ�ѯ��ʹ�������乹����.");
	}
}