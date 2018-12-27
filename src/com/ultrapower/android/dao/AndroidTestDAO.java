package com.ultrapower.android.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ultrapower.android.util.json.JSONObject;

public class AndroidTestDAO {
	private Logger logger = LoggerFactory.getLogger(AndroidTestDAO.class);

	private static SqlMapClient sqlMap = AndroidTestSqlMapFactory.getSqlMap();

	private static AndroidTestDAO dao = null;

	private AndroidTestDAO() {
	}

	public static AndroidTestDAO getInstance() {
		if (dao == null) {
			dao = new AndroidTestDAO();
		}
		return dao;
	}

	public boolean batchInsert(List<Map<String, Object>> mapList) {
		try {
			List<Map<String, Object>> backList = new ArrayList<Map<String, Object>>();
			
			//publiclog入库（排除背景业务）
			sqlMap.startTransaction();
			sqlMap.startBatch();
			for (Map<String, Object> map : mapList) {
				if("BACKGROUND".equals(map.get("APP_SERVICEREQUEST"))){
					backList.add(map);
				}else{
					sqlMap.insert("insertANDROID_TEST_PUBLICLOG", map);
				}
				
			}
			sqlMap.executeBatch();
			sqlMap.commitTransaction();
			
			//背景业务入库
			sqlMap.startTransaction();
			sqlMap.startBatch();
			for (Map<String, Object> map : backList) {
				if("".equals(map.get(""))){
					backList.add(map);
				}else{
					sqlMap.insert("insertANDROID_TEST_BACK", map);
				}
				
			}
			sqlMap.executeBatch();
			sqlMap.commitTransaction();

			return true;
		} catch (Exception e) {
			logger.debug("batchinsert异常", e);
			return false;
		} finally {
			try {
				sqlMap.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean insertANDROID_TEST_FTP(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_FTP", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_FTP异常", e);
			return false;
		}
	}

	public boolean insertANDROID_ZEALOT_LOG(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_ZEALOT_LOG", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_ZEALOT_LOG异常", e);
			return false;
		}
	}

	public boolean insertANDROID_REPORT_LOG(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_REPORT_LOG", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_REPORT_LOG异常", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_HTTP(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_HTTP", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_HTTP异常", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_PING(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_PING", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_PING异常", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_PROBLEM(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_PROBLEM", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_PROBLEM异常", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_PUBLICLOG(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_PUBLICLOG", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_PUBLICLOG异常", e);
			return false;
		}
	}

	public boolean insertBatchANDROID_TEST_PUBLICLOG(List<Map<String, Object>> data) {
		try {
			sqlMap.insert("insertBatchANDROID_TEST_PUBLICLOG", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertBatchANDROID_TEST_PUBLICLOG异常", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_VIDEO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_VIDEO", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_VIDEO异常", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_VOICE(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_VOICE", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_VOICE异常", e);
			return false;
		}
	}
	
	//用户注册
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectANDROID_REGISTRATION_PERSON(String MSISDN) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("MSISDN", MSISDN);
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getRegistrationPerson", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public boolean insertANDROID_REGISTRATION_PERSON_JX(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_REGISTRATION_PERSON_JX", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_REGISTRATION_PERSON_JX异常",e);
			return false;
		}
	}
	public boolean insertANDROID_REGISTRATION_PERSON(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_REGISTRATION_PERSON", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_REGISTRATION_PERSON异常",e);
			return false;
		}
	}
	public boolean updateANDROID_REGISTRATION_PERSON(Map<String, Object> data) {
		try {
			sqlMap.insert("updateANDROID_REGISTRATION_PERSON", data);
			return true;
		} catch (Exception e) {
			logger.error("updateANDROID_REGISTRATION_PERSON异常",e);
			return false;
		}
	}
	
	//用户登录
	public boolean insertANDROID_ACCOUNT_LOGIN(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_ACCOUNT_LOGIN", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_ACCOUNT_LOGIN异常",e);
			return false;
		}
	}

	//集客工参下载
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDetailedGroupGcSelect() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedGroupGcSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//集客基本信息
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDetailedGroupSelect() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedGroupSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//集客基本信息查询（是否存在）
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectANDROID_GROUP(String GROUP_ID) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("GROUP_ID", GROUP_ID);
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_GROUP", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectANDROID_GROUP1(String GROUP_ID) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("GROUP_ID", GROUP_ID);
		try {
			List<Map<String, Object>> list = sqlMap.queryForList("selectANDROID_GROUP", map);
			if (list.size() > 0) {
				return ((Map<String, Object>) list.get(0));
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.debug("selectANDROID_GROUP异常", e);
		}
		return null;
	}
	
	//集客基本信息添加
	public boolean insertANDROID_GROUP(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_GROUP", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_GROUP异常",e);
			return false;
		}
	}
	
	public boolean updateANDROID_GROUP_CREATEDATE(Map<String, Object> data) {
		try {
			sqlMap.insert("updateANDROID_GROUP_CREATEDATE", data);
			return true;
		} catch (Exception e) {
			logger.error("updateANDROID_GROUP_CREATEDATE异常",e);
			return false;
		}
	}
	
	public boolean updateANDROID_GROUP(Map<String, Object> data) {
		try {
			sqlMap.insert("updateANDROID_GROUP", data);
			return true;
		} catch (Exception e) {
			logger.error("updateANDROID_GROUP异常",e);
			return false;
		}
	}
	
	public boolean deleteANDROID_GROUP(Map<String, Object> data) {
		try {
			sqlMap.insert("deleteANDROID_GROUP", data);
			return true;
		} catch (Exception e) {
			logger.error("deleteANDROID_GROUP异常",e);
			return false;
		}
	}
	
	//集客中心位置
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDetailedGroupCenterSelect() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedGroupCenterSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//集客中心删除-级联
	public boolean deleteANDROID_GROUP_CENTER1(Map<String, Object> data) {
		try {
			sqlMap.insert("deleteANDROID_GROUP_CENTER1", data);
			return true;
		} catch (Exception e) {
			logger.error("deleteANDROID_GROUP_CENTER1异常",e);
			return false;
		}
	}
	
	//集客中心删除
	public boolean deleteANDROID_GROUP_CENTER(Map<String, Object> data) {
		try {
			sqlMap.insert("deleteANDROID_GROUP_CENTER", data);
			return true;
		} catch (Exception e) {
			logger.error("deleteANDROID_GROUP_CENTER异常",e);
			return false;
		}
	}
	
	//集客中心添加
	public boolean insertANDROID_GROUP_CENTER(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_GROUP_CENTER", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_GROUP_CENTER异常",e);
			return false;
		}
	}
	
	//字典表下载（4个）
	//角色
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectANDROID_DICT_ROLE() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedDiceRoleSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//场景大类
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectANDROID_DICT_SCENE() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedDictSceneSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//客服场景
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectANDROID_DICT_CUSTOM() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedDictCustomSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//集客场景
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectANDROID_DICT_GROUP() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedDictGroupSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//ftp
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectANDROID_DICT_FTP() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedDiceFTPSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//问题点回复前端查询服务
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDetailedProblemPointSelect(Map<String, Object> map) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedProblemPointSelect", map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("getDetailedProblemPointSelect异常", e);
		}
		return list;
	}
	
	//问题点历史记录-集客
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectGROUP_PROBLEM_POINT_ALL(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectGROUP_PROBLEM_POINT_ALL", map);
		} catch (Exception e) {
			logger.debug("selectGROUP_PROBLEM_POINT_ALL异常", e);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectGROUP_PROBLEM_POINT(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectGROUP_PROBLEM_POINT", map);
		} catch (Exception e) {
			logger.debug("selectGROUP_PROBLEM_POINT异常", e);
		}
		return null;
	}
	//问题点历史记录-集客
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectGROUP_PROBLEM_POINT_PHONE(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectGROUP_PROBLEM_POINT_PHONE", map);
		} catch (Exception e) {
			logger.debug("selectGROUP_PROBLEM_POINT_PHONE异常", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectZELOT_PUBLIC_TABLE_PROBLEM(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectZELOT_PUBLIC_TABLE_PROBLEM", map);
		} catch (Exception e) {
			logger.debug("selectZELOT_PUBLIC_TABLE_PROBLEM异常", e);
		}
		return null;
	}
	//问题点历史记录-客服
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCUSTOM_PROBLEM_POINT_ALL(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCUSTOM_PROBLEM_POINT_ALL", map);
		} catch (Exception e) {
			logger.debug("selectCUSTOM_PROBLEM_POINT_ALL异常", e);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCUSTOM_PROBLEM_POINT(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCUSTOM_PROBLEM_POINT", map);
		} catch (Exception e) {
			logger.debug("selectCUSTOM_PROBLEM_POINT异常", e);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCUSTOM_PROBLEM_POINT_PHONE(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCUSTOM_PROBLEM_POINT_PHONE", map);
		} catch (Exception e) {
			logger.debug("selectCUSTOM_PROBLEM_POINT_PHONE异常", e);
		}
		return null;
	}
	
	//（集客和客服）问题点查询一个月的信息-广西
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSelectProblemPoint_GX(Map<String, Object> data) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getSelectProblemPoint_GX",data);
		} catch (Exception e) {
			logger.error("getSelectProblemPoint_GX异常",e);
		}
		return list;
	}
	//（集客和客服）问题点查询具体某条信息所有字段信息-广西
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getProblemPoint_GX(Map<String, Object> data) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getProblemPoint_GX",data);
		} catch (Exception e) {
			logger.error("getProblemPoint_GX异常",e);
		}
		return list;
	}
	
	//集客日志添加
	public boolean insertANDROID_GROUP_OPERATION(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_GROUP_OPERATION", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_GROUP_OPERATION异常",e);
			return false;
		}
	}
	
	public boolean updateANDROID_PROBLEM_POINT(String CUSTOMER_MANAGER_PHONE) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CUSTOMER_MANAGER_PHONE", CUSTOMER_MANAGER_PHONE);
		try {
			sqlMap.insert("updateANDROID_PROBLEM_POINT", map);
			return true;
		} catch (Exception e) {
			logger.error("updateANDROID_PROBLEM_POINT异常",e);
			return false;
		}
	}
	
	public boolean updateANDROID_PROBLEM_POINT1(String CUSTOMER_MANAGER_PHONE) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CUSTOMER_MANAGER_PHONE", CUSTOMER_MANAGER_PHONE);
		try {
			sqlMap.insert("updateANDROID_PROBLEM_POINT1", map);
			return true;
		} catch (Exception e) {
			logger.error("updateANDROID_PROBLEM_POINT1异常",e);
			return false;
		}
	}
	
	//问题点回复前端查询服务
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDetailedworkParamSelect(String net,String lac_tac,String ci_eci) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CELL_NETWORKTYPE", net);
		map.put("CELL_TAC_LAC", lac_tac);
		map.put("CELL_ID", ci_eci);
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedworkParamSelect", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//投诉上报
	public boolean insertANDROID_ISSUE_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_ISSUE_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_ISSUE_INFO异常",e);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectANDROID_ISSUE_INFO(Map<String, Object> data) {
		List<HashMap<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_ISSUE_INFO",data);
		} catch (Exception e) {
			logger.error("selectANDROID_ISSUE_INFO异常",e);
		}
		return list;
	}
	
	public boolean insertANDROID_TRACK_SHOT(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TRACK_SHOT", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_TRACK_SHOT异常",e);
			return false;
		}
	}
	
	public boolean insertANDROID_LOG_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_LOG_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_LOG_INFO异常",e);
			return false;
		}
	}
	
	public boolean deleteANDROID_LOG_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("deleteANDROID_LOG_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("deleteANDROID_LOG_INFO异常",e);
			return false;
		}
	}
	
	public int selectANDROID_LOG_INFO_MAXID() {
		int id = 0;
		try {
			if(sqlMap.queryForObject("selectANDROID_LOG_INFO_MAXID") != null){
				id = (Integer) sqlMap.queryForObject("selectANDROID_LOG_INFO_MAXID");
			}
			return id;
		} catch (Exception e) {
			logger.error("selectANDROID_LOG_INFO_MAXID异常",e);
			return id;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectANDROID_LOG_INFO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectANDROID_LOG_INFO", map);
		} catch (Exception e) {
			logger.debug("selectANDROID_LOG_INFO异常", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectANDROID_LOG_INFO_NAME(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectANDROID_LOG_INFO_NAME", map);
		} catch (Exception e) {
			logger.debug("selectANDROID_LOG_INFO_NAME异常", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectZEALOT_PUBLIC_TABLE(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectZEALOT_PUBLIC_TABLE", map);
		} catch (Exception e) {
			logger.debug("selectZEALOT_PUBLIC_TABLE异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectZEALOT_PUBLIC_TABLE_V2(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectZEALOT_PUBLIC_TABLE_V2", map);
		} catch (Exception e) {
			logger.debug("selectZEALOT_PUBLIC_TABLE_V2异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectZEALOT_PAGE_COUNT(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectZEALOT_PAGE_COUNT", map);
		} catch (Exception e) {
			logger.debug("selectZEALOT_PAGE_COUNT异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_FTP_GROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_FTP_GROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_FTP_GROUP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_FTP_SUBGROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_FTP_SUBGROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_FTP_SUBGROUP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_HTTP_GROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_HTTP_GROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_HTTP_GROUP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_HTTP_SUBGROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_HTTP_SUBGROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_HTTP_SUBGROUP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_PING_GROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_PING_GROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_PING_GROUP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_PING_SUBGROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_PING_SUBGROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_PING_SUBGROUP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSERVER_LOCATION(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSERVER_LOCATION", map);
		} catch (Exception e) {
			logger.debug("selectSERVER_LOCATION异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectTopCitys_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectTopCitys_FTP", map);
		} catch (Exception e) {
			logger.debug("selectTopCitys_FTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectBottomCitys_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectBottomCitys_FTP", map);
		} catch (Exception e) {
			logger.debug("selectBottomCitys_FTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectTopCitys_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectTopCitys_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectTopCitys_HTTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectBottomCitys_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectBottomCitys_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectBottomCitys_HTTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectTopCitys_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectTopCitys_PING", map);
		} catch (Exception e) {
			logger.debug("selectTopCitys_PING异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectBottomCitys_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectBottomCitys_PING", map);
		} catch (Exception e) {
			logger.debug("selectBottomCitys_PING异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_FTP", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_FTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_FTP", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_FTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_FTP", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_FTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_FTP", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_FTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_FTP", map);
		} catch (Exception e) {
			logger.debug("selectDATA_FTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_FTP", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_FTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_HTTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_HTTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_HTTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_HTTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectDATA_HTTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_HTTP异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_PING", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_PING异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_PING", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_PING异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_PING", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_PING异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_PING", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_PING异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_PING", map);
		} catch (Exception e) {
			logger.debug("selectDATA_PING异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_PING", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_PING异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_VIDEO异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_VIDEO异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_VIDEO异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_VIDEO异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectDATA_VIDEO异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_VIDEO异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_CONNECTNET异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_CONNECTNET异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_CONNECTNET异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_CONNECTNET异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectDATA_CONNECTNET异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_CONNECTNET异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PROBLEM(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_PROBLEM", map);
		} catch (Exception e) {
			logger.debug("selectDATA_PROBLEM异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_HTTP_RANK(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_HTTP_RANK", map);
		} catch (Exception e) {
			logger.debug("selectDATA_HTTP_RANK异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_FTP_RANK(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_FTP_RANK", map);
		} catch (Exception e) {
			logger.debug("selectDATA_FTP_RANK异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PING_RANK(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_PING_RANK", map);
		} catch (Exception e) {
			logger.debug("selectDATA_PING_RANK异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_VIDEO_RANK(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_VIDEO_RANK", map);
		} catch (Exception e) {
			logger.debug("selectDATA_VIDEO_RANK异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PROBLEM2(Map<String, Object> map) {
		try {
			List<HashMap<String, Object>> l = sqlMap.queryForList("selectDATA_PROBLEM2", map);
			return l;
		} catch (Exception e) {
			logger.debug("selectDATA_PROBLEM2异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PROBLEM3(Map<String, Object> map) {
		try {
			List<HashMap<String, Object>> l = sqlMap.queryForList("selectDATA_PROBLEM3", map);
			return l;
		} catch (Exception e) {
			logger.debug("selectDATA_PROBLEM3异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PROBLEM4(Map<String, Object> map) {
		try {
			List<HashMap<String, Object>> l = sqlMap.queryForList("selectDATA_PROBLEM4", map);
			return l;
		} catch (Exception e) {
			logger.debug("selectDATA_PROBLEM4异常", e);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	public String selectGroupIDByCityName(String city) {
		String group_ids = "";
		Map<String, String> map = new HashMap<String, String>();
		map.put("group_name", city);
		try {

			List list = sqlMap.queryForList("selectGroupIDByCityName", map);
			if (list.size() > 0) {
				group_ids = ((Map) list.get(0)).get("GROUP_IDS").toString();
			}

		} catch (Exception e) {
			logger.debug("selectGroupIDByCityName异常", e);
		}
		return group_ids;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> selectTestUnit(Map<String, Object> map) {
		try {
			List<Map<String, Object>> list = sqlMap.queryForList("selectTestUnit", map);
			if (list.size() > 0) {
				return ((Map<String, Object>) list.get(0));
			} else {
				return null;
			}

		} catch (Exception e) {
			logger.debug("selectTestUnit异常", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> selectTestUnit1(Map<String, Object> map) {
		try {
			List<Map<String, Object>> list = sqlMap.queryForList("selectTestUnit1", map);
			if (list.size() > 0) {
				return ((Map<String, Object>) list.get(0));
			} else {
				return null;
			}

		} catch (Exception e) {
			logger.debug("selectTestUnit1异常", e);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getCommand(String id) {
		Map map = new HashMap();
		map.put("ID", Integer.parseInt(id));
		List list = null;
		try {
			list = sqlMap.queryForList("getCommand", map);
		} catch (Exception e) {
			logger.debug("getCommand异常", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDetailedVideoSelect() {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedVideoSelect");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> selectTestUnitByPlanCode(String planCode) {
		Map<String, Object> map = new HashMap();
		map.put("PLAN_CODE", planCode);
		List list = null;
		try {
			list = sqlMap.queryForList("getDefaultTestUnit", map);
			if (list.size() > 0) {
				return ((Map) list.get(0));
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.debug("getCommand异常", e);
		}
		return null;
	}

	public boolean updateDataFTP(Map<String, Object> data) {
		try {
			sqlMap.update("updateDataFTP", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateDataHTTP(Map<String, Object> data) {
		try {
			sqlMap.update("updateDataHTTP", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateDataPING(Map<String, Object> data) {
		try {
			sqlMap.update("updateDataPING", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateDataVIDEO(Map<String, Object> data) {
		try {
			sqlMap.update("updateDataVIDEO", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateDataPUBLIC(Map<String, Object> data) {
		try {
			sqlMap.update("updateDataPUBLIC", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//查询zealot_public_table表里面的group_id空值
	@SuppressWarnings("unchecked")
	public List<Object> selectANDROID_ZEALOT_PUBLIC_TABLE(String startTime,String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("STARTTIME", startTime);
		map.put("ENDTIME", endTime);
		List<Object> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_ZEALOT_PUBLIC_TABLE", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<Object> selectANDROID_ZEALOT_PUBLIC_TABLE1(String startTime,String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("STARTTIME", startTime);
		map.put("ENDTIME", endTime);
		List<Object> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_ZEALOT_PUBLIC_TABLE1", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//查询zealot_public_back表里面的group_id空值
	@SuppressWarnings("unchecked")
	public List<Object> selectANDROID_ZEALOT_PUBLIC_BACK(String startTime,String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("STARTTIME", startTime);
		map.put("ENDTIME", endTime);
		List<Object> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_ZEALOT_PUBLIC_BACK", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<Object> selectANDROID_ZEALOT_PUBLIC_BACK1(String startTime,String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("STARTTIME", startTime);
		map.put("ENDTIME", endTime);
		List<Object> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_ZEALOT_PUBLIC_BACK1", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//查询zealot_public_table和zealot_public_back表里面的group_id空值
	@SuppressWarnings("unchecked")
	public List<Object> selectANDROID_ZEALOT_PUBLIC_ALL(String startTime,String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("STARTTIME", startTime);
		map.put("ENDTIME", endTime);
		List<Object> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_ZEALOT_PUBLIC_ALL", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<Object> selectANDROID_ZEALOT_PUBLIC_ALL1(String startTime,String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("STARTTIME", startTime);
		map.put("ENDTIME", endTime);
		List<Object> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_ZEALOT_PUBLIC_ALL1", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public boolean updateANDROID_ZEALOT_PUBLIC_TABLE(Map<String, Object> data) {
		try {
			sqlMap.update("updateANDROID_ZEALOT_PUBLIC_TABLE", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateANDROID_ZEALOT_PUBLIC_BACK(Map<String, Object> data) {
		try {
			sqlMap.update("updateANDROID_ZEALOT_PUBLIC_BACK", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateANDROID_ZEALOT_PUBLIC_TABLE1(Map<String, Object> data) {
		try {
			sqlMap.update("updateANDROID_ZEALOT_PUBLIC_TABLE1", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateANDROID_ZEALOT_PUBLIC_BACK1(Map<String, Object> data) {
		try {
			sqlMap.update("updateANDROID_ZEALOT_PUBLIC_BACK1", data);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectSCENE_GPS_CENTER(JSONObject requestJson){
		List<Map<String, Object>> list = null;
		Map<String, Object> map = new HashMap<String, Object>();
		
		Long lat = Long.parseLong(requestJson.getString("lat")) ;
		Long lon = Long.parseLong(requestJson.getString("lon")) ;
		Long radius = Long.parseLong(requestJson.getString("Radius")) ;
		
		map.put("lat", lat);
		map.put("lon", lon);
		map.put("radius", radius) ;
		try {
			list = sqlMap.queryForList("getsceneGPSCenterSelect", map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("getDetailedProblemPointSelect异常", e);
		}
		return list;
	}

	public boolean insertFeedbackOpinion(Map<String, Object> map) {
		try {
			sqlMap.insert("insertFeedbackOpinion", map);
			return true ;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.debug("insertFeedbackOpinion异常", e);
			return false ;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectfeedbackOpinion(Map<String, Object> map) {
		List<Map<String,Object>> list = null ;
		try {
			list = sqlMap.queryForList("selectfeedbackOpinion", map);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug("selectfeedbackOpinion异常", e);
		}
		return list ;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectUrlDetailed(Map<String, Object> map) {
		List<Map<String,Object>> list = null ;
		try {
			list = sqlMap.queryForList("selectUrlDetailed", map);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug("selectUrlDetailed异常", e);
		}
		return list ;
	}
	
	
}
