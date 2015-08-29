package com.jiuqi.dna.core.internal.db.datasource;

import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.jiuqi.dna.core.impl.BundleStub;
import com.jiuqi.dna.core.impl.ResolveHelper;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.misc.SXElement;

public final class JdbcDriverManager {

	private JdbcDriverManager() {
	}

	private JdbcDriverProvider providers;

	private static final String DEFAULT_DRIVER_INSTANCE_FIELD = "driver";

	public final void register(BundleStub bundle, SXElement element) {
		try {
			String className = element.getString(JdbcDriverProvider.xml_attr_class);
			final Class<?> clz;
			try {
				clz = bundle.loadClass(className, null);
			} catch (Throwable e) {
				ResolveHelper.logStartInfo("无法装载声明的JDBC驱动[" + className + "]：" + e.getMessage());
				return;
			}
			if (clz == null) {
				throw new IllegalArgumentException();
			}
			final String fieldName = element.getAttribute(JdbcDriverProvider.xml_attr_field, DEFAULT_DRIVER_INSTANCE_FIELD);
			final Field field;
			try {
				field = clz.getDeclaredField(fieldName);
			} catch (Throwable th) {
				ResolveHelper.logStartInfo("注册JDBC驱动错误：" + clz.getName() + "，未提供名为[" + fieldName + "]的静态成员变量。");
				return;
			}
			final Object obj = field.get(null);
			if (obj != null) {
				if (obj instanceof Driver) {
					final Driver driver = (Driver) obj;
					if (this.register((Driver) obj)) {
						ResolveHelper.logStartInfo("注册JDBC驱动[" + clz.getSimpleName() + "]，驱动版本[" + driver.getMajorVersion() + "." + driver.getMinorVersion() + "]");
					}
				} else {
					ResolveHelper.logStartInfo("注册JDBC驱动错误：" + clz.getName() + "，静态成员变量[" + fieldName + "]不是有效的JDBC驱动实例。");
					return;
				}
			}
		} catch (Throwable th) {
			bundle.application.catcher.catchException(th, bundle);
		}
	}

	private final boolean register(Driver driver) {
		final JdbcDriverProvider p = new JdbcDriverProvider(driver);
		if (this.providers == null) {
			this.providers = p;
			return true;
		} else if (versionGreater(driver, this.providers.driver)) {
			p.next = this.providers;
			this.providers = p;
			return true;
		} else {
			JdbcDriverProvider another = this.providers;
			for (; another.next != null; another = another.next) {
				if (versionGreater(driver, another.next.driver)) {
					p.next = another.next;
					another.next = p;
					return true;
				}
			}
			another.next = p;
			return true;
		}
	}

	private static final boolean versionGreater(Driver left, Driver right) {
		return left.getMajorVersion() > right.getMajorVersion() || left.getMajorVersion() == right.getMajorVersion() && left.getMinorVersion() > right.getMinorVersion();
	}

	public final JdbcDriverDataSource tryJdbcDataSource(String url, String usr,
			String pwd) {
		for (JdbcDriverProvider p = this.providers; p != null; p = p.next) {
			try {
				if (p.driver.acceptsURL(url)) {
					return new JdbcDriverDataSource(p, url, usr, pwd);
				}
			} catch (SQLException e) {
				continue;
			}
		}
		return null;
	}

	public static final JdbcDriverManager INSTANCE = new JdbcDriverManager();

	public final Driver find(String url) {
		for (JdbcDriverProvider p = this.providers; p != null; p = p.next) {
			try {
				if (p.driver.acceptsURL(url)) {
					return p.driver;
				}
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
		return null;
	}

	public final Iterable<Driver> itr() {
		return new Iterable<Driver>() {
			public Iterator<Driver> iterator() {
				return new Itr();
			}
		};
	}

	private final class Itr implements Iterator<Driver> {

		private JdbcDriverProvider p = JdbcDriverManager.this.providers;

		public final boolean hasNext() {
			return this.p != null;
		}

		public final Driver next() {
			if (this.p == null) {
				throw new NoSuchElementException();
			}
			JdbcDriverProvider current = this.p;
			this.p = current.next;
			return current.driver;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}