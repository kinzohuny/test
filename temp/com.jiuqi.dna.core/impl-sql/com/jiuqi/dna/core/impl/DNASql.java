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
 * DNA-SQL功能外观类
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
					msg = "在声明器[" + this.declarator.getClass().getName() + "]\r\n出现DNA-SQL语法错误：";
				} else {
					msg = "出现DNA-SQL语法错误：";
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
			throw new UnsupportedOperationException("声明器必须由框架构造");
		}
		DeclaratorBase.newInstanceByCore = null;
		final String className = declaratorClass.getName();
		final Reader reader = context.occorAt.openDeclareScriptReader(className.substring(className.lastIndexOf('.') + 1, className.length()), dt);
		return (MetaElement) parseDefine(reader, context, dt.declareBaseClass, declarator);
	}

	/**
	 * 解析DNA-SQL生成Statement出错则抛出异常
	 * 
	 * @param dnaSql
	 * @param oQuerier
	 * @param defineClass
	 *            限定类型，为空则不限制类型
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
				throw new IllegalArgumentException("DNA-SQL 所定义的类型与要求不符：[" + defineClass.getName() + "]类型");
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
	 * 分析SQL生成NStatement结构，用于代码生成
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