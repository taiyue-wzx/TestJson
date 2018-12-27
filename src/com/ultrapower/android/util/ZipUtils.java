package com.ultrapower.android.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtils {
	static final int BUFFER = 8192;

	private File zipFile;

	private String pathName;

	public ZipUtils(String pathName) {
		this.pathName = pathName;
		zipFile = new File(pathName);
	}

	/**
	 * ��ѹ���ļ�
	 * 
	 * @param srcPathName
	 * @return ��ѹ������ļ���
	 */
	@SuppressWarnings("rawtypes")
	public List<String> unCompress(String path) {
		List<String> filePath = new ArrayList<String>();
		File file = new File(pathName);
		if (!file.exists()) {
			throw new RuntimeException(pathName + "�����ڣ�");
		}

		// ����zip�ļ�����
		try {
			ZipFile zipFile = new ZipFile(file, "GBK");
			// ������zip�ļ���ѹĿ¼
			File unzipFile = new File(path);
			if (!unzipFile.exists()) {
				unzipFile.mkdirs();
			}
			// �õ�zip�ļ���Ŀö�ٶ���
			Enumeration zipEnum = zipFile.getEntries();
			// �����������������
			InputStream input = null;
			OutputStream output = null;
			// �������
			ZipEntry entry = null;
			// ѭ����ȡ��Ŀ
			while (zipEnum.hasMoreElements()) {
				// �õ���ǰ��Ŀ
				entry = (ZipEntry) zipEnum.nextElement();
				String entryName = new String(entry.getName());
				input = zipFile.getInputStream(entry);
				output = new FileOutputStream(new File(path + "/" + entryName));
				filePath.add(path + "/" + entryName);
				byte[] buffer = new byte[1024 * 8];
				int readLen = 0;
				while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1) {
					output.write(buffer, 0, readLen);
				}
				// �ر���

				input.close();
				output.flush();
				output.close();
				zipFile.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return filePath;
	}

	public static String getSuffixName(String name) {
		return name.substring(0, name.lastIndexOf("."));
	}

	public void compress(String srcPathName) {
		File file = new File(srcPathName);
		if (!file.exists())
			throw new RuntimeException(srcPathName + "�����ڣ�");
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
			ZipOutputStream out = new ZipOutputStream(cos);
			String basedir = "";

			compress(file, out, basedir);
			out.setEncoding("GBK");
			out.closeEntry();
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void compress(File file, ZipOutputStream out, String basedir) {
		/* �ж���Ŀ¼�����ļ� */
		if (file.isDirectory()) {
			System.out.println("ѹ����" + basedir + file.getName());
			this.compressDirectory(file, out, basedir);
		} else {
			System.out.println("ѹ����" + basedir + file.getName());
			this.compressFile(file, out, basedir);
		}
	}

	/** ѹ��һ��Ŀ¼ */
	private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
		if (!dir.exists())
			return;

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			/* �ݹ� */
			compress(files[i], out, basedir + dir.getName() + "/");
		}
	}

	/** ѹ��һ���ļ� */
	private void compressFile(File file, ZipOutputStream out, String basedir) {
		if (!file.exists()) {
			return;
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			ZipEntry entry = new ZipEntry(basedir + file.getName());
			out.putNextEntry(entry);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		ZipUtils utils = new ZipUtils("E:\\adtpapp\\webapp\\atu\\unzip\\workparam\\root-��ͨWCDMA-20140525.zip");
		// utils.compress("E:\\adtpapp\\webapp\\atu\\zip\\workparam\\root-��ͨWCDMA-20140525.xml");
		utils.unCompress("E:\\adtpapp\\webapp\\atu\\unzip\\workparam");
	}
}