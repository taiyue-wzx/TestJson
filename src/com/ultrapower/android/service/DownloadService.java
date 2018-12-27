package com.ultrapower.android.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ultrapower.android.dao.YjDao;
import com.ultrapower.android.util.json.JSONArray;
import com.ultrapower.android.util.json.JSONObject;

public class DownloadService {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	public String sbuscribeList(JSONObject requestJson) {
		JSONObject yjGroupJson = new JSONObject();
		JSONArray yjGroupArray = new JSONArray();
		//查询订阅的地市、群、集客点
		int sbuscribeType = requestJson.getInt("sbuscribe_type");
		List<Map<String, Object>> yjSubscribeList = null;
		if(sbuscribeType == 1){
			yjSubscribeList = YjDao.getYjInstance().getYjSubscribeList(requestJson.getString("phone_number"),sbuscribeType);
		}else{
			yjSubscribeList = YjDao.getYjInstance().getYjSubscribeListss(requestJson.getString("phone_number"),sbuscribeType);
		}
		yjGroupJson.put("sbuscribes", yjGroupArray);
		if(yjSubscribeList != null){
			for (Map map : yjSubscribeList) {
				JSONObject yjGroup = new JSONObject();
				if(sbuscribeType == 1){
					yjGroup.put("follow", map.get("STATE"));
					yjGroup.put("sbuscribe_id", map.get("GROUP_GEO_ID"));
					yjGroup.put("sbuscribe_name", map.get("GROUP_NAME"));
				}else{
					yjGroup.put("sbuscribe_id", map.get("YJ_ID"));
					yjGroup.put("sbuscribe_name", map.get("YJ_NAME"));
				}
				
				if(sbuscribeType == 2){
					JSONArray yjGroupCateArray = new JSONArray();
					yjGroup.put("group_list", yjGroupCateArray);
					List<Map<String, Object>> yjGroupCateList = YjDao.getYjInstance().getYjGroupCateList(Integer.parseInt(map.get("YJ_ID")+""));
					if(yjGroupCateList != null){
						for (Map maps : yjGroupCateList) {
							JSONObject yjGroupCate = new JSONObject();
							yjGroupCate.put("group_id", maps.get("GROUP_ID"));
							yjGroupCate.put("group_name", maps.get("GROUP_NAME"));
							yjGroupCateArray.put(yjGroupCate);
						}
					}
				}if(sbuscribeType == 1){
					yjGroup.put("follow", map.get("STATE"));
				}
				yjGroupArray.put(yjGroup);
			}
		}
		return yjGroupJson.toString();
	}
	
	public String warnGroupList(JSONObject requestJson) {
		//预警查询
		JSONObject yjWarnGroupJson = new JSONObject();
		JSONArray yjWarnGroupArray = new JSONArray();
		yjWarnGroupJson.put("warnings", yjWarnGroupArray);
		//查询订阅的地市、群、集客点
		List<Map<String, Object>> yjSubscribeList = YjDao.getYjInstance().getYjSubscribeLists(requestJson.getString("phone_number"));
		if(yjSubscribeList != null){
			for (Map mapSubscribe : yjSubscribeList) {
				//去订阅表里面查询是否今天已发送
				Map<String, Object> yjSubscribe = new HashMap<String, Object>();
				yjSubscribe.put("PHONE_NUMBER", requestJson.getString("phone_number"));
				yjSubscribe.put("YJ_ID", mapSubscribe.get("YJ_ID")+"");
				yjSubscribe.put("SEND_DATE", format.format(new Date()));
				//指标名称处理
				String[] indexName = {"FTP_UPLOAD","FTP_DOWNLOAD","HTTP","VIDEO","PING"};
				for(int i = 0; i < indexName.length; i++){
					yjSubscribe.put("INDEX_NAME", indexName[i]);
					Map<String, Object> yjSendStates = YjDao.getYjInstance().getYjSendStates(yjSubscribe);
					if(yjSendStates != null){
						//已发送(num加1)
						int messageNum =  Integer.parseInt(yjSendStates.get("MESSAGE_NUM")+"")+1;
						YjDao.getYjInstance().updateYJ_SENDSTATE_INFO(mapSubscribe.get("YJ_ID")+"",messageNum,indexName[i]);
					}else{
						//查询统计表
						yjSubscribe.put("STARTTIME", format.format(new Date())+ " 00:00:00");
						yjSubscribe.put("ENDTIME", format.format(new Date())+ " 23:59:59");
						Map<String, Object> yjStatistices = YjDao.getYjInstance().getYjStatistices(yjSubscribe);
						if(yjStatistices != null){
							yjSubscribe.put("TYPE", yjStatistices.get("TYPE")+"");
							//推送给手机端信息
							JSONObject obj = new JSONObject();
							obj.put("warning_type", yjStatistices.get("TYPE")+"");
							obj.put("warning_id", yjSubscribe.get("YJ_ID")+"");
							obj.put("warning_name", yjSubscribe.get("INDEX_NAME")+"");
							obj.put("warnings_info", yjStatistices.get("YJ_DESC")+"");
							yjWarnGroupArray.put(obj);
							
							//未发送(新增数据)
							yjSubscribe.put("MESSAGE_NUM", 1);
							YjDao.getYjInstance().insertYJ_SENDSTATE_INFO(yjSubscribe);
						}
					}
				}
			}
		}
		return yjWarnGroupJson.toString();
	}
}
