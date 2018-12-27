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
 * ����zealot_public_table��zealot_public_back�������group_id��group_name���ĸ�ֵ
 * ����ֵͨ���ռ�������и�ֵ
 */
public class HandleServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(HandleServlet.class);
	
	public HandleServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	//��һ�����������������Ϊ1�Ǵ���zealot_public_table��Ϊ2�Ǵ���zealot_public_back��;Ϊ3�Ǵ��������������
	//�ڶ����͵����������Ǵ���Ŀ�ʼ���ںͽ�������
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//��ȡҳ�洫������ֵ
			String handleTime = request.getParameter("handleTime");
			String type1 = request.getParameter("type");
			String ttype1 = request.getParameter("ttype");
			int type = Integer.parseInt(type1);
			int ttype = Integer.parseInt(ttype1);
			//����map�ڸ��µ�ʱ��ʹ�ã�����Ҫ��ֵ���뵽map�У�
			Map<String, Object> map = new HashMap<String, Object>();
			//��ѯ��Ҫ���µ����ݵ�list
			List<Object> list = null;
			//��γ��
			String app_lat = null;
			String app_lon = null;
			String app_group_name = null;
	    	//�������
	    	JSONObject jo = new JSONObject();
	    	//��Ϊ���е���ÿ�����ڲ�ѯ���£����Կ�ʼʱ��ͽ���ʱ�����������ͬ��
	    	String startTime = handleTime + " 00:00:00";
	    	String endTime = handleTime + " 23:59:59";
	    	map.put("STARTTIME",startTime);
			map.put("ENDTIME",endTime);
			//��ѯҪ���µ�����
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
				logger.debug("���´���ʼ");
				for (int i = 0; i < list.size(); i++) {
					//��list����ľ�γ�Ȳ�ѯ��ֵ
	    			HashMap<String, Object> o = (HashMap<String, Object>) list.get(i);
	    			app_lat = o.get("APP_LAT")+"";
	    			app_lon = o.get("APP_LON")+"";
	    			app_group_name = o.get("APP_GROUP_NAME")+"";
	    			//ȡֵ��ֵ�����ȡ������ֵ�ǿյ�ֱ�Ӹ�ֵΪnull
	    			if(("null").equals(app_group_name)){
	    				app_group_name = null;
	    			}
	    			if(("null").equals(app_lat) || ("0").equals(app_lat)){
	    				app_lat = null;
	    			}
	    			if(("null").equals(app_lon) || ("0").equals(app_lon)){
	    				app_lon = null;
	    			}
	    			//���������������null����ô�������ռ����㷽����ֱ�Ӹ�ֵΪnull�����ٳ������в���Ҫ�ķ���
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
	    					logger.debug(app_lat+"--"+app_lon+"����ʧ��");
	    				}
	    			}else{
	    				//ͨ����γ�Ƚ��пռ����㣬�õ�ϵͳ�ж���õ���ص�����
	        			GridCodeDao dao = new GridCodeDao();
	        			GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
	        			cityInfo = getGroupIdInfo(app_lat, app_lon, app_group_name, greJDBC);
	        			if(cityInfo != null){
	        				logger.debug("ͨ���ռ������Ѳ鵽��Ӧ������");
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
		    					logger.debug(app_lat+"--"+app_lon+"����ʧ��");
		    				}
	        			}
	    			}
	    		}
				logger.debug("���½���");
				jo.put(handleTime, "���ݸ��³ɹ���"+m+"�����ݣ�����ʧ����"+n+"������");
			}else{
				logger.debug("���ڣ�"+handleTime+"��û�в�ѯ����Ҫ���µ�����");
				jo.put(handleTime, "���ڣ�"+handleTime+"��û�в�ѯ����Ҫ���µ�����");
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
		logger.debug("��������Ӧ���ݣ�"+new String(data, "UTF-8"));
	}
}
