package com.jiuqi.dna.core.da;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.Utils;

/**
 * 数据库产品
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
	 * 检测数据库类型
	 * 
	 * @param dbmd
	 * @return
	 */
	public static final DbProduct detect(DatabaseMetaData dbmd) {
		if (dbmd == null) {
			throw new NullArgumentException("JDBC数据库元数据");
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
	 * 数据库产品类型的回调方法
	 * 
	 * @param callee
	 *            回调器
	 * @param data
	 *            数据
	 * @return
	 */
	public abstract <TData, TResult> TResult callback(
			Callee<TData, TResult> callee, TData data);

	/**
	 * 数据库产品类型的回调实现
	 * 
	 * @author houchunlei
	 * 
	 * @param <TData>
	 * @param <TResult>
	 */
	public static abstract class Callee<TData, TResult> {

		/**
		 * 产品为Oracle时回调该方法
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onOracle(TData data) throws Throwable {
			return null;
		}

		/**
		 * 产品为DB2时回调该方法
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onDB2(TData data) throws Throwable {
			return null;
		}

		/**
		 * 产品为MySQL时回调该方法
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onMySQL(TData data) throws Throwable {
			return null;
		}

		/**
		 * 产品为SQLServer时回调该方法
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onSQLServer(TData data) throws Throwable {
			return null;
		}

		/**
		 * 产品为Postgre时回调该方法
		 * 
		 * @param dataChannelServerUtil
		 * @return
		 * @throws Throwable
		 */
		public TResult onPostgre(TData data) throws Throwable {
			return null;
		}

		/**
		 * 产品为Hana时回调该方法
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onHana(TData data) throws Throwable {
			return null;
		}

		/**
		 * 产品为达梦时回调该方法
		 * 
		 * @param data
		 * @return
		 * @throws Throwable
		 */
		public TResult onDameng(TData data) throws Throwable {
			return null;
		}

		/**
		 * 产品未知时回调该方法
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