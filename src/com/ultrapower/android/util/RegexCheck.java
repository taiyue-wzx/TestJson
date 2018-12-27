package com.ultrapower.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则验证
 * 
 * @author CXF
 * 
 */
public class RegexCheck {

	public static boolean startCheck(String reg, String string) {
		boolean tem = false;
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(string == null ? "" : string);
		tem = matcher.matches();
		return tem;
	}

	/**
	 * 数字 返回true 数字以外，null，""返回false
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNumber(String str) {
		if (str != null && !str.equals("")) {
			String reg = "^[0-9]*$";
			return startCheck(reg, str);
		}
		return false;
	}

	/**
	 * 负整数 返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNegativeNumber(String str) {
		String reg = "^-[0-9]*[1-9][0-9]*$";
		return startCheck(reg, str);
	}

	/**
	 * 负小数 返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNegativeFloat(String str) {
		String reg = "^-[0-9]+\\.{0,1}[0-9]{0,9}$";
		return startCheck(reg, str);
	}

	/**
	 * 0或正整数或者正小数(最多9位小数) 返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkIntOrDecimal(String str) {
		String reg = "^[0-9]+\\.{0,1}[0-9]{0,9}$";
		return startCheck(reg, str);
	}

	public static void main(String[] args) {
		System.out.println(checkIntOrDecimal(null));
	}
}
