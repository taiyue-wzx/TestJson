package com.ultrapower.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ������֤
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
	 * ���� ����true �������⣬null��""����false
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
	 * ������ ����true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNegativeNumber(String str) {
		String reg = "^-[0-9]*[1-9][0-9]*$";
		return startCheck(reg, str);
	}

	/**
	 * ��С�� ����true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNegativeFloat(String str) {
		String reg = "^-[0-9]+\\.{0,1}[0-9]{0,9}$";
		return startCheck(reg, str);
	}

	/**
	 * 0��������������С��(���9λС��) ����true
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
