package com.ultrapower.android.util;

import java.util.Date;

public class RandomUtils {

	private static String str = "ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz";
	private static String mathstr = "1234567890";

	/**
	 * 产生随机字符串
	 * 
	 * @return
	 */
	public static String createRandomStr() {

		StringBuffer buffer = new StringBuffer();
		Date date = new Date();
		buffer.append(org.apache.tools.ant.util.DateUtils.format(date, "yyMMddHHmmss"));
		for (int i = 0; i < 20; i++) {
			int num = (int) (Math.random() * 50);
			buffer.append(str.substring(num, num + 1));
		}
		return buffer.toString();
	}

	/**
	 * 创建数字字符串
	 * 
	 * @return
	 */
	public static String createDigitalRandomStr() {

		StringBuffer buffer = new StringBuffer();
		Date date = new Date();
		buffer.append(org.apache.tools.ant.util.DateUtils.format(date, "yyMMddHHmmss"));
		for (int i = 0; i < 10; i++) {
			int num = (int) (Math.random() * 10);
			buffer.append(mathstr.substring(num, num + 1));
		}
		return buffer.toString();
	}

	public static void main(String[] args) {
		System.out.println(createRandomStr());
	}

}
