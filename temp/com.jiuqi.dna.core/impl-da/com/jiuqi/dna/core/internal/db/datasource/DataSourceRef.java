package com.jiuqi.dna.core.internal.db.datasource;

import com.jiuqi.dna.core.impl.Transaction;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 数据库连接信息
 * 
 * @author gaojingxin
 * 
 */
public final class DataSourceRef {

	// oracle url
	// jdbc:oracle:thin:@hostname:1521:sid

	// sqlserver url
	// jdbc:sqlserver://hostname:1433;databasename=databasename

	// db2 url
	// jdbc:db2://hostname:50000/databasename

	// mysql url
	// jdbc:mysql://hostname:3306/databasename?allowMultiQueries=true

	// dameng url
	// jdbc:dm://host:5236/database

	// hana url

	final String catalog;
	public final ComboDataSource dataSource;

	public final PooledConnection alloc(Transaction transaction) {
		return this.dataSource.alloc();
	}

	public final DbMetadata getDbMetadata() {
		return this.dataSource.getMetadata();
	}

	public final static String xml_element_datasourceref = "datasource-ref";
	public final static String xml_attr_space = "space";
	public final static String xml_attr_catalog = "catalog";
	public final static String xml_attr_datasource_author = "datasource-author";
	public final static String xml_attr_datasource = "datasource";

	public DataSourceRef(DataSourceManager manager, SXElement element) {
		String dataSourceName = element.getAttribute(xml_attr_datasource, null);
		String dataSourceAuthor = dataSourceName == null ? null : element.getAttribute(xml_attr_datasource_author, null);
		this.dataSource = manager.getDataSource(dataSourceAuthor, dataSourceName);
		this.catalog = element.getAttribute(xml_attr_catalog, null);
	}

	public DataSourceRef(ComboDataSource datasource) {
		this.dataSource = datasource;
		this.catalog = null;
	}
}