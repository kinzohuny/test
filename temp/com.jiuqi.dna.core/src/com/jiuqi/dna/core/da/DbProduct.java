package com.jiuqi.dna.core.da;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.Utils;

/**
 * ���ݿ��Ʒ
 * 
 * @author houchunlei
 * 
 */
public enum DbProduct {

	Oracle() {

		@Override
		public final <TData, TResult> TResult callback(
				Callee<TData, TResult> callee, TData data) {
			try {
				return callee.onOracle(data);
			} catch (Throwable th) {
				throw Utils.tryThrowException(th);
			}
		}

		@Override
		final boolean match(String name) {
			return name.indexOf("oracle") >= 0;
		}

		@Override
		public final void quote(Appendable obj, String id) {
			try {
				obj.append('"').append(id).append('"');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	},

	DB2() {

		@Override
		public final <TData, TResult> TResult callback(
				Callee<TData, TResult> callee, TData data) {
			try {
				return callee.onDB2(data);
			} catch (Throwable th) {
				throw Utils.tryThrowException(th);
			}
		}

		@Override
		final boolean match(String name) {
			return name.indexOf("db2") >= 0;
		}

		@Override
		public final void quote(Appendable obj, String id) {
			try {
				obj.append('"').append(id).append('"');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	},

	MySQL() {

		@Override
		public final <TData, TResult> TResult callback(
				Callee<TData, TResult> callee, TData data) {
			try {
				return callee.onMySQL(data);
			} catch (Throwable th) {
				throw Utils.tryThrowException(th);
			}
		}

		@Override
		final boolean match(String name) {
			return name.indexOf("mysql") >= 0;
		}

		@Override
		public final void quote(Appendable obj, String id) {
			try {
				obj.append('`').append(id).append('`');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	},

	SQLServer() {

		@Override
		public final <TData, TResult> TResult callback(
				Callee<TData, TResult> callee, TData data) {
			try {
				return callee.onSQLServer(data);
			} catch (Throwable th) {
				throw Utils.tryThrowException(th);
			}
		}

		@Override
		final boolean match(String name) {
			return name.indexOf("sql server") >= 0 || name.indexOf("sqlserver") >= 0;
		}

		@Override
		public final void quote(Appendable obj, String id) {
			try {
				obj.append('[').append(id).append(']');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	},

	Postgre() {

		@Override
		public final <TData, TResult> TResult callback(
				Callee<TData, TResult> callee, TData data) {
			try {
				return callee.onPostgre(data);
			} catch (Throwable th) {
				throw Utils.tryThrowException(th);
			}
		}

		@Override
		final boolean match(String name) {
			return name.indexOf("postgre") >= 0;
		}

		@Override
		public final void quote(Appendable obj, String id) {
			try {
				obj.append('"').append(id).append('"');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	},

	Hana {

		@Override
		boolean match(String name) {
			return name.toLowerCase().indexOf("hdb") >= 0;
		}

		@Override
		public <TData, TResult> TResult callback(Callee<TData, TResult> callee,
				TData data) {
			try {
				return callee.onHana(data);
			} catch (Throwable th) {
				throw Utils.tryThrowException(th);
			}
		}

		@Override
		public final void quote(Appendable obj, String id) {
			try {
				obj.append('"').append(id).append('"');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	},

	Dameng() {

		@Override
		final boolean match(String name) {
			return name.toLowerCase().indexOf("dm dbms") >= 0;
		}

		@Override
		public final <TData, TResult> TResult callback(
				Callee<TData, TResult> callee, TData data) {
			try {
				return callee.onDameng(data);
			} catch (Throwable th) {
				throw Utils.tryThrowException(th);
			}
		}

		@Override
		public final void quote(Appendable obj, String id) {
			try {
				obj.append('"').append(id).append('"');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	},

	Unknown() {

		@Override
		public final <TData, TResult> TResult callback(
				Callee<TData, TResult> callee, TData data) {
			try {
				return callee.onUnknown(data);
			} catch (Throwable th) {
				throw Utils.tryThrowException(th);
			}
		}

		@Override
		final boolean match(String name) {
			return false;
		}

		@Override
		public final void quote(Appendable obj, String id) {
			throw new UnsupportedOperationException();
		}
	}

	;

	/**
	 * ������ݿ�����
	 * 
	 * @param dbmd
	 * @return
	 */
	public static final DbProduct detect(DatabaseMetaData dbmd) {
		if (dbmd == null) {
			throw new NullArgumentException("JDBC���ݿ�Ԫ����");
		}
		final String name;
		try {
			name = dbmd.getDatabaseProductName().toLowerCase();
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		for (DbProduct product : DbProduct.values()) {
			if (product.match(name)) {
				return product;
			}
		}
		return Unknown;
	}

	abstract boolean match(String name);

	/**
	 * ���ݿ��Ʒ���͵Ļص�����
	 * 
	 * @param callee
	 *            �ص���
	 * @param data
	 *            ����
	 * @return
	 */
	public abstract <TData, TResult> TResult callback(
			Callee<TData, TResult> callee, TData data);

	/**
	 * ���ݿ��Ʒ���͵Ļص�ʵ��
	 * 
	 * @author houchunlei
	 * 
	 * @param <TData>
	 * @param <TResult>
	 */
	public static abstract class Callee<TData, TResult> {

		/**
		 * ��ƷΪOracleʱ�ص��÷���
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onOracle(TData data) throws Throwable {
			return null;
		}

		/**
		 * ��ƷΪDB2ʱ�ص��÷���
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onDB2(TData data) throws Throwable {
			return null;
		}

		/**
		 * ��ƷΪMySQLʱ�ص��÷���
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onMySQL(TData data) throws Throwable {
			return null;
		}

		/**
		 * ��ƷΪSQLServerʱ�ص��÷���
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onSQLServer(TData data) throws Throwable {
			return null;
		}

		/**
		 * ��ƷΪPostgreʱ�ص��÷���
		 * 
		 * @param dataChannelServerUtil
		 * @return
		 * @throws Throwable
		 */
		public TResult onPostgre(TData data) throws Throwable {
			return null;
		}

		/**
		 * ��ƷΪHanaʱ�ص��÷���
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onHana(TData data) throws Throwable {
			return null;
		}

		/**
		 * ��ƷΪ����ʱ�ص��÷���
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onDameng(TData data) throws Throwable {
			return null;
		}

		/**
		 * ��Ʒδ֪ʱ�ص��÷���
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onUnknown(TData data) throws Throwable {
			return null;
		}
	}

	public abstract void quote(Appendable obj, String id);
}