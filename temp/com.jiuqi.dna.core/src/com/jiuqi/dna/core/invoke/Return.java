package com.jiuqi.dna.core.invoke;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ��������ĳЩ�ֶ���Ҫ��ִ������أ��ñ��ֻ��Զ�̵���ʱ������
 * 
 * @author gaojingxin
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Return {
	// Nothing
}
