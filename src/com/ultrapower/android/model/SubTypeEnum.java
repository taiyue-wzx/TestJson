package com.ultrapower.android.model;

public enum SubTypeEnum {
	
	//upload,subType
	/*
	 * Ԥ������
	 */
	// ��עȺ�б����
	update_follow_list_category,
	// ��ע�����б����
	update_follow_list_city,
	// ��ע�����б����
	update_follow_list_jike,
	
	//download,subtype
	/*
	 * Ԥ������
	 */
	// ��עȺ�б�����
	get_follow_list_category,
	// ��עȺԤ������
	get_warning_list_category,
	// ��ע�����б�����
	get_follow_list_city,
	// ��ע�����б�����
	get_warning_list_city,
	// ��ע�����б�����
	get_follow_list_jike,
	// ��ע�����б�����
	get_warning_list_jike;
	
	public static SubTypeEnum get(String enumStr) {
		return valueOf(enumStr);
	}
}
