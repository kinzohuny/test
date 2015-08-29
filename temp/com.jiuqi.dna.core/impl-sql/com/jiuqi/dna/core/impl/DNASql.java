package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import com.jiuqi.dna.core.def.DNASqlType;
import com.jiuqi.dna.core.def.MetaElement;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.spi.sql.SQLCombinedException;
import com.jiuqi.dna.core.spi.sql.SQLOutput;
import com.jiuqi.dna.core.spi.sql.SQLParseException;

/**
 * DNA-SQL���������
 * 
 * @author gaojingxin
 * 
 */
public final class DNASql {
	private static class RaisableSQLOutput implements SQLOutput {
		final DeclaratorBase declarator;

		private ArrayList<Exception> list;

		RaisableSQLOutput(DeclaratorBase declarator) {
			this.declarator = declarator;
		}

		public void raise(SQLParseException ex) {
			if (this.list == null) {
				this.list = new ArrayList<Exception>();
			}
			this.list.add(ex);
		}

		public void tryRaise() {
			if (this.list != null) {
				String msg;
				if (this.declarator != null) {
					msg = "��������[" + this.declarator.getClass().getName() + "]\r\n����DNA-SQL�﷨����";
				} else {
					msg = "����DNA-SQL�﷨����";
				}
				throw new SQLCombinedException(msg, this.list.toArray(new Exception[this.list.size()]));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static MetaElement parseForDeclarator(DeclaratorBase declarator) {
		Class declaratorClass = declarator.getClass();
		final DNASqlType dt = DNASqlType.declareScriptSupportedTypeOfDeclaratorClass(declaratorClass);
		final ContextImpl<?, ?, ?> context = DeclaratorBase.newInstanceByCore;
		if (context == null) {
			throw new UnsupportedOperationException("�����������ɿ�ܹ���");
		}
		DeclaratorBase.newInstanceByCore = null;
		final String className = declaratorClass.getName();
		final Reader reader = context.occorAt.openDeclareScriptReader(className.substring(className.lastIndexOf('.') + 1, className.length()), dt);
		return (MetaElement) parseDefine(reader, context, dt.declareBaseClass, declarator);
	}

	/**
	 * ����DNA-SQL����Statement�������׳��쳣
	 * 
	 * @param dnaSql
	 * @param oQuerier
	 * @param defineClass
	 *            �޶����ͣ�Ϊ������������
	 * @return
	 */
	public static <TDefine> TDefine parseDefine(Reader dnaSql,
			ContextImpl<?, ?, ?> context, Class<TDefine> defineClass) {
		return parseDefine(dnaSql, context, defineClass, null);
	}

	@SuppressWarnings("unchecked")
	public static <TDefine> TDefine parseDefine(Reader dnaSql,
			ContextImpl<?, ?, ?> context, Class<TDefine> defineClass,
			DeclaratorBase declarator) {
		if (dnaSql == null) {
			throw new NullArgumentException("dnaSql");
		}
		try {
			if (context == null) {
				throw new NullArgumentException("context");
			}
			RaisableSQLOutput out = new RaisableSQLOutput(declarator);
			SQLScript s = new SQLParser().parse(new SQLLexer(dnaSql), out, null, context);
			out.tryRaise();
			Object define = s.prepare(context, declarator);
			out.tryRaise();
			if (define == null) {
				throw new IllegalStateException();
			}
			if (defineClass != null && !defineClass.isInstance(define)) {
				throw new IllegalArgumentException("DNA-SQL �������������Ҫ�󲻷���[" + defineClass.getName() + "]����");
			}
			return (TDefine) define;
		} finally {
			try {
				dnaSql.close();
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	/**
	 * ����SQL����NStatement�ṹ�����ڴ�������
	 * 
	 * @param <TStatement>
	 * @param dnaSql
	 * @param statementClass
	 * @param holder
	 * @return
	 */
	public static <TStatement extends NStatement> TStatement parseNStatement(
			Reader dnaSql, Class<TStatement> statementClass,
			DefineHolderImpl holder) {
		if (dnaSql == null) {
			throw new NullArgumentException("dnaSql");
		}
		try {
			RaisableSQLOutput out = new RaisableSQLOutput(null);
			SQLScript s = new SQLParser().parse(new SQLLexer(dnaSql), out, holder, null);
			out.tryRaise();
			return s.content(statementClass);
		} finally {
			try {
				dnaSql.close();
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}
}