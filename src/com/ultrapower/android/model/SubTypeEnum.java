package com.ultrapower.android.model;

public enum SubTypeEnum {
	
	//upload,subType
	/*
	 * 预警管理
	 */
	// 关注群列表更新
	update_follow_list_category,
	// 关注城市列表更新
	update_follow_list_city,
	// 关注集客列表更新
	update_follow_list_jike,
	
	//download,subtype
	/*
	 * 预警管理
	 */
	// 关注群列表下载
	get_follow_list_category,
	// 关注群预警下载
	get_warning_list_category,
	// 关注城市列表下载
	get_follow_list_city,
	// 关注城市列表下载
	get_warning_list_city,
	// 关注集客列表下载
	get_follow_list_jike,
	// 关注集客列表下载
	get_warning_list_jike;
	
	public static SubTypeEnum get(String enumStr) {
		return valueOf(enumStr);
	}
}
