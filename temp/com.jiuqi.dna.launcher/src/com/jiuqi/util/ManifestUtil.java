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
 * MANIFEST工具类
 * @author 黄凯斌
 */
public class ManifestUtil {

	/**
	 * MANIFEST.MF文件路径
	 */
	public static final String MANIFEST_FILE_PATH = "/META-INF/MANIFEST.MF";

	/**
	 * 指定eclipse工程或jar包的路径，获取Manifest信息
	 * @param path eclipse工程或jar包的路径
	 * @return Manifest对象
	 */
	public static Manifest getManifest(String path) {
		return getManifest(new File(path));
	}

	/**
	 * 指定eclipse工程或jar包的File对象，获取Manifest信息
	 * @param file eclipse工程或jar包的File对象
	 * @return Manifest对象
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
	 * 指定工程目录，从eclipse工程中读取Manifest信息
	 * @param path eclipse工程路径
	 * @return Manifest信息。如果获取失败，则返回<code>null</code>。
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
	 * 指定工程目录的file，从eclipse工程中读取Manifest信息
	 * @param file eclipse工程目录file
	 * @return Manifest信息。如果获取失败，则返回<code>null</code>。
	 */
	public static Manifest getManifestFromDir(File file) {
		return getManifestFromDir(file.getAbsolutePath());
	}

	/**
	 * 指定jar文件的路径，从jar文件中读取Manifest信息
	 * @param path jar文件的路径
	 * @return Manifest信息。如果获取失败，则返回<code>null</code>。
	 */
	public static Manifest getManifestFromJar(String path) {
		return getManifestFromJar(new File(path));
	}

	/**
	 * 指定jar文件，从jar文件中读取Manifest信息
	 * @param file jar文件
	 * @return Manifest信息。如果获取失败，则返回<code>null</code>。
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
	 * 指定文件夹，以该文件夹为根目录保存Manifest文件。
	 * Manifest文件文件路径为“/META-INF/MANIFEST.MF”。
	 * @param path 指定的文件夹
	 * @param manifest Manifest对象
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
	 * 获取指定的属性的原始值
	 * @param attributes 属性类
	 * @param attributeName 属性名
	 * @return 未经处理的属性的原始值
	 */
	public static String getAttributeRawValue(Attributes attributes,
			String attributeName) {
		return attributes.getValue(attributeName);
	}

	/**
	 * 获取指定的属性值
	 * @param attributes 属性类
	 * @param attributeName 属性名
	 * @return 属性值
	 */
	public static String getAttributeValue(Attributes attributes,
			String attributeName) {
		String[] values = getAttributeValues(attributes, attributeName);
		return values.length > 0 ? values[0] : null;
	}

	/**
	 * 指定属性名，获取属性值列表
	 * @param attributes 属性类
	 * @param attributeName 属性名
	 * @return 属性值列表
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
