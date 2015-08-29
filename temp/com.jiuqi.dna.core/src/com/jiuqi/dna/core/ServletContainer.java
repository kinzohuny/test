package com.jiuqi.dna.core;

public interface ServletContainer {
	void addServlet(Class<?> clazz, String path);

	void addServlet(Class<?> clazz, String path, int initOrder);
}
