package com.ultrapower.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtils {

	public static byte[] getBytes(String filePath) {
		try {
			File file = new File(filePath);
			FileInputStream stream = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (Exception e) {
		}
		return null;

	}

	public static String createRandomPath(String fileType) {
		String fileName = RandomUtils.createRandomStr();
		String path = FileUtils.class.getClassLoader().getResource("").getPath();
		return path + fileName + "." + fileType;
	}

	public static void deleteFile(String path) {
		File file = new File(path);
		file.delete();
	}

	public static String getFileName(String path) {
		File file = new File(path);
		return file.getName();
	}

	/**
	 * ±£´æÎÄ¼þ
	 * 
	 * @param context
	 * @param filePath
	 */
	public static void saveString2File(String context, String filePath) {
		File filename = new File(filePath);

		RandomAccessFile mm = null;
		try {
			mm = new RandomAccessFile(filename, "rw");
			mm.write(context.getBytes("UTF-8"));

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (mm != null) {
				try {
					mm.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {
		System.out.println(createRandomPath("txt"));
	}

}
