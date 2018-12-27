package com.ultrapower.android.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class DataUtils {
	static final int BUFFER = 8192;

	private File zipFile;

	private String pathName;

	public DataUtils(String pathName) {
		this.pathName = pathName;
		zipFile = new File(pathName);
	}

	public static byte[] ungzip(byte[] data) throws IOException {
		InputStream is;
		is = new GZIPInputStream(new ByteArrayInputStream(data));
		return InputStream2Bytes(is);
	}
	
	public static byte[] gzip(byte[] data) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(bos);
		gos.write(data);
		gos.finish();
		bos.flush();
		byte[] dataArray = bos.toByteArray();
		gos.close();
		gos.close();
		return dataArray;
	}

	public static byte[] InputStream2Bytes(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte dataBuffer[] = new byte[BUFFER];
		int count;
		while ((count = is.read(dataBuffer, 0, BUFFER)) != -1) {
			bos.write(dataBuffer, 0, count);
		}
		bos.flush();
		byte[] dataArray = bos.toByteArray();
		bos.close();
		return dataArray;
	}
	

	/**
	 * 解压缩文件
	 * 
	 * @param srcPathName
	 * @return 解压缩后的文件名
	 */
	@SuppressWarnings("rawtypes")
	public List<String> unCompress(String path) {
		List<String> filePath = new ArrayList<String>();
		File file = new File(pathName);
		if (!file.exists()) {
			throw new RuntimeException(pathName + "不存在！");
		}

		// 创建zip文件对象
		try {
			ZipFile zipFile = new ZipFile(file, "GBK");
			// 创建本zip文件解压目录
			File unzipFile = new File(path);
			if (!unzipFile.exists()) {
				unzipFile.mkdirs();
			}
			// 得到zip文件条目枚举对象
			Enumeration zipEnum = zipFile.getEntries();
			// 定义输入输出流对象
			InputStream input = null;
			OutputStream output = null;
			// 定义对象
			ZipEntry entry = null;
			// 循环读取条目
			while (zipEnum.hasMoreElements()) {
				// 得到当前条目
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
				// 关闭流

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
			throw new RuntimeException(srcPathName + "不存在！");
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
		/* 判断是目录还是文件 */
		if (file.isDirectory()) {
			System.out.println("压缩：" + basedir + file.getName());
			this.compressDirectory(file, out, basedir);
		} else {
			System.out.println("压缩：" + basedir + file.getName());
			this.compressFile(file, out, basedir);
		}
	}

	/** 压缩一个目录 */
	private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
		if (!dir.exists())
			return;

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			/* 递归 */
			compress(files[i], out, basedir + dir.getName() + "/");
		}
	}

	/** 压缩一个文件 */
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
	public static byte[] decry_RC4_byte_byte(byte[] data, String key) {
		if (data == null || key == null) {
			return null;
		}
		return decry_RC4(new String(data),key).getBytes();
	}
	public static String decry_RC4(byte[] data, String key) {
		if (data == null || key == null) {
			return null;
		}
		return asString(RC4Base(data, key));
	}

	public static String decry_RC4(String data, String key) {
		if (data == null || key == null) {
			return null;
		}
		return new String(RC4Base(HexString2Bytes(data), key));
	}

	public static byte[] encry_RC4_byte(String data, String key) {
		if (data == null || key == null) {
			return null;
		}
		byte b_data[] = data.getBytes();
		return RC4Base(b_data, key);
	}

	public static byte[] encry_RC4_byte_byte(byte[] data, String key) {
		if (data == null || key == null) {
			return null;
		}
		return toHexString(asString(encry_RC4_byte(new String(data), key))).getBytes();
	}
	public static String encry_RC4_string(String data, String key) {
		if (data == null || key == null) {
			return null;
		}
		return toHexString(asString(encry_RC4_byte(data, key)));
	}

	private static String asString(byte[] buf) {
		StringBuffer strbuf = new StringBuffer(buf.length);
		for (int i = 0; i < buf.length; i++) {
			strbuf.append((char) buf[i]);
		}
		return strbuf.toString();
	}

	private static byte[] initKey(String aKey) {
		byte[] b_key = aKey.getBytes();
		byte state[] = new byte[256];

		for (int i = 0; i < 256; i++) {
			state[i] = (byte) i;
		}
		int index1 = 0;
		int index2 = 0;
		if (b_key == null || b_key.length == 0) {
			return null;
		}
		for (int i = 0; i < 256; i++) {
			index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
			byte tmp = state[i];
			state[i] = state[index2];
			state[index2] = tmp;
			index1 = (index1 + 1) % b_key.length;
		}
		return state;
	}

	private static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch & 0xFF);
			if (s4.length() == 1) {
				s4 = '0' + s4;
			}
			str = str + s4;
		}
		return str;// 0x表示十六进制
	}

	private static byte[] HexString2Bytes(String src) {
		int size = src.length();
		byte[] ret = new byte[size / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < size / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	private static byte uniteBytes(byte src0, byte src1) {
		char _b0 = (char) Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (char) (_b0 << 4);
		char _b1 = (char) Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	public static byte[] RC4Base(byte[] input, String mKkey) {
		int x = 0;
		int y = 0;
		byte key[] = initKey(mKkey);
		int xorIndex;
		byte[] result = new byte[input.length];

		for (int i = 0; i < input.length; i++) {
			x = (x + 1) & 0xff;
			y = ((key[x] & 0xff) + y) & 0xff;
			byte tmp = key[x];
			key[x] = key[y];
			key[y] = tmp;
			xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
			result[i] = (byte) (input[i] ^ key[xorIndex]);
		}
		return result;
	}
	

	public static void main(String[] args) {
		String test = "做个好人";
		System.out.println(test);
		try {
			System.out.println(Arrays.toString(test.getBytes()));
			byte[] b1 = DataUtils.gzip(test.getBytes());
			System.out.println("gzip"+Arrays.toString(b1));
			byte[] b11= DataUtils.ungzip(b1);
			System.out.println("ungzip"+Arrays.toString(b11));
			byte[] b2= DataUtils.RC4Base(b1, "abcd1234");
			System.out.println("gzip->encry"+Arrays.toString(b2));
			byte[] b3= DataUtils.RC4Base(b2, "abcd1234");
			System.out.println("decry->gzip"+Arrays.toString(b3));
			byte[] b4= DataUtils.ungzip(b3);
			System.out.println("ungzip"+Arrays.toString(b4));
			String result = new String(b4);
			System.out.println(result);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		DataUtils utils = new DataUtils("E:\\adtpapp\\webapp\\atu\\unzip\\workparam\\root-联通WCDMA-20140525.zip");
		// utils.compress("E:\\adtpapp\\webapp\\atu\\zip\\workparam\\root-联通WCDMA-20140525.xml");
//		utils.unCompress("E:\\adtpapp\\webapp\\atu\\unzip\\workparam");
	}
}