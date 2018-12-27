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
			
			//publiclog��⣨�ų�����ҵ��
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
			
			//����ҵ�����
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
			logger.debug("batchinsert�쳣", e);
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
			logger.debug("insertANDROID_TEST_FTP�쳣", e);
			return false;
		}
	}

	public boolean insertANDROID_ZEALOT_LOG(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_ZEALOT_LOG", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_ZEALOT_LOG�쳣", e);
			return false;
		}
	}

	public boolean insertANDROID_REPORT_LOG(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_REPORT_LOG", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_REPORT_LOG�쳣", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_HTTP(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_HTTP", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_HTTP�쳣", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_PING(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_PING", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_PING�쳣", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_PROBLEM(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_PROBLEM", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_PROBLEM�쳣", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_PUBLICLOG(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_PUBLICLOG", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_PUBLICLOG�쳣", e);
			return false;
		}
	}

	public boolean insertBatchANDROID_TEST_PUBLICLOG(List<Map<String, Object>> data) {
		try {
			sqlMap.insert("insertBatchANDROID_TEST_PUBLICLOG", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertBatchANDROID_TEST_PUBLICLOG�쳣", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_VIDEO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_VIDEO", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_VIDEO�쳣", e);
			return false;
		}
	}

	public boolean insertANDROID_TEST_VOICE(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TEST_VOICE", data);
			return true;
		} catch (Exception e) {
			logger.debug("insertANDROID_TEST_VOICE�쳣", e);
			return false;
		}
	}
	
	//�û�ע��
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
			logger.error("insertANDROID_REGISTRATION_PERSON_JX�쳣",e);
			return false;
		}
	}
	public boolean insertANDROID_REGISTRATION_PERSON(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_REGISTRATION_PERSON", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_REGISTRATION_PERSON�쳣",e);
			return false;
		}
	}
	public boolean updateANDROID_REGISTRATION_PERSON(Map<String, Object> data) {
		try {
			sqlMap.insert("updateANDROID_REGISTRATION_PERSON", data);
			return true;
		} catch (Exception e) {
			logger.error("updateANDROID_REGISTRATION_PERSON�쳣",e);
			return false;
		}
	}
	
	//�û���¼
	public boolean insertANDROID_ACCOUNT_LOGIN(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_ACCOUNT_LOGIN", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_ACCOUNT_LOGIN�쳣",e);
			return false;
		}
	}

	//���͹�������
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
	
	//���ͻ�����Ϣ
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
	
	//���ͻ�����Ϣ��ѯ���Ƿ���ڣ�
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
			logger.debug("selectANDROID_GROUP�쳣", e);
		}
		return null;
	}
	
	//���ͻ�����Ϣ���
	public boolean insertANDROID_GROUP(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_GROUP", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_GROUP�쳣",e);
			return false;
		}
	}
	
	public boolean updateANDROID_GROUP_CREATEDATE(Map<String, Object> data) {
		try {
			sqlMap.insert("updateANDROID_GROUP_CREATEDATE", data);
			return true;
		} catch (Exception e) {
			logger.error("updateANDROID_GROUP_CREATEDATE�쳣",e);
			return false;
		}
	}
	
	public boolean updateANDROID_GROUP(Map<String, Object> data) {
		try {
			sqlMap.insert("updateANDROID_GROUP", data);
			return true;
		} catch (Exception e) {
			logger.error("updateANDROID_GROUP�쳣",e);
			return false;
		}
	}
	
	public boolean deleteANDROID_GROUP(Map<String, Object> data) {
		try {
			sqlMap.insert("deleteANDROID_GROUP", data);
			return true;
		} catch (Exception e) {
			logger.error("deleteANDROID_GROUP�쳣",e);
			return false;
		}
	}
	
	//��������λ��
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
	
	//��������ɾ��-����
	public boolean deleteANDROID_GROUP_CENTER1(Map<String, Object> data) {
		try {
			sqlMap.insert("deleteANDROID_GROUP_CENTER1", data);
			return true;
		} catch (Exception e) {
			logger.error("deleteANDROID_GROUP_CENTER1�쳣",e);
			return false;
		}
	}
	
	//��������ɾ��
	public boolean deleteANDROID_GROUP_CENTER(Map<String, Object> data) {
		try {
			sqlMap.insert("deleteANDROID_GROUP_CENTER", data);
			return true;
		} catch (Exception e) {
			logger.error("deleteANDROID_GROUP_CENTER�쳣",e);
			return false;
		}
	}
	
	//�����������
	public boolean insertANDROID_GROUP_CENTER(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_GROUP_CENTER", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_GROUP_CENTER�쳣",e);
			return false;
		}
	}
	
	//�ֵ�����أ�4����
	//��ɫ
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
	//��������
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
	//�ͷ�����
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
	//���ͳ���
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
	
	//�����ظ�ǰ�˲�ѯ����
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDetailedProblemPointSelect(Map<String, Object> map) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getDetailedProblemPointSelect", map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("getDetailedProblemPointSelect�쳣", e);
		}
		return list;
	}
	
	//�������ʷ��¼-����
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectGROUP_PROBLEM_POINT_ALL(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectGROUP_PROBLEM_POINT_ALL", map);
		} catch (Exception e) {
			logger.debug("selectGROUP_PROBLEM_POINT_ALL�쳣", e);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectGROUP_PROBLEM_POINT(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectGROUP_PROBLEM_POINT", map);
		} catch (Exception e) {
			logger.debug("selectGROUP_PROBLEM_POINT�쳣", e);
		}
		return null;
	}
	//�������ʷ��¼-����
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectGROUP_PROBLEM_POINT_PHONE(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectGROUP_PROBLEM_POINT_PHONE", map);
		} catch (Exception e) {
			logger.debug("selectGROUP_PROBLEM_POINT_PHONE�쳣", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectZELOT_PUBLIC_TABLE_PROBLEM(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectZELOT_PUBLIC_TABLE_PROBLEM", map);
		} catch (Exception e) {
			logger.debug("selectZELOT_PUBLIC_TABLE_PROBLEM�쳣", e);
		}
		return null;
	}
	//�������ʷ��¼-�ͷ�
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCUSTOM_PROBLEM_POINT_ALL(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCUSTOM_PROBLEM_POINT_ALL", map);
		} catch (Exception e) {
			logger.debug("selectCUSTOM_PROBLEM_POINT_ALL�쳣", e);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCUSTOM_PROBLEM_POINT(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCUSTOM_PROBLEM_POINT", map);
		} catch (Exception e) {
			logger.debug("selectCUSTOM_PROBLEM_POINT�쳣", e);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCUSTOM_PROBLEM_POINT_PHONE(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCUSTOM_PROBLEM_POINT_PHONE", map);
		} catch (Exception e) {
			logger.debug("selectCUSTOM_PROBLEM_POINT_PHONE�쳣", e);
		}
		return null;
	}
	
	//�����ͺͿͷ���������ѯһ���µ���Ϣ-����
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSelectProblemPoint_GX(Map<String, Object> data) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getSelectProblemPoint_GX",data);
		} catch (Exception e) {
			logger.error("getSelectProblemPoint_GX�쳣",e);
		}
		return list;
	}
	//�����ͺͿͷ���������ѯ����ĳ����Ϣ�����ֶ���Ϣ-����
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getProblemPoint_GX(Map<String, Object> data) {
		List<Map<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("getProblemPoint_GX",data);
		} catch (Exception e) {
			logger.error("getProblemPoint_GX�쳣",e);
		}
		return list;
	}
	
	//������־���
	public boolean insertANDROID_GROUP_OPERATION(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_GROUP_OPERATION", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_GROUP_OPERATION�쳣",e);
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
			logger.error("updateANDROID_PROBLEM_POINT�쳣",e);
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
			logger.error("updateANDROID_PROBLEM_POINT1�쳣",e);
			return false;
		}
	}
	
	//�����ظ�ǰ�˲�ѯ����
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
	
	//Ͷ���ϱ�
	public boolean insertANDROID_ISSUE_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_ISSUE_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_ISSUE_INFO�쳣",e);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectANDROID_ISSUE_INFO(Map<String, Object> data) {
		List<HashMap<String, Object>> list = null;
		try {
			list = sqlMap.queryForList("selectANDROID_ISSUE_INFO",data);
		} catch (Exception e) {
			logger.error("selectANDROID_ISSUE_INFO�쳣",e);
		}
		return list;
	}
	
	public boolean insertANDROID_TRACK_SHOT(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_TRACK_SHOT", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_TRACK_SHOT�쳣",e);
			return false;
		}
	}
	
	public boolean insertANDROID_LOG_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("insertANDROID_LOG_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("insertANDROID_LOG_INFO�쳣",e);
			return false;
		}
	}
	
	public boolean deleteANDROID_LOG_INFO(Map<String, Object> data) {
		try {
			sqlMap.insert("deleteANDROID_LOG_INFO", data);
			return true;
		} catch (Exception e) {
			logger.error("deleteANDROID_LOG_INFO�쳣",e);
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
			logger.error("selectANDROID_LOG_INFO_MAXID�쳣",e);
			return id;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectANDROID_LOG_INFO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectANDROID_LOG_INFO", map);
		} catch (Exception e) {
			logger.debug("selectANDROID_LOG_INFO�쳣", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectANDROID_LOG_INFO_NAME(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectANDROID_LOG_INFO_NAME", map);
		} catch (Exception e) {
			logger.debug("selectANDROID_LOG_INFO_NAME�쳣", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectZEALOT_PUBLIC_TABLE(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectZEALOT_PUBLIC_TABLE", map);
		} catch (Exception e) {
			logger.debug("selectZEALOT_PUBLIC_TABLE�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectZEALOT_PUBLIC_TABLE_V2(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectZEALOT_PUBLIC_TABLE_V2", map);
		} catch (Exception e) {
			logger.debug("selectZEALOT_PUBLIC_TABLE_V2�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectZEALOT_PAGE_COUNT(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectZEALOT_PAGE_COUNT", map);
		} catch (Exception e) {
			logger.debug("selectZEALOT_PAGE_COUNT�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_FTP_GROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_FTP_GROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_FTP_GROUP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_FTP_SUBGROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_FTP_SUBGROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_FTP_SUBGROUP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_HTTP_GROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_HTTP_GROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_HTTP_GROUP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_HTTP_SUBGROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_HTTP_SUBGROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_HTTP_SUBGROUP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_PING_GROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_PING_GROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_PING_GROUP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSCORE_PING_SUBGROUP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSCORE_PING_SUBGROUP", map);
		} catch (Exception e) {
			logger.debug("selectSCORE_PING_SUBGROUP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectSERVER_LOCATION(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectSERVER_LOCATION", map);
		} catch (Exception e) {
			logger.debug("selectSERVER_LOCATION�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectTopCitys_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectTopCitys_FTP", map);
		} catch (Exception e) {
			logger.debug("selectTopCitys_FTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectBottomCitys_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectBottomCitys_FTP", map);
		} catch (Exception e) {
			logger.debug("selectBottomCitys_FTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectTopCitys_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectTopCitys_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectTopCitys_HTTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectBottomCitys_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectBottomCitys_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectBottomCitys_HTTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectTopCitys_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectTopCitys_PING", map);
		} catch (Exception e) {
			logger.debug("selectTopCitys_PING�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectBottomCitys_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectBottomCitys_PING", map);
		} catch (Exception e) {
			logger.debug("selectBottomCitys_PING�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_FTP", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_FTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_FTP", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_FTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_FTP", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_FTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_FTP", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_FTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_FTP", map);
		} catch (Exception e) {
			logger.debug("selectDATA_FTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_FTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_FTP", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_FTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_HTTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_HTTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_HTTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_HTTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectDATA_HTTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_HTTP(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_HTTP", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_HTTP�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_PING", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_PING�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_PING", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_PING�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_PING", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_PING�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_PING", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_PING�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_PING", map);
		} catch (Exception e) {
			logger.debug("selectDATA_PING�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_PING(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_PING", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_PING�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_VIDEO�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_VIDEO�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_VIDEO�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_VIDEO�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectDATA_VIDEO�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_VIDEO(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_VIDEO", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_VIDEO�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_CONNECTNET�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectPROVINCE_DATA_ALL_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectPROVINCE_DATA_ALL_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectPROVINCE_DATA_ALL_CONNECTNET�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_CONNECTNET�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectCITY_DATA_ALL_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectCITY_DATA_ALL_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectCITY_DATA_ALL_CONNECTNET�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectDATA_CONNECTNET�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_ALL_CONNECTNET(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_ALL_CONNECTNET", map);
		} catch (Exception e) {
			logger.debug("selectDATA_ALL_CONNECTNET�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PROBLEM(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_PROBLEM", map);
		} catch (Exception e) {
			logger.debug("selectDATA_PROBLEM�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_HTTP_RANK(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_HTTP_RANK", map);
		} catch (Exception e) {
			logger.debug("selectDATA_HTTP_RANK�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_FTP_RANK(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_FTP_RANK", map);
		} catch (Exception e) {
			logger.debug("selectDATA_FTP_RANK�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PING_RANK(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_PING_RANK", map);
		} catch (Exception e) {
			logger.debug("selectDATA_PING_RANK�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_VIDEO_RANK(Map<String, Object> map) {
		try {
			return sqlMap.queryForList("selectDATA_VIDEO_RANK", map);
		} catch (Exception e) {
			logger.debug("selectDATA_VIDEO_RANK�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PROBLEM2(Map<String, Object> map) {
		try {
			List<HashMap<String, Object>> l = sqlMap.queryForList("selectDATA_PROBLEM2", map);
			return l;
		} catch (Exception e) {
			logger.debug("selectDATA_PROBLEM2�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PROBLEM3(Map<String, Object> map) {
		try {
			List<HashMap<String, Object>> l = sqlMap.queryForList("selectDATA_PROBLEM3", map);
			return l;
		} catch (Exception e) {
			logger.debug("selectDATA_PROBLEM3�쳣", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<HashMap<String, Object>> selectDATA_PROBLEM4(Map<String, Object> map) {
		try {
			List<HashMap<String, Object>> l = sqlMap.queryForList("selectDATA_PROBLEM4", map);
			return l;
		} catch (Exception e) {
			logger.debug("selectDATA_PROBLEM4�쳣", e);
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
			logger.debug("selectGroupIDByCityName�쳣", e);
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
			logger.debug("selectTestUnit�쳣", e);
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
			logger.debug("selectTestUnit1�쳣", e);
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
			logger.debug("getCommand�쳣", e);
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
			logger.debug("getCommand�쳣", e);
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
	
	//��ѯzealot_public_table�������group_id��ֵ
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
	
	//��ѯzealot_public_back�������group_id��ֵ
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
	
	//��ѯzealot_public_table��zealot_public_back�������group_id��ֵ
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
			logger.debug("getDetailedProblemPointSelect�쳣", e);
		}
		return list;
	}

	public boolean insertFeedbackOpinion(Map<String, Object> map) {
		try {
			sqlMap.insert("insertFeedbackOpinion", map);
			return true ;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.debug("insertFeedbackOpinion�쳣", e);
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
			logger.debug("selectfeedbackOpinion�쳣", e);
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
			logger.debug("selectUrlDetailed�쳣", e);
		}
		return list ;
	}
	
	
}
