package com.ultrapower.dt.grid;

import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.Logger;

public class GridCodeDao {

	//private  Logger logger = Logger.getLogger(GridCodeDao.class);
	
	private DBCPManager dbcpManager=new DBCPManager();
	public void getConn() {
		dbcpManager.getConn();
	}

	public void closeConn() {
		dbcpManager.closeConn();
	}

	/**
	 * 自动打开连接和关闭连接
	 * @param tableName
	 * @param lon
	 * @param lat
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List  selectGridIdAuto(String tableName, double lon, double lat) {

		dbcpManager.getConn();
		List  result = selectGridId(tableName, lon, lat);


		dbcpManager.closeConn();

		return result;
	}

	/**
	 * 需要手动打开和关闭连接
	 * @param tableName
	 * @param lon
	 * @param lat
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List selectGridId(String tableName, double lon, double lat) {
		List  result = new ArrayList ();
		StringBuffer buffer = new StringBuffer();
		buffer.append("select gridid from ");
		buffer.append(tableName);
		buffer.append("  where  st_intersects(geom,ST_geometryfromtext('POINT(");
		buffer.append(lon);
		buffer.append("  ");
		buffer.append(lat);
		buffer.append(")',4326)) ");

		result = dbcpManager.queryForList(buffer.toString());
		//result= dbcpManager.queryForList("select id  gridid from black_point where id =?","dd");
		return result;

	}

	@SuppressWarnings("rawtypes")
	public List selectSceneGPSCenter(String tableName, Double lon, Double lat) {
		dbcpManager.getConn();
		List  result = selectSceneGPS(tableName, lon, lat);

		dbcpManager.closeConn();

		return result;
	}

	@SuppressWarnings("rawtypes")
	private List selectSceneGPS(String tableName, Double lon, Double lat) {
		List  result = new ArrayList ();
		StringBuffer buffer = new StringBuffer();
		buffer.append("select scene_id,scene_name,city,scene,sub_scene,st_astext(geom) geom from ");
		buffer.append(tableName);
		buffer.append(" where st_intersects(geom,st_geomfromtext('POINT(");
		buffer.append(lon);
		buffer.append(" ");
		buffer.append(lat);
		buffer.append(")',4326)) ");

		result = dbcpManager.queryForList(buffer.toString());
		//result= dbcpManager.queryForList("select id  gridid from black_point where id =?","dd");
		return result;
	}

}