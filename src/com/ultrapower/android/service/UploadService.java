package com.ultrapower.android.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ultrapower.android.dao.YjDao;
import com.ultrapower.android.util.json.JSONArray;
import com.ultrapower.android.util.json.JSONObject;

public class UploadService {
	
	//预警更新
	public String[] sbuscribeUpdate(JSONObject requestJson){
		//处理结果
		String [] results = new String [2];
		JSONObject jo = new JSONObject();
		boolean isOk = false;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> dataMaps = new HashMap<String, Object>();
		Map<String, Object> dataMapss = new HashMap<String, Object>();
		dataMap.put("STATE", requestJson.getInt("update_type"));
		dataMap.put("PHONE_NUMBER", requestJson.getString("phone_number"));
		dataMap.put("UPDATE_DATE",new Date());
		int state = requestJson.getInt("update_type");
		int id = 0;
		int sbuscribeType = requestJson.getInt("sbuscribe_type");
		switch(state){
			case 1:
				//添加
				Map<String, Object> subscribeMap = new HashMap<String, Object>();
				id = YjDao.getYjInstance().selectYJ_CATEGORY_ID();
				if(sbuscribeType == 2){
					dataMap.put("NAME", requestJson.getString("sbuscribe_name"));
					dataMap.put("CREATE_DATE",new Date());
					if(id == 0){
						id = 1;
					}else{
						id += 1;
					}
					dataMap.put("ID",id);
					isOk = YjDao.getYjInstance().insertYJ_CATEGORY_INFO(dataMap);
					JSONArray maps = requestJson.getJSONArray("group_list");
					for(int i = 0; i < maps.length(); i++){
						JSONObject sbuscribeInfo = maps.getJSONObject(i);
						dataMaps.put("FLOCK_ID", id);
						dataMaps.put("GROUP_ID", sbuscribeInfo.get("group_id"));
						dataMaps.put("GROUP_NAME", sbuscribeInfo.get("group_name"));
						isOk = YjDao.getYjInstance().insertYJ_GROUP_CATEGORY_INFO(dataMaps);
					}
					subscribeMap.put("ID", id);
					subscribeMap.put("TYPE", sbuscribeType);
					subscribeMap.put("PHONE_NUMBER", requestJson.getString("phone_number"));
					subscribeMap.put("YJ_ID", id);
					subscribeMap.put("YJ_NAME", requestJson.getString("sbuscribe_name"));
					subscribeMap.put("STATE", 1);
					isOk = YjDao.getYjInstance().insertYJ_SUBSCRIBE_INFO(subscribeMap);
				}else if(sbuscribeType == 1){
					JSONArray mapss = requestJson.getJSONArray("sbuscribes");
					for(int i = 0; i < mapss.length(); i++){
						JSONObject sbuscribeInfo = mapss.getJSONObject(i);
						if(id == 0){
							id = 1;
						}else{
							id += 1;
						}
						subscribeMap.put("ID", id);
						subscribeMap.put("TYPE", sbuscribeType);
						subscribeMap.put("PHONE_NUMBER", requestJson.getString("phone_number"));
						subscribeMap.put("YJ_ID", sbuscribeInfo.getString("sbuscribe_id")+"");
						subscribeMap.put("YJ_NAME", sbuscribeInfo.getString("sbuscribe_name"));
						subscribeMap.put("STATE", sbuscribeInfo.getInt(("follow")));
						Map<String, Object> yjSubscribes = YjDao.getYjInstance().getYjSubscribes(subscribeMap);
						if(yjSubscribes != null){
							isOk = YjDao.getYjInstance().updateYJ_SUBSCRIBE_INFO(Integer.parseInt(yjSubscribes.get("ID")+""),sbuscribeType,sbuscribeInfo.getInt(("follow")));
						}else{
							isOk = YjDao.getYjInstance().insertYJ_SUBSCRIBE_INFO(subscribeMap);
						}
					}
				}else{
					isOk = YjDao.getYjInstance().updateYJ_SUBSCRIBE_GROUP(requestJson.getString("phone_number"));
					JSONArray mapss = requestJson.getJSONArray("sbuscribes");
					for(int i = 0; i < mapss.length(); i++){
						JSONObject sbuscribeInfo = mapss.getJSONObject(i);
						if(id == 0){
							id = 1;
						}else{
							id += 1;
						}
						subscribeMap.put("ID", id);
						subscribeMap.put("TYPE", sbuscribeType);
						subscribeMap.put("PHONE_NUMBER", requestJson.getString("phone_number"));
						subscribeMap.put("YJ_ID", sbuscribeInfo.getString("sbuscribe_id")+"");
						subscribeMap.put("YJ_NAME", sbuscribeInfo.getString("sbuscribe_name"));
						subscribeMap.put("STATE", 1);
						isOk = YjDao.getYjInstance().insertYJ_SUBSCRIBE_INFO(subscribeMap);
					}
				}
				break;
			case 2:
				//修改
				id = requestJson.getInt("sbuscribe_id");
				dataMap.put("ID", id);
				dataMap.put("NAME", requestJson.getString("sbuscribe_name"));
				isOk = YjDao.getYjInstance().updateYJ_CATEGORY_INFO(dataMap);
				dataMapss.put("ID", id);
				dataMapss.put("TYPE", sbuscribeType);
				dataMapss.put("YJ_NAME", requestJson.getString("sbuscribe_name"));
				isOk = YjDao.getYjInstance().updateYJ_SUBSCRIBE_NAME(dataMapss);
				isOk = YjDao.getYjInstance().deleteYJ_GROUP_CATEGORY_INFO(id);
				JSONArray mapss = requestJson.getJSONArray("group_list");
				for(int i = 0; i < mapss.length(); i++){
					JSONObject sbuscribeInfo = mapss.getJSONObject(i);
					dataMaps.put("FLOCK_ID", id);
					dataMaps.put("GROUP_ID", sbuscribeInfo.get("group_id"));
					dataMaps.put("GROUP_NAME", sbuscribeInfo.get("group_name"));
					isOk = YjDao.getYjInstance().insertYJ_GROUP_CATEGORY_INFO(dataMaps);
				}
				break;
			case 3:
				//删除
				if(sbuscribeType == 2){
					id = requestJson.getInt("sbuscribe_id");
					isOk = YjDao.getYjInstance().deteleYJ_CATEGORY_INFO(id);
					isOk = YjDao.getYjInstance().updateYJ_SUBSCRIBE_INFO(id,sbuscribeType,sbuscribeType);
				}
				break;
		}
		if (isOk) {
			//正确，返回ok
			jo.put("result", "OK");
			if(sbuscribeType == 2){
				jo.put("sbuscribe_id", id);
			}
			results[0]="OK";
		} else {
			//错误，返回error
			jo.put("result", "Insert Exception");
			if(sbuscribeType == 2){
				jo.put("sbuscribe_id", id);
			}
			results[0]="FAIL_REASON";
		}
		results[1]=jo.toString();
		return results;
	}
}
