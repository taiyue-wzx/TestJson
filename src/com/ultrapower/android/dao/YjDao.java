package com.ultrapower.android.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;

public class YjDao {
	private Logger logger = LoggerFactory.getLogger(AndroidTestDAO.class);

	private static SqlMapClient sqlMap = AndroidTestSqlMapFactory.getSqlMap();

	private static YjDao dao = null;

	private YjDao() {
	}
	
	public static YjDao getYjInstance() {
		if (dao == null) {
			dao = new YjDao();
		}
		return dao;
	}
	
	//预警管理
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getYjSubscribeList(String phoneNumber,Integer scrType) {
		Map map = new HashMap();
		map.put("PHONE_NUMBER", phoneNumber);
		map.put("TYPE", scrType);
		List list = null;
		try {
			list = sqlMap.queryForList("getYjSubscribeList", map);
		} catch (Exception e) {
			logger.debug("getYjSubscribeList异常", e);
		}
		return list;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getYjSubscribeLists(String phoneNumber) {
		Map map = new HashMap();
		map.put("PHONE_NUMBER", phoneNumber);
		List list = null;
		try {
			list = sqlMap.queryForList("getYjSubscribeLists", map);
		} catch (Exception e) {
			logger.debug("getYjSubscribeLists异常", e);
		}
		return list;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getYjSubscribeListss(String phoneNumber,Integer scrType) {
		Map map = new HashMap();
		map.put("PHONE_NUMBER", phoneNumber);
		map.put("TYPE", scrType);
		List list = null;
		try {
			list = sqlMap.queryForList("getYjSubscribeListss", map);
		} catch (Exception e) {
			logger.debug("getYjSubscribeListss异常", e);
		}
		return list;
	}
	@SuppressWarnings({"unchecked" })
	public Map<String, Object> getYjSubscribes(Map<String, Object> data) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getYjSubscribes", data);
			if (list.size() > 0) {
				return ((Map<String, Object>) list.get(0));
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.debug("getYjSubscribes异常", e);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getYjSendStates(Map<String, Object> data) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getYjSendStateList", data);
			if (list.size() > 0) {
				return ((Map<String, Object>) list.get(0));
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.debug("getYjSendStateList异常", e);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getYjStatistices(Map<String, Object> data) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getYjStatistices", data);
			if (list.size() > 0) {
				return ((Map<String, Object>) list.get(0));
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.debug("getYjStatistices异常", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getYjGroupCateList(Integer id) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getYjGroupCateList", id);
		} catch (Exception e) {
			logger.debug("getYjGroupCateList异常", e);
		}
		return list;
	}
	public boolean insertYJ_CATEGORY_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertYJ_CATEGORY_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("insertYJ_CATEGORY_INFO异常",e);
			return false;
		}
	}
	public int selectYJ_CATEGORY_ID() {
		int id = 0;
		try {
			if(sqlMap.queryForObject("selectYJ_CATEGORY_ID") != null){
				id = (Integer) sqlMap.queryForObject("selectYJ_CATEGORY_ID");
			}
			return id;
		} catch (Exception e) {
			logger.error("selectYJ_CATEGORY_ID异常",e);
			return id;
		}
	}
	public boolean updateYJ_CATEGORY_INFO(Map<String, Object> data) {
		try {
			sqlMap.update("updateYJ_CATEGORY_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("updateYJ_CATEGORY_INFO异常",e);
			return false;
		}
	}
	public boolean deteleYJ_CATEGORY_INFO(Integer id) {
		try {
			sqlMap.update("deteleYJ_CATEGORY_INFO", id);
			return true;
		} catch (Exception e) {
			logger.error("deteleYJ_CATEGORY_INFO异常",e);
			return false;
		}
	}
	//中间表
	public boolean insertYJ_GROUP_CATEGORY_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertYJ_GROUP_CATEGORY_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("insertYJ_GROUP_CATEGORY_INFO异常",e);
			return false;
		}
	}
	public boolean updateYJ_GROUP_CATEGORY_INFO(Map<String, Object> data) {
		try {
			sqlMap.update("updateYJ_GROUP_CATEGORY_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("updateYJ_GROUP_CATEGORY_INFO异常",e);
			return false;
		}
	}
	public boolean deleteYJ_GROUP_CATEGORY_INFO(Integer id) {
		try {
			sqlMap.update("deleteYJ_GROUP_CATEGORY_INFO", id);
			return true;
		} catch (Exception e) {
			logger.error("deleteYJ_GROUP_CATEGORY_INFO异常",e);
			return false;
		}
	}
	
	//订阅
	public int selectYJ_SUBSCRIBE_ID() {
		int id = 0;
		try {
			if(sqlMap.queryForObject("selectYJ_SUBSCRIBE_ID") != null){
				id = (Integer) sqlMap.queryForObject("selectYJ_SUBSCRIBE_ID");
			}
			return id;
		} catch (Exception e) {
			logger.error("selectYJ_SUBSCRIBE_ID异常",e);
			return id;
		}
	}
	public boolean insertYJ_SUBSCRIBE_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertYJ_SUBSCRIBE_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("insertYJ_SUBSCRIBE_INFO异常",e);
			return false;
		}
	}
	public boolean updateYJ_SUBSCRIBE_NAME(Map<String, Object> data) {
		try {
			sqlMap.update("updateYJ_SUBSCRIBE_NAME", data);
			return true;
		} catch (Exception e) {
			logger.error("updateYJ_SUBSCRIBE_NAME异常",e);
			return false;
		}
	}
	public boolean updateYJ_SUBSCRIBE_GROUP(String phoneNum) {
		try {
			sqlMap.update("updateYJ_SUBSCRIBE_GROUP", phoneNum);
			return true;
		} catch (Exception e) {
			logger.error("updateYJ_SUBSCRIBE_GROUP异常",e);
			return false;
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean updateYJ_SUBSCRIBE_INFO(Integer id,Integer type,Integer state) {
		Map map = new HashMap();
		map.put("ID", id);
		map.put("TYPE", type);
		map.put("STATE", state);
		try {
			sqlMap.update("updateYJ_SUBSCRIBE_INFO", map);
			return true;
		} catch (Exception e) {
			logger.error("updateYJ_SUBSCRIBE_INFO异常",e);
			return false;
		}
	}
	public boolean insertYJ_SENDSTATE_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertYJ_SENDSTATE_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("insertYJ_SENDSTATE_INFO异常",e);
			return false;
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean updateYJ_SENDSTATE_INFO(String id,Integer messageNum,String name) {
		Map map = new HashMap();
		map.put("YJ_ID", id);
		map.put("MESSAGE_NUM", messageNum);
		map.put("INDEX_NAME", name);
		try {
			sqlMap.update("updateYJ_SENDSTATE_INFO", map);
			return true;
		} catch (Exception e) {
			logger.error("updateYJ_SENDSTATE_INFO异常",e);
			return false;
		}
	}
}
