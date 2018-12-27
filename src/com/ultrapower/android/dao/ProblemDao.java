package com.ultrapower.android.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;

public class ProblemDao {
	private Logger logger = LoggerFactory.getLogger(AndroidTestDAO.class);

	private static SqlMapClient sqlMap = AndroidTestSqlMapFactory.getSqlMap();

	private static ProblemDao dao = null;

	private ProblemDao() {
	}
	
	public static ProblemDao getProblemInstance() {
		if (dao == null) {
			dao = new ProblemDao();
		}
		return dao;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getproblemListForJX(Map<String, Object> dataMap) {
		List list = null;
		try {
			list = sqlMap.queryForList("getproblemListForJX", dataMap);
		} catch (Exception e) {
			logger.debug("getproblemListForJX异常", e);
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getproblemListCityForJX(Map<String, Object> dataMap) {
		List list = null;
		try {
			list = sqlMap.queryForList("getproblemListCityForJX", dataMap);
		} catch (Exception e) {
			logger.debug("getproblemListCityForJX异常", e);
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getproblemStatisticsForJX(Map<String, Object> dataMap) {
		List list = null;
		try {
			list = sqlMap.queryForList("getproblemStatisticsForJX", dataMap);
		} catch (Exception e) {
			logger.debug("getproblemStatisticsForJX异常", e);
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<HashMap<String, Object>> getUpdataBaseDataForGroup(String groupID) {
		Map data = new HashMap();
		data.put("PARENT_ID", groupID);
		List list = null;
		try {
			list = sqlMap.queryForList("getUpdataBaseDataForGroup",data);
		} catch (Exception e) {
			logger.debug("getUpdataBaseDataForGroup异常", e);
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<HashMap<String, Object>> getUpdataBaseData(String baseType) {
		Map data = new HashMap();
		data.put("DIC_TYPE_CODE", baseType);
		List list = null;
		try {
			list = sqlMap.queryForList("getUpdataBaseData",data);
		} catch (Exception e) {
			logger.debug("getUpdataBaseData异常", e);
		}
		return list;
	}
}
