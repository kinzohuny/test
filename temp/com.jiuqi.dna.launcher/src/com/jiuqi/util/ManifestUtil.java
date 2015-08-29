package com.jiuqi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.osgi.util.ManifestElement;

/**
 * MANIFEST������
 * @author �ƿ���
 */
public class ManifestUtil {

	/**
	 * MANIFEST.MF�ļ�·��
	 */
	public static final String MANIFEST_FILE_PATH = "/META-INF/MANIFEST.MF";

	/**
	 * ָ��eclipse���̻�jar����·������ȡManifest��Ϣ
	 * @param path eclipse���̻�jar����·��
	 * @return Manifest����
	 */
	public static Manifest getManifest(String path) {
		return getManifest(new File(path));
	}

	/**
	 * ָ��eclipse���̻�jar����File���󣬻�ȡManifest��Ϣ
	 * @param file eclipse���̻�jar����File����
	 * @return Manifest����
	 */
	public static Manifest getManifest(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		if (file.isDirectory()) {
			return getManifestFromDir(file);
		} else if (file.isFile()) {
			return getManifestFromJar(file);
		} else {
			return null;
		}
	}

	/**
	 * ָ������Ŀ¼����eclipse�����ж�ȡManifest��Ϣ
	 * @param path eclipse����·��
	 * @return Manifest��Ϣ�������ȡʧ�ܣ��򷵻�<code>null</code>��
	 */
	public static Manifest getManifestFromDir(String path) {
		Manifest manifest = null;
		try {
			InputStream in = new FileInputStream(path + MANIFEST_FILE_PATH);
			manifest = new Manifest(in);
		} catch (IOException e) {
			manifest = null;
		}
		return manifest;
	}

	/**
	 * ָ������Ŀ¼��file����eclipse�����ж�ȡManifest��Ϣ
	 * @param file eclipse����Ŀ¼file
	 * @return Manifest��Ϣ�������ȡʧ�ܣ��򷵻�<code>null</code>��
	 */
	public static Manifest getManifestFromDir(File file) {
		return getManifestFromDir(file.getAbsolutePath());
	}

	/**
	 * ָ��jar�ļ���·������jar�ļ��ж�ȡManifest��Ϣ
	 * @param path jar�ļ���·��
	 * @return Manifest��Ϣ�������ȡʧ�ܣ��򷵻�<code>null</code>��
	 */
	public static Manifest getManifestFromJar(String path) {
		return getManifestFromJar(new File(path));
	}

	/**
	 * ָ��jar�ļ�����jar�ļ��ж�ȡManifest��Ϣ
	 * @param file jar�ļ�
	 * @return Manifest��Ϣ�������ȡʧ�ܣ��򷵻�<code>null</code>��
	 */
	public static Manifest getManifestFromJar(File file) {
		Manifest manifest = null;
		try {
			JarFile jar = new JarFile(file);
			manifest = jar.getManifest();
		} catch (IOException e) {
			manifest = null;
		}
		return manifest;
	}

	/**
	 * ָ���ļ��У��Ը��ļ���Ϊ��Ŀ¼����Manifest�ļ���
	 * Manifest�ļ��ļ�·��Ϊ��/META-INF/MANIFEST.MF����
	 * @param path ָ�����ļ���
	 * @param manifest Manifest����
	 */
	public static void saveManifest(String path, Manifest manifest) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(path + MANIFEST_FILE_PATH);
			manifest.write(out);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * ��ȡָ�������Ե�ԭʼֵ
	 * @param attributes ������
	 * @param attributeName ������
	 * @return δ����������Ե�ԭʼֵ
	 */
	public static String getAttributeRawValue(Attributes attributes,
			String attributeName) {
		return attributes.getValue(attributeName);
	}

	/**
	 * ��ȡָ��������ֵ
	 * @param attributes ������
	 * @param attributeName ������
	 * @return ����ֵ
	 */
	public static String getAttributeValue(Attributes attributes,
			String attributeName) {
		String[] values = getAttributeValues(attributes, attributeName);
		return values.length > 0 ? values[0] : null;
	}

	/**
	 * ָ������������ȡ����ֵ�б�
	 * @param attributes ������
	 * @param attributeName ������
	 * @return ����ֵ�б�
	 */
	public static String[] getAttributeValues(Attributes attributes,
			String attributeName) {
		String rawValue = getAttributeRawValue(attributes, attributeName);
		if (rawValue == null) {
			return new String[0];
		}
		String[] values = null;
		try {
			ManifestElement[] elements = ManifestElement.parseHeader(
					attributeName, rawValue);
			values = new String[elements.length];
			for (int i = 0; i < elements.length; ++i) {
				values[i] = elements[i].getValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (values == null) {
			values = new String[0];
		}
		return values;
	}

}
