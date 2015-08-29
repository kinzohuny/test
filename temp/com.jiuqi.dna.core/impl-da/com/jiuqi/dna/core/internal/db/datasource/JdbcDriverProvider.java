package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.Driver;
import java.util.Properties;

/**
 * @author Hcl
 * 
 */
public final class JdbcDriverProvider {

	final Driver driver;
	final Properties props;
	final int priority;

	JdbcDriverProvider(Driver driver) {
		this.driver = driver;
		this.props = new Properties();
		this.priority = 0;
	}

	JdbcDriverProvider next;

	public static final String xml_element_jdbc_drivers = "jdbc-drivers";
	public static final String xml_element_jdbc_driver = "jdbc-driver";
	public static final String xml_attr_class = "class";
	public static final String xml_attr_field = "field";
	public static final String xml_attr_priority = "priority";
	public static final String xml_element_property = "property";
}