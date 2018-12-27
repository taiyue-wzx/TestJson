package com.ultrapower.android.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ultrapower.android.dao.ProblemDao;
import com.ultrapower.android.util.json.JSONArray;
import com.ultrapower.android.util.json.JSONObject;

public class ProblemService {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	@SuppressWarnings("rawtypes")
	public String problemListForJX(JSONObject requestJson){
		//处理结果
		JSONObject objectJson = new JSONObject();
		JSONArray objectArray = new JSONArray();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("DATA_ID", requestJson.getString("startid"));
		dataMap.put("DATA_NUMBER", requestJson.getString("datanumber"));
		if(("").equals(requestJson.getString("problem_state"))){
			dataMap.put("PROBLEM_STATE", "-1,0,1,2");
		}else{
			if(("0").equals(requestJson.getString("problem_state"))){
				dataMap.put("PROBLEM_STATE", "-1,0,2");
			}
			if(("1").equals(requestJson.getString("problem_state"))){
				dataMap.put("PROBLEM_STATE", "1");
			}
		}
		dataMap.put("STARTTIME", format.format(requestJson.getLong("starttime"))+ " 00:00:00");
		dataMap.put("ENDTIME", format.format(requestJson.getLong("endtime"))+ " 23:59:59");
		List<Map<String, Object>> objList = null;
		if("get_problem_list_jx".equals(requestJson.getString("subtype"))){
			double range = requestJson.getInt("range")*0.00001;
			double startLat = Double.parseDouble(requestJson.getString("lat")) - range;
			double endLat = Double.parseDouble(requestJson.getString("lat")) + range;
			double startLon = Double.parseDouble(requestJson.getString("lon")) - range;
			double endLon = Double.parseDouble(requestJson.getString("lon")) + range;
			dataMap.put("STARTLAT", startLat);
			dataMap.put("ENDLAT", endLat);
			dataMap.put("STARTLON", startLon);
			dataMap.put("ENDLON", endLon);
			objList = ProblemDao.getProblemInstance().getproblemListForJX(dataMap);
		}
		if("get_problem_list_city_jx".equals(requestJson.getString("subtype"))){
			dataMap.put("SUBGROUP_ID", requestJson.getString("city_id"));
			dataMap.put("MSISDN", requestJson.getString("user_name"));
			dataMap.put("KEYWORD", requestJson.getString("keyword"));
			objList = ProblemDao.getProblemInstance().getproblemListCityForJX(dataMap);
		}
		objectJson.put("problem_list", objectArray);
		if(objList != null){
			for (Map map : objList) {
				JSONObject obj = new JSONObject();
				obj.put("id", map.get("ID"));
				obj.put("log_name", map.get("LOG_NAME") == null ? "" : requestJson.getString("FTPPath")+map.get("LOG_NAME"));
				obj.put("question_classification", map.get("QUESTION_CLASSIFICATION") == null ? "" : map.get("QUESTION_CLASSIFICATION"));
				obj.put("problem_description", map.get("PROBLEM_DESCRIPTION") == null ? "" : map.get("PROBLEM_DESCRIPTION"));
				obj.put("location", map.get("LOCATION") == null ? "" : map.get("LOCATION"));
				obj.put("city_id", map.get("SUBGROUP_ID") == null ? "" : map.get("SUBGROUP_ID"));
				obj.put("problem_state", map.get("PROBLEM_STATE") == null ? "" : map.get("PROBLEM_STATE"));
				obj.put("problem_report_time", map.get("PROBLEM_REPORT_TIME") == null ? "" : map.get("PROBLEM_REPORT_TIME"));
				obj.put("net_type", map.get("NET_TYPE") == null ? "" : map.get("NET_TYPE"));
				obj.put("user_name", map.get("MSISDN") == null ? "" : map.get("MSISDN"));
				obj.put("problem_photos", map.get("PROBLEM_PHOTOS") == null ? "" : requestJson.getString("HTTPPath")+map.get("PROBLEM_PHOTOS")+"");
				obj.put("problem_audio", map.get("AUDIO") == null ? "" : requestJson.getString("HTTPPath")+map.get("AUDIO")+"");
				if("get_problem_list_city_jx".equals(requestJson.getString("subtype"))){
					obj.put("lon", map.get("LON") == null ? "" : map.get("LON"));
					obj.put("lat", map.get("LAT") == null ? "" : map.get("LAT"));
				}
				objectArray.put(obj);
			}
		}
		return objectJson.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public String problemStatisticsForJX(JSONObject requestJson){
		//处理结果
		JSONObject objectJson = new JSONObject();
		JSONArray objectArray = new JSONArray();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		double range = requestJson.getInt("range")*0.00001;
		double startLat = Double.parseDouble(requestJson.getString("lat")) - range;
		double endLat = Double.parseDouble(requestJson.getString("lat")) + range;
		double startLon = Double.parseDouble(requestJson.getString("lon")) - range;
		double endLon = Double.parseDouble(requestJson.getString("lon")) + range;
		dataMap.put("STARTLAT", startLat);
		dataMap.put("ENDLAT", endLat);
		dataMap.put("STARTLON", startLon);
		dataMap.put("ENDLON", endLon);
		if(("1").equals(requestJson.getString("statistics_state"))){
			dataMap.put("STATISTICS_STATE", "QUESTION_CLASSIFICATION");
			objectJson.put("statistics_state", "问题类型");
		}
		if(("2").equals(requestJson.getString("statistics_state"))){
			dataMap.put("STATISTICS_STATE", "QUESTION_BUSINESS_TYPE");
			objectJson.put("statistics_state", "业务分类");
		}
		dataMap.put("STARTTIME", format.format(requestJson.getLong("starttime"))+ " 00:00:00");
		dataMap.put("ENDTIME", format.format(requestJson.getLong("endtime"))+ " 23:59:59");
		List<Map<String, Object>> objList = ProblemDao.getProblemInstance().getproblemStatisticsForJX(dataMap);
		objectJson.put("statistics_list", objectArray);
		if(objList != null){
			for (Map map : objList) {
				JSONObject obj = new JSONObject();
				obj.put("code", map.get("STATISTICS_STATE") == null ? "0" : map.get("STATISTICS_STATE"));
				obj.put("name", "");
				obj.put("count", map.get("NUM"));
				objectArray.put(obj);
			}
		}
		return objectJson.toString();
	}
	
	@SuppressWarnings("rawtypes")
	public String updataBaseDataForJX(JSONObject requestJson){
		//处理结果
		JSONObject jsonObjects = new JSONObject();
		//域查询
		String groupID = "10540";
		List<HashMap<String, Object>> listGroups = ProblemDao.getProblemInstance().getUpdataBaseDataForGroup(groupID);
		JSONArray jsonArray = new JSONArray();
		if(listGroups != null){
			JSONObject jsonObject1 = new JSONObject();
			for (Map map : listGroups) {
				JSONArray jsonArray1 = new JSONArray();
				jsonObject1.put("province", map.get("PROVINCE_NAME") == null ? "" : map.get("PROVINCE_NAME"));
				jsonObject1.put("province_id", map.get("PROVINCE_ID") == null ? "" : map.get("PROVINCE_ID"));
				jsonObject1.put("city_list", jsonArray1);
				if(map.get("CITYS") != null){
					String[] contents = map.get("CITYS").toString().split(",");
					for(int y=0;y<contents.length;y++){
						JSONObject jsonObj= new JSONObject();
						String[] cons = contents[y].split("-");
						jsonObj.put("city", cons[0]);
						jsonObj.put("city_id", cons[1]);
						jsonArray1.put(jsonObj);
					}
				}
			}
			jsonArray.put(jsonObject1);
			jsonObjects.put("city_info",jsonObject1);
		}
		//字典表查询
		//读取配置txt文件
		try{
			Scanner in = new Scanner(new File(requestJson.getString("webPath")+"/uBaseData.txt"));
			String strContent = null;
			while (in.hasNextLine()) {
                strContent = in.nextLine();
            }
			String[] baseTypes = strContent.split(",");
			List<HashMap<String, Object>> listInfos = null;
			for(int x=0;x<baseTypes.length;x++){
				String[] baseType = baseTypes[x].split("-");
				JSONArray jsonArray2 = new JSONArray();
				listInfos = ProblemDao.getProblemInstance().getUpdataBaseData(baseType[0]);
				jsonObjects.put(baseType[1], jsonArray2);
				if(listInfos != null){
					for (Map map : listInfos) {
						JSONObject jsonObject2 = new JSONObject();
						jsonObject2.put("type_name", map.get("DIC_VALUE") == null ? "" : map.get("DIC_VALUE"));
						if(map.get("DIC_KEY") == null){
							jsonObject2.put("type_id", "");
						}else{
							String typeId = map.get("DIC_KEY")+"";
							if(typeId.length() == 4){
								jsonObject2.put("type_id", Integer.parseInt(typeId.substring(3, 4)));
							}
							if(typeId.length() == 5){
								jsonObject2.put("type_id", Integer.parseInt(typeId.substring(3, 5)));
							}
						}
						jsonArray2.put(jsonObject2);
					}
				}
			}
		}catch(Exception e){
			System.out.println("文件读取失败");
		}
		return jsonObjects.toString();
	}
}
