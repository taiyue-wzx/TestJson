package com.ultrapower.dt.grid;

import java.util.List;
import java.util.Map;

public class GridCodePostGreJDBC {
	private String lastGridId = "";
	public static final String GRID_GLOBAL = "global";
	public static final String GRID_HIGHWAY = "HighWay";
	public static final String GRID_RAILWAY = "RailWay";
	public static final String GRID_MAJORLINE = "MajorLine";
	public static final String GRID_COUNTYSEAT = "CountySeat";


	private double LAST_LON = 0;
	private double LAST_LAT = 0;
	private GridCodeDao gridCodeDao;

	public GridCodePostGreJDBC() {
	}

	public GridCodePostGreJDBC(GridCodeDao gridCodeDao) {
		this.gridCodeDao = gridCodeDao;
	}

	/**
	 * 自动打开连接和关闭连接
	 * @param lon
	 * @param lat
	 * @param testTarget
	 * @param masterGridID
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getGridCodeAuto(double lon, double lat, int testTarget, String masterGridID) {
		String tableName = getTableName(testTarget);
		if (tableName == null) {
			return "";
		}
		if (!"".equals(lastGridId)&&checkSimilarity(lon, lat)) {
			return this.lastGridId;
		}

		List gridIds = this.gridCodeDao.selectGridIdAuto(tableName, lon, lat);
		if ((gridIds != null) && (gridIds.size() > 0)) {
			String rtnGrid = ((Map) gridIds.get(0)).get("GRIDID").toString();
			if (gridIds.size() > 1) {
				if ((masterGridID != null) && (masterGridID.length() > 0) && (gridIds.contains(masterGridID)))
					rtnGrid = masterGridID;
				else if (gridIds.contains(this.lastGridId)) {
					rtnGrid = this.lastGridId;
				}
			}
			this.lastGridId = rtnGrid;
			return rtnGrid;
		}
		return "";
	}
	
	public List getsceneGPSCenter(Map<String, Object> data){
		String tableName = "SCENE_GRID" ;
		Double lat = Double.valueOf(String.valueOf(data.get("lat") )) ;
		Double lon = Double.valueOf(String.valueOf(data.get("long"))) ;
		return this.gridCodeDao.selectSceneGPSCenter(tableName, lon, lat);
		
	}

	public void getConn() {
		gridCodeDao.getConn();
	}

	public void closeConn() {
		gridCodeDao.closeConn();
	}

	/**
	 * 需要手动打开连接和关闭连接
	 * @param lon
	 * @param lat
	 * @param testTarget
	 * @param masterGridID
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getGridCode(double lon, double lat, int testTarget, String masterGridID) {
		String tableName = getTableName(testTarget);
		if (tableName == null) {
			return "";
		}
		if (checkSimilarity(lon, lat)) {
			return this.lastGridId;
		}

		List gridIds = this.gridCodeDao.selectGridId(tableName, lon, lat);
		if ((gridIds != null) && (gridIds.size() > 0)) {
			String rtnGrid = ((Map) gridIds.get(0)).get("GRIDID").toString();
			if (gridIds.size() > 1) {
				if ((masterGridID != null) && (masterGridID.length() > 0) && (gridIds.contains(masterGridID)))
					rtnGrid = masterGridID;
				else if (gridIds.contains(this.lastGridId)) {
					rtnGrid = this.lastGridId;
				}
			}
			this.lastGridId = rtnGrid;
			return rtnGrid;
		}
		return "";
	}


	/**
	 * 检查是否相似
	 */
	private boolean checkSimilarity(double lon, double lat) {
		if (Math.abs(lon - LAST_LON) > 0.01 || Math.abs(lat - LAST_LAT) > 0.01) {
			LAST_LON = lon;
			LAST_LAT = lat;
			return false;
		} else {
			LAST_LON = lon;
			LAST_LAT = lat;
		}


		return true;

	}


	private String getTableName(int testTarget) {
		switch (testTarget) {
		case 1:
			return "global";
		case 2:
			return "HighWay";
		case 3:
			return "RailWay";
		case 4:
			return "MajorLine";
		case 5:
			return "CountySeat";
		}
		return null;
	}

	public GridCodeDao getGridCodeDao() {
		return gridCodeDao;
	}

	public void setGridCodeDao(GridCodeDao gridCodeDao) {
		this.gridCodeDao = gridCodeDao;
	}

}
