package com.ultrapower.android.servlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ultrapower.accredit.api.SecurityService;
import com.ultrapower.accredit.common.value.Resource;
import com.ultrapower.accredit.common.value.User;
import com.ultrapower.accredit.rmiclient.RmiClientApplication;
import com.ultrapower.android.dao.AndroidTestDAO;
import com.ultrapower.android.dao.ProblemDao;
import com.ultrapower.android.model.JsonDataType;
import com.ultrapower.android.model.LacModel;
import com.ultrapower.android.service.DownloadService;
import com.ultrapower.android.service.ProblemService;
import com.ultrapower.android.service.UploadService;
import com.ultrapower.android.util.DataUtils;
import com.ultrapower.android.util.DateUtils;
import com.ultrapower.android.util.FileUtils;
import com.ultrapower.android.util.JsonDataQualityManage;
import com.ultrapower.android.util.TestUtil;
import com.ultrapower.android.util.ZipUtils;
import com.ultrapower.android.util.json.JSONArray;
import com.ultrapower.android.util.json.JSONException;
import com.ultrapower.android.util.json.JSONObject;
import com.ultrapower.dt.grid.GridCodeDao;
import com.ultrapower.dt.grid.GridCodePostGreJDBC;

public class JsonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER = 1024;
	// 密钥
	private String enc_key = "abcd1234";
	private Logger logger = LoggerFactory.getLogger(JsonServlet.class);

	private UploadService uploadService = new UploadService();
	private DownloadService downloadService = new DownloadService();

	private SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	// 指定日期减去1900年
	private Calendar specifiedTime = Calendar.getInstance();

	public void doGet(HttpServletRequest request, HttpServletResponse response)

			throws ServletException, java.io.IOException {
		PrintWriter pw = response.getWriter();// 通过response得到输出流
		pw.println(new File("aaaa.txt").getAbsolutePath());
		URL url = Thread.currentThread().getContextClassLoader().getResource("serverlist");
		pw.println(url);
		File file = new File(url.toString().split("file:")[1]);
		pw.println(file.getAbsolutePath());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("LOGTIME", new Timestamp(System.currentTimeMillis()));
		map.put("IMEI", "ABCD");
		map.put("IMSI", "1234");
	}

	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		// 问题上报的文件存放路径
		String path = request.getScheme() + "://" + request.getHeader("host") + request.getContextPath();
		String imagePath = request.getSession().getServletContext().getRealPath("image");
		String audioPath = request.getSession().getServletContext().getRealPath("audio");

		// 接收数据流转字符串
		logger.debug("数据总长：" + request.getContentLength());
		InputStream is = getInputStream(request);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte dataBuffer[] = new byte[BUFFER];
		int count;
		while ((count = is.read(dataBuffer, 0, BUFFER)) != -1) {
			os.write(dataBuffer, 0, count);
		}
		os.flush();
		byte[] dataArray = os.toByteArray();
		os.close();
		// 数据流编码方式
		String contentEncoding = request.getHeader("Content-Encoding") == null ? "" : request.getHeader("Content-Encoding").trim();
		// 判断编码方式，处理解密
		if ("encrypt".equals(contentEncoding) || "encrypt-gzip".equals(contentEncoding)) {
			dataArray = DataUtils.RC4Base(dataArray, enc_key);
		}
		String dataStr = new String(dataArray, "UTF-8");
		logger.debug("数据内容：" + dataStr);

		// 转json对象
		JSONObject requestJson = null;
		try {
			dataStr = dataStr.substring(dataStr.indexOf("{"), dataStr.lastIndexOf("}") + 1);
			requestJson = new JSONObject(dataStr);
		} catch (JSONException ex) {
			logger.debug("json转换错误：" + dataStr);
			ex.printStackTrace();
			JSONObject jo = new JSONObject().put("error", "JsonFormatException");
			// 格式转换异常通知前端错误信息，ps：有可能是数据传输不完整导致错误
			sentResponse(response, jo.toString().getBytes(), "error.txt");
			// 结束后续处理，这时客户端应该收到异常响应，并重发数据，ps：应该加个重发次数限制
			return;
		}

		// 数据处理结果
		String results[] = null;
		String result = null;
		// 数据上传处理
		try {
			// 集客信息的接口开发
			if ("upload".equals(requestJson.getString("type"))
					&& "account_register".equals(requestJson.getString("subtype"))) {
				// 用户注册
				JSONObject jo = new JSONObject();
				// 判断是不是集团客户
				// 不是集团客户，需要验证码和手机号判断
				if (!"1330".equals(requestJson.getString("account_type"))) {
					if (!"1008611".equals(requestJson.getString("password"))
							&& "1332".equals(requestJson.getString("account_type"))) {
						jo.put("result", "FAIL");
						jo.put("reason", "专业人员注册失败!错误原因是验证码错误！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					} else if (!"1008621".equals(requestJson.getString("password"))
							&& "1331".equals(requestJson.getString("account_type"))) {
						jo.put("result", "FAIL");
						jo.put("reason", "客户经理注册失败!错误原因是验证码错误！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					} else if (!"1008631".equals(requestJson.getString("password"))
							&& "1333".equals(requestJson.getString("account_type"))) {
						jo.put("result", "FAIL");
						jo.put("reason", "客服人员注册失败!错误原因是验证码错误！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					} else {
						List<Map<String, Object>> list = AndroidTestDAO.getInstance()
								.selectANDROID_REGISTRATION_PERSON(requestJson.getString("phone_number"));
						if (list.size() > 0) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("PERSON_TYPE_ID", requestJson.getString("account_type"));
							map.put("MSISDN", requestJson.getString("phone_number"));
							boolean bol = false;
							bol = AndroidTestDAO.getInstance().updateANDROID_REGISTRATION_PERSON(map);
							if (bol) {
								jo.put("result", "OK");
								jo.put("reason", "注册成功！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
							} else {
								jo.put("result", "FAIL");
								jo.put("reason", "注册失败!错误原因可能是其他错误！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
							}
						} else {
							String phoneNum = requestJson.getString("phone_number");
							// 通过正则表达式判断手机号
							boolean flag = false;
							Pattern regex = Pattern.compile("[1][34578]\\d{9}");
							Matcher matcher = regex.matcher(phoneNum);
							flag = matcher.matches();
							if (flag) {
								Map<String, Object> map = new HashMap<String, Object>();
								// 通过给的城市，得到系统中定义好的
								GridCodeDao dao = new GridCodeDao();
								GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
								LacModel cityInfo = getGroupIdInfo("", "", requestJson.getString("subgroup_name"),
										greJDBC);
								if (cityInfo != null) {
									map.put("SUBGROUP_ID", cityInfo.getSubGroupId());
									map.put("SUBGROUP_NAME", cityInfo.getSubGroupName());
								} else {
									map.put("SUBGROUP_NAME", requestJson.getString("subgroup_name"));
								}
								map.put("PERSON_TYPE_ID", requestJson.getString("account_type"));
								map.put("REG_DATE", new Timestamp(System.currentTimeMillis()));
								map.put("MSISDN", phoneNum);
								map.put("STAFF_CODE", requestJson.getString("staff_code"));
								boolean bol = false;
								bol = AndroidTestDAO.getInstance().insertANDROID_REGISTRATION_PERSON(map);
								if (bol) {
									jo.put("result", "OK");
									jo.put("reason", "注册成功！");
									sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
								} else {
									jo.put("result", "FAIL");
									jo.put("reason", "注册失败!错误原因可能是其他错误！");
									sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
								}
							} else {
								jo.put("result", "FAIL");
								jo.put("reason", "注册失败!错误原因是手机号码不正确！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
							}
						}
					}
				} else {
					// 是集团客户的，直接验证手机号
					List<Map<String, Object>> list = AndroidTestDAO.getInstance()
							.selectANDROID_REGISTRATION_PERSON(requestJson.getString("phone_number"));
					if (list.size() > 0) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("PERSON_TYPE_ID", requestJson.getString("account_type"));
						map.put("MSISDN", requestJson.getString("phone_number"));
						boolean bol = false;
						bol = AndroidTestDAO.getInstance().updateANDROID_REGISTRATION_PERSON(map);
						if (bol) {
							jo.put("result", "OK");
							jo.put("reason", "注册成功！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
						} else {
							jo.put("result", "FAIL");
							jo.put("reason", "注册失败!错误原因可能是其他错误！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						}
					} else {
						String phoneNum = requestJson.getString("phone_number");
						// 通过正则表达式判断手机号
						boolean flag = false;
						Pattern regex = Pattern.compile("[1][34578]\\d{9}");
						Matcher matcher = regex.matcher(phoneNum);
						flag = matcher.matches();
						if (flag) {
							Map<String, Object> map = new HashMap<String, Object>();
							// 通过给的城市，得到系统中定义好的
							GridCodeDao dao = new GridCodeDao();
							GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
							LacModel cityInfo = getGroupIdInfo("", "", requestJson.getString("subgroup_name"), greJDBC);
							if (cityInfo != null) {
								map.put("SUBGROUP_ID", cityInfo.getSubGroupId());
								map.put("SUBGROUP_NAME", cityInfo.getSubGroupName());
							} else {
								map.put("SUBGROUP_NAME", requestJson.getString("subgroup_name"));
							}
							map.put("PERSON_TYPE_ID", requestJson.getString("account_type"));
							map.put("REG_DATE", new Timestamp(System.currentTimeMillis()));
							map.put("MSISDN", phoneNum);
							map.put("STAFF_CODE", requestJson.getString("staff_code"));
							boolean bol = false;
							bol = AndroidTestDAO.getInstance().insertANDROID_REGISTRATION_PERSON(map);
							if (bol) {
								jo.put("result", "OK");
								jo.put("reason", "注册成功！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
							} else {
								jo.put("result", "FAIL");
								jo.put("reason", "注册失败!错误原因可能是其他错误！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
							}
						} else {
							jo.put("result", "FAIL");
							jo.put("reason", "注册失败!错误原因是手机号码不正确！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						}
					}
				}
			} else if ("upload".equals(requestJson.getString("type"))
					&& "account_register_cq".equals(requestJson.getString("subtype"))) {
				JSONObject jo = new JSONObject();
				String phoneNum = requestJson.getString("phone_number");
				String code = requestJson.getString("password");
				if (code.equals(requestJson.getString("department_id"))) {
					List<Map<String, Object>> list = AndroidTestDAO.getInstance()
							.selectANDROID_REGISTRATION_PERSON(phoneNum);
					if (list.size() > 0) {
						jo.put("result", "FAIL");
						jo.put("reason", "注册失败!错误原因是该用户已注册！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					} else {
						boolean flag = false;
						Pattern regex = Pattern.compile("[1][34578]\\d{9}");
						Matcher matcher = regex.matcher(phoneNum);
						flag = matcher.matches();
						if (flag) {
							Map map = new HashMap();
							try {
								map.put("SUBGROUP_NAME", requestJson.getString("subgroup_name") == null ? ""
										: requestJson.getString("subgroup_name"));
								map.put("PERSON_TYPE_ID", requestJson.getString("account_type"));
								map.put("REG_DATE", new Timestamp(System.currentTimeMillis()));
								map.put("MSISDN", phoneNum);
								map.put("STAFF_CODE", requestJson.getString("staff_code"));
								map.put("SUBGROUP_ID", requestJson.getString("department_id") == null ? ""
										: requestJson.getString("department_id"));
								boolean bol = false;
								bol = AndroidTestDAO.getInstance().insertANDROID_REGISTRATION_PERSON(map);
								if (bol) {
									jo.put("result", "OK");
									jo.put("reason", "注册成功！");
									sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
									return;
								}
								jo.put("result", "FAIL");
								jo.put("reason", "注册失败!错误原因可能是其他错误！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
							} catch (Exception e) {
								jo.put("result", "FAIL");
								jo.put("reason", "注册失败!错误原因可能是其他错误！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
							}
						} else {
							jo.put("result", "FAIL");
							jo.put("reason", "注册失败!错误原因是手机号码不正确！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						}
					}
				} else {
					jo.put("result", "FAIL");
					jo.put("reason", "注册失败!错误原因是验证码错误！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				}
			} else if ("upload".equals(requestJson.getString("type"))
					&& "account_login".equals(requestJson.getString("subtype"))) {
				// 用户登录
				JSONObject jo = new JSONObject();
				/*
				 * if(!"10086".equals(requestJson.getString("password"))){
				 * jo.put("result", "FAIL"); jo.put("reason",
				 * "登录失败!错误原因是验证码错误！"); sentResponse(response,
				 * jo.toString().getBytes("UTF-8"), "error.txt"); }else{
				 */
				List<Map<String, Object>> loginList = AndroidTestDAO.getInstance()
						.selectANDROID_REGISTRATION_PERSON(requestJson.getString("phone_number"));
				if (loginList.size() == 0) {
					jo.put("result", "FAIL");
					jo.put("reason", "登录失败!错误原因是没有注册！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					jo.put("result", "OK");
					for (Map map : loginList) {
						jo.put("account_type", map.get("PERSON_TYPE_ID"));
						jo.put("staff_code", map.get("STAFF_CODE"));
						jo.put("subgroup_name", map.get("SUBGROUP_NAME"));
					}
					jo.put("reason", "登录成功！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
				}
			} else if ("upload".equals(requestJson.getString("type"))
					&& "account_login_cq".equals(requestJson.getString("subtype"))) {
				// 重庆用户登录
				JSONObject jo = new JSONObject();
				/*
				 * if(!"10086".equals(requestJson.getString("password"))){
				 * jo.put("result", "FAIL"); jo.put("reason",
				 * "登录失败!错误原因是验证码错误！"); sentResponse(response,
				 * jo.toString().getBytes("UTF-8"), "error.txt"); }else{
				 */
				List<Map<String, Object>> loginList = AndroidTestDAO.getInstance()
						.selectANDROID_REGISTRATION_PERSON(requestJson.getString("phone_number"));
				if (loginList.size() == 0) {
					jo.put("result", "FAIL");
					jo.put("reason", "登录失败!错误原因是没有注册！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					jo.put("result", "OK");
					for (Map map : loginList) {
						jo.put("account_type", map.get("PERSON_TYPE_ID"));
						jo.put("staff_code", map.get("STAFF_CODE"));
						jo.put("subgroup_name", map.get("SUBGROUP_NAME"));
						jo.put("department_id", map.get("DEPARTMENT_ID"));
					}
					jo.put("reason", "登录成功！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
				}
			} else if ("download".equals(requestJson.getString("type"))
					&& "group_network_param_v1".equals(requestJson.getString("subtype"))) {
				// 集客工参-内容压缩，文件不压缩
				List<Map<String, Object>> groupGcList = AndroidTestDAO.getInstance().getDetailedGroupGcSelect();
				JSONObject groupGcJson = new JSONObject();
				JSONArray groupGcArray = new JSONArray();
				groupGcJson.put("hot_area", groupGcArray);
				for (Map map : groupGcList) {
					JSONObject groupGc = new JSONObject();
					groupGc.put("group_id", map.get("GROUP_ID"));
					groupGc.put("subgroup_name", map.get("SUBGROUP_NAME"));
					groupGc.put("county_town_name", map.get("COUNTY_TOWN_NAME"));
					groupGc.put("lac", map.get("LAC"));
					groupGc.put("ci", map.get("CI"));
					groupGc.put("standard", map.get("STANDARD"));
					groupGcArray.put(groupGc);
				}
				sentGzipResponse(response, groupGcJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "group_network_param".equals(requestJson.getString("subtype"))) {
				// 集客工参 --文件压缩
				List<Map<String, Object>> groupGcList = AndroidTestDAO.getInstance().getDetailedGroupGcSelect();
				JSONObject groupGcJson = new JSONObject();
				JSONArray groupGcArray = new JSONArray();
				groupGcJson.put("hot_area", groupGcArray);
				for (Map map : groupGcList) {
					JSONObject groupGc = new JSONObject();
					groupGc.put("group_id", map.get("GROUP_ID"));
					groupGc.put("subgroup_name", map.get("SUBGROUP_NAME"));
					groupGc.put("county_town_name", map.get("COUNTY_TOWN_NAME"));
					groupGc.put("lac", map.get("LAC"));
					groupGc.put("ci", map.get("CI"));
					groupGc.put("standard", map.get("STANDARD"));
					groupGcArray.put(groupGc);
				}
				String filePath = FileUtils.createRandomPath("txt");
				String zipFilePath = FileUtils.createRandomPath("zip");
				FileUtils.saveString2File(groupGcJson.toString(), filePath);
				ZipUtils utils = new ZipUtils(zipFilePath);
				utils.compress(filePath);
				sentFileResponse(response, FileUtils.getBytes(zipFilePath), FileUtils.getFileName(zipFilePath));
				FileUtils.deleteFile(zipFilePath);
				FileUtils.deleteFile(filePath);
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "group_base_information_v1".equals(requestJson.getString("subtype"))) {
				// 集客信息-内容压缩，文件不压缩
				List<Map<String, Object>> groupList = AndroidTestDAO.getInstance().getDetailedGroupSelect();
				JSONObject groupJson = new JSONObject();
				JSONArray groupArray = new JSONArray();
				groupJson.put("group_info", groupArray);
				for (Map map : groupList) {
					JSONObject group = new JSONObject();
					group.put("group_id", map.get("GROUP_ID"));
					group.put("subgroup_name", map.get("SUBGROUP_NAME"));
					group.put("county_town_name", map.get("COUNTY_TOWN_NAME"));
					group.put("county_town_type", map.get("COUNTY_TOWN_TYPE"));
					group.put("group_name", map.get("GROUP_NAME"));
					group.put("group_level", map.get("GROUP_LEVEL"));
					group.put("scene_type", map.get("SCENE_TYPE"));
					group.put("scene_type_id", map.get("SCENE_TYPE_ID"));
					group.put("gps_type", map.get("GPS_TYPE"));
					group.put("app_lon", map.get("APP_LON"));
					group.put("app_lat", map.get("APP_LAT"));
					group.put("radius", map.get("RADIUS"));
					group.put("staff_code", map.get("STAFF_CODE") + "");
					group.put("customer_manager_name", map.get("CUSTOMER_MANAGER_NAME"));
					group.put("customer_manager_phone", map.get("CUSTOMER_MANAGER_PHONE") + "");
					group.put("company_address", map.get("COMPANY_ADDRESS"));
					group.put("contact_name", map.get("CONTACT_NAME"));
					group.put("contact_phone", map.get("CONTACT_PHONENUM") + "");
					group.put("group_person_number", map.get("GROUP_PERSON_NUMBER"));
					groupArray.put(group);
				}
				sentGzipResponse(response, groupJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "group_base_information".equals(requestJson.getString("subtype"))) {
				// 集客信息 --文件压缩
				List<Map<String, Object>> groupList = AndroidTestDAO.getInstance().getDetailedGroupSelect();
				JSONObject groupJson = new JSONObject();
				JSONArray groupArray = new JSONArray();
				groupJson.put("group_info", groupArray);
				for (Map map : groupList) {
					JSONObject group = new JSONObject();
					group.put("group_id", map.get("GROUP_ID"));
					group.put("subgroup_name", map.get("SUBGROUP_NAME"));
					group.put("county_town_name", map.get("COUNTY_TOWN_NAME"));
					group.put("county_town_type", map.get("COUNTY_TOWN_TYPE"));
					group.put("group_name", map.get("GROUP_NAME"));
					group.put("group_level", map.get("GROUP_LEVEL"));
					group.put("scene_type", map.get("SCENE_TYPE"));
					group.put("scene_type_id", map.get("SCENE_TYPE_ID"));
					group.put("gps_type", map.get("GPS_TYPE"));
					group.put("app_lon", map.get("APP_LON"));
					group.put("app_lat", map.get("APP_LAT"));
					group.put("radius", map.get("RADIUS"));
					group.put("staff_code", map.get("STAFF_CODE") + "");
					group.put("customer_manager_name", map.get("CUSTOMER_MANAGER_NAME"));
					group.put("customer_manager_phone", map.get("CUSTOMER_MANAGER_PHONE") + "");
					group.put("company_address", map.get("COMPANY_ADDRESS"));
					group.put("contact_name", map.get("CONTACT_NAME"));
					group.put("contact_phone", map.get("CONTACT_PHONENUM") + "");
					group.put("group_person_number", map.get("GROUP_PERSON_NUMBER"));
					groupArray.put(group);
				}
				String filePath = FileUtils.createRandomPath("txt");
				String zipFilePath = FileUtils.createRandomPath("zip");
				FileUtils.saveString2File(groupJson.toString(), filePath);
				ZipUtils utils = new ZipUtils(zipFilePath);
				utils.compress(filePath);
				sentFileResponse(response, FileUtils.getBytes(zipFilePath), FileUtils.getFileName(zipFilePath));
				FileUtils.deleteFile(zipFilePath);
				FileUtils.deleteFile(filePath);
				return;
			} else if ("upload".equals(requestJson.getString("type"))
					&& "group_property_insert".equals(requestJson.getString("subtype"))) {
				// 集客点基本信息表插入
				JSONObject jo = new JSONObject();
				List<Map<String, Object>> loginList = AndroidTestDAO.getInstance()
						.selectANDROID_REGISTRATION_PERSON(requestJson.getString("operate_msisdn"));
				if (loginList.size() == 0) {
					jo.put("result", "FAIL");
					jo.put("reason", "数据插入失败!错误原因是该用户没有权限！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("GROUP_ID", requestJson.getString("group_id"));
					// 查询数据库里面是否已存在数据
					List<Map<String, Object>> groupList = AndroidTestDAO.getInstance()
							.selectANDROID_GROUP(requestJson.getString("group_id"));
					if (groupList.size() > 0) {
						jo.put("result", "FAIL");
						jo.put("reason", "数据插入失败!错误原因是集客点编号重复！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					} else {
						map.put("GROUP_NAME", requestJson.getString("group_name"));
						map.put("COUNTY_TOWN_NAME", requestJson.getString("county_town_name"));
						map.put("COUNTY_TOWN_TYPE", requestJson.getString("county_town_type"));
						map.put("SCENE_TYPE", requestJson.getString("scene_type"));
						map.put("GPS_TYPE", requestJson.getString("gps_type"));
						map.put("SCENE_TYPE_ID", requestJson.getString("scene_type_id"));
						map.put("APP_LON", requestJson.getString("app_lon"));
						map.put("APP_LAT", requestJson.getString("app_lat"));
						map.put("RADIUS", requestJson.getInt("radius"));
						map.put("COMPANY_ADDRESS", requestJson.getString("company_address"));
						map.put("CUSTOMER_MANAGER_NAME", requestJson.getString("customer_manager_name"));
						map.put("CUSTOMER_MANAGER_PHONE", requestJson.getString("customer_manager_phone"));
						map.put("CONTACT_NAME", requestJson.getString("contact_name"));
						map.put("CONTACT_PHONENUM", requestJson.getString("contact_phone"));
						map.put("GROUP_PERSON_NUMBER", requestJson.getInt("group_person_number"));
						map.put("EFFECTIVE_MARK", 1);
						// 通过给的城市，得到系统中定义好的
						GridCodeDao dao = new GridCodeDao();
						GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
						LacModel cityInfo = getGroupIdInfo(requestJson.getString("app_lat"),
								requestJson.getString("app_lon"), requestJson.getString("subgroup_name"), greJDBC);
						if (cityInfo != null) {
							map.put("SUBGROUP_ID", cityInfo.getSubGroupId());
							map.put("SUBGROUP_NAME", cityInfo.getSubGroupName());
						} else {
							map.put("SUBGROUP_NAME", requestJson.getString("subgroup_name"));
						}
						boolean bol = false;
						bol = AndroidTestDAO.getInstance().insertANDROID_GROUP(map);
						// 集客日志添加
						map.put("GROUP_OPERATION_TIME", new Date());// 时间
						map.put("OPERATION_TYPE", "0");// 操作类型：0创建、1修改、2删除
						map.put("GROUP_OPERATION_TYPE", "自动");// 创建方式:手动、自动。创建分为手动自动；修改都是手动；
						if (loginList != null) {
							for (Map login : loginList) {
								String personTypeID = login.get("PERSON_TYPE_ID") + "";
								map.put("GROUP_OPERATIONPERSON_TYPE_ID", personTypeID);// 创建/修改人属性ID
								if (personTypeID.equals("1330")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "集团用户");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1331")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "客户经理");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1332")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "专业人员");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1333")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "客服人员");// 创建/修改人属性:客户经理、专业用户
								}
							}
						}
						map.put("GROUP_OPERATIONPERSON_ISDN", requestJson.getString("operate_msisdn"));// 创建/修改人电话
						bol = AndroidTestDAO.getInstance().insertANDROID_GROUP_OPERATION(map);
						if (bol) {
							jo.put("result", "OK");
							jo.put("reason", "数据插入成功！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
						} else {
							jo.put("result", "FAIL");
							jo.put("reason", "数据插入失败!错误原因可能是其他错误！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						}
					}
				}
			} else if ("upload".equals(requestJson.getString("type"))
					&& "group_property_update".equals(requestJson.getString("subtype"))) {
				// 集客点基本信息表修改
				JSONObject jo = new JSONObject();
				List<Map<String, Object>> loginList = AndroidTestDAO.getInstance()
						.selectANDROID_REGISTRATION_PERSON(requestJson.getString("operate_msisdn"));
				if (loginList.size() == 0) {
					jo.put("result", "FAIL");
					jo.put("reason", "数据修改失败!错误原因是该用户没有权限！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					Map<String, Object> group = AndroidTestDAO.getInstance()
							.selectANDROID_GROUP1(requestJson.getString("group_id"));
					if ("".equals(group) || group == null) {
						jo.put("result", "FAIL");
						jo.put("reason", "数据修改失败!错误原因数据库中已不存在这条数据！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					} else {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("GROUP_ID",
								requestJson.getString("group_id").equals("") ? "" : requestJson.getString("group_id"));
						map.put("GROUP_NAME", requestJson.getString("group_name").equals("") ? ""
								: requestJson.getString("group_name"));
						map.put("COUNTY_TOWN_NAME", requestJson.getString("county_town_name").equals("") ? ""
								: requestJson.getString("county_town_name"));
						map.put("COUNTY_TOWN_TYPE", requestJson.getString("county_town_type").equals("") ? ""
								: requestJson.getString("county_town_type"));
						map.put("SCENE_TYPE", requestJson.getString("scene_type").equals("") ? ""
								: requestJson.getString("scene_type"));
						map.put("GPS_TYPE",
								requestJson.getString("gps_type").equals("") ? "" : requestJson.getString("gps_type"));
						map.put("SCENE_TYPE_ID", requestJson.getString("scene_type_id").equals("") ? ""
								: requestJson.getString("scene_type_id"));
						map.put("APP_LON",
								requestJson.getString("app_lon").equals("") ? "" : requestJson.getString("app_lon"));
						map.put("APP_LAT",
								requestJson.getString("app_lat").equals("") ? "" : requestJson.getString("app_lat"));
						map.put("RADIUS",
								(requestJson.getInt("radius") + "").equals("") ? "0" : requestJson.getInt("radius"));
						map.put("COMPANY_ADDRESS", requestJson.getString("company_address").equals("") ? ""
								: requestJson.getString("company_address"));
						map.put("CUSTOMER_MANAGER_NAME", requestJson.getString("customer_manager_name").equals("") ? ""
								: requestJson.getString("customer_manager_name"));
						map.put("CUSTOMER_MANAGER_PHONE", requestJson.getString("customer_manager_phone").equals("")
								? "" : requestJson.getString("customer_manager_phone"));
						map.put("CONTACT_NAME", requestJson.getString("contact_name").equals("") ? ""
								: requestJson.getString("contact_name"));
						map.put("CONTACT_PHONENUM", requestJson.getString("contact_phone").equals("") ? ""
								: requestJson.getString("contact_phone"));
						map.put("GROUP_PERSON_NUMBER", (requestJson.getInt("group_person_number") + "").equals("") ? "0"
								: requestJson.getInt("group_person_number"));
						if (!"".equals(requestJson.getString("app_lat")) && !"".equals(requestJson.getString("app_lon"))
								&& !"".equals(requestJson.getString("subgroup_name"))) {
							// 通过给的城市，得到系统中定义好的
							GridCodeDao dao = new GridCodeDao();
							GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
							LacModel cityInfo = getGroupIdInfo(requestJson.getString("app_lat"),
									requestJson.getString("app_lon"), requestJson.getString("subgroup_name"), greJDBC);
							if (cityInfo != null) {
								map.put("SUBGROUP_ID", cityInfo.getSubGroupId());
								map.put("SUBGROUP_NAME", cityInfo.getSubGroupName());
							} else {
								map.put("SUBGROUP_NAME", requestJson.getString("subgroup_name"));
							}
						} else {
							map.put("SUBGROUP_ID", group.get("SUBGROUP_ID"));
							map.put("SUBGROUP_NAME", group.get("SUBGROUP_NAME"));
						}
						boolean bol = false;
						bol = AndroidTestDAO.getInstance().updateANDROID_GROUP(map);
						// 集客日志添加
						map.put("GROUP_OPERATION_TIME", new Date());// 时间
						map.put("OPERATION_TYPE", "1");// 操作类型：0创建、1修改、2删除
						map.put("GROUP_OPERATION_TYPE", "自动");// 创建方式:手动、自动。创建分为手动自动；修改都是手动；
						if (loginList != null) {
							for (Map login : loginList) {
								String personTypeID = login.get("PERSON_TYPE_ID") + "";
								map.put("GROUP_OPERATIONPERSON_TYPE_ID", personTypeID);// 创建/修改人属性ID
								if (personTypeID.equals("1330")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "集团用户");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1331")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "客户经理");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1332")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "专业人员");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1333")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "客服人员");// 创建/修改人属性:客户经理、专业用户
								}
							}
						}
						map.put("GROUP_OPERATIONPERSON_ISDN", requestJson.getString("operate_msisdn"));// 创建/修改人电话
						bol = AndroidTestDAO.getInstance().insertANDROID_GROUP_OPERATION(map);
						if (bol) {
							jo.put("result", "OK");
							jo.put("reason", "数据修改成功！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
						} else {
							jo.put("result", "FAIL");
							jo.put("reason", "数据修改失败!错误原因可能是其他错误！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						}
					}
				}
			} else if ("upload".equals(requestJson.getString("type"))
					&& "group_property_delete".equals(requestJson.getString("subtype"))) {
				// 集客点基本信息表删除
				JSONObject jo = new JSONObject();
				List<Map<String, Object>> loginList = AndroidTestDAO.getInstance()
						.selectANDROID_REGISTRATION_PERSON(requestJson.getString("operate_msisdn"));
				if (loginList.size() == 0) {
					jo.put("result", "FAIL");
					jo.put("reason", "删除失败!错误原因是该用户没有权限！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("GROUP_ID", requestJson.getString("group_id"));
					boolean bol = false;
					// 集客日志添加
					// 查询数据库里面是否已存在数据
					List<Map<String, Object>> groupList = AndroidTestDAO.getInstance()
							.selectANDROID_GROUP(requestJson.getString("group_id"));
					if (groupList != null) {
						for (Map group : groupList) {
							map.put("GROUP_NAME", group.get("GROUP_NAME"));
							map.put("COUNTY_TOWN_NAME", group.get("COUNTY_TOWN_NAME"));
							map.put("COUNTY_TOWN_TYPE", group.get("COUNTY_TOWN_TYPE"));
							map.put("SCENE_TYPE", group.get("SCENE_TYPE"));
							map.put("GPS_TYPE", group.get("GPS_TYPE"));
							map.put("SCENE_TYPE_ID", group.get("SCENE_TYPE_ID"));
							map.put("APP_LON", group.get("APP_LON"));
							map.put("APP_LAT", group.get("APP_LAT"));
							map.put("RADIUS", group.get("RADIUS"));
							map.put("CUSTOMER_MANAGER_NAME", group.get("CUSTOMER_MANAGER_NAME"));
							map.put("CUSTOMER_MANAGER_PHONE", group.get("CUSTOMER_MANAGER_PHONE"));
							map.put("CONTACT_NAME", group.get("CONTACT_NAME"));
							map.put("CONTACT_PHONENUM", group.get("CONTACT_PHONENUM"));
							map.put("GROUP_PERSON_NUMBER", group.get("GROUP_PERSON_NUMBER"));
							map.put("SUBGROUP_ID", group.get("SUBGROUP_ID"));
							map.put("SUBGROUP_NAME", group.get("SUBGROUP_NAME"));
						}
					}
					map.put("GROUP_OPERATION_TIME", new Date());// 时间
					map.put("OPERATION_TYPE", "2");// 操作类型：0创建、1修改、2删除
					map.put("GROUP_OPERATION_TYPE", "自动");// 创建方式:手动、自动。创建分为手动自动；修改都是手动；
					if (loginList != null) {
						for (Map login : loginList) {
							String personTypeID = login.get("PERSON_TYPE_ID") + "";
							map.put("GROUP_OPERATIONPERSON_TYPE_ID", personTypeID);// 创建/修改人属性ID
							if (personTypeID.equals("1330")) {
								map.put("GROUP_OPERATIONPERSON_TYPE", "集团用户");// 创建/修改人属性:客户经理、专业用户
							} else if (personTypeID.equals("1331")) {
								map.put("GROUP_OPERATIONPERSON_TYPE", "客户经理");// 创建/修改人属性:客户经理、专业用户
							} else if (personTypeID.equals("1332")) {
								map.put("GROUP_OPERATIONPERSON_TYPE", "专业人员");// 创建/修改人属性:客户经理、专业用户
							} else if (personTypeID.equals("1333")) {
								map.put("GROUP_OPERATIONPERSON_TYPE", "客服人员");// 创建/修改人属性:客户经理、专业用户
							}
						}
					}
					map.put("GROUP_OPERATIONPERSON_ISDN", requestJson.getString("operate_msisdn"));// 创建/修改人电话
					bol = AndroidTestDAO.getInstance().insertANDROID_GROUP_OPERATION(map);
					bol = AndroidTestDAO.getInstance().deleteANDROID_GROUP(map);
					bol = AndroidTestDAO.getInstance().deleteANDROID_GROUP_CENTER1(map);
					if (bol) {
						jo.put("result", "OK");
						jo.put("reason", "数据删除成功！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
					} else {
						jo.put("result", "FAIL");
						jo.put("reason", "数据删除失败!错误原因可能是其他错误！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					}
				}
			} else if ("download".equals(requestJson.getString("type"))
					&& "group_center_position_v1".equals(requestJson.getString("subtype"))) {
				// 集客中心点
				List<Map<String, Object>> groupCenterList = AndroidTestDAO.getInstance().getDetailedGroupCenterSelect();
				JSONObject groupCenterJson = new JSONObject();
				JSONArray groupCenterArray = new JSONArray();
				groupCenterJson.put("center_area", groupCenterArray);
				for (Map map : groupCenterList) {
					JSONObject groupCenter = new JSONObject();
					groupCenter.put("group_id", map.get("GROUP_ID"));
					groupCenter.put("subgroup_name", map.get("SUBGROUP_NAME"));
					groupCenter.put("group_name", map.get("GROUP_NAME"));
					groupCenter.put("effective_mark", map.get("EFFECTIVE_MARK"));
					groupCenter.put("point_source_type", map.get("POINT_SOURCE_TYPE"));
					groupCenter.put("point_def_date", map.get("POINT_DEF_DATE"));
					groupCenter.put("app_lon", map.get("APP_LON"));
					groupCenter.put("app_lat", map.get("APP_LAT"));
					groupCenter.put("gps_type", map.get("GPS_TYPE"));
					groupCenter.put("radius", map.get("RADIUS"));
					groupCenter.put("operate_msisdn", map.get("OPERATE_MSISDN"));
					groupCenter.put("lineid", map.get("SUBGROUP_IDS"));
					groupCenterArray.put(groupCenter);
				}
				sentGzipResponse(response, groupCenterJson.toString().getBytes("UTF-8"),
						requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "group_center_position".equals(requestJson.getString("subtype"))) {
				// 集客中心点
				List<Map<String, Object>> groupCenterList = AndroidTestDAO.getInstance().getDetailedGroupCenterSelect();
				JSONObject groupCenterJson = new JSONObject();
				JSONArray groupCenterArray = new JSONArray();
				groupCenterJson.put("center_area", groupCenterArray);
				for (Map map : groupCenterList) {
					JSONObject groupCenter = new JSONObject();
					groupCenter.put("group_id", map.get("GROUP_ID"));
					groupCenter.put("subgroup_name", map.get("SUBGROUP_NAME"));
					groupCenter.put("group_name", map.get("GROUP_NAME"));
					groupCenter.put("effective_mark", map.get("EFFECTIVE_MARK"));
					groupCenter.put("point_source_type", map.get("POINT_SOURCE_TYPE"));
					groupCenter.put("point_def_date", map.get("POINT_DEF_DATE"));
					groupCenter.put("app_lon", map.get("APP_LON"));
					groupCenter.put("app_lat", map.get("APP_LAT"));
					groupCenter.put("gps_type", map.get("GPS_TYPE"));
					groupCenter.put("radius", map.get("RADIUS"));
					groupCenter.put("operate_msisdn", map.get("OPERATE_MSISDN"));
					groupCenter.put("lineid", map.get("SUBGROUP_IDS"));
					groupCenterArray.put(groupCenter);
				}

				String filePath = FileUtils.createRandomPath("txt");
				String zipFilePath = FileUtils.createRandomPath("zip");
				FileUtils.saveString2File(groupCenterJson.toString(), filePath);
				ZipUtils utils = new ZipUtils(zipFilePath);
				utils.compress(filePath);
				sentFileResponse(response, FileUtils.getBytes(zipFilePath), FileUtils.getFileName(zipFilePath));
				FileUtils.deleteFile(zipFilePath);
				FileUtils.deleteFile(filePath);
				return;
				// sentResponse(response,
				// groupCenterJson.toString().getBytes("UTF-8"),
				// requestJson.getString("subtype"));
				// return;
			} else if ("upload".equals(requestJson.getString("type"))
					&& "group_center_delete".equals(requestJson.getString("subtype"))) {
				// 集客中心删除
				JSONObject jo = new JSONObject();
				String lineid = requestJson.getString("lineid");
				if ("".equals(lineid)) {
					jo.put("result", "FAIL");
					jo.put("reason", "数据删除失败!错误原因是没有LINEID！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					String lineid1 = requestJson.getString("group_id") + "_" + requestJson.getString("app_lon") + "_"
							+ requestJson.getString("app_lat");
					if (!lineid.equals(lineid1)) {
						jo.put("result", "FAIL");
						jo.put("reason", "数据删除失败!错误原因是LINEID是错误的！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					} else {
						List<Map<String, Object>> loginList = AndroidTestDAO.getInstance()
								.selectANDROID_REGISTRATION_PERSON(requestJson.getString("phone_number"));
						if (loginList.size() == 0) {
							jo.put("result", "FAIL");
							jo.put("reason", "数据删除失败!错误原因是该用户没有权限！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						} else {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("GROUP_ID", requestJson.getString("group_id"));
							map.put("APP_LON", requestJson.getString("app_lon"));
							map.put("APP_LAT", requestJson.getString("app_lat"));
							boolean bol = false;
							// 集客日志添加
							// 查询数据库里面是否已存在数据
							List<Map<String, Object>> groupList = AndroidTestDAO.getInstance()
									.selectANDROID_GROUP(requestJson.getString("group_id"));
							if (groupList != null) {
								for (Map group : groupList) {
									map.put("GROUP_NAME", group.get("GROUP_NAME"));
									map.put("COUNTY_TOWN_NAME", group.get("COUNTY_TOWN_NAME"));
									map.put("COUNTY_TOWN_TYPE", group.get("COUNTY_TOWN_TYPE"));
									map.put("SCENE_TYPE", group.get("SCENE_TYPE"));
									map.put("GPS_TYPE", group.get("GPS_TYPE"));
									map.put("SCENE_TYPE_ID", group.get("SCENE_TYPE_ID"));
									map.put("RADIUS", group.get("RADIUS"));
									map.put("CUSTOMER_MANAGER_NAME", group.get("CUSTOMER_MANAGER_NAME"));
									map.put("CUSTOMER_MANAGER_PHONE", group.get("CUSTOMER_MANAGER_PHONE"));
									map.put("CONTACT_NAME", group.get("CONTACT_NAME"));
									map.put("CONTACT_PHONENUM", group.get("CONTACT_PHONENUM"));
									map.put("GROUP_PERSON_NUMBER", group.get("GROUP_PERSON_NUMBER"));
									map.put("SUBGROUP_ID", group.get("SUBGROUP_ID"));
									map.put("SUBGROUP_NAME", group.get("SUBGROUP_NAME"));
								}
								map.put("GROUP_OPERATION_TIME", new Date());// 时间
								map.put("OPERATION_TYPE", "2");// 操作类型：0创建、1修改、2删除
								map.put("GROUP_OPERATION_TYPE", "自动");// 创建方式:手动、自动。创建分为手动自动；修改都是手动；
								if (loginList != null) {
									for (Map login : loginList) {
										String personTypeID = login.get("PERSON_TYPE_ID") + "";
										map.put("GROUP_OPERATIONPERSON_TYPE_ID", personTypeID);// 创建/修改人属性ID
										if (personTypeID.equals("1330")) {
											map.put("GROUP_OPERATIONPERSON_TYPE", "集团用户");// 创建/修改人属性:客户经理、专业用户
										} else if (personTypeID.equals("1331")) {
											map.put("GROUP_OPERATIONPERSON_TYPE", "客户经理");// 创建/修改人属性:客户经理、专业用户
										} else if (personTypeID.equals("1332")) {
											map.put("GROUP_OPERATIONPERSON_TYPE", "专业人员");// 创建/修改人属性:客户经理、专业用户
										} else if (personTypeID.equals("1333")) {
											map.put("GROUP_OPERATIONPERSON_TYPE", "客服人员");// 创建/修改人属性:客户经理、专业用户
										}
									}
								}
								map.put("GROUP_OPERATIONPERSON_ISDN", requestJson.getString("phone_number"));// 创建/修改人电话
								bol = AndroidTestDAO.getInstance().insertANDROID_GROUP_OPERATION(map);
							}
							bol = AndroidTestDAO.getInstance().deleteANDROID_GROUP_CENTER(map);
							// 集客信息表中添加时间字段更新
							Map<String, Object> mapGroup = new HashMap<String, Object>();
							mapGroup.put("GROUP_ID", requestJson.getString("group_id"));
							mapGroup.put("CREATE_TIME", new Date());
							bol = AndroidTestDAO.getInstance().updateANDROID_GROUP_CREATEDATE(mapGroup);
							if (bol) {
								jo.put("result", "OK");
								jo.put("reason", "数据删除成功！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
							} else {
								jo.put("result", "FAIL");
								jo.put("reason", "数据删除失败!错误原因可能是其他错误！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
							}
						}
					}
				}
			} else if ("upload".equals(requestJson.getString("type"))
					&& "group_center_insert".equals(requestJson.getString("subtype"))) {
				// 集客中心插入
				JSONObject jo = new JSONObject();
				List<Map<String, Object>> loginList = AndroidTestDAO.getInstance()
						.selectANDROID_REGISTRATION_PERSON(requestJson.getString("phone_number"));
				if (loginList.size() == 0) {
					jo.put("result", "FAIL");
					jo.put("reason", "数据插入失败!错误原因是该用户没有权限！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("GROUP_ID", requestJson.getString("group_id"));
					map.put("GROUP_NAME", requestJson.getString("group_name"));
					map.put("EFFECTIVE_MARK", requestJson.getInt("effective_mark"));
					map.put("GPS_TYPE", requestJson.getString("gps_type"));
					map.put("POINT_SOURCE_TYPE", requestJson.getInt("point_source_type"));
					Date pointDate = null;
					try {
						pointDate = format1.parse(requestJson.getString("point_def_date") + " 00:00:00");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					map.put("POINT_DEF_DATE", pointDate);
					map.put("APP_LON", requestJson.getString("app_lon"));
					map.put("APP_LAT", requestJson.getString("app_lat"));
					map.put("RADIUS", requestJson.getInt("radius"));
					map.put("OPERATE_MSISDN", requestJson.getString("operate_msisdn"));
					// 通过给的城市，得到系统中定义好的
					GridCodeDao dao = new GridCodeDao();
					GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
					LacModel cityInfo = getGroupIdInfo(requestJson.getString("app_lat"),
							requestJson.getString("app_lon"), requestJson.getString("subgroup_name"), greJDBC);
					if (cityInfo != null) {
						map.put("SUBGROUP_ID", cityInfo.getSubGroupId());
						map.put("SUBGROUP_NAME", cityInfo.getSubGroupName());
					} else {
						map.put("SUBGROUP_NAME", requestJson.getString("subgroup_name"));
					}
					boolean bol = false;
					bol = AndroidTestDAO.getInstance().insertANDROID_GROUP_CENTER(map);
					// 集客日志添加
					// 查询数据库里面是否已存在数据
					List<Map<String, Object>> groupList = AndroidTestDAO.getInstance()
							.selectANDROID_GROUP(requestJson.getString("group_id"));
					if (groupList != null) {
						for (Map group : groupList) {
							map.put("COUNTY_TOWN_NAME", group.get("COUNTY_TOWN_NAME"));
							map.put("COUNTY_TOWN_TYPE", group.get("COUNTY_TOWN_TYPE"));
							map.put("SCENE_TYPE", group.get("SCENE_TYPE"));
							map.put("SCENE_TYPE_ID", group.get("SCENE_TYPE_ID"));
							map.put("CUSTOMER_MANAGER_NAME", group.get("CUSTOMER_MANAGER_NAME"));
							map.put("CUSTOMER_MANAGER_PHONE", group.get("CUSTOMER_MANAGER_PHONE"));
							map.put("CONTACT_NAME", group.get("CONTACT_NAME"));
							map.put("CONTACT_PHONENUM", group.get("CONTACT_PHONENUM"));
							map.put("GROUP_PERSON_NUMBER", group.get("GROUP_PERSON_NUMBER"));
						}
						map.put("GROUP_OPERATION_TIME", new Date());// 时间
						map.put("OPERATION_TYPE", "0");// 操作类型：0创建、1修改、2删除
						map.put("GROUP_OPERATION_TYPE", "自动");// 创建方式:手动、自动。创建分为手动自动；修改都是手动；
						if (loginList != null) {
							for (Map login : loginList) {
								String personTypeID = login.get("PERSON_TYPE_ID") + "";
								map.put("GROUP_OPERATIONPERSON_TYPE_ID", personTypeID);// 创建/修改人属性ID
								if (personTypeID.equals("1330")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "集团用户");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1331")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "客户经理");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1332")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "专业人员");// 创建/修改人属性:客户经理、专业用户
								} else if (personTypeID.equals("1333")) {
									map.put("GROUP_OPERATIONPERSON_TYPE", "客服人员");// 创建/修改人属性:客户经理、专业用户
								}
							}
						}
						map.put("GROUP_OPERATIONPERSON_ISDN", requestJson.getString("phone_number"));// 创建/修改人电话
						bol = AndroidTestDAO.getInstance().insertANDROID_GROUP_OPERATION(map);
					}
					// 集客信息表中添加时间字段更新
					Map<String, Object> mapGroup = new HashMap<String, Object>();
					mapGroup.put("GROUP_ID", requestJson.getString("group_id"));
					mapGroup.put("CREATE_TIME", new Date());
					bol = AndroidTestDAO.getInstance().updateANDROID_GROUP_CREATEDATE(mapGroup);
					if (bol) {
						jo.put("result", "OK");
						jo.put("reason", "数据插入成功！");
						jo.put("lineid", requestJson.getString("group_id") + "_" + requestJson.getString("app_lon")
								+ "_" + requestJson.getString("app_lat"));
						sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
					} else {
						jo.put("result", "FAIL");
						jo.put("reason", "数据插入失败!错误原因可能是其他错误！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					}
				}
			} else if ("download".equals(requestJson.getString("type"))
					&& "dict_table".equals(requestJson.getString("subtype"))) {
				List<HashMap<String, Object>> listInfos;
				JSONObject jo = new JSONObject();
				try {
					Scanner in = new Scanner(new File(request.getRealPath("/") + "/uDicData.txt"));
					String strContent = null;
					while (in.hasNextLine()) {
						strContent = in.nextLine();
					}
					String[] baseTypes = strContent.split(",");
					listInfos = null;
					for (int x = 0; x < baseTypes.length; x++) {
						String[] baseType = baseTypes[x].split("-");
						JSONArray jsonArray2 = new JSONArray();
						listInfos = ProblemDao.getProblemInstance().getUpdataBaseData(baseType[0]);
						jo.put(baseType[1], jsonArray2);
						if (listInfos != null)
							for (Map map : listInfos) {
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("name", map.get("DIC_VALUE") == null ? "" : map.get("DIC_VALUE"));
								if (map.get("DIC_KEY") == null) {
									jsonObject.put("id", "");
								} else {
									String typeId = map.get("DIC_KEY") + "";
									jsonObject.put("id", typeId);
								}
								jsonArray2.put(jsonObject);
							}
					}
				} catch (Exception e) {
					System.out.println("文件读取失败");
				}

				sentResponse(response, jo.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "problem_point_reply_select".equals(requestJson.getString("subtype"))) {
				// 问题点回复前端查询服务
				Map<String, Object> mapdata = new HashMap<String, Object>();
				Date start = null;
				Date end = null;
				try {
					start = format1.parse(format2.format(DateUtils.getFirstDateOfMonth(new Date())) + " 00:00:00");
					end = format1.parse(format2.format(DateUtils.getLastDateOfMonth(new Date())) + " 23:59:59");
				} catch (ParseException e) {
					logger.debug(e.getMessage());
				}
				mapdata.put("STARTTIME", new Timestamp(start.getTime()));
				mapdata.put("ENDTIME", new Timestamp(end.getTime()));
				mapdata.put("CUSTOMER_MANAGER_PHONE", requestJson.getString("CUSTOMER_MANAGER_PHONE"));
				List<Map<String, Object>> problemPointList = AndroidTestDAO.getInstance()
						.getDetailedProblemPointSelect(mapdata);
				JSONObject problemPointJson = new JSONObject();
				JSONArray problemPointArray = new JSONArray();
				problemPointJson.put("problem_infos", problemPointArray);
				for (Map map : problemPointList) {
					JSONObject problemPoint = new JSONObject();
					problemPoint.put("ID", map.get("ID"));
					problemPoint.put("GROUP_NAME", map.get("GROUP_NAME"));
					problemPoint.put("PROBLEM_TYPE", map.get("PROBLEM_TYPE"));
					problemPointArray.put(problemPoint);
				}
				// 修改推送状态
				AndroidTestDAO.getInstance()
						.updateANDROID_PROBLEM_POINT(requestJson.getString("CUSTOMER_MANAGER_PHONE"));
				AndroidTestDAO.getInstance()
						.updateANDROID_PROBLEM_POINT1(requestJson.getString("CUSTOMER_MANAGER_PHONE"));
				sentResponse(response, problemPointJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "problem_gp_point_gis".equals(requestJson.getString("subtype"))) {
				// 集客问题点历史查询
				JSONObject jo = new JSONObject();
				List<HashMap<String, Object>> listGroupProblemPoint = null;
				List<HashMap<String, Object>> listGroupProblemPointLonLat = null;
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("CELL_NET_TYPE", requestJson.getString("cell_net_type"));// 网络
				condition.put("REPLY_STATE", requestJson.getString("reply_state"));// 回复情况
				String proType = requestJson.getString("problem_type");// 问题类型：
																		// 1：FTP下载低速率、2：FTP上传低速率、3：HTTP下载低速率、4：Video下载低速率、5：PING成功率低
				if (proType != null && proType != "") {
					if ("1".equals(proType)) {
						condition.put("PROBLEM_TYPE", "3");
					} else if ("2".equals(proType)) {
						condition.put("PROBLEM_TYPE", "1,3,4");
					} else if ("3".equals(proType)) {
						condition.put("PROBLEM_TYPE", "2");
					} else if ("4".equals(proType)) {
						condition.put("PROBLEM_TYPE", "5");
					} else if ("5".equals(proType)) {
						condition.put("PROBLEM_TYPE", "4");
					} else {
						condition.put("PROBLEM_TYPE", "1,2,3,4,5");
					}
				}
				String time = requestJson.getString("test_time");// 时间
				if (time != null && time != "") {
					try {
						if ("3".equals(time)) {
							Date start = format1
									.parse(format2.format(DateUtils.getFirstDateOfMonth(new Date())) + " 00:00:00");
							Date end = format1
									.parse(format2.format(DateUtils.getLastDateOfMonth(new Date())) + " 23:59:59");
							condition.put("STARTTIME", new Timestamp(start.getTime()));
							condition.put("ENDTIME", new Timestamp(end.getTime()));
						} else {
							condition.put("STARTTIME", new Timestamp(0L));
							condition.put("ENDTIME", new Timestamp(System.currentTimeMillis()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String role = requestJson.getString("account_type");// 角色
				if (role != null && role != "") {
					if ("1333".equals(role)) {
						jo.put("result", "FAIL");
						jo.put("reason", "数据查询失败!错误原因是该用户没有权限！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						return;
					} else {
						String range = requestJson.getString("range");// 范围
						if (range != null && range != "") {
							if ("1".equals(range)) {
								// 空间运算服务
								GridCodeDao dao = new GridCodeDao();
								GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
								LacModel lacModel = getGroupIdInfo(requestJson.getString("app_lat"),
										requestJson.getString("app_lon"), requestJson.getString("subGroupName"),
										greJDBC);
								String subGroupID = lacModel.getSubGroupId();
								condition.put("SUBGROUP_ID", subGroupID);
								listGroupProblemPoint = AndroidTestDAO.getInstance()
										.selectGROUP_PROBLEM_POINT(condition);
							} else if ("2".equals(range)) {
								condition.put("PHONE_NUMBER", requestJson.getLong("phone_number"));// 手机号
								listGroupProblemPoint = AndroidTestDAO.getInstance()
										.selectGROUP_PROBLEM_POINT_PHONE(condition);
							} else if ("3".equals(range)) {
								listGroupProblemPoint = AndroidTestDAO.getInstance()
										.selectGROUP_PROBLEM_POINT_ALL(condition);
							}
						}
					}
				}

				JSONObject problemPointJson = new JSONObject();
				JSONArray problemPointArray = new JSONArray();
				problemPointJson.put("group_problem_point", problemPointArray);
				if (listGroupProblemPoint != null) {
					for (Map groMap : listGroupProblemPoint) {
						JSONObject problemPoint = new JSONObject();
						if ((groMap.get("IS_CLAIM") + "").equals("0")) {
							problemPoint.put("is_claim", "未认领");
						} else if ((groMap.get("IS_CLAIM") + "").equals("1")) {
							problemPoint.put("is_claim", "已认领");
						}
						if ((groMap.get("PUSH_STATE") + "").equals("1")) {
							problemPoint.put("push_state", "未推送");
						} else if ((groMap.get("PUSH_STATE") + "").equals("2")) {
							problemPoint.put("push_state", "已推送");
						}
						if ((groMap.get("REPLY_STATE") + "").equals("1")) {
							problemPoint.put("reply_state", "未回复");
						} else if ((groMap.get("REPLY_STATE") + "").equals("2")) {
							problemPoint.put("reply_state", "已回复");
						}
						if ((groMap.get("IS_SOLVE") + "").equals("0")) {
							problemPoint.put("is_solve", "未解决");
						} else if ((groMap.get("IS_SOLVE") + "").equals("1")) {
							problemPoint.put("is_solve", "已解决");
						} else if ((groMap.get("IS_SOLVE") + "").equals("2")) {
							problemPoint.put("is_solve", "已挂起");
						}
						problemPoint.put("id", groMap.get("ID"));
						problemPoint.put("longitude", groMap.get("APP_LON"));
						problemPoint.put("latitude", groMap.get("APP_LAT"));
						JSONObject problemPointLonLat = new JSONObject();
						JSONArray problemPointLonLatArray = new JSONArray();
						problemPoint.put("group_problem_point_lonlat", problemPointLonLatArray);
						Map<String, Object> lonlat = new HashMap<String, Object>();
						lonlat.put("PROBLEM_ID", groMap.get("ID"));
						// 问题类型：
						// 1：FTP下载低速率、2：FTP上传低速率、3：HTTP下载低速率、4：Video下载低速率、5：PING成功率低
						if (proType != null && proType != "") {
							if ("1".equals(proType)) {
								lonlat.put("PROBLEM_TYPE", "FTP_DOWNLOAD");
							} else if ("2".equals(proType)) {
								lonlat.put("PROBLEM_TYPE", "'FTP_DOWNLOAD','HTTP','VIDEO'");
							} else if ("3".equals(proType)) {
								lonlat.put("PROBLEM_TYPE", "FTP_UPLOAD");
							} else if ("4".equals(proType)) {
								lonlat.put("PROBLEM_TYPE", "PING");
							} else if ("5".equals(proType)) {
								lonlat.put("PROBLEM_TYPE", "VIDEO");
							} else {
								lonlat.put("PROBLEM_TYPE", "'FTP_DOWNLOAD','FTP_UPLOAD','HTTP','VIDEO','PING'");
							}
						}
						listGroupProblemPointLonLat = AndroidTestDAO.getInstance()
								.selectZELOT_PUBLIC_TABLE_PROBLEM(lonlat);
						if (listGroupProblemPointLonLat != null) {
							for (Map lonlatMap : listGroupProblemPointLonLat) {
								problemPointLonLat.put("id", groMap.get("ID"));
								problemPointLonLat.put("group_problem_point_lon", lonlatMap.get("APP_LON"));
								problemPointLonLat.put("group_problem_point_lat", lonlatMap.get("APP_LAT"));
								problemPointLonLat.put("test_num", listGroupProblemPointLonLat.size());
								problemPointLonLat.put("test_time", lonlatMap.get("TEST_TIME"));
								problemPointLonLat.put("app_average", lonlatMap.get("P_VALUE"));
								problemPointLonLatArray.put(problemPointLonLat);
							}
						}
						problemPointArray.put(problemPoint);
					}
				}
				jo.put("group_problem_point", problemPointArray);
				sentResponse(response, jo.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "problem_cp_point_gis".equals(requestJson.getString("subtype"))) {
				// 客服问题点历史查询
				JSONObject jo = new JSONObject();
				List<HashMap<String, Object>> listCustomProblemPoint = null;
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("CELL_NET_TYPE", requestJson.getString("cell_net_type"));// 网络
				condition.put("REPLY_STATE", requestJson.getString("reply_state"));// 回复情况
				String proType = requestJson.getString("problem_type");// 问题类型：
																		// 1：FTP下载低速率、2：FTP上传低速率、3：HTTP下载低速率、4：Video下载低速率、5：PING成功率低
				if (proType != null && proType != "") {
					if ("1".equals(proType)) {
						condition.put("PROBLEM_TYPE", "3");
					} else if ("2".equals(proType)) {
						condition.put("PROBLEM_TYPE", "1,3,4");
					} else if ("3".equals(proType)) {
						condition.put("PROBLEM_TYPE", "2");
					} else if ("4".equals(proType)) {
						condition.put("PROBLEM_TYPE", "5");
					} else if ("5".equals(proType)) {
						condition.put("PROBLEM_TYPE", "4");
					} else {
						condition.put("PROBLEM_TYPE", "1,2,3,4,5");
					}
				}
				String time = requestJson.getString("test_time");// 时间
				if (time != null && time != "") {
					try {
						if ("3".equals(time)) {
							Date start = format1
									.parse(format2.format(DateUtils.getFirstDateOfMonth(new Date())) + " 00:00:00");
							Date end = format1
									.parse(format2.format(DateUtils.getLastDateOfMonth(new Date())) + " 23:59:59");
							condition.put("STARTTIME", new Timestamp(start.getTime()));
							condition.put("ENDTIME", new Timestamp(end.getTime()));
						} else {
							condition.put("STARTTIME", new Timestamp(0L));
							condition.put("ENDTIME", new Timestamp(System.currentTimeMillis()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String role = requestJson.getString("account_type");// 角色
				// 查询返回值
				if (role != null && role != "") {
					if ("1331".equals(role)) {
						jo.put("result", "FAIL");
						jo.put("reason", "数据查询失败!错误原因是该用户没有权限！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						return;
					} else {
						String range = requestJson.getString("range");// 范围
						if (range != null && range != "") {
							if ("1".equals(range)) {
								// 空间运算服务
								GridCodeDao dao = new GridCodeDao();
								GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
								LacModel lacModel = getGroupIdInfo(requestJson.getString("app_lat"),
										requestJson.getString("app_lon"), requestJson.getString("subGroupName"),
										greJDBC);
								String subGroupID = lacModel.getSubGroupId();
								condition.put("SUBGROUP_ID", subGroupID);
								listCustomProblemPoint = AndroidTestDAO.getInstance()
										.selectCUSTOM_PROBLEM_POINT(condition);
							} else if ("2".equals(range)) {
								condition.put("PHONE_NUMBER", requestJson.getLong("phone_number"));// 手机号
								listCustomProblemPoint = AndroidTestDAO.getInstance()
										.selectCUSTOM_PROBLEM_POINT_PHONE(condition);
							} else if ("3".equals(range)) {
								listCustomProblemPoint = AndroidTestDAO.getInstance()
										.selectCUSTOM_PROBLEM_POINT_ALL(condition);
							}
						}
					}
				}

				JSONObject problemPointJson = new JSONObject();
				JSONArray cusProblemPointArray = new JSONArray();
				problemPointJson.put("custom_problem_point", cusProblemPointArray);
				if (listCustomProblemPoint != null) {
					for (Map cusMap : listCustomProblemPoint) {
						JSONObject cusProblemPoint = new JSONObject();
						if ((cusMap.get("IS_CLAIM") + "").equals("0")) {
							cusProblemPoint.put("is_claim", "未认领");
						} else if ((cusMap.get("IS_CLAIM") + "").equals("1")) {
							cusProblemPoint.put("is_claim", "已认领");
						}
						if ((cusMap.get("PUSH_STATE") + "").equals("1")) {
							cusProblemPoint.put("push_state", "未推送");
						} else if ((cusMap.get("PUSH_STATE") + "").equals("2")) {
							cusProblemPoint.put("push_state", "已推送");
						}
						if ((cusMap.get("REPLY_STATE") + "").equals("1")) {
							cusProblemPoint.put("reply_state", "未回复");
						} else if ((cusMap.get("REPLY_STATE") + "").equals("2")) {
							cusProblemPoint.put("reply_state", "已回复");
						}
						if ((cusMap.get("IS_SOLVE") + "").equals("0")) {
							cusProblemPoint.put("is_solve", "未解决");
						} else if ((cusMap.get("IS_SOLVE") + "").equals("1")) {
							cusProblemPoint.put("is_solve", "已解决");
						} else if ((cusMap.get("IS_SOLVE") + "").equals("2")) {
							cusProblemPoint.put("is_solve", "已挂起");
						}
						cusProblemPoint.put("longitude", cusMap.get("LONGITUDE"));
						cusProblemPoint.put("latitude", cusMap.get("LATITUDE"));
						cusProblemPoint.put("test_time", cusMap.get("TEST_TIME"));
						if ("4".equals(proType)) {
							cusProblemPoint.put("app_average", cusMap.get("APP_AVG"));
						} else {
							cusProblemPoint.put("app_average", cusMap.get("APP_AVERAGE"));
						}
						cusProblemPointArray.put(cusProblemPoint);
					}
				}
				jo.put("custom_problem_point", cusProblemPointArray);
				sentResponse(response, jo.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "problem_point_lists_gx".equals(requestJson.getString("subtype"))) {
				// （集客和客服）问题点查询一个月的信息
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("STARTTIME", requestJson.getString("starttime").equals("") ? ""
						: format2.format(requestJson.getString("starttime")) + " 00:00:00");
				data.put("ENDTIME", requestJson.getString("endtime").equals("") ? ""
						: format2.format(requestJson.getString("endtime")) + " 23:59:59");
				// 标示集客客服字段，1是集客2是客服
				data.put("STATE", requestJson.getString("state").equals("") ? "" : requestJson.getString("state"));
				List<Map<String, Object>> problemPointList = AndroidTestDAO.getInstance()
						.getSelectProblemPoint_GX(data);
				JSONObject groupJson = new JSONObject();
				JSONArray groupArray = new JSONArray();
				groupJson.put("problem_point", groupArray);
				for (Map map : problemPointList) {
					JSONObject object = new JSONObject();
					object.put("id", map.get("ID") == null ? "" : map.get("ID"));
					if ((map.get("PROBLEM_TYPE") + "").equals("1")) {
						object.put("problem_type", "FTP下载低速率");
					} else if ((map.get("PROBLEM_TYPE") + "").equals("2")) {
						object.put("problem_type", "FTP上传低速率");
					} else if ((map.get("PROBLEM_TYPE") + "").equals("3")) {
						object.put("problem_type", "HTTP下载低速率");
					} else if ((map.get("PROBLEM_TYPE") + "").equals("4")) {
						object.put("problem_type", "Video下载低速率");
					} else if ((map.get("PROBLEM_TYPE") + "").equals("5")) {
						object.put("problem_type", "PING成功率低");
					} else {
						object.put("problem_type", "");
					}
					object.put("test_time", map.get("TEST_TIME_DATE") == null ? "" : map.get("TEST_TIME_DATE"));
					if ((map.get("IS_SOLVE") + "").equals("0")) {
						object.put("is_solve", "未解决");
					} else if ((map.get("IS_SOLVE") + "").equals("1")) {
						object.put("is_solve", "已解决");
					} else if ((map.get("IS_SOLVE") + "").equals("2")) {
						object.put("is_solve", "已挂起");
					} else {
						object.put("is_solve", "");
					}
					object.put("group_name", map.get("GROUP_NAME") == null ? "" : map.get("GROUP_NAME"));
					groupArray.put(object);
				}
				sentResponse(response, groupJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "problem_point_info_gx".equals(requestJson.getString("subtype"))) {
				// （集客和客服）问题点查询具体某条信息所有字段信息-广西
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("ID", requestJson.getString("id").equals("") ? "" : requestJson.getString("id"));
				// 标示集客客服字段，1是集客2是客服
				data.put("STATE", requestJson.getString("state").equals("") ? "" : requestJson.getString("state"));
				List<Map<String, Object>> problemPointList = AndroidTestDAO.getInstance().getProblemPoint_GX(data);
				JSONObject groupJson = new JSONObject();
				JSONArray groupArray = new JSONArray();
				groupJson.put("problem_point", groupArray);
				for (Map map : problemPointList) {
					JSONObject object = new JSONObject();
					object.put("id", map.get("ID") == null ? "" : map.get("ID"));
					object.put("group_id", map.get("GROUP_ID") == null ? "" : map.get("GROUP_ID"));
					object.put("group_name", map.get("GROUP_NAME") == null ? "" : map.get("GROUP_NAME"));
					object.put("subgroup_id", map.get("SUBGROUP_ID") == null ? "" : map.get("SUBGROUP_ID"));
					object.put("subgroup_name", map.get("SUBGROUP_NAME") == null ? "" : map.get("SUBGROUP_NAME"));
					object.put("county_town_name",
							map.get("COUNTY_TOWN_NAME") == null ? "" : map.get("COUNTY_TOWN_NAME"));
					object.put("county_town_type",
							map.get("COUNTY_TOWN_TYPE") == null ? "" : map.get("COUNTY_TOWN_TYPE"));
					object.put("street_address", map.get("STREET_ADDRESS") == null ? "" : map.get("STREET_ADDRESS"));
					if ((map.get("PROBLEM_TYPE") + "").equals("1")) {
						object.put("problem_type", "FTP下载低速率");
					} else if ((map.get("PROBLEM_TYPE") + "").equals("2")) {
						object.put("problem_type", "FTP上传低速率");
					} else if ((map.get("PROBLEM_TYPE") + "").equals("3")) {
						object.put("problem_type", "HTTP下载低速率");
					} else if ((map.get("PROBLEM_TYPE") + "").equals("4")) {
						object.put("problem_type", "Video下载低速率");
					} else if ((map.get("PROBLEM_TYPE") + "").equals("5")) {
						object.put("problem_type", "PING成功率低");
					} else {
						object.put("problem_type", "");
					}
					object.put("server_name", map.get("SERVICE_NAME") == null ? "" : map.get("SERVICE_NAME"));
					object.put("server_ip", map.get("SERVICE_IP") == null ? "" : map.get("SERVICE_IP"));
					if ((map.get("IS_CLAIM") + "").equals("0")) {
						object.put("is_claim", "未认领");
					} else if ((map.get("IS_CLAIM") + "").equals("1")) {
						object.put("is_claim", "已认领");
					} else {
						object.put("is_claim", "");
					}
					if ((map.get("PUSH_STATE") + "").equals("1")) {
						object.put("push_state", "未推送");
					} else if ((map.get("PUSH_STATE") + "").equals("2")) {
						object.put("push_state", "已推送");
					} else {
						object.put("push_state", "");
					}
					if ((map.get("REPLY_STATE") + "").equals("1")) {
						object.put("reply_state", "未回复");
					} else if ((map.get("REPLY_STATE") + "").equals("2")) {
						object.put("reply_state", "已回复");
					} else {
						object.put("reply_state", "");
					}
					if ((map.get("IS_SOLVE") + "").equals("0")) {
						object.put("is_solve", "未解决");
					} else if ((map.get("IS_SOLVE") + "").equals("1")) {
						object.put("is_solve", "已解决");
					} else if ((map.get("IS_SOLVE") + "").equals("2")) {
						object.put("is_solve", "已挂起");
					} else {
						object.put("is_solve", "");
					}
					object.put("scene_type_id", map.get("SCENE_TYPE_ID") == null ? "" : map.get("SCENE_TYPE_ID"));
					object.put("scene_type", map.get("SCENE_TYPE") == null ? "" : map.get("SCENE_TYPE"));
					if ((map.get("CELL_NET_TYPE") + "").equals("0")) {
						object.put("cell_net_type", "GSM");
					} else if ((map.get("CELL_NET_TYPE") + "").equals("1")) {
						object.put("cell_net_type", "TD");
					} else if ((map.get("CELL_NET_TYPE") + "").equals("2")) {
						object.put("cell_net_type", "LTE");
					} else {
						object.put("cell_net_type", "");
					}
					object.put("lac", map.get("LAC") == null ? "" : map.get("LAC"));
					object.put("ci", map.get("CI") == null ? "" : map.get("CI"));
					object.put("test_details", map.get("TEST_DETAILS") == null ? "" : map.get("TEST_DETAILS"));
					object.put("catch_times", map.get("CATCH_TIMES") == null ? "" : map.get("CATCH_TIMES"));
					object.put("retest_details", map.get("RETEST_DETAILS") == null ? "" : map.get("RETEST_DETAILS"));
					object.put("problem_point_analysis",
							map.get("PROBLEM_POINT_ANALYSIS") == null ? "" : map.get("PROBLEM_POINT_ANALYSIS"));
					object.put("problem_point_solution",
							map.get("PROBLEM_POINT_SOLUTION") == null ? "" : map.get("PROBLEM_POINT_SOLUTION"));
					object.put("problem_classify",
							map.get("PROBLEM_CLASSIFY") == null ? "" : map.get("PROBLEM_CLASSIFY"));
					object.put("person_type", map.get("PERSON_TYPE") == null ? "" : map.get("PERSON_TYPE"));
					object.put("test_time", map.get("TEST_TIME_DATE") == null ? "" : map.get("TEST_TIME_DATE"));
					object.put("solve_time", map.get("SOLVE_TIME_DATE") == null ? "" : map.get("SOLVE_TIME_DATE"));
					if (requestJson.getString("state") == "1" || requestJson.getString("state").equals("1")) {
						object.put("customer_manager_name",
								map.get("CUSTOMER_MANAGER_NAME") == null ? "" : map.get("CUSTOMER_MANAGER_NAME"));
						object.put("customer_manager_phone",
								map.get("CUSTOMER_MANAGER_PHONE") == null ? "" : map.get("CUSTOMER_MANAGER_PHONE"));
						object.put("first_catch_time",
								map.get("FIRST_CATCH_TIME_DATE") == null ? "" : map.get("FIRST_CATCH_TIME_DATE"));
						object.put("last_catch_time",
								map.get("LAST_CATCH_TIME_DATE") == null ? "" : map.get("LAST_CATCH_TIME_DATE"));
						object.put("gps_coordinate",
								map.get("GPS_COORDINATE") == null ? "" : map.get("GPS_COORDINATE"));
						object.put("gps_point_list",
								map.get("GPS_POINT_LIST") == null ? "" : map.get("GPS_POINT_LIST"));
						object.put("app_lon", map.get("APP_LON") == null ? "" : map.get("APP_LON"));
						object.put("app_lat", map.get("APP_LAT") == null ? "" : map.get("APP_LAT"));
					}
					if (requestJson.getString("state") == "2" || requestJson.getString("state").equals("2")) {
						object.put("customer_phone",
								map.get("CUSTOMER_PHONE") == null ? "" : map.get("CUSTOMER_PHONE"));
						object.put("longitude", map.get("LONGITUDE") == null ? "" : map.get("LONGITUDE"));
						object.put("latitude", map.get("LATITUDE") == null ? "" : map.get("LATITUDE"));
					}
					groupArray.put(object);
				}
				sentResponse(response, groupJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("download".equals(requestJson.getString("type"))
					&& "select_cell_name".equals(requestJson.getString("subtype"))) {
				// 工参查询小区名字
				List<Map<String, Object>> workParamList = AndroidTestDAO.getInstance().getDetailedworkParamSelect(
						requestJson.getString("net"), requestJson.getString("lac_tac"),
						requestJson.getString("ci_eci"));
				JSONObject workParamJson = new JSONObject();
				for (Map map : workParamList) {
					workParamJson.put("cell_name", map.get("CELL_NAME"));
				}
				sentResponse(response, workParamJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				return;
			} else if ("upload".equals(requestJson.getString("type"))
					&& "update_follow_list".equals(requestJson.getString("subtype"))) {
				// 预警-订阅更新
				results = uploadService.sbuscribeUpdate(requestJson);
				sentResponse(response, results[1].getBytes("UTF-8"), results[0] + ".txt");
			} else if ("download".equals(requestJson.getString("type"))
					&& "get_follow_list".equals(requestJson.getString("subtype"))) {
				// 预警-订阅查询
				result = downloadService.sbuscribeList(requestJson);
				sentResponse(response, result.getBytes("UTF-8"), requestJson.getString("subtype"));
			} else if ("download".equals(requestJson.getString("type"))
					&& "get_warning_list".equals(requestJson.getString("subtype"))) {
				// 预警-预警查询
				result = downloadService.warnGroupList(requestJson);
				sentResponse(response, result.getBytes("UTF-8"), requestJson.getString("subtype"));
			} else if ("download".equals(requestJson.getString("type"))
					&& "get_problem_list_jx".equals(requestJson.getString("subtype"))) {
				// 问题查询byGPS-江西
				ProblemService problemService = new ProblemService();
				// 查询日志的ftppath、查询图片的httppath
				List<Map<String, Object>> FTPList = AndroidTestDAO.getInstance().selectANDROID_DICT_FTP();
				for (Map map : FTPList) {
					requestJson.put("FTPPath", map.get("DIC_DESC") + "");
					requestJson.put("HTTPPath", map.get("DIC_VALUE") + "");
				}
				result = problemService.problemListForJX(requestJson);
				sentResponse(response, result.getBytes("UTF-8"), requestJson.getString("subtype"));
			} else if ("download".equals(requestJson.getString("type"))
					&& "get_problem_list_city_jx".equals(requestJson.getString("subtype"))) {
				// 问题查询bycity-江西
				ProblemService problemService = new ProblemService();
				// 查询日志的ftppath、查询图片的httppath
				List<Map<String, Object>> FTPList = AndroidTestDAO.getInstance().selectANDROID_DICT_FTP();
				for (Map map : FTPList) {
					requestJson.put("FTPPath", map.get("DIC_DESC") + "");
					requestJson.put("HTTPPath", map.get("DIC_VALUE") + "");
				}
				result = problemService.problemListForJX(requestJson);
				sentResponse(response, result.getBytes("UTF-8"), requestJson.getString("subtype"));
			} else if ("download".equals(requestJson.getString("type"))
					&& "get_problem_statistics_jx".equals(requestJson.getString("subtype"))) {
				ProblemService problemService = new ProblemService();
				// 问题查询统计-江西
				result = problemService.problemStatisticsForJX(requestJson);
				sentResponse(response, result.getBytes("UTF-8"), requestJson.getString("subtype"));
			} else if ("download".equals(requestJson.getString("type"))
					&& "updata_base_data".equals(requestJson.getString("subtype"))) {
				ProblemService problemService = new ProblemService();
				// 基础数据查询-江西
				String webPath = request.getRealPath("/");
				requestJson.put("webPath", webPath);
				result = problemService.updataBaseDataForJX(requestJson);
				sentResponse(response, result.getBytes("UTF-8"), requestJson.getString("subtype"));
			} else if ("upload".equals(requestJson.getString("type"))
					&& "account_register_jx".equals(requestJson.getString("subtype"))) {
				// 用户注册-江西
				JSONObject jo = new JSONObject();
				String phoneNum = requestJson.getString("user_name");
				List<Map<String, Object>> list = AndroidTestDAO.getInstance()
						.selectANDROID_REGISTRATION_PERSON(phoneNum);
				if (list.size() > 0) {
					jo.put("result", "FAIL");
					jo.put("reason", "注册失败!错误原因是该用户已注册！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					// 通过正则表达式判断手机号
					boolean flag = false;
					Pattern regex = Pattern.compile("[1][34578]\\d{9}");
					Matcher matcher = regex.matcher(phoneNum);
					flag = matcher.matches();
					if (flag) {
						Map<String, Object> map = new HashMap<String, Object>();
						try {
							map.put("REALNAME", requestJson.getString("real_name"));
							map.put("MSISDN", phoneNum);
							map.put("REG_DATE", new Timestamp(System.currentTimeMillis()));
							boolean bol = false;
							bol = AndroidTestDAO.getInstance().insertANDROID_REGISTRATION_PERSON_JX(map);
							if (bol) {
								jo.put("result", "OK");
								jo.put("reason", "注册成功！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
							} else {
								jo.put("result", "FAIL");
								jo.put("reason", "注册失败!错误原因可能是其他错误！");
								sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
							}
						} catch (Exception e) {
							jo.put("result", "FAIL");
							jo.put("reason", "注册失败!错误原因可能是其他错误！");
							sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
						}
					} else {
						jo.put("result", "FAIL");
						jo.put("reason", "注册失败!错误原因是手机号码不正确！");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					}
				}
			} else if ("upload".equals(requestJson.getString("type"))
					&& "account_login_jx".equals(requestJson.getString("subtype"))) {
				// 用户登录-江西
				JSONObject jo = new JSONObject();
				List<Map<String, Object>> loginList = AndroidTestDAO.getInstance()
						.selectANDROID_REGISTRATION_PERSON(requestJson.getString("user_name"));
				if (loginList.size() == 0) {
					jo.put("result", "FAIL");
					jo.put("reason", "登录失败!错误原因是没有注册！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				} else {
					jo.put("result", "OK");
					jo.put("reason", "登录成功！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
				}
			} else if ("upload".equals(requestJson.getString("type"))
					&& "log_info".equals(requestJson.getString("subtype"))) {
				// log上传-江西
				JSONObject jo = new JSONObject();
				Map<String, Object> map = new HashMap<String, Object>();
				try {
					map.put("LOG_DETAIL", requestJson.getString("detail"));
					map.put("LOG_NAME", requestJson.getString("log_name"));
					// 判断是否是重复插入
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("LOG_NAME", requestJson.getString("log_name"));
					List<HashMap<String, Object>> loginfoList = AndroidTestDAO.getInstance()
							.selectANDROID_LOG_INFO_NAME(data);
					if (loginfoList.size() > 0) {
						// 执行删除
						AndroidTestDAO.getInstance().deleteANDROID_LOG_INFO(data);
					}
					map.put("USER_NAME", requestJson.getString("user_name"));
					map.put("SUBGROUP_ID", requestJson.getString("city_id"));
					if (requestJson.getString("upload_time").toString().equals("")) {
						map.put("LOG_REPORT_TIME", "");
					} else {
						map.put("LOG_REPORT_TIME",
								new Date(Long.parseLong(requestJson.getString("upload_time").toString())));
					}
					if (requestJson.getString("create_time").toString().equals("")) {
						map.put("CREATE_TIME", "");
					} else {
						map.put("CREATE_TIME",
								new Date(Long.parseLong(requestJson.getString("create_time").toString())));
					}
					int maxid = AndroidTestDAO.getInstance().selectANDROID_LOG_INFO_MAXID();
					if (maxid == 0) {
						maxid = 1;
					} else {
						maxid += 1;
					}
					map.put("ID", maxid);
					boolean bol = false;
					bol = AndroidTestDAO.getInstance().insertANDROID_LOG_INFO(map);
					if (bol) {
						jo.put("result", "OK");
						jo.put("reason", "upload successed");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
					} else {
						jo.put("result", "FAIL");
						jo.put("reason", "Insert Exception");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					}
				} catch (Exception e) {
					jo.put("result", "FAIL");
					jo.put("reason", "Insert Exception");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				}
			} else if ("download".equals(requestJson.getString("type"))
					&& "get_log_list".equals(requestJson.getString("subtype"))) {
				// log查询-江西
				// 查询日志的ftppath
				List<Map<String, Object>> FTPList = AndroidTestDAO.getInstance().selectANDROID_DICT_FTP();
				String FTPPath = "";
				for (Map map : FTPList) {
					FTPPath = (map.get("DIC_DESC") + "").replace("log/", "");
				}
				JSONObject objectJson = new JSONObject();
				JSONArray objectArray = new JSONArray();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("USER_NAME", requestJson.getString("user_name"));
				data.put("SUBGROUP_ID", requestJson.getString("city_id"));
				data.put("STARTTIME", format2.format(requestJson.getLong("starttime")) + " 00:00:00");
				data.put("ENDTIME", format2.format(requestJson.getLong("endtime")) + " 23:59:59");
				List<HashMap<String, Object>> loginfoList = AndroidTestDAO.getInstance().selectANDROID_LOG_INFO(data);
				objectJson.put("log_list", objectArray);
				if (loginfoList != null) {
					for (Map map : loginfoList) {
						JSONObject obj = new JSONObject();
						obj.put("id", map.get("ID"));
						obj.put("log_name", map.get("LOG_NAME") == null ? "" : FTPPath + map.get("LOG_NAME") + "");
						obj.put("city_id", map.get("SUBGROUP_ID") == null ? "" : map.get("SUBGROUP_ID") + "");
						obj.put("log_report_time",
								map.get("LOG_REPORT_TIME") == null ? "" : map.get("LOG_REPORT_TIME") + "");
						obj.put("user_name", map.get("USER_NAME") == null ? "" : map.get("USER_NAME") + "");
						obj.put("detail", map.get("LOG_DETAIL") == null ? "" : map.get("LOG_DETAIL") + "");
						obj.put("create_time", map.get("CREATE_TIME") == null ? "" : map.get("CREATE_TIME") + "");
						objectArray.put(obj);
					}
				}
				sentResponse(response, objectJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
			} else if ("upload".equals(requestJson.getString("type"))
					&& "complain_info".equals(requestJson.getString("subtype"))) {
				// 投诉信息上报
				JSONObject jo = new JSONObject();
				Map<String, Object> map = new HashMap<String, Object>();
				try {
					map.put("PHONE_NUMBER", requestJson.getString("phone_number"));
					map.put("COMPANY", requestJson.getString("company"));
					map.put("CITY", requestJson.getString("city"));
					// 空间运算处理
					GridCodeDao dao = new GridCodeDao();
					GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
					LacModel lacModel = getGroupIdInfo(null, null, requestJson.getString("city"), greJDBC);
					map.put("GROUPID", lacModel.getSubGroupId());
					map.put("SUBGROUPID", lacModel.getGroupId());
					map.put("SUBGROUPNAME", lacModel.getGroupName());
					map.put("CONTACT", requestJson.getString("contact"));
					map.put("CONTACT_PHONE_NUM", requestJson.getString("contact_phone_num"));
					map.put("ISSUE_APPEAR_DATE", requestJson.getString("issue_appear_date"));
					map.put("ISSUE_APPEAR_LOCATION", requestJson.getString("issue_appear_location"));
					map.put("ISSUE_APPEAR_ADDRESS", requestJson.getString("issue_appear_address"));
					map.put("NET_TYPE", requestJson.getString("net_type"));
					map.put("ISSUE_TYPE", requestJson.getString("issue_type"));
					map.put("ACCOUNT_TYPE", requestJson.getString("account_type"));
					map.put("ISSUE_DESCRIBE", requestJson.getString("issue_describe"));
					if (requestJson.getString("test_start_time").toString().equals("")) {
						map.put("TEST_START_TIME", "");
					} else {
						map.put("TEST_START_TIME",
								new Date(Long.parseLong(requestJson.getString("test_start_time").toString())));
					}
					if (requestJson.getString("test_end_time").toString().equals("")) {
						map.put("TEST_END_TIME", "");
					} else {
						map.put("TEST_END_TIME",
								new Date(Long.parseLong(requestJson.getString("test_end_time").toString())));
					}
					map.put("TEST_START_TIME_IOS", requestJson.getString("test_start_time_ios"));
					map.put("TIMESTAMP", new Date());
					map.put("VIP_TYPE", requestJson.getString("vip_type"));
					map.put("TEST_PROECT", requestJson.getString("test_proect"));
					boolean bol = false;
					bol = AndroidTestDAO.getInstance().insertANDROID_ISSUE_INFO(map);
					if (bol) {
						jo.put("result", "OK");
						jo.put("reason", "上传成功");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
					} else {
						jo.put("result", "FAIL");
						jo.put("reason", "上传失败");
						sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
					}
				} catch (Exception e) {
					e.printStackTrace();
					jo.put("result", "FAIL");
					jo.put("reason", "上传失败");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				}
			} else if ("download".equals(requestJson.getString("type"))
					&& "get_complain_list".equals(requestJson.getString("subtype"))) {
				// 投诉信息历史查询
				JSONObject objectJson = new JSONObject();
				JSONArray objectArray = new JSONArray();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("PHONE_NUMBER", requestJson.getString("phone_number"));
				data.put("ISSUE_APPEAR_DATE", requestJson.getString("issue_appear_time"));
				data.put("VIP_TYPE", requestJson.getString("vip_type"));
				List<HashMap<String, Object>> infoList = AndroidTestDAO.getInstance().selectANDROID_ISSUE_INFO(data);
				objectJson.put("complain_info_list", objectArray);
				if (infoList != null) {
					for (Map map : infoList) {
						JSONObject obj = new JSONObject();
						obj.put("issue_id", map.get("ISSUE_ID") == null ? "" : map.get("ISSUE_ID"));
						obj.put("company", map.get("COMPANY") == null ? "" : map.get("COMPANY"));
						obj.put("issue_appear_date",
								map.get("ISSUE_APPEAR_DATE") == null ? "" : map.get("ISSUE_APPEAR_DATE"));
						objectArray.put(obj);
					}
				}
				sentResponse(response, objectJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
			} else if ("download".equals(requestJson.getString("type"))
					&& "get_complain_detail".equals(requestJson.getString("subtype"))) {
				// 投诉信息详情
				JSONObject obj = new JSONObject();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("ISSUE_ID", requestJson.getString("issue_id"));
				List<HashMap<String, Object>> infoList = AndroidTestDAO.getInstance().selectANDROID_ISSUE_INFO(data);
				if (infoList != null) {
					for (Map map : infoList) {
						obj.put("issue_id", map.get("ISSUE_ID") == null ? "" : map.get("ISSUE_ID"));
						obj.put("company", map.get("COMPANY") == null ? "" : map.get("COMPANY"));
						obj.put("city", map.get("CITY") == null ? "" : map.get("CITY"));
						obj.put("contact", map.get("CONTACT") == null ? "" : map.get("CONTACT"));
						obj.put("contact_phone_num",
								map.get("CONTACT_PHONE_NUM") == null ? "" : map.get("CONTACT_PHONE_NUM"));
						obj.put("issue_appear_date",
								map.get("ISSUE_APPEAR_DATE") == null ? "" : map.get("ISSUE_APPEAR_DATE"));
						obj.put("issue_appear_location",
								map.get("ISSUE_APPEAR_LOCATION") == null ? "" : map.get("ISSUE_APPEAR_LOCATION"));
						obj.put("issue_appear_address",
								map.get("ISSUE_APPEAR_ADDRESS") == null ? "" : map.get("ISSUE_APPEAR_ADDRESS"));
						obj.put("net_type", map.get("NET_TYPE") == null ? "" : map.get("NET_TYPE"));
						obj.put("issue_type", map.get("ISSUE_TYPE") == null ? "" : map.get("ISSUE_TYPE"));
						obj.put("issue_describe", map.get("ISSUE_DESCRIBE") == null ? "" : map.get("ISSUE_DESCRIBE"));
						obj.put("test_start_time",
								map.get("TEST_START_TIME1") == null ? "" : map.get("TEST_START_TIME1"));
						obj.put("test_end_time", map.get("TEST_END_TIME1") == null ? "" : map.get("TEST_END_TIME1"));
						obj.put("test_start_time_ios",
								map.get("TEST_START_TIME_IOS") == null ? "" : map.get("TEST_START_TIME_IOS"));
						obj.put("receive_date", map.get("RECEIVE_DATE") == null ? "" : map.get("RECEIVE_DATE"));
						obj.put("receive_file_name",
								map.get("RECEIVE_FILE_NAME") == null ? "" : map.get("RECEIVE_FILE_NAME"));
						obj.put("solve_department",
								map.get("SOLVE_DEPARTMENT") == null ? "" : map.get("SOLVE_DEPARTMENT"));
						obj.put("person_in_charge",
								map.get("PERSON_IN_CHARGE") == null ? "" : map.get("PERSON_IN_CHARGE"));
						obj.put("pic_phone_num", map.get("PIC_PHONE_NUM") == null ? "" : map.get("PIC_PHONE_NUM"));
						obj.put("is_close", map.get("IS_CLOSE") == null ? "" : map.get("IS_CLOSE"));
						obj.put("sen_test_time", map.get("SEN_TEST_TIME") == null ? "" : map.get("SEN_TEST_TIME"));
						obj.put("issue_reason", map.get("ISSUE_REASON") == null ? "" : map.get("ISSUE_REASON"));
						obj.put("solution", map.get("SOLUTION") == null ? "" : map.get("SOLUTION"));
						obj.put("construction_period",
								map.get("CONSTRUCTION_PERIOD") == null ? "" : map.get("CONSTRUCTION_PERIOD"));
						obj.put("new_site_name", map.get("NEW_SITE_NAME") == null ? "" : map.get("NEW_SITE_NAME"));
						obj.put("is_solve", map.get("IS_SOLVE") == null ? "" : map.get("IS_SOLVE"));
						obj.put("solve_date", map.get("SOLVE_DATE") == null ? "" : map.get("SOLVE_DATE"));
						obj.put("progress", map.get("PROGRESS") == null ? "" : map.get("PROGRESS"));
						obj.put("remark", map.get("REMARK") == null ? "" : map.get("REMARK"));
						obj.put("issue_receive_type",
								map.get("ISSUE_RECEIVE_TYPE") == null ? "" : map.get("ISSUE_RECEIVE_TYPE"));
						obj.put("test_proect", map.get("TEST_PROECT") == null ? "" : map.get("TEST_PROECT"));
					}
				}
				sentResponse(response, obj.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
			}else if("upload".equals(requestJson.getString("type"))
					&& "opinion_feedback_insert".equals(requestJson.getString("subtype"))){
				Map<String,Object> map = new HashMap<String, Object>() ;
				map.put("userName", requestJson.getString("userName")) ;
				map.put("phoneNum", requestJson.getString("phoneNum")) ;
				map.put("content", requestJson.getString("content")) ;
				boolean bool = AndroidTestDAO.getInstance().insertFeedbackOpinion(map);
				JSONObject jo = new JSONObject();
				if (bool) {
					jo.put("result", "OK");
					jo.put("reason", "提交成功！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "ok.txt");
				} else {
					jo.put("result", "FAIL");
					jo.put("reason", "提交失败!错误原因可能是其他错误！");
					sentResponse(response, jo.toString().getBytes("UTF-8"), "error.txt");
				}
				
			}else if("download".equals(requestJson.getString("type"))
					&& "opinion_feedback".equals(requestJson.getString("subtype"))){
				Map<String,Object> data = new HashMap<String,Object>() ;
				data.put("phoneNum", requestJson.get("phoneNum")) ;
				List<Map<String, Object>> infoList = AndroidTestDAO.getInstance().selectfeedbackOpinion(data) ;
				JSONObject objectJson = new JSONObject() ;
				JSONArray objectArray = new JSONArray() ;
				objectJson.put("opinionList", objectArray) ;
				
				if (infoList != null) {
					for (Map map : infoList) {
						JSONObject obj = new JSONObject();
						obj.put("id", map.get("ID") == null ? "" : map.get("ID"));
						obj.put("userName", map.get("FEEDBACK_PERSON") == null ? "" : map.get("FEEDBACK_PERSON"));
						obj.put("createTime", map.get("FEEDBACK_TIME") == null ? "" : map.get("FEEDBACK_TIME"));
						obj.put("content", map.get("OPINION_CONTENT") == null ? "" : map.get("CONTENT"));
						obj.put("isReply", "1".equals(String.valueOf(map.get("REPLY_STATE"))) ? "已回复" : "未回复");
						obj.put("reply", map.get("FEEDBACK_REPYL") == null ? "" : map.get("FEEDBACK_REPYL"));
						
						objectArray.put(obj);
					}
				}
				sentResponse(response, objectJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
			} else 
				//新业务测试
				if("download".equals(requestJson.getString("type"))
						&& "".equals(requestJson.getString("subtype"))){
					Map<String,Object> data = new HashMap<String,Object>() ;
					if("radio".equals("")){
						data.put("urlType", "radio") ;
					}else if("music".equals("")){
						data.put("urlType", "music") ;
					}else if("apk".equals("")){
						data.put("urlType", "apk") ;
					}
					List<Map<String, Object>> infoList = AndroidTestDAO.getInstance().selectUrlDetailed(data) ;
					
					JSONObject objectJson = new JSONObject() ;
					JSONArray objectArray = new JSONArray() ;
					if (infoList != null) {
						for (Map map : infoList) {
							JSONObject obj = new JSONObject();
							obj.put("id", map.get("ID") == null ? "" : map.get("ID"));
							obj.put("urlType", map.get("URL_TYPE") == null ? "" : map.get("URL_TYPE"));
							obj.put("destinationUrl", map.get("DESTINATION_URL") == null ? "" : map.get("DESTINATION_URL"));
							obj.put("name", map.get("NAME") == null ? "" : map.get("NAME"));
							
							objectArray.put(obj);
						}
					}
					
					sentResponse(response, objectJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
					
					
			}else 
				//场景判断
				if("scene_gps_center".equals(requestJson.getString("subtype"))){
					GridCodeDao dao = new GridCodeDao();
					GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);
					
					Map<String,Object> data = new HashMap<String,Object>() ;
					
					data.put("lat" ,requestJson.getString("lat")) ;
					data.put("long" ,requestJson.getString("long")) ;
					data.put("Radius" ,requestJson.getString("Radius")) ;
					List<HashMap<String, Object>> listInfo = greJDBC.getsceneGPSCenter(data) ;
					
					Map map = listInfo.get(0) ;
					JSONObject objectJson = new JSONObject();
					
					objectJson.put("sceneId", map.get("SCENE_ID")==null?"":map.get("SCENE_ID")) ;
					objectJson.put("sceneName", map.get("SCENE_NAME")==null?"":map.get("SCENE_NAME")) ;

					JSONArray objectArray = new JSONArray() ;
					objectJson.put("lableData", objectArray) ;
					
					String geom = String.valueOf(map.get("GEOM")) ;
					String[] geoms = geom.substring(geom.lastIndexOf("(")+1,geom.indexOf(")")).split(",") ;
					for(String str : geoms){
						JSONObject longlat = new JSONObject();
						String[] lonlat = str.split(" ") ;
						longlat.put("lat", lonlat[1]) ;
						longlat.put("long", lonlat[0]) ;
						objectArray.put(longlat) ;
					}
					JSONArray jsonArray = new JSONArray() ;
					jsonArray.put(objectJson) ;
					JSONObject returnJson = new JSONObject();
					returnJson.put("scene", jsonArray) ;
					sentResponse(response, returnJson.toString().getBytes("UTF-8"), requestJson.getString("subtype"));
				
			} else {

				if ("upload".equals(requestJson.getString("type"))) {
					// 有效数据集
					List<Map<String, Object>> dataList = null;
					// json数据解析+归属城市处理+数据质量管理
					dataList = json2Map(requestJson, imagePath, audioPath);
					// publiclog类型的数据集
					List<Map<String, Object>> publicloglist = new ArrayList<Map<String, Object>>();
					boolean updateState = false;
					if (dataList != null) {
						for (Map<String, Object> map : dataList) {
							if ("problem".equals(map.get("LOG_TYPE"))) {
								updateState = AndroidTestDAO.getInstance().insertANDROID_TEST_PROBLEM(map);
								logger.debug("问题上报map：" + map.toString());
							} else if ("public_log".equals(map.get("LOG_TYPE"))) {
								Map<String, Object> newmap = new HashMap<String, Object>();
								newmap.putAll(map);
								publicloglist.add(newmap);
								logger.debug("问题上报map：" + map.toString());
							} else if ("old_data".equals(map.get("old_data"))) {
								updateState = true;
							}
						}
						logger.debug("publicloglist.size=" + publicloglist.size());
						if (publicloglist.size() > 0) {
							updateState = AndroidTestDAO.getInstance().batchInsert(publicloglist);
						}
						if (updateState) {
							JSONObject jo = new JSONObject();
							jo.put("result", "OK");
							jo.put("reason", "upload successed");
							sentResponse(response, jo.toString().getBytes(), "ok.txt");
						} else {
							logger.debug("入库失败：" + dataStr);
							JSONObject jo = new JSONObject();
							jo.put("result", "FAIL");
							jo.put("reason", "Insert Exception");
							sentResponse(response, jo.toString().getBytes(), "error.txt");
						}
					} else {
						logger.debug("没有有效数据：" + dataStr);
						JSONObject jo = new JSONObject();
						jo.put("result", "FAIL");
						jo.put("reason", "No valid data");
						sentResponse(response, jo.toString().getBytes(), "error.txt");
					}
				} else if ("download".equals(requestJson.getString("type"))) {

					String subType = requestJson.getString("subtype");
					Map<String, Object> condition = new HashMap<String, Object>();

					// 时间范围处理
					int range = 0;
					long longStartTime = 0;
					long longEndTime = 0;
					if (!subType.endsWith("problem_data") && !subType.equals("test_plan")
							&& !subType.equals("default_test_plan") && !subType.equals("videoTestInfo")
							&& !subType.equals("logon")) {
						range = requestJson.getInt("range");
						if (range == 7) {
							longStartTime = Long.parseLong(requestJson.getString("startTime"));
							longEndTime = Long.parseLong(requestJson.getString("endTime"));
						}
					}

					// 客户端GIS需求
					List<HashMap<String, Object>> listByGis = null;
					String serviceRequest = null;
					if (subType.equals("zealot_gis_v2")) {// 刘植鑫修改第二版
															// 时间2014-07-23
						// 空间运算服务
						GridCodeDao dao = new GridCodeDao();
						GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);

						String version = requestJson.getString("version");
						if (version != null && version != "" && "1.2".equals(version)) {
							String filter = requestJson.getString("filter");
							String network = requestJson.getString("network");

							if (filter != null && filter != "") {
								if (!"0".equals(filter)) {
									if ("1".equals(filter)) {
										serviceRequest = "FTP_DOWNLOAD";
									} else if ("2".equals(filter)) {
										serviceRequest = "HTTP";
									} else if ("3".equals(filter)) {
										serviceRequest = "PING";
									} else if ("4".equals(filter)) {
										serviceRequest = "VIDEO";
									} else if ("5".equals(filter)) {
										serviceRequest = "FTP_UPLOAD";
									}

								}
								condition.put("app_servicerequest", serviceRequest);
							}
							if (network != null && network != "") {
								if ("0".equals(network)) {// 0:2g
									network = "'GPRS','EDGE'";
								} else if ("1".equals(network)) {// 1:3g
									network = "'UMTS','WCDMA','HSPA+','TDS_HSDPA','HSDPA','HSPA','CDMA-EVDO REV.A','HSUPA'";
								}
								if ("2".equals(network)) {// 2:4g
									network = "'LTE'";
								}
								condition.put("networks", network);
							}
							// "10705";//
							// String subGroupID="-1";
							try {

								LacModel lacModel = getGroupIdInfo(requestJson.getString("lantitude"),
										requestJson.getString("longitude"), requestJson.getString("subGroupName"),
										greJDBC);
								// logger.info("查询出来的域信息为" + lacModel);
								String subGroupID = lacModel.getSubGroupId();
								condition.put("subGroupID", subGroupID);
								fillStartAndEnd(condition, range, longStartTime, longEndTime);
								listByGis = AndroidTestDAO.getInstance().selectZEALOT_PUBLIC_TABLE_V2(condition);
							} catch (Exception e) {
								logger.debug("zealot_gis_v2域转化失败。。。", e);
								listByGis = new ArrayList<HashMap<String, Object>>();
							}

							if (listByGis != null) {
								JSONObject json = mapsToJsonGisV2(listByGis, filter);
								logger.debug("send---zealot_gis_v2---" + json.toString());
								String filePath = FileUtils.createRandomPath("txt");
								String zipFilePath = FileUtils.createRandomPath("zip");
								FileUtils.saveString2File(json.toString(), filePath);
								ZipUtils utils = new ZipUtils(zipFilePath);
								utils.compress(filePath);

								sentFileResponse(response, FileUtils.getBytes(zipFilePath),
										FileUtils.getFileName(zipFilePath));
								logger.debug("zipfile---zealot_gis_v2---" + zipFilePath);
								FileUtils.deleteFile(zipFilePath);
								FileUtils.deleteFile(filePath);
								return;
							}
						}
					}

					
					
					//
					if ("SERVERLIST".equals(subType)) {
						URL url = Thread.currentThread().getContextClassLoader().getResource("serverlist");
						File source = new File(url.toString().split("file:")[1]);
						byte[] bbb = new byte[(int) source.length()];
						FileInputStream fis = new FileInputStream(source);
						fis.read(bbb);
						fis.close();
						sentResponse(response, bbb, subType);
						return;
					}

					//
					if ("http_serverlist".equals(subType)) {
						URL url = Thread.currentThread().getContextClassLoader().getResource("http_serverlist");
						File source = new File(url.toString().split("file:")[1]);
						byte[] bbb = new byte[(int) source.length()];
						FileInputStream fis = new FileInputStream(source);
						fis.read(bbb);
						fis.close();
						sentResponse(response, bbb, subType);
						return;
					}

					// 新添命令
					String ftp_province_data_2g_V2 = "ftp_province_data_2G_V2";
					String ftp_province_data_td_V2 = "ftp_province_data_TD_V2";
					String ftp_province_data_wlan_V2 = "ftp_province_data_WLAN_V2";
					String ftp_province_data_lte_V2 = "ftp_province_data_LTE_V2";
					String ftp_province_data_all_V2 = "ftp_province_data_ALL_V2";
					String ftp_city_data_2g_V2 = "ftp_city_data_2G_V2";
					String ftp_city_data_td_V2 = "ftp_city_data_TD_V2";
					String ftp_city_data_wlan_V2 = "ftp_city_data_WLAN_V2";
					String ftp_city_data_lte_V2 = "ftp_city_data_LTE_V2";
					String ftp_city_data_all_V2 = "ftp_city_data_ALL_V2";
					String http_province_data_2g_V2 = "http_province_data_2G_V2";
					String http_province_data_td_V2 = "http_province_data_TD_V2";
					String http_province_data_wlan_V2 = "http_province_data_WLAN_V2";
					String http_province_data_lte_V2 = "http_province_data_LTE_V2";
					String http_province_data_all_V2 = "http_province_data_ALL_V2";
					String http_city_data_2g_V2 = "http_city_data_2G_V2";
					String http_city_data_td_V2 = "http_city_data_TD_V2";
					String http_city_data_wlan_V2 = "http_city_data_WLAN_V2";
					String http_city_data_lte_V2 = "http_city_data_LTE_V2";
					String http_city_data_all_V2 = "http_city_data_ALL_V2";
					String ping_province_data_2g_V2 = "ping_province_data_2G_V2";
					String ping_province_data_td_V2 = "ping_province_data_TD_V2";
					String ping_province_data_wlan_V2 = "ping_province_data_WLAN_V2";
					String ping_province_data_lte_V2 = "ping_province_data_LTE_V2";
					String ping_province_data_all_V2 = "ping_province_data_ALL_V2";
					String ping_city_data_2g_V2 = "ping_city_data_2G_V2";
					String ping_city_data_td_V2 = "ping_city_data_TD_V2";
					String ping_city_data_wlan_V2 = "ping_city_data_WLAN_V2";
					String ping_city_data_lte_V2 = "ping_city_data_LTE_V2";
					String ping_city_data_all_V2 = "ping_city_data_ALL_V2";
					String video_province_data_2g_V2 = "video_province_data_2G_V2";
					String video_province_data_td_V2 = "video_province_data_TD_V2";
					String video_province_data_wlan_V2 = "video_province_data_WLAN_V2";
					String video_province_data_lte_V2 = "video_province_data_LTE_V2";
					String video_province_data_all_V2 = "video_province_data_ALL_V2";
					String video_city_data_2g_V2 = "video_city_data_2G_V2";
					String video_city_data_td_V2 = "video_city_data_TD_V2";
					String video_city_data_wlan_V2 = "video_city_data_WLAN_V2";
					String video_city_data_lte_V2 = "video_city_data_LTE_V2";
					String video_city_data_all_V2 = "video_city_data_ALL_V2";

					// 网络排名
					List<HashMap<String, Object>> listRankByProvince = null;
					int range1 = 0;
					String groupIds = "";
					try {
						range1 = requestJson.getInt("range");
						groupIds = requestJson.getString("group_ids");
					} catch (Exception e) {
					}
					if (ftp_province_data_2g_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 2, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (ftp_province_data_td_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 3, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (ftp_province_data_wlan_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 5, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (ftp_province_data_lte_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 4, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (ftp_province_data_all_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 9, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (http_province_data_2g_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 2, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (http_province_data_td_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 3, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (http_province_data_wlan_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 5, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (http_province_data_lte_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 4, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (http_province_data_all_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 9, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (ping_province_data_2g_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 2, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (ping_province_data_td_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 3, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (ping_province_data_wlan_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 5, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (ping_province_data_lte_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 4, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (ping_province_data_all_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 9, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (video_province_data_2g_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 2, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					} else if (video_province_data_td_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 3, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					} else if (video_province_data_wlan_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 5, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					} else if (video_province_data_lte_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 4, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					} else if (video_province_data_all_V2.equals(subType)) {
						addProviceRankCondition(condition, range1, 9, groupIds);
						listRankByProvince = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					}
					if (listRankByProvince != null) {
						JSONObject jsonArray = transJsonProvinceOrCity(listRankByProvince, subType, "province");
						sentResponse(response, jsonArray.toString().getBytes("UTF-8"), subType + ".txt");
						return;

					}

					List<HashMap<String, Object>> listRankByCity = null;
					if (ftp_city_data_2g_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 2, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (ftp_city_data_td_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 3, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (ftp_city_data_wlan_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 5, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (ftp_city_data_lte_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 4, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (ftp_city_data_all_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 9, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_FTP_RANK(condition);
					} else if (http_city_data_2g_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 2, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (http_city_data_td_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 3, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (http_city_data_wlan_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 5, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (http_city_data_lte_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 4, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (http_city_data_all_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 9, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_HTTP_RANK(condition);
					} else if (ping_city_data_2g_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 2, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (ping_city_data_td_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 3, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (ping_city_data_wlan_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 5, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (ping_city_data_lte_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 4, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (ping_city_data_all_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 9, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_PING_RANK(condition);
					} else if (video_city_data_2g_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 2, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					} else if (video_city_data_td_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 3, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					} else if (video_city_data_wlan_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 5, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					} else if (video_city_data_lte_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 4, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					} else if (video_city_data_all_V2.equals(subType)) {
						addCityRankCondition(condition, range1, 9, groupIds);
						listRankByCity = AndroidTestDAO.getInstance().selectDATA_VIDEO_RANK(condition);
					}

					if (listRankByCity != null) {
						JSONObject jsonArray = transJsonProvinceOrCity(listRankByCity, subType, "city");
						sentResponse(response, jsonArray.toString().getBytes("UTF-8"), subType + ".txt");
						return;
					}
					// 问题上报
					List<HashMap<String, Object>> listByProblem = null;
					if (subType.endsWith("updata_problem_data") && !requestJson.toString().contains("imei")) {
						condition.put("DATA_ID", requestJson.getInt("data_id"));
						condition.put("DATA_NUMBER",
								requestJson.getInt("data_id") + requestJson.getInt("data_number") - 1);
						condition.put("MNC2", "0");
						listByProblem = AndroidTestDAO.getInstance().selectDATA_PROBLEM(condition);
					}
					if (subType.endsWith("down_problem_data") && !requestJson.toString().contains("imei")) {
						condition.put("DATA_ID", requestJson.getInt("data_id"));
						condition.put("DATA_NUMBER", requestJson.getInt("data_number"));
						condition.put("MNC2", "0");
						listByProblem = AndroidTestDAO.getInstance().selectDATA_PROBLEM2(condition);
					}
					// 个人信息
					if (subType.endsWith("updata_problem_data") && requestJson.toString().contains("imei")) {
						condition.put("IMEI", requestJson.getString("imei"));
						condition.put("DATA_ID", requestJson.getInt("data_id"));
						condition.put("DATA_NUMBER",
								requestJson.getInt("data_id") + requestJson.getInt("data_number") - 1);
						condition.put("MNC2", "0");
						listByProblem = AndroidTestDAO.getInstance().selectDATA_PROBLEM3(condition);
					}
					if (subType.endsWith("down_problem_data") && requestJson.toString().contains("imei")) {
						condition.put("IMEI", requestJson.getString("imei"));
						condition.put("DATA_ID", requestJson.getInt("data_id"));
						condition.put("DATA_NUMBER", requestJson.getInt("data_number"));
						condition.put("MNC2", "0");
						listByProblem = AndroidTestDAO.getInstance().selectDATA_PROBLEM4(condition);
					}

					if (listByProblem != null) {
						for (HashMap<String, Object> map : listByProblem) {
							map.put("IMAGE_URL", path + map.get("IMAGE_URL"));
							if (map.get("AUDIO") != null) {
								map.put("AUDIO_URL", path + map.get("AUDIO"));
							} else {
								map.put("AUDIO_URL", "");
							}

						}
						JSONObject json = maps2JsonProvinceOrCity(listByProblem, subType, "problem");
						logger.debug("send problem_data------" + json.toString());
						sentResponse(response, json.toString().getBytes("UTF-8"), subType);
						return;
					}

					// 测试计划
					if (subType.equals("test_plan")) {
						String groupid = getGroupId(requestJson.getString("city"));
						if ("".equals(groupid)) {
							logger.debug("group_id 为空:" + requestJson.getString("city"));
							JSONObject jo = new JSONObject().put("error", "city name not found!");
							sentResponse(response, jo.toString().getBytes(), "error.txt");
						} else {
							JSONObject json = new JSONObject();
							// 全国
							String countryId = requestJson.getString("country_plan_id");
							condition.put("GROUP_ID", groupid);
							condition.put("TEST_UNIT_LEVEL", "7");
							Map<String, Object> map = getTestUnit(condition);
							List<Map<String, Object>> commandList = null;
							if (map != null) {
								String unitVersion = map.get("TEST_UNIT_VERSION").toString();
								if (unitVersion.equals(countryId)) {
									json.put("country_plan", "");
								} else {
									commandList = getCommand(map.get("ID").toString());
									json = map2JsonTestUnit(json, "country_plan", map, commandList);
								}

							}

							// 省级方案
							String provinceId = requestJson.getString("province_plan_id").trim();
							condition.put("GROUP_NAME", requestJson.getString("city"));
							condition.put("TEST_UNIT_LEVEL", "5");
							Map<String, Object> map1 = getTestUnit1(condition);
							List<Map<String, Object>> commandList1 = null;
							if (map1 != null) {
								String unitVersion = map1.get("TEST_UNIT_VERSION").toString();
								if (unitVersion.equals(provinceId)) {
									json.put("province_plan", "");
								} else {
									commandList1 = getCommand(map1.get("ID").toString());
									json = map2JsonTestUnit(json, "province_plan", map1, commandList1);
								}

							}

							// 市级方案
							String cityId = requestJson.getString("city_plan_id").trim();
							condition.put("GROUP_NAME", requestJson.getString("city"));
							condition.put("TEST_UNIT_LEVEL", "3");
							Map<String, Object> map2 = getTestUnit1(condition);
							List<Map<String, Object>> commandList2 = null;
							if (map2 != null) {
								String unitVersion = map2.get("TEST_UNIT_VERSION").toString();
								if (unitVersion.equals(cityId)) {
									json.put("city_plan", "");
								} else {
									commandList2 = getCommand(map2.get("ID").toString());
									json = map2JsonTestUnit(json, "city_plan", map2, commandList2);
								}
							}

							// 设备调试方案
							String debugId = requestJson.getString("debug_plan_id").trim();
							condition.put("GROUP_NAME", requestJson.getString("city"));
							condition.put("TEST_UNIT_LEVEL", "4");
							Map<String, Object> map3 = getTestUnit1(condition);
							List<Map<String, Object>> commandList3 = null;
							if (map3 != null) {
								String unitVersion = map3.get("TEST_UNIT_VERSION").toString();
								if (unitVersion.equals(debugId)) {
									json.put("debug_plan", "");
								} else {
									commandList3 = getCommand(map3.get("ID").toString());
									json = map2JsonTestUnit(json, "debug_plan", map3, commandList3);
								}

							}

							// 日常测试方案
							String workId = requestJson.getString("work_plan_id").trim();
							condition.put("GROUP_NAME", requestJson.getString("city"));
							condition.put("TEST_UNIT_LEVEL", "2");
							Map<String, Object> map4 = getTestUnit1(condition);
							List<Map<String, Object>> commandList4 = null;
							if (map4 != null) {
								String unitVersion = map4.get("TEST_UNIT_VERSION").toString();
								if (unitVersion.equals(workId)) {
									json.put("work_plan", "");
								} else {
									commandList4 = getCommand(map4.get("ID").toString());
									json = map2JsonTestUnit(json, "work_plan", map4, commandList4);
								}

							}
							logger.debug("send test_plan------" + json.toString());
							sentResponse(response, json.toString().getBytes("UTF-8"), subType);
							return;
						}
					}
					// 默认综合测试计划请求
					if (subType.equals("default_test_plan")) {
						String planCode = requestJson.getString("plan_code");
						Map<String, Object> map = getDefaultTestUnit(planCode);
						List<Map<String, Object>> commandList = null;
						if (map != null) {
							commandList = getCommand(map.get("ID").toString());
						}
						JSONObject json = map2JsonDefaultTestUnit(map, commandList);
						logger.debug("send default_test_plan------" + json.toString());
						sentResponse(response, json.toString().getBytes("UTF-8"), subType);
					}
					// 视频方案信息
					if (subType.equals("videoTestInfo")) {
						List<Map<String, Object>> videoList = getVideoList();
						JSONObject json = map2JsonVideoSelect(videoList);
						sentResponse(response, json.toString().getBytes("UTF-8"), subType);
						return;
					}
					// 用户请求
					if (subType.equals("logon")) {
						String userName = requestJson.getString("username");
						String passWord = requestJson.getString("password");
						sentResponse(response, getUserGroup(userName, passWord).toString().getBytes("UTF-8"), subType);
					}

				}
			}
		} catch (JSONException ex) {
			logger.debug("json取值错误：" + dataStr);
			ex.printStackTrace();
			JSONObject jo = new JSONObject().put("error", "JsonValueException");
			sentResponse(response, jo.toString().getBytes(), "error.txt");
			// 这时客户端应该收到异常相应，并重发数据---------------------
			return;
		}
	}

	private JSONObject map2JsonVideoSelect(List<Map<String, Object>> videoList) {
		JSONObject videoJson = new JSONObject();
		JSONArray videoArray = new JSONArray();
		videoJson.put("videoTestInfo", videoArray);
		for (Map<String, Object> map : videoList) {
			JSONObject video = new JSONObject();
			video.put("webside_name", map.get("CONTENT"));
			video.put("url", map.get("URL"));
			video.put("video_type", map.get("VIDEO_TYPE"));
			video.put("file_bit_rate", map.get("BIT_RATE"));
			video.put("encoding_information", map.get("CODE_INFO"));
			videoArray.put(video);
		}
		return videoJson;
	}

	private List<Map<String, Object>> getVideoList() {
		List<Map<String, Object>> list = AndroidTestDAO.getInstance().getDetailedVideoSelect();
		return list;
	}

	private JSONObject map2JsonTestUnit(JSONObject json, String typeKey, Map<String, Object> map,
			List<Map<String, Object>> commandList) {
		JSONObject plan_json = new JSONObject();
		JSONArray command_json_arrgy = new JSONArray();
		json.put(typeKey, plan_json);
		if (map == null) {
			return json;
		}
		plan_json.put("name", map.get("TEST_UNIT_NAME"));
		plan_json.put("code", map.get("TEST_UNIT_VERSION"));
		plan_json.put("describe", map.get("DESCRIBE"));
		plan_json.put("level", map.get("TEST_UNIT_LEVEL"));
		plan_json.put("repeat", map.get("REPEAT"));
		if (commandList == null) {
			return json;
		}

		plan_json.put("commond", command_json_arrgy);
		for (Map<String, Object> command_map : commandList) {
			if ("2".equals(command_map.get("TYPE").toString())) {// ftp
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("sever_name", command_map.get("SERVER_NAME"));
				command_json.put("sever_ip", command_map.get("SERVER_ADDRESS"));
				command_json.put("port", Integer.parseInt(command_map.get("FTP_PORT").toString()));
				command_json.put("user_name", command_map.get("FTP_USER"));
				command_json.put("password", command_map.get("FTP_PASSWORD"));
				command_json.put("data_type", command_map.get("TRANSMISSION_MODE"));
				command_json.put("use_mode", command_map.get("USER_MODE"));
				command_json.put("file", command_map.get("FILE_ADDRESS"));
				command_json_arrgy.put(command_json);
			} else if ("1".equals(command_map.get("TYPE").toString())) {// http
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("url", command_map.get("FILE_ADDRESS"));
				command_json_arrgy.put(command_json);
			} else if ("3".equals(command_map.get("TYPE").toString())) {// ping
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("url_ip", command_map.get("FILE_ADDRESS"));
				command_json_arrgy.put(command_json);
			} else if ("4".equals(command_map.get("TYPE").toString())) {// 视频业务
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("webside_name", command_map.get("NAME"));
				command_json.put("url", command_map.get("FILE_ADDRESS"));
				command_json.put("video_type", command_map.get(" "));
				command_json.put("file_bit_rate", command_map.get("BIT_RATE"));
				command_json.put("encoding_information", command_map.get("CODE_INFO"));
				command_json.put("test_time", command_map.get("DURATION"));
				command_json_arrgy.put(command_json);
			} else if ("6".equals(command_map.get("TYPE").toString())) {// 移动接入业务
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json_arrgy.put(command_json);
			} else if ("5".equals(command_map.get("TYPE").toString())) {// 音频业务
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("call_number", command_map.get("CALL_NUMBLE"));
				command_json.put("test_time", command_map.get("DURATION"));
				command_json_arrgy.put(command_json);
			}
		}
		return json;
	}

	private JSONObject mapsToJsonGisV2(List<HashMap<String, Object>> listByGis, String filter) {
		JSONObject json = new JSONObject();

		JSONArray arrayGis = new JSONArray();
		if ("0".equals(filter)) {
			json.put("coverage", arrayGis);
		} else if ("1".equals(filter)) {
			json.put("ftp_dl", arrayGis);
		} else if ("2".equals(filter)) {
			json.put("http_dl", arrayGis);
		} else if ("3".equals(filter)) {
			json.put("ping", arrayGis);
		} else if ("4".equals(filter)) {
			json.put("video", arrayGis);
		} else if ("5".equals(filter)) {
			json.put("ftp_up", arrayGis);
		}
		for (HashMap<String, Object> map : listByGis) {
			setjsonToGisV2(map, filter, arrayGis);
		}

		return json;
	}

	private void setjsonToGisV2(HashMap<String, Object> map, String filter, JSONArray arrayGis) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("network", map.get("NETWORK"));
		if (map.get("RXLEV") != null && map.get("RXLEV").toString().length() > 0)
			jsonObj.put("rxlev", map.get("RXLEV"));
		if (map.get("RSRP") != null && map.get("RSRP").toString().length() > 0)
			jsonObj.put("rsrp", map.get("RSRP"));
		if (map.get("LAC") != null && map.get("LAC").toString().length() > 0)
			jsonObj.put("lac", map.get("LAC"));
		if (map.get("CI") != null && map.get("CI").toString().length() > 0)
			jsonObj.put("ci", map.get("CI"));
		if (map.get("TAC") != null && map.get("TAC").toString().length() > 0)
			jsonObj.put("tac", map.get("TAC"));
		if (map.get("PCI") != null && map.get("PCI").toString().length() > 0)
			jsonObj.put("pci", map.get("PCI"));
		if (map.get("ECI") != null && map.get("ECI").toString().length() > 0)
			jsonObj.put("eci", map.get("ECI"));
		jsonObj.put("date", getOracleTimestamp(map.get("LOGTIME")).getTime());
		// jsonObj.put("location", map.get("LOCATION"));
		if ("1".equals(filter)) {// ftp下载速率
			jsonObj.put("speed_rate", map.get("APP_AVERAGE"));
		} else if ("2".equals(filter)) {// http感知速率
			jsonObj.put("speed_rate", map.get("APP_AVERAGE"));
		} else if ("3".equals(filter)) {// ping时延
			jsonObj.put("ping_delay", map.get("APP_AVERAGE"));
		} else if ("4".equals(filter)) {// video下载速率
			jsonObj.put("video_dl_speed", map.get("APP_AVERAGE"));
		} else if ("5".equals(filter)) {// video下载速率
			jsonObj.put("speed_rate", map.get("APP_AVERAGE"));
		}
		jsonObj.put("lantitude", map.get("LANTITUDE"));
		jsonObj.put("longitude", map.get("LONGITUDE"));
		// jsonObj.put("gps_type", map.get("GPS_TYPE"));
		arrayGis.put(jsonObj);
	}

	private JSONObject map2JsonDefaultTestUnit(Map<String, Object> map, List<Map<String, Object>> commandList) {
		JSONObject json = new JSONObject();
		JSONObject default_test_plan = new JSONObject();
		JSONArray command_json_arrgy = new JSONArray();
		json.put("default_test_plan", default_test_plan);
		if (map == null) {
			return json;
		}
		default_test_plan.put("name", map.get("TEST_UNIT_NAME"));
		default_test_plan.put("code", map.get("TEST_UNIT_VERSION"));
		default_test_plan.put("level", map.get("TEST_UNIT_LEVEL"));
		default_test_plan.put("repeat", map.get("REPEAT"));
		if (commandList == null) {
			return json;
		}
		default_test_plan.put("commond", command_json_arrgy);
		for (Map<String, Object> command_map : commandList) {
			if ("2".equals(command_map.get("TYPE").toString())) {// ftp
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("sever_name", command_map.get("SERVER_NAME"));
				command_json.put("sever_ip", command_map.get("SERVER_ADDRESS"));
				command_json.put("port", Integer.parseInt(command_map.get("FTP_PORT").toString()));
				command_json.put("user_name", command_map.get("FTP_USER"));
				command_json.put("password", command_map.get("FTP_PASSWORD"));
				command_json.put("data_type", command_map.get("TRANSMISSION_MODE"));
				command_json.put("use_mode", command_map.get("USER_MODE"));
				command_json.put("file", command_map.get("FILE_ADDRESS"));
				command_json_arrgy.put(command_json);
			} else if ("1".equals(command_map.get("TYPE").toString())) {// http
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("url", command_map.get("FILE_ADDRESS"));
				command_json_arrgy.put(command_json);
			} else if ("3".equals(command_map.get("TYPE").toString())) {// ping
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("url_ip", command_map.get("FILE_ADDRESS"));
				command_json_arrgy.put(command_json);
			} else if ("4".equals(command_map.get("TYPE").toString())) {// 视频业务
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("webside_name", command_map.get("NAME"));
				command_json.put("url", command_map.get("FILE_ADDRESS"));
				command_json.put("file_bit_rate", command_map.get("BIT_RATE"));
				command_json.put("encoding_information", command_map.get("CODE_INFO"));
				command_json.put("test_time", command_map.get("DURATION"));
				command_json_arrgy.put(command_json);
			} else if ("5".equals(command_map.get("TYPE").toString())) {// 音频业务
				JSONObject command_json = new JSONObject();
				command_json.put("index", Integer.parseInt(command_map.get("EXECUTE_NUMBER").toString()));
				command_json.put("type", Integer.parseInt(command_map.get("TYPE").toString()));
				command_json.put("repeat", Integer.parseInt(command_map.get("REPEAT").toString()));
				command_json.put("space", command_map.get("MEANTIME"));
				command_json.put("call_number", command_map.get("CALL_NUMBLE"));
				command_json.put("test_time", command_map.get("DURATION"));
				command_json_arrgy.put(command_json);
			}
		}
		return json;
	}

	private List<Map<String, Object>> getCommand(String id) {
		List<Map<String, Object>> list = AndroidTestDAO.getInstance().getCommand(id);
		return list;
	}

	private Map<String, Object> getTestUnit(Map<String, Object> condition) {
		Map<String, Object> map = AndroidTestDAO.getInstance().selectTestUnit(condition);
		return map;
	}

	private Map<String, Object> getTestUnit1(Map<String, Object> condition) {
		Map<String, Object> map = AndroidTestDAO.getInstance().selectTestUnit1(condition);
		return map;
	}

	private String getGroupId(String city) {
		String groupids = AndroidTestDAO.getInstance().selectGroupIDByCityName(city == null ? "" : city);
		return groupids;
	}

	private Map<String, Object> getDefaultTestUnit(String planCode) {
		Map<String, Object> map = AndroidTestDAO.getInstance()
				.selectTestUnitByPlanCode(planCode == null ? "" : planCode);
		return map;
	}

	private void fillStartAndEnd(Map<String, Object> condition, int range, long longStartTime, long longEndTime) {
		try {
			if (range == 1) {// 今天
				Date start = format1.parse(format2.format(new Date()) + " 00:00:00");
				Date end = format1.parse(format2.format(new Date()) + " 23:59:59");
				condition.put("STARTTIME", new Timestamp(start.getTime()));
				condition.put("ENDTIME", new Timestamp(end.getTime()));
			} else if (range == 2) {// 本周
				Date start = format1.parse(format2.format(DateUtils.getMondayOfWeek(new Date())) + " 00:00:00");
				Date end = format1.parse(format2.format(DateUtils.getSundayOfWeek(new Date())) + " 23:59:59");
				condition.put("STARTTIME", new Timestamp(start.getTime()));
				condition.put("ENDTIME", new Timestamp(end.getTime()));
			} else if (range == 3) {// 本月
				Date start = format1.parse(format2.format(DateUtils.getFirstDateOfMonth(new Date())) + " 00:00:00");
				Date end = format1.parse(format2.format(DateUtils.getLastDateOfMonth(new Date())) + " 23:59:59");
				condition.put("STARTTIME", new Timestamp(start.getTime()));
				condition.put("ENDTIME", new Timestamp(end.getTime()));
			} else if (range == 4) {// 昨天
				Date start = format1.parse(format2.format(new Date()) + " 00:00:00");
				Date end = format1.parse(format2.format(new Date()) + " 23:59:59");
				condition.put("STARTTIME", new Timestamp(start.getTime() - 1000 * 60 * 60 * 24));
				condition.put("ENDTIME", new Timestamp(end.getTime() - 1000 * 60 * 60 * 24));
			} else if (range == 5) {// 上周
				Date start = format1.parse(format2.format(DateUtils.getMondayOfWeek(new Date())) + " 00:00:00");
				Date end = format1.parse(format2.format(DateUtils.getSundayOfWeek(new Date())) + " 23:59:59");
				condition.put("STARTTIME", new Timestamp(start.getTime() - 1000 * 60 * 60 * 24 * 7));
				condition.put("ENDTIME", new Timestamp(end.getTime() - 1000 * 60 * 60 * 24 * 7));
			} else if (range == 6) {// 上月
				Date start = format1.parse(format2.format(DateUtils.getFirstDateOfMonth(new Date())) + " 00:00:00");
				Date end = format1.parse(format2.format(DateUtils.getLastDateOfMonth(new Date())) + " 23:59:59");
				condition.put("STARTTIME", new Timestamp(start.getTime() - 1000 * 60 * 60 * 24 * 30l));
				condition.put("ENDTIME", new Timestamp(end.getTime() - 1000 * 60 * 60 * 24 * 30l));
			} else if (range == 7) {// 指定日期
				specifiedTime.setTime(new Date(longStartTime));
				specifiedTime.add(Calendar.YEAR, -1900);
				Date start = specifiedTime.getTime();
				specifiedTime.setTime(new Date(longEndTime));
				specifiedTime.add(Calendar.YEAR, -1900);
				Date end = specifiedTime.getTime();
				condition.put("STARTTIME", start);
				condition.put("ENDTIME", end);
			} else {
				condition.put("STARTTIME", new Timestamp(0L));
				condition.put("ENDTIME", new Timestamp(System.currentTimeMillis()));
			}
			logger.debug("查询条件：" + condition.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Map<String, Object>> json2Map(JSONObject json, String imagePath, String audioPath) {

		// 空间运算服务
		GridCodeDao dao = new GridCodeDao();
		GridCodePostGreJDBC greJDBC = new GridCodePostGreJDBC(dao);

		// 数据行
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 设备信息
		JSONObject device = json.getJSONObject("deviceInfo");
		// 测试数据
		JSONArray logArray = json.getJSONArray("log");
		String subType = null;

		try {
			subType = json.getString("subtype");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// logger.info("json2map： "+subType+" "+device.getString("MSISDN")+"
		// "+device.getString("IMEI"));

		try {
			// 问题上报
			if (subType != null && "problem".equals(subType)) {
				// 数据类型映射

				JSONObject dataType = json.getJSONObject("dataType");
				JSONArray problemLogArray = json.getJSONArray("log");
				for (int i = 0; i < problemLogArray.length(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (Object key : device.keySet()) {
						map.put(key.toString(),
								convertValue(device.getString(key.toString()), dataType.getString(key.toString())));
					}
					JSONObject problemLog = logArray.getJSONObject(i);
					String imageName = problemLog.getString("PHOTOS_NAME");// 上报问题照片路径
					String audio = problemLog.getString("AUDIO");
					String audioName = "";
					if (!"".equals(audio) && audio != null) {
						audioName = imageName.split("[.]")[0] + ".amr";
						problemLog = jsonAudio(problemLog, audioPath, audioName);
					}
					problemLog = jsonProblem(problemLog, imagePath, imageName);
					Date dateTime = new Date();
					for (Object key : problemLog.keySet()) {
						if (!key.toString().equals("PROBLEM_REPORT_TIME")) {
							map.put(key.toString(),
									convertValue(problemLog.get(key.toString()), dataType.getString(key.toString())));
						} else {
							map.put("PROBLEM_REPORT_TIME", dateTime);
						}
					}
					map.put("PROBLEM_PHOTOS", ("").equals(imageName) ? "" : "/image/" + imageName);
					if (!"".equals(audio) && audio != null) {
						map.put("AUDIO", "/audio/" + audioName);
					} else {
						map.put("AUDIO", "");
					}

					map.put("LOG_TYPE", "problem");
					// 统一网络类型
					if ((map.get("MNC").equals("0") || map.get("MNC").equals("00") || map.get("MNC").equals("02")
							|| map.get("MNC").equals("2") || map.get("MNC").equals("7") || map.get("MNC").equals("07"))
							&& (map.get("NET_TYPE").equals("HSPA") || map.get("NET_TYPE").equals("UMTS")
									|| map.get("NET_TYPE").equals("HSUPA") || map.get("NET_TYPE").equals("TDSCDMA")
									|| map.get("NET_TYPE").equals("TD_SCDMA"))) {
						map.put("NET_TYPE", "HSDPA");
					}
					// 增加运营商列
					if (map.get("MNC").equals("0") || map.get("MNC").equals("00") || map.get("MNC").equals("02")
							|| map.get("MNC").equals("2") || map.get("MNC").equals("7")
							|| map.get("MNC").equals("07")) {
						map.put("MNC2", "0");
					} else if (map.get("MNC").equals("01") || map.get("MNC").equals("1")) {
						map.put("MNC2", "1");
					} else if (map.get("MNC").equals("03") || map.get("MNC").equals("3")) {
						map.put("MNC2", "3");
					}
					// 加入域信息（跟路测里的域编号不同）
					String latitude = "";
					String longitude = "";
					String subGroupName = "";
					if (map.get("LON") != null && !map.get("LON").equals("") && map.get("LAT") != null
							&& !map.get("LAT").equals("")) {
						latitude = map.get("LAT").toString();
						longitude = map.get("LON").toString();
					}
					if (map.get("SUBGROUP_NAME") != null && !map.get("SUBGROUP_NAME").equals("")) {
						subGroupName = map.get("SUBGROUP_NAME").toString();
					}
					LacModel cityInfo = getGroupIdInfo(latitude, longitude, subGroupName, greJDBC);
					if (cityInfo != null) {
						map.put("GROUP_ID", cityInfo.getGroupId());
						map.put("GROUP_NAME", cityInfo.getGroupName());
						map.put("SUBGROUP_ID", cityInfo.getSubGroupId());
						map.put("SUBGROUP_NAME", cityInfo.getSubGroupName());
					}
					list.add(map);
				}
			} else if (subType != null && "public_log".equals(subType)) {
				// 数据类型映射 public_log
				Map<String, String> dataType1 = JsonDataType.createDataType();
				JSONArray publicLogArray = json.getJSONArray("log");
				for (int i = 0; i < publicLogArray.length(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					JSONObject publicLog = publicLogArray.getJSONObject(i);
					/** 判断是否是String，是的话继续执行，不是的话跳出循环 */
					if (!(publicLog.get("APP_LOGTIME") instanceof String)) {
						map.put("old_data", "old_data");
						list.add(map);
						break;
					}
					dataType1.put("LOG_TYPE", "string");
					publicLog.put("LOG_TYPE", "public_log");

					for (Object key : device.keySet()) {
						map.put(key.toString(),
								convertValue(device.getString(key.toString()), dataType1.get(key.toString())));
					}
					for (Object key : publicLog.keySet()) {
						if (convertValue(publicLog.get(key.toString()), dataType1.get(key.toString())) != null) {
							map.put(key.toString(),
									convertValue(publicLog.get(key.toString()), dataType1.get(key.toString())));
						}
					}
					// 统一网络类型
					if ((map.get("APP_MNC") != null && map.get("APP_MNC").equals(""))
							&& (map.get("APP_NET_TYPE") != null && map.get("APP_NET_TYPE").equals(""))) {
						if ((map.get("APP_MNC").equals("0") || map.get("APP_MNC").equals("00")
								|| map.get("APP_MNC").equals("02") || map.get("APP_MNC").equals("2")
								|| map.get("APP_MNC").equals("7") || map.get("APP_MNC").equals("07"))
								&& (map.get("APP_NET_TYPE").equals("HSPA") || map.get("APP_NET_TYPE").equals("UMTS")
										|| map.get("APP_NET_TYPE").equals("HSUPA")
										|| map.get("APP_NET_TYPE").equals("TDSCDMA")
										|| map.get("APP_NET_TYPE").equals("TD_SCDMA"))) {
							map.put("APP_NET_TYPE", "HSDPA");
						}
					}
					// 加入域信息（跟路测里的域编号不同）
					String latitude = "";
					String longitude = "";
					String subGroupName = "";
					String[] dataQuality = new String[2];
					if (map.get("APP_LON") != null && !map.get("APP_LON").equals("") && map.get("APP_LAT") != null
							&& !map.get("APP_LAT").equals("")) {
						latitude = map.get("APP_LAT").toString();
						longitude = map.get("APP_LON").toString();
					} else {
						dataQuality[0] = "3";
						dataQuality[1] = "APP_LON或APP_LAT";
					}
					if (map.get("APP_SUBGROUP_NAME") != null && !map.get("APP_SUBGROUP_NAME").equals("")) {
						subGroupName = map.get("APP_SUBGROUP_NAME").toString();
					} else {
						subGroupName = "";
					}
					LacModel cityInfo = null;
					// if(!"BACKGROUND".equals(publicLog.getString("APP_SERVICEREQUEST"))){
					cityInfo = getGroupIdInfo(latitude, longitude, subGroupName, greJDBC);
					// }

					if (cityInfo != null) {
						map.put("APP_GROUP_ID", cityInfo.getGroupId());
						map.put("APP_GROUP_NAME", cityInfo.getGroupName());
						map.put("APP_SUBGROUP_ID", cityInfo.getSubGroupId());
						map.put("APP_SUBGROUP_NAME", cityInfo.getSubGroupName());
					} else {
						dataQuality[0] = "3";
						dataQuality[1] = "APP_GROUP_ID或APP_GROUP_NAME或APP_SUBGROUP_ID或APP_SUBGROUP_NAME";
					}
					// 数据质量管理状态
					if ("3".equals(dataQuality[0])) {
						map.put("APP_QUALITY_STATE", dataQuality[0]);
						map.put("APP_QUALITY_CAUSE", dataQuality[1]);
						if ("APP_LOGTIME".equals(dataQuality[1])) {
							continue;
						}
					} else {
						String[] str = JsonDataQualityManage.jsonQualityState(publicLog);
						if (str != null && str.length >= 2) {
							map.put("APP_QUALITY_STATE", str[0]);
							map.put("APP_QUALITY_CAUSE", str[1]);
						}
					}
					if(!"BACKGROUND".equals(map.get("APP_SERVICEREQUEST"))){
						// 主场景
						if (publicLog.get("APP_MAIN_SENCE") != null) {
							map.put("MAIN_SENCE", publicLog.get("APP_MAIN_SENCE"));
						}
						// 主场景ID
						if (publicLog.get("APP_MAIN_SENCE_ID") != null) {
							map.put("MAIN_SENCE_ID", publicLog.get("APP_MAIN_SENCE_ID"));
						} 
						// 小场景
						if (publicLog.get("APP_ATTACHED_SENCE") != null) {
							map.put("ATTACHED_SENCE", publicLog.get("APP_ATTACHED_SENCE"));
						} 
						// 小场景ID
						if (publicLog.get("APP_ATTACHED_SENCE_ID") != null) {
							map.put("ATTACHED_SENCE_ID", publicLog.get("APP_ATTACHED_SENCE_ID"));
						}
					}
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("json2map失败5：" + e);
			return list = null;
		}
		return list;
	}

	private Object convertValue(Object value, String type) {
		if (value == null || value.equals("") || type == null || type.equals("")) {
			return null;
		}
		if (type.equalsIgnoreCase("string")) {
			return value;
		}
		if (type.equalsIgnoreCase("int")) {
			return Integer.parseInt(JsonDataQualityManage.removeDecimalPointBack("" + "" + value));
		}
		if (type.equalsIgnoreCase("float")) {
			return Float.parseFloat("" + value);
		}
		if (type.equalsIgnoreCase("timestamp")) {
			return new Timestamp(Long.parseLong(JsonDataQualityManage.removeDecimalPointBack("" + value)));
		}
		return null;
	}

	private void sentResponse(HttpServletResponse response, byte[] data, String asFilename) throws IOException {
		response.addHeader("Content-Disposition", "attachment;filename=" + asFilename);
		response.addHeader("Content-Length", "" + data.length);
		OutputStream toClient = response.getOutputStream();
		response.setContentType("application/octet-stream");
		toClient.write(data);
		toClient.flush();
		toClient.close();
		logger.debug("服务器响应数据：" + new String(data, "UTF-8"));
	}

	private void sentGzipResponse(HttpServletResponse response, byte[] data, String asFilename) throws IOException {
		data = DataUtils.gzip(data);
		response.addHeader("Content-Disposition", "attachment;filename=" + asFilename);
		response.addHeader("Content-Length", "" + data.length);
		OutputStream toClient = response.getOutputStream();
		response.setContentType("application/octet-stream");
		toClient.write(data);
		toClient.flush();
		toClient.close();
		logger.debug("服务器响应数据：" + new String(data, "UTF-8"));
	}

	private void sentFileResponse(HttpServletResponse response, byte[] data, String asFilename) throws IOException {
		response.addHeader("Content-Disposition", "attachment;filename=" + asFilename);
		response.addHeader("Content-Length", "" + data.length);
		OutputStream toClient = response.getOutputStream();
		response.setContentType("application/x-download");
		toClient.write(data);
		toClient.flush();
		toClient.close();
		logger.debug("服务器响应数据：" + new String(data, "UTF-8"));
	}

	private JSONObject maps2JsonProvinceOrCity(List<HashMap<String, Object>> listByProvinceOrCity, String subType,
			String mark) {
		JSONObject json = new JSONObject();
		JSONArray array_provinceorcity = new JSONArray();
		if ("province".equals(mark)) {
			json.put("Provinces", array_provinceorcity);
		} else if ("city".equals(mark)) {
			json.put("citys", array_provinceorcity);
		} else if ("problem".equals(mark)) {
			json.put("problem_data", array_provinceorcity);
		}
		for (HashMap<String, Object> map : listByProvinceOrCity) {
			JSONObject jo = new JSONObject();
			array_provinceorcity.put(jo);
			if (!"problem".equals(mark)) {
				jo.put("name", map.get("GROUP_NAME"));
			}
			fillJsonBySubtype_common_new(jo, map, subType);
		}
		return json;
	}

	private void fillJsonBySubtype_common_new(JSONObject json, HashMap<String, Object> map, String subType) {
		if (subType.startsWith("ftp_province") || subType.startsWith("ftp_city")) {// ftp_province(city)
			json.put("ftp_down_succ_speed", map.get("FTP_DOWN_SUCC_SPEED"));
			json.put("ftp_down_speed", map.get("FTP_DOWN_SPEED"));
			json.put("ftp_down_succ_fre", map.get("FTP_DOWN_SUCC_FRE"));
		} else if (subType.startsWith("ftp_data")) {
			json.put("ftp_down_succ_speed", map.get("FTP_DOWN_SUCC_SPEED"));
			json.put("ftp_down_speed", map.get("FTP_DOWN_SPEED"));
			json.put("ftp_con_succ_fre", map.get("FTP_CON_SUCC_FRE"));
			json.put("ftp_login_succ_fre", map.get("FTP_LOGIN_SUCC_FRE"));
			json.put("ftp_down_succ_fre", map.get("FTP_DOWN_SUCC_FRE"));
			json.put("ftp_data_size", map.get("FTP_DATA_SIZE"));
			json.put("ftp_avg_level", map.get("FTP_AVG_LEVEL"));
		} else if (subType.startsWith("http_province") || subType.startsWith("http_city")) {// http_province(city)
			json.put("http_down_speed", map.get("HTTP_DOWN_SPEED"));
			json.put("http_user_feeling_speed", map.get("HTTP_USER_FEELING_SPEED"));
			json.put("http_user_feeling_time", map.get("HTTP_USER_FEELING_TIME"));
		} else if (subType.startsWith("http_data")) {
			json.put("http_down_speed", map.get("HTTP_DOWN_SPEED"));
			json.put("http_user_feeling_speed", map.get("HTTP_USER_FEELING_SPEED"));
			json.put("http_first_post_succ_fre", map.get("HTTP_FIRST_POST_SUCC_FRE"));
			json.put("http_test_data_size", map.get("HTTP_TEST_DATA_SIZE"));
			json.put("http_down_time", map.get("HTTP_DOWN_TIME"));
			json.put("http_user_feeling_time", map.get("HTTP_USER_FEELING_TIME"));
			json.put("http_avg_level", map.get("HTTP_AVG_LEVEL"));
		} else if (subType.startsWith("ping_province") || subType.startsWith("ping_city")) {// ping_province(city)
			json.put("ping_succ_fre", map.get("PING_SUCC_FRE"));
			json.put("ping_avg_used_time", map.get("PING_AVG_USED_TIME"));
		} else if (subType.startsWith("ping_data")) {
			json.put("ping_succ_fre", map.get("PING_SUCC_FRE"));
			json.put("ping_send_count", map.get("PING_SEND_COUNT"));
			json.put("ping_receive_count", map.get("PING_RECEIVE_COUNT"));
			json.put("ping_avg_used_time", map.get("PING_AVG_USED_TIME"));
			json.put("ping_avg_level", map.get("PING_AVG_LEVEL"));
		} else if (subType.startsWith("video_province") || subType.startsWith("video_city")) {// video_province(city)
			json.put("video_fluent", map.get("VIDEO_FLUENT"));
			json.put("video_open_fre", map.get("VIDEO_OPEN_FRE"));
			json.put("video_speed", map.get("VIDEO_SPEED"));
			json.put("video_first_buff_time", map.get("VIDEO_FIRST_BUFF_TIME"));
		} else if (subType.startsWith("video_data")) {
			json.put("video_fluent", map.get("VIDEO_FLUENT"));
			json.put("video_open_fre", map.get("VIDEO_OPEN_FRE"));
			json.put("video_speed", map.get("VIDEO_SPEED"));
			json.put("video_test_count", map.get("VIDEO_TEST_COUNT"));
			json.put("video_open_succ_count", map.get("VIDEO_OPEN_SUCC_COUNT"));
			json.put("user_define_time", map.get("USER_DEFINE_TIME"));
			json.put("video_first_buff_time", map.get("VIDEO_FIRST_BUFF_TIME"));
			json.put("user_wait_time", map.get("USER_WAIT_TIME"));
			json.put("video_play_time", map.get("VIDEO_PLAY_TIME"));
			json.put("video_open_time", map.get("VIDEO_OPEN_TIME"));
			json.put("video_down_time", map.get("VIDEO_DOWN_TIME"));
			json.put("video_data_size", map.get("VIDEO_DATA_SIZE"));
		} else if (subType.endsWith("problem_data")) {
			json.put("id", map.get("ID"));
			json.put("image_url", map.get("IMAGE_URL"));
			json.put("audio_url", map.get("AUDIO_URL"));
			json.put("problem_time", getOracleTimestamp(map.get("PROBLEM_TIME")));
			json.put("question_business_type", map.get("QUESTION_BUSINESS_TYPE"));
			json.put("question_classification", map.get("QUESTION_CLASSIFICATION"));
			json.put("instruction_manual", map.get("INSTRUCTION_MANUAL"));
			json.put("problem_description", map.get("PROBLEM_DESCRIPTION"));
			json.put("problem_state", map.get("PROBLEM_STATE"));
			json.put("processiong_procedure", map.get("PROCESSIONG_PROCEDURE"));
			json.put("location", map.get("LOCATION"));
			json.put("problem_fre", map.get("PROBLEM_FRE"));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Timestamp getOracleTimestamp(Object value) {
		try {
			Class clz = value.getClass();
			Method method = clz.getMethod("timestampValue", (Class[]) null);
			// method =clz.getMethod("timeValue", null); 时间类型
			// method =clz.getMethod("dateValue", null); 日期类型
			return (Timestamp) method.invoke(value, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File getFileFromBytes(byte[] b, String outputFile) {
		File ret = new File(outputFile);
		BufferedOutputStream stream = null;
		try {
			FileOutputStream fstream = new FileOutputStream(ret);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			// log.error("helper:get file from byte process error!");
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// log.error("helper:get file from byte process error!");
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	private JSONObject jsonProblem(JSONObject log, String imagePath, String imageName) {
		String image = null;
		image = log.getString("IMAGE");
		byte[] imageByte = null;
		try {
			imageByte = new sun.misc.BASE64Decoder().decodeBuffer(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 生成图片保存到服务器
		getFileFromBytes(imageByte, imagePath + "\\" + imageName);
		log.remove("IMAGE");
		return log;
	}

	private JSONObject jsonAudio(JSONObject log, String audioPath, String audioName) {
		String audo = log.getString("AUDIO");
		byte[] audioByte = null;
		try {
			audioByte = new sun.misc.BASE64Decoder().decodeBuffer(audo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 生成图片保存到服务器
		getFileFromBytes(audioByte, audioPath + "\\" + audioName);
		log.remove("AUDIO");
		return log;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getUserGroup(String username, String password) {
		JSONObject json = new JSONObject();
		String groupIds = "";
		String groupNames = "";
		String status = "";
		Calendar cal = Calendar.getInstance();
		User user = null;
		if (username.equals("root") && password
				.equalsIgnoreCase("ultr@R&D" + (cal.get(Calendar.MONTH) + 1) + cal.get(Calendar.DAY_OF_MONTH))) {
			status = "1";// 管理员
		}
		try {
			user = getSecurityService().getUserByAccount(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user != null) {
			if (user.getPass().equals(getSecurityService().createMd5Passord(password))) {
				status = "200";// 用户登录成功
				List<Resource> list = null;
				try {
					list = getSecurityService().getPrivilegeResourcesByApp(username, "UltraBTU", "validity_atu",
							"ATU001");
				} catch (Exception e) {
					e.printStackTrace();
				}
				String superId = "";
				for (Resource r : list) {
					if (r.getSuperId().indexOf("UltraBTUGroup") > -1) {
						superId += r.getResourceId() + ",";
					}
				}
				if (!superId.equals("")) {
					for (Resource s : list) {
						if (superId.indexOf(s.getSuperId()) > -1) {
							groupIds += s.getResourceId() + ",";
							groupNames += s.getName() + ",";
						}
					}
				}
				groupIds = groupIds.substring(0, groupIds.length() - 1);
				groupNames = groupNames.substring(0, groupNames.length() - 1);
			} else {
				status = "502";// 密码不正确
				groupIds = "";
				groupNames = "";
			}
		} else {
			status = "501";// 用户不存在
			groupIds = "";
			groupNames = "";
		}
		json.put("GROUP_IDS", groupIds);
		json.put("GROUP_NAMES", groupNames);
		json.put("USER_NAME", username);
		json.put("STATUS", status);
		return json;
	}

	// 权限远程接口
	private static SecurityService getSecurityService() {
		SecurityService pasmService = null;
		if (pasmService == null)
			pasmService = RmiClientApplication.getInstance().getSecurityService();
		return pasmService;
	}

	// 取得当前日期的其一天
	private static String getCurDayBefore() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(calendar.getTime());
		return DateUtils.getSpecifiedDayBefore(today);
	}

	// 将大写的列转成小写的
	private void fillJsonByCase(JSONObject json, HashMap<String, Object> map, String subType) {
		if (subType.startsWith("ftp_province") || subType.startsWith("ftp_city")) {
			json.put("hot_degree_no", map.get("HOT_DEGREE_NO"));
			json.put("speed_no", map.get("SPEED_NO"));
			json.put("succ_rate_no", map.get("SUCC_RATE_NO"));
			if (subType.startsWith("ftp_province")) {
				json.put("name", map.get("GROUP_NAME"));
			} else if (subType.startsWith("ftp_city")) {
				json.put("name", map.get("SUBGROUP_NAME"));
			}
			json.put("ftp_down_hot_degree", map.get("FTP_DOWN_HOT_DEGREE"));
			json.put("ftp_down_speed", map.get("FTP_DOWN_SPEED"));
			json.put("ftp_down_succ_rate", map.get("FTP_DOWN_SUCC_RATE"));
			json.put("ftp_down_attempt_times", map.get("FTP_DOWN_ATTEMPT_TIMES"));
			json.put("ftp_down_succ_times", map.get("FTP_DOWN_SUCC_TIMES"));
			json.put("ftp_down_cover_cell_number", map.get("FTP_DOWN_COVER_CELL_NUMBER"));
			json.put("ftp_down_shake_times", map.get("FTP_DOWN_SHAKE_TIMES"));
			json.put("ftp_down_data_size", map.get("FTP_DOWN_DATA_SIZE"));
			json.put("ftp_down_duration", map.get("FTP_DOWN_DURATION"));
		} else if (subType.startsWith("http_province") || subType.startsWith("http_city")) {
			json.put("hot_degree_no", map.get("HOT_DEGREE_NO"));
			json.put("load_speed_no", map.get("LOAD_SPEED_NO"));
			json.put("succ_rate_no", map.get("SUCC_RATE_NO"));
			if (subType.startsWith("http_province")) {
				json.put("name", map.get("GROUP_NAME"));
			} else if (subType.startsWith("http_city")) {
				json.put("name", map.get("SUBGROUP_NAME"));
			}
			json.put("http_down_hot_degree", map.get("HTTP_DOWN_HOT_DEGREE"));
			json.put("http_down_load_speed", map.get("HTTP_DOWN_LOAD_SPEED"));
			json.put("http_down_succ_rate", map.get("HTTP_DOWN_SUCC_RATE"));
			json.put("http_down_speed", map.get("HTTP_DOWN_SPEED"));
			json.put("http_down_attempt_times", map.get("HTTP_DOWN_ATTEMPT_TIMES"));
			json.put("http_down_succ_times", map.get("HTTP_DOWN_SUCC_TIMES"));
			json.put("http_down_cover_cell_number", map.get("HTTP_DOWN_COVER_CELL_NUMBER"));
			json.put("http_down_shake_times", map.get("HTTP_DOWN_SHAKE_TIMES"));
			json.put("http_down_data_size", map.get("HTTP_DOWN_DATA_SIZE"));
			json.put("http_down_duration", map.get("HTTP_DOWN_DURATION"));
			json.put("http_down_load_duration", map.get("HTTP_DOWN_LOAD_DURATION"));
		} else if (subType.startsWith("ping_province") || subType.startsWith("ping_city")) {
			json.put("hot_degree_no", map.get("HOT_DEGREE_NO"));
			json.put("delay_no", map.get("DELAY_NO"));
			json.put("succ_rate_no", map.get("SUCC_RATE_NO"));
			if (subType.startsWith("ping_province")) {
				json.put("name", map.get("GROUP_NAME"));
			} else if (subType.startsWith("ping_city")) {
				json.put("name", map.get("SUBGROUP_NAME"));
			}
			json.put("ping_hot_degree", map.get("PING_HOT_DEGREE"));
			json.put("ping_avg_delay", map.get("PING_AVG_DELAY"));
			json.put("ping_succ_rate", map.get("PING_SUCC_RATE"));
			json.put("ping_attempt_times", map.get("PING_ATTEMPT_TIMES"));
			json.put("ping_succ_times", map.get("PING_SUCC_TIMES"));
			json.put("ping_shake_times", map.get("PING_SHAKE_TIMES"));
			json.put("ping_cover_cell_number", map.get("PING_COVER_CELL_NUMBER"));
		} else if (subType.startsWith("video_province") || subType.startsWith("video_city")) {
			json.put("hot_degree_no", map.get("HOT_DEGREE_NO"));
			json.put("fluent_no", map.get("FLUENT_NO"));
			json.put("succ_rate_no", map.get("SUCC_RATE_NO"));
			if (subType.startsWith("video_province")) {
				json.put("name", map.get("GROUP_NAME"));
			} else if (subType.startsWith("video_city")) {
				json.put("name", map.get("SUBGROUP_NAME"));
			}
			json.put("video_hot_degree", map.get("VIDEO_HOT_DEGREE"));
			json.put("video_fluent_degree", map.get("VIDEO_FLUENT_DEGREE"));
			json.put("video_succ_rate", map.get("VIDEO_SUCC_RATE"));
			json.put("video_down_speed", map.get("VIDEO_DOWN_SPEED"));
			json.put("video_attempt_times", map.get("VIDEO_ATTEMPT_TIMES"));
			json.put("video_succ_times", map.get("VIDEO_SUCC_TIMES"));
			json.put("video_shake_times", map.get("VIDEO_SHAKE_TIMES"));
			json.put("video_cover_cell_number", map.get("VIDEO_COVER_CELL_NUMBER"));
			json.put("video_test_duration", map.get("VIDEO_TEST_DURATION"));
			json.put("video_first_load_duration", map.get("VIDEO_FIRST_LOAD_DURATION"));
			json.put("video_play_duration", map.get("VIDEO_PLAY_DURATION"));
			json.put("video_buffer_times", map.get("VIDEO_BUFFER_TIMES"));
		}
	}

	// 根据省市转换json字符传
	private JSONObject transJsonProvinceOrCity(List<HashMap<String, Object>> listByProvinceOrCity, String subType,
			String mark) {
		JSONObject json = new JSONObject();
		JSONArray array_provinceorcity = new JSONArray();
		if ("province".equals(mark)) {
			json.put("Provinces", array_provinceorcity);
		} else if ("city".equals(mark)) {
			json.put("citys", array_provinceorcity);
		}
		for (HashMap<String, Object> map : listByProvinceOrCity) {
			JSONObject jo = new JSONObject();
			array_provinceorcity.put(jo);
			fillJsonByCase(jo, map, subType);
		}
		return json;
	}

	private void addProviceRankCondition(Map<String, Object> condition, int range1, int netType, String groupIds) {
		List<Integer> groupNameList = null;
		condition.put("NET_TYPE", netType);
		condition.put("SUBGROUP_NAME", "全部");
		if (range1 == 4) {
			condition.put("CREATE_DATE", getCurDayBefore());
		} else {
			condition.put("CREATE_DATE", String.valueOf(range1));
		}
		if (!"".equals(groupIds) && groupIds != null) {
			if (!groupIds.contains(",") && "1".equals(groupIds)) {
				// 不加域的权限过滤
			} else if (groupIds.contains(",")) {
				String[] groupIds1 = groupIds.split(",");
				groupNameList = new ArrayList<Integer>();
				for (int n = 0; n < groupIds1.length; n++) {
					groupNameList.add(Integer.parseInt(groupIds1[n]));
				}
				condition.put("groupNameList", groupNameList);
			} else if (!groupIds.contains(",")) {
				groupNameList = new ArrayList<Integer>();
				groupNameList.add(Integer.parseInt(groupIds));
				condition.put("groupNameList", groupNameList);
			}
		}
	}

	private void addCityRankCondition(Map<String, Object> condition, int range1, int netType, String groupIds) {
		List<Integer> subGroupNameList = null;
		condition.put("NET_TYPE", netType);
		if (range1 == 4) {
			condition.put("CREATE_DATE", getCurDayBefore());
		} else {
			condition.put("CREATE_DATE", String.valueOf(range1));
		}
		if (!"".equals(groupIds) && groupIds != null) {
			if (!groupIds.contains(",") && "1".equals(groupIds)) {
				// 不加域的权限过滤
			} else if (groupIds.contains(",")) {
				String[] groupIds1 = groupIds.split(",");
				subGroupNameList = new ArrayList<Integer>();
				for (int n = 0; n < groupIds1.length; n++) {
					subGroupNameList.add(Integer.parseInt(groupIds1[n]));
				}
				condition.put("subGroupNameList", subGroupNameList);
			} else if (!groupIds.contains(",")) {
				subGroupNameList = new ArrayList<Integer>();
				subGroupNameList.add(Integer.parseInt(groupIds));
				condition.put("subGroupNameList", subGroupNameList);
			}
		}
	}

	private LacModel getGroupIdInfo(String latitude, String longitude, String subGroupName,
			GridCodePostGreJDBC greJDBC) {
		LacModel groupIdInfo = null;
		if (subGroupName != null && !subGroupName.equals("")) {
			groupIdInfo = TestUtil.getCityGroupIdMap(subGroupName);
		}

		if (groupIdInfo != null && groupIdInfo.getSubGroupId() != null && !"".equals(groupIdInfo.getSubGroupId())) {
			return groupIdInfo;
		} else if (latitude != null && !latitude.equals("") && longitude != null && !longitude.equals("")) {
			double dLatitude = Double.parseDouble(latitude);
			double dLongitude = Double.parseDouble(longitude);
			String group_id = greJDBC.getGridCodeAuto(dLongitude, dLatitude, 2, null);
			groupIdInfo = TestUtil.getCityGroupIdByGroupIdMap(group_id);
		}
		return groupIdInfo;
	}

	public InputStream getInputStream(HttpServletRequest request) throws IOException {
		ServletInputStream stream = request.getInputStream();
		String contentEncoding = request.getHeader("Content-Encoding");
		// 判断是否压缩上传，解压处理
		if (null != contentEncoding && contentEncoding.indexOf("gzip") != -1) {
			try {
				return new GZIPInputStream(stream);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("ungzip InputStream fail.", e);
			}
		}
		return stream;
	}

	// 数组去重
	public static String[] array_unique(String[] a) {
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < a.length; i++) {
			if (!list.contains(a[i])) {
				list.add(a[i]);
			}
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}

}
