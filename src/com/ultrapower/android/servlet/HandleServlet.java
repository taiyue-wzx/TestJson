package com.ultrapower.android.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ultrapower.android.dao.AndroidTestDAO;
import com.ultrapower.android.model.LacModel;
import com.ultrapower.android.util.TestUtil;
import com.ultrapower.android.util.json.JSONException;
import com.ultrapower.android.util.json.JSONObject;
import com.ultrapower.dt.grid.GridCodeDao;
import com.ultrapower.dt.grid.GridCodePostGreJDBC;

/**
 * 处理zealot_public_table和zealot_public_back表里面的group_id、group_name等四个值
 * 将空值通过空间运算进行赋值
 */
public class HandleServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(HandleServlet.class);
	
	public HandleServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	//第一个参数有三种情况，为1是处理zealot_public_table表；为2是处理zealot_public_back表;为3是处理上面的两个表
	//第二个和第三个参数是处理的开始日期和结束日期
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//获取页面传过来的值
			String handleTime = request.getParameter("handleTime");
			String type1 = request.getParameter("type");
			String ttype1 = request.getParameter("ttype");
			int type = Integer.parseInt(type1);
			int ttype = Integer.parseInt(ttype1);
			//定义map在更新的时候使用（将需要的值存入到map中）
			Map<String, Object> map = new HashMap<String, Object>();
			//查询需要更新的数据的list
			List<Object> list = null;
			//经纬度
			String app_lat = null;
			String app_lon = null;
			String app_group_name = null;
	    	//结果返回
	    	JSONObject jo = new JSONObject();
	    	//因为进行的是每个日期查询更新，所以开始时间和结束时间的日期是相同的
	    	String startTime = handleTime + " 00:00:00";
	    	String endTime = handleTime + " 23:59:59";
	    	map.put("STARTTIME",startTime);
			map.put("ENDTIME",endTime);
			//查询要更新的数据
			if(type == 1){
				if(ttype == 1){
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_TABLE1(startTime, endTime);
				}else if(ttype == 2){
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_TABLE(startTime, endTime);
				}else{
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_TABLE(startTime, endTime);
				}
			}else if(type == 2){
				if(ttype == 1){
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_BACK1(startTime, endTime);
				}else if(ttype == 2){
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_BACK(startTime, endTime);
				}else{
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_BACK(startTime, endTime);
				}
			}else if(type == 3){
				if(ttype == 1){
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_ALL1(startTime, endTime);
				}else if(ttype == 2){
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_ALL(startTime, endTime);
				}else{
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_ALL(startTime, endTime);
				}
			}else{
				if(ttype == 1){
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_ALL1(startTime, endTime);
				}else if(ttype == 2){
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_ALL(startTime, endTime);
				}else{
					list=AndroidTestDAO.getInstance().selectANDROID_ZEALOT_PUBLIC_ALL(startTime, endTime);
				}
			}
			if(list.size() != 0){
				int m=0;int n=0;
				String mapLat = map.get("APP_LAT")+"";
				String mapLon = map.get("APP_LON")+"";
				String mapGroupName = map.get("APP_GROUP_NAME")+"";
				logger.debug("更新处理开始");
				for (int i = 0; i < list.size(); i++) {
					//将list里面的经纬度查询赋值
	    			HashMap<String, Object> o = (HashMap<String, Object>) list.get(i);
	    			app_lat = o.get("APP_LAT")+"";
	    			app_lon = o.get("APP_LON")+"";
	    			app_group_name = o.get("APP_GROUP_NAME")+"";
	    			//取值赋值，如果取出来的值是空的直接赋值为null
	    			if(("null").equals(app_group_name)){
	    				app_group_name = null;
	    			}
	    			if(("null").equals(app_lat) || ("0").equals(app_lat)){
	    				app_lat = null;
	    			}
	    			if(("null").equals(app_lon) || ("0").equals(app_lon)){
	    				app_lon = null;
	    			}
	    			//如果三个参数都是null，那么不经过空间运算方法，直接赋值为null，减少程序运行不必要的方法
	    			LacModel cityInfo = null;
	    			if(app_group_name == null && app_lat == null && app_lon == null){
	    				cityInfo = null;
	    			}else if(mapLat.equals(app_lat) || mapLon.equals(app_lon) || mapGroupName.equals(app_group_name)){
	    				boolean bol = false;
	    				if(type == 1){
	    					if(app_group_name != null && app_lat == null && app_lon == null){
	    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE1(map);
	    					}else{
	    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE(map);
	    					}
	    				}else if(type == 2){
	    					if(app_group_name != null && app_lat == null && app_lon == null){
	    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK1(map);
	    					}else{
	    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK(map);
	    					}
	    				}else if(type == 3){
	    					if(app_group_name != null && app_lat == null && app_lon == null){
	    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE1(map);
		    					bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK1(map);
	    					}else{
	    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE(map);
		    					bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK(map);
	    					}
	    				}else{
	    					if(app_group_name != null && app_lat == null && app_lon == null){
	    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE1(map);
		    					bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK1(map);
	    					}else{
	    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE(map);
		    					bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK(map);
	    					}
	    				}
	    				if(bol){
	    					m = m+1;
	    				}else{
	    					n = n+1;
	    					logger.debug(app_lat+"--"+app_lon+"更新失败");
	    				}
	    			}else{
	    				//通过经纬度进行空间运算，得到系统中定义好的相关的数据
	        			GridCodeDao dao = new GridCodeDao();
	        			GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
	        			cityInfo = getGroupIdInfo(app_lat, app_lon, app_group_name, greJDBC);
	        			if(cityInfo != null){
	        				logger.debug("通过空间运算已查到对应的数据");
		    				map.put("APP_LAT", app_lat);
		    				map.put("APP_LON", app_lon);
		    				map.put("APP_GROUP_ID", cityInfo.getGroupId());
		    				map.put("APP_GROUP_NAME", cityInfo.getGroupName());
		    				map.put("APP_SUBGROUP_ID", cityInfo.getSubGroupId());
		    				map.put("APP_SUBGROUP_NAME", cityInfo.getSubGroupName());
		    				boolean bol = false;
		    				if(type == 1){
		    					if(app_group_name != null && app_lat == null && app_lon == null){
		    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE1(map);
		    					}else{
		    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE(map);
		    					}
		    				}else if(type == 2){
		    					if(app_group_name != null && app_lat == null && app_lon == null){
		    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK1(map);
		    					}else{
		    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK(map);
		    					}
		    				}else if(type == 3){
		    					if(app_group_name != null && app_lat == null && app_lon == null){
		    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE1(map);
			    					bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK1(map);
		    					}else{
		    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE(map);
			    					bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK(map);
		    					}
		    				}else{
		    					if(app_group_name != null && app_lat == null && app_lon == null){
		    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE1(map);
			    					bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK1(map);
		    					}else{
		    						bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_TABLE(map);
			    					bol=AndroidTestDAO.getInstance().updateANDROID_ZEALOT_PUBLIC_BACK(map);
		    					}
		    				}
		    				if(bol){
		    					m = m+1;
		    				}else{
		    					n = n+1;
		    					logger.debug(app_lat+"--"+app_lon+"更新失败");
		    				}
	        			}
	    			}
	    		}
				logger.debug("更新结束");
				jo.put(handleTime, "数据更新成功了"+m+"条数据，更新失败了"+n+"条数据");
			}else{
				logger.debug("日期："+handleTime+"天没有查询到需要更新的数据");
				jo.put(handleTime, "日期："+handleTime+"天没有查询到需要更新的数据");
			}
			sentResponse(response, jo.toString().getBytes("UTF-8"));
		}catch (JSONException ex) {
			ex.printStackTrace();
			JSONObject jo = new JSONObject().put("error", "JsonValueException");
			sentResponse(response, jo.toString().getBytes());
			return;
		}
	}
	
	private LacModel getGroupIdInfo(String latitude, String longitude, String subGroupName, GridCodePostGreJDBC greJDBC) {
		LacModel groupIdInfo = null;
		if (subGroupName != null && !subGroupName.equals("")) {
			groupIdInfo = TestUtil.getCityGroupIdMap(subGroupName);
		}

		if (groupIdInfo!=null&&groupIdInfo.getSubGroupId() != null && !"".equals(groupIdInfo.getSubGroupId())) {
			return groupIdInfo;
		} else if (latitude != null && !latitude.equals("") && longitude != null && !longitude.equals("")) {
			double dLatitude = Double.parseDouble(latitude);
			double dLongitude = Double.parseDouble(longitude);
			String group_id = greJDBC.getGridCodeAuto(dLongitude, dLatitude, 2, null);
			groupIdInfo = TestUtil.getCityGroupIdByGroupIdMap(group_id);
		}
		return groupIdInfo;
	}
	
	private void sentResponse(HttpServletResponse response, byte[] data) throws IOException {
		response.addHeader("Content-Length", "" + data.length);
		OutputStream toClient = response.getOutputStream();
		response.setContentType("application/octet-stream");
		toClient.write(data);
		toClient.flush();
		toClient.close();
		logger.debug("服务器响应数据："+new String(data, "UTF-8"));
	}
}
