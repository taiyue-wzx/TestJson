package com.ultrapower.android.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.ultrapower.android.util.json.JSONObject;

public class JsonDataQualityManage {
	static String NETTYPE_ALL = ",WLAN,GPRS,EDGE,UMTS,CDMA,EVDO,1xRTT,HSDPA,HSUPA,HSPA,IDEN,LTE,HSPA+,UNKNOWN,GSM,TD_SCDMA,EVDO,EHRPD,";
	static String NETTYPE_TD_WCDMA = ",UMTS,HSDPA,HSUPA,HSPA,HSPA+,TD_SCDMA,EVDO,EHRPD,";
	static String STATUS_WHITE = "5";
	static String STATUS_BLACK = "3";

	public static String[] jsonQualityState(JSONObject log) {
		String[] strTemp = { STATUS_WHITE, null };
		if (log != null) {
			/** 公共字段 */
			if (isExistField(log, "APP_LOGTIME") || null_negative_black(log.getString("APP_LOGTIME"))|| bigger_paraTime_black(log.getString("APP_LOGTIME"), new Date().getTime()+604800000l+"")) {
				return returnAndlogPrint("APP_LOGTIME");
			}
			if (isExistField(log, "APP_NET_TYPE") || out_netType_black(log.getString("APP_NET_TYPE"))) {
				return returnAndlogPrint("APP_NET_TYPE");
			}
			if (isExistField(log, "APP_MCC", "APP_NET_TYPE") || out_APP_MCC_black(log.getString("APP_MCC"), log.getString("APP_NET_TYPE"))) {
				return returnAndlogPrint("APP_MCC");
			}
			if (isExistField(log, "APP_MNC", "APP_NET_TYPE") || out_APP_MNC_black(log.getString("APP_MNC"), log.getString("APP_NET_TYPE"))) {
				return returnAndlogPrint("APP_MNC");
			}
			if (isExistField(log, "APP_CI") || out_num1_num2_notend_black(log.getString("APP_CI"), 0, 16777216)) {
				return returnAndlogPrint("APP_CI");
			}
			if (isExistField(log, "APP_RXLEVEL") || out_negative_f1_f2_black(log.getString("APP_RXLEVEL"), -120, -25)) {
				return returnAndlogPrint("APP_RXLEVEL");
			}
			if (isExistField(log, "APP_RSRP") || out_negative_f1_f2_black(log.getString("APP_RSRP"), -140, -40)) {
				return returnAndlogPrint("APP_RSRP");
			}
			/** 语音独有 */
			if ("VOICE_SERVICE".equals(log.getString("APP_SERVICEREQUEST")) ) {
				if (isExistField(log, "APP_URL") || isMsisdn(log.getString("APP_URL"))) {
					return returnAndlogPrint("APP_URL");
				}
				if (isExistField(log, "APP_DATA_SIZE") || null_negative_black(log.getString("APP_DATA_SIZE"))) {
					return returnAndlogPrint("APP_DATA_SIZE");
				}
				if (isExistField(log, "APP_DOWNLOAD_TIME") || bigger_APP_MAX_black(log.getString("APP_DOWNLOAD_TIME"), "0")) {
					return returnAndlogPrint("APP_DOWNLOAD_TIME");
				}
				if (isExistField(log, "APP_SENT_NUMBER") || null_negative_black(log.getString("APP_SENT_NUMBER"))) {
					return returnAndlogPrint("APP_SENT_NUMBER");
				}
				if (isExistField(log, "APP_SUCC_RECEIVED_NUMBER") || null_negative_black(log.getString("APP_SUCC_RECEIVED_NUMBER"))) {
					return returnAndlogPrint("APP_SUCC_RECEIVED_NUMBER");
				}
				if (isExistField(log, "APP_CON_TYPE") || out_num1_num2_black(log.getString("APP_CON_TYPE"),0,8)) {
					return returnAndlogPrint("APP_CON_TYPE");
				}
				if (isExistField(log, "APP_LOGIN_ETIME", "APP_CON_ETIME") || bigger_APP_MAX_black(log.getString("APP_LOGIN_ETIME"), log.getString("APP_CON_ETIME"))) {
					return returnAndlogPrint("APP_LOGIN_ETIME");
				}
				if (isExistField(log, "APP_RES_TIME") || bigger_APP_MAX_black(log.getString("APP_RES_TIME"), log.getString("APP_LOGIN_ETIME"))) {
					return returnAndlogPrint("APP_RES_TIME");
				}
				if (isExistField(log, "APP_RES_TYPE") || ",1,2,3,".indexOf(log.getString("APP_RES_TYPE"))<0) {
					return returnAndlogPrint("APP_RES_TYPE");
				}
				if (isExistField(log, "APP_OPEN_TIME") || bigger_APP_MAX_black(log.getString("APP_OPEN_TIME"),"0")) {
					return returnAndlogPrint("APP_OPEN_TIME");
				}
				if (isExistField(log, "APP_CUSHION_TIME") || bigger_APP_MAX_black(log.getString("APP_CUSHION_TIME"), "0")) {
					return returnAndlogPrint("APP_CUSHION_TIME");
				}
				if (isExistField(log, "APP_ENCODING_INFORMATION") || out_num1_num2_black(log.getString("APP_ENCODING_INFORMATION"),0,8)) {
					return returnAndlogPrint("APP_ENCODING_INFORMATION");
				}
				if (isExistField(log, "APP_TTL_MIN") ||out_num1_num2_notstart_black1(log.getString("APP_TTL_MIN"), 0, 200*1024)) {
					return returnAndlogPrint("APP_TTL_MIN");
				}
				if (isExistField(log, "APP_TTL_MAX") ||out_num1_num2_notstart_black1(log.getString("APP_TTL_MAX"), 0, 200*1024)) {
					return returnAndlogPrint("APP_TTL_MAX");
				}
			}
			/** FTP独有 */
			if ("FTP_DOWNLOAD".equals(log.getString("APP_SERVICEREQUEST")) || "FTP_UPLOAD".equals(log.getString("APP_SERVICEREQUEST"))) {
				if (isExistField(log, "APP_VERSION") || is_null_black(log.getString("APP_VERSION"))) {
					return returnAndlogPrint("APP_VERSION");
				}
				if (isExistField(log, "APP_MAX", "APP_MIN") || bigger_APP_MAX_black(log.getString("APP_MAX"), log.getString("APP_MIN"))) {
					return returnAndlogPrint("APP_MIN");
				}
				if ((isExistField(log, "APP_AVERAGE", "APP_MIN", "APP_MAX") || out_num1_num2_black(log.getString("APP_AVERAGE"),
						string2float(log.getString("APP_MIN")), string2float(log.getString("APP_MAX"))))
						|| (isExistField(log, "APP_AVERAGE", "APP_NET_TYPE", "APP_MCC") || out_APP_AVERAGE_black(log.getString("APP_AVERAGE"),
								log.getString("APP_NET_TYPE"), log.getString("APP_MCC")))) {
					return returnAndlogPrint("APP_AVERAGE");
				}
				
				if (isExistField(log, "APP_DOWNLOAD_TIME") || negative_number_black(log.getString("APP_DOWNLOAD_TIME"))
						|| out_num1_num2_notend_black(log.getString("APP_DOWNLOAD_TIME"), 0, 600)) {
					return returnAndlogPrint("APP_DOWNLOAD_TIME");
				}
				if (isExistField(log, "APP_SUCC_RECEIVED_NUMBER","APP_SENT_NUMBER") || null_negative_black(log.getString("APP_SUCC_RECEIVED_NUMBER"))||bigger_APP_MAX_black(log.getString("APP_SENT_NUMBER"), log.getString("APP_SUCC_RECEIVED_NUMBER"))) {
					return returnAndlogPrint("APP_SUCC_RECEIVED_NUMBER");
				}

				if (isExistField(log, "APP_CON_STIME", "APP_LOGTIME") || bigger_paraTime_black(log.getString("APP_CON_STIME"), log.getString("APP_LOGTIME"))) {
					return returnAndlogPrint("APP_CON_STIME");
				}
				if (isExistField(log, "APP_CON_ETIME", "APP_CON_STIME")
						|| litter_paraTime_black(log.getString("APP_CON_ETIME"), log.getString("APP_CON_STIME"))) {
					return returnAndlogPrint("APP_CON_ETIME");
				}
				if (isExistField(log, "APP_LOGIN_STIME", "APP_CON_ETIME")
						|| litter_paraTime_black(log.getString("APP_LOGIN_STIME"), log.getString("APP_CON_ETIME"))) {
					return returnAndlogPrint("APP_LOGIN_STIME");
				}
				if (isExistField(log, "APP_LOGIN_ETIME", "APP_LOGIN_STIME")
						|| litter_paraTime_black(log.getString("APP_LOGIN_ETIME"), log.getString("APP_LOGIN_STIME"))) {
					return returnAndlogPrint("APP_LOGIN_ETIME");
				}
				if (isExistField(log, "APP_REQ_TIME", "APP_LOGIN_ETIME")
						|| litter_paraTime_black(log.getString("APP_REQ_TIME"), log.getString("APP_LOGIN_ETIME"))) {
					return returnAndlogPrint("APP_REQ_TIME");
				}
				if (isExistField(log, "APP_RES_TIME", "APP_REQ_TIME") || litter_paraTime_black(log.getString("APP_RES_TIME"), log.getString("APP_REQ_TIME"))) {
					return returnAndlogPrint("APP_RES_TIME");
				}
				 if (isExistField(log, "APP_CUSHION_TIME") || out_num1_num2_notstart_black(log.getString("APP_CUSHION_TIME"), 0, 120)) {
					 return returnAndlogPrint("APP_CUSHION_TIME");
				 }
				if (isExistField(log, "APP_CUSHION_TOTAL_TIME") || out_num1_num2_notstart_black(log.getString("APP_CUSHION_TOTAL_TIME"), 0, 99999999)) {
					return returnAndlogPrint("APP_CUSHION_TOTAL_TIME");
				}

			}
			/** 流媒体独有 */
			if ("VIDEO".equals(log.getString("APP_SERVICEREQUEST"))) {
				if (isExistField(log, "APP_VERSION") || is_null_black(log.getString("APP_VERSION"))) {
					return returnAndlogPrint("APP_VERSION");
				}
				if (isExistField(log, "APP_MAX", "APP_MIN") || bigger_APP_MAX_black(log.getString("APP_MAX"), log.getString("APP_MIN"))) {
					return returnAndlogPrint("APP_MIN");
				}
				if ((isExistField(log, "APP_AVERAGE", "APP_MIN", "APP_MAX") || out_num1_num2_black(log.getString("APP_AVERAGE"),
						string2float(log.getString("APP_MIN")), string2float(log.getString("APP_MAX"))))
						|| (isExistField(log, "APP_AVERAGE", "APP_NET_TYPE", "APP_MCC") || out_APP_AVERAGE_black(log.getString("APP_AVERAGE"),
								log.getString("APP_NET_TYPE"), log.getString("APP_MCC")))) {
					return returnAndlogPrint("APP_AVERAGE");
				}
				if (isExistField(log, "APP_DOWNLOAD_TIME") || negative_number_black(log.getString("APP_DOWNLOAD_TIME"))
						|| out_num1_num2_notend_black(log.getString("APP_DOWNLOAD_TIME"), 0, 600)) {
					return returnAndlogPrint("APP_DOWNLOAD_TIME");
				}
				if (isExistField(log, "APP_SUCC_RECEIVED_NUMBER","APP_SENT_NUMBER") || null_negative_black(log.getString("APP_SUCC_RECEIVED_NUMBER"))||bigger_APP_MAX_black(log.getString("APP_SENT_NUMBER"), log.getString("APP_SUCC_RECEIVED_NUMBER"))) {
					return returnAndlogPrint("APP_SUCC_RECEIVED_NUMBER");
				}

				if (isExistField(log, "APP_CON_STIME", "APP_LOGTIME") || bigger_paraTime_black(log.getString("APP_CON_STIME"), log.getString("APP_LOGTIME"))) {
					return returnAndlogPrint("APP_CON_STIME");
				}
				if (isExistField(log, "APP_CON_ETIME", "APP_CON_STIME")
						|| litter_paraTime_black(log.getString("APP_CON_ETIME"), log.getString("APP_CON_STIME"))) {
					return returnAndlogPrint("APP_CON_ETIME");
				}
				if (isExistField(log, "APP_LOGIN_STIME", "APP_CON_ETIME")
						|| litter_paraTime_black(log.getString("APP_LOGIN_STIME"), log.getString("APP_CON_ETIME"))) {
					return returnAndlogPrint("APP_LOGIN_STIME");
				}
				if (isExistField(log, "APP_LOGIN_ETIME", "APP_LOGIN_STIME")
						|| litter_paraTime_black(log.getString("APP_LOGIN_ETIME"), log.getString("APP_LOGIN_STIME"))) {
					return returnAndlogPrint("APP_LOGIN_ETIME");
				}
				if (isExistField(log, "APP_REQ_TIME", "APP_LOGTIME") || bigger_paraTime_black(log.getString("APP_REQ_TIME"), log.getString("APP_LOGTIME"))) {
					return returnAndlogPrint("APP_REQ_TIME");
				}
				if (isExistField(log, "APP_RES_TIME", "APP_REQ_TIME") || litter_paraTime_black(log.getString("APP_RES_TIME"), log.getString("APP_REQ_TIME"))) {
					return returnAndlogPrint("APP_RES_TIME");
				}
				if (isExistField(log, "APP_PROGRAM_TIME") || out_num1_num2_notend_black(log.getString("APP_PROGRAM_TIME"), 0, 600)) {
					return returnAndlogPrint("APP_PROGRAM_TIME");
				}
				if (isExistField(log, "APP_PLAY_TIME") || out_num1_num2_notend_black(log.getString("APP_PLAY_TIME"), 0, 600)
						|| bigger_APP_MAX_black(log.getString("APP_PROGRAM_TIME"), log.getString("APP_PLAY_TIME"))) {
					return returnAndlogPrint("APP_PLAY_TIME");
				}
				if (isExistField(log, "APP_OPEN_TIME") || out_num1_num2_notend_black(log.getString("APP_OPEN_TIME"), 0, 99999999)
						|| bigger_APP_MAX_black(log.getString("APP_PROGRAM_TIME"), log.getString("APP_OPEN_TIME"))) {
					return returnAndlogPrint("APP_OPEN_TIME");
				}
				if (isExistField(log, "APP_CUSHION_TIME") || out_num1_num2_notend_black(log.getString("APP_CUSHION_TIME"), 0, 99999999)
						|| bigger_APP_MAX_black(log.getString("APP_PROGRAM_TIME"), log.getString("APP_CUSHION_TIME"))) {
					return returnAndlogPrint("APP_CUSHION_TIME");
				}
				if (isExistField(log, "APP_CUSHION_TOTAL_TIME")
						|| bigger_APP_MAX_black(log.getString("APP_PROGRAM_TIME"), log.getString("APP_CUSHION_TOTAL_TIME"))) {
					return returnAndlogPrint("APP_CUSHION_TOTAL_TIME");
				}
			}

			/** HTTP下载独有 */
			if ("HTTP_DOWNLOAD".equals(log.getString("APP_SERVICEREQUEST"))) {
				if (isExistField(log, "APP_MAX", "APP_MIN") || bigger_APP_MAX_black(log.getString("APP_MAX"), log.getString("APP_MIN"))) {
					return returnAndlogPrint("APP_MIN");
				}
				if ((isExistField(log, "APP_AVERAGE", "APP_MIN", "APP_MAX") || out_num1_num2_black(log.getString("APP_AVERAGE"),
						string2float(log.getString("APP_MIN")), string2float(log.getString("APP_MAX"))))
						|| (isExistField(log, "APP_AVERAGE", "APP_NET_TYPE", "APP_MCC") || out_APP_AVERAGE_black(log.getString("APP_AVERAGE"),
								log.getString("APP_NET_TYPE"), log.getString("APP_MCC")))) {
					return returnAndlogPrint("APP_AVERAGE");
				}
				if (isExistField(log, "APP_VERSION") || is_null_black(log.getString("APP_VERSION"))) {
					return returnAndlogPrint("APP_VERSION");
				}
				if (isExistField(log, "APP_DOWNLOAD_TIME") || negative_number_black(log.getString("APP_DOWNLOAD_TIME"))
						|| out_num1_num2_notend_black(log.getString("APP_DOWNLOAD_TIME"), 0, 600)) {
					return returnAndlogPrint("APP_DOWNLOAD_TIME");
				}
				if (isExistField(log, "APP_SUCC_RECEIVED_NUMBER","APP_SENT_NUMBER") || null_negative_black(log.getString("APP_SUCC_RECEIVED_NUMBER"))||bigger_APP_MAX_black(log.getString("APP_SENT_NUMBER"), log.getString("APP_SUCC_RECEIVED_NUMBER"))) {
					return returnAndlogPrint("APP_SUCC_RECEIVED_NUMBER");
				}

				if (isExistField(log, "APP_CON_STIME", "APP_LOGTIME") || bigger_paraTime_black(log.getString("APP_CON_STIME"), log.getString("APP_LOGTIME"))) {
					return returnAndlogPrint("APP_CON_STIME");
				}
				if (isExistField(log, "APP_CON_ETIME", "APP_CON_STIME")
						|| litter_paraTime_black(log.getString("APP_CON_ETIME"), log.getString("APP_CON_STIME"))) {
					return returnAndlogPrint("APP_CON_ETIME");
				}
				if (isExistField(log, "APP_REQ_TIME", "APP_CON_ETIME") || litter_paraTime_black(log.getString("APP_REQ_TIME"), log.getString("APP_CON_ETIME"))) {
					return returnAndlogPrint("APP_REQ_TIME");
				}
				if (isExistField(log, "APP_RES_TIME", "APP_REQ_TIME") || litter_paraTime_black(log.getString("APP_RES_TIME"), log.getString("APP_REQ_TIME"))) {
					return returnAndlogPrint("APP_RES_TIME");
				}
				if (isExistField(log, "APP_CUSHION_TIME") || out_num1_num2_notstart_black(log.getString("APP_CUSHION_TIME"), 0, 120)) {
					return returnAndlogPrint("APP_CUSHION_TIME");
				}

			}

			/** HTTP上传独有 */
			if ("HTTP_UPLOAD".equals(log.getString("APP_SERVICEREQUEST"))) {
				if (isExistField(log, "APP_MAX", "APP_MIN") || bigger_APP_MAX_black(log.getString("APP_MAX"), log.getString("APP_MIN"))) {
					return returnAndlogPrint("APP_MIN");
				}
				if ((isExistField(log, "APP_AVERAGE", "APP_MIN", "APP_MAX") || out_num1_num2_black(log.getString("APP_AVERAGE"),
						string2float(log.getString("APP_MIN")), string2float(log.getString("APP_MAX"))))
						|| (isExistField(log, "APP_AVERAGE", "APP_NET_TYPE", "APP_MCC") || out_APP_AVERAGE_black(log.getString("APP_AVERAGE"),
								log.getString("APP_NET_TYPE"), log.getString("APP_MCC")))) {
					return returnAndlogPrint("APP_AVERAGE");
				}
				if (isExistField(log, "APP_VERSION") || is_null_black(log.getString("APP_VERSION"))) {
					return returnAndlogPrint("APP_VERSION");
				}
				if (isExistField(log, "APP_DOWNLOAD_TIME") || negative_number_black(log.getString("APP_DOWNLOAD_TIME"))
						|| out_num1_num2_notend_black(log.getString("APP_DOWNLOAD_TIME"), 0, 600)) {
					return returnAndlogPrint("APP_DOWNLOAD_TIME");
				}
				if (isExistField(log, "APP_SUCC_RECEIVED_NUMBER","APP_SENT_NUMBER") || null_negative_black(log.getString("APP_SUCC_RECEIVED_NUMBER"))||bigger_APP_MAX_black(log.getString("APP_SENT_NUMBER"), log.getString("APP_SUCC_RECEIVED_NUMBER"))) {
					return returnAndlogPrint("APP_SUCC_RECEIVED_NUMBER");
				}

				if (isExistField(log, "APP_CON_STIME", "APP_LOGTIME") || bigger_paraTime_black(log.getString("APP_CON_STIME"), log.getString("APP_LOGTIME"))) {
					return returnAndlogPrint("APP_CON_STIME");
				}
				if (isExistField(log, "APP_CON_ETIME", "APP_CON_STIME")
						|| litter_paraTime_black(log.getString("APP_CON_ETIME"), log.getString("APP_CON_STIME"))) {
					return returnAndlogPrint("APP_CON_ETIME");
				}
				if (isExistField(log, "APP_RES_TIME", "APP_CON_STIME") || litter_paraTime_black(log.getString("APP_RES_TIME"), log.getString("APP_CON_STIME"))) {
					return returnAndlogPrint("APP_RES_TIME");
				}
				if (isExistField(log, "APP_CUSHION_TIME") || out_num1_num2_notstart_black(log.getString("APP_CUSHION_TIME"), 0, 120)) {
					return returnAndlogPrint("APP_CUSHION_TIME");
				}
			}

			/** HTTP网页独有 */
			if ("HTTP".equals(log.getString("APP_SERVICEREQUEST"))) {
				if (isExistField(log, "APP_MAX", "APP_MIN") || bigger_APP_MAX_black(log.getString("APP_MAX"), log.getString("APP_MIN"))) {
					return returnAndlogPrint("APP_MIN");
				}
				if ((isExistField(log, "APP_AVERAGE", "APP_MIN", "APP_MAX") || out_num1_num2_black(log.getString("APP_AVERAGE"),
						string2float(log.getString("APP_MIN")), string2float(log.getString("APP_MAX"))))
						|| (isExistField(log, "APP_AVERAGE", "APP_NET_TYPE", "APP_MCC") || out_APP_AVERAGE_black(log.getString("APP_AVERAGE"),
								log.getString("APP_NET_TYPE"), log.getString("APP_MCC")))) {
					return returnAndlogPrint("APP_AVERAGE");
				}
				if (isExistField(log, "APP_VERSION") || is_null_black(log.getString("APP_VERSION"))) {
					return returnAndlogPrint("APP_VERSION");
				}
				if (isExistField(log, "APP_DOWNLOAD_TIME") || negative_number_black(log.getString("APP_DOWNLOAD_TIME"))
						|| out_num1_num2_notend_black(log.getString("APP_DOWNLOAD_TIME"), 0, 600)) {
					return returnAndlogPrint("APP_DOWNLOAD_TIME");
				}
				if (isExistField(log, "APP_SUCC_RECEIVED_NUMBER","APP_SENT_NUMBER") || null_negative_black(log.getString("APP_SUCC_RECEIVED_NUMBER"))||bigger_APP_MAX_black(log.getString("APP_SENT_NUMBER"), log.getString("APP_SUCC_RECEIVED_NUMBER"))) {
					return returnAndlogPrint("APP_SUCC_RECEIVED_NUMBER");
				}

				if (isExistField(log, "APP_CON_STIME", "APP_LOGTIME") || bigger_paraTime_black(log.getString("APP_CON_STIME"), log.getString("APP_LOGTIME"))) {
					return returnAndlogPrint("APP_CON_STIME");
				}
				if (isExistField(log, "APP_CON_ETIME", "APP_CON_STIME")
						|| litter_paraTime_black(log.getString("APP_CON_ETIME"), log.getString("APP_CON_STIME"))) {
					return returnAndlogPrint("APP_CON_ETIME");
				}
				if (isExistField(log, "APP_REQ_TIME", "APP_CON_STIME") || litter_paraTime_black(log.getString("APP_REQ_TIME"), log.getString("APP_CON_STIME"))) {
					return returnAndlogPrint("APP_REQ_TIME");
				}
				if (isExistField(log, "APP_RES_TIME", "APP_REQ_TIME") || litter_paraTime_black(log.getString("APP_RES_TIME"), log.getString("APP_REQ_TIME"))) {
					return returnAndlogPrint("APP_RES_TIME");
				}
				if (isExistField(log, "APP_USER_FEELING_TIME") || out_num1_num2_black(log.getString("APP_USER_FEELING_TIME"), 0, 120)) {
					return returnAndlogPrint("APP_USER_FEELING_TIME");
				}
				if (isExistField(log, "APP_CUSHION_TIME") || out_num1_num2_notend_black(log.getString("APP_CUSHION_TIME"), 0, 120)) {
					return returnAndlogPrint("APP_CUSHION_TIME");
				}
			}
			/** PING独有 */
			if ("PING".equals(log.getString("APP_SERVICEREQUEST"))) {
				if (isExistField(log, "APP_MAX") || out_num1_num2_black(log.getString("APP_MAX"), 0, 120000)) {
					return returnAndlogPrint("APP_MAX");
				}
				if ((isExistField(log, "APP_AVERAGE", "APP_MIN", "APP_MAX") || out_num1_num2_black(log.getString("APP_AVERAGE"),
						string2float(log.getString("APP_MIN")), string2float(log.getString("APP_MAX"))))
						|| (isExistField(log, "APP_AVERAGE") || out_num1_num2_black(log.getString("APP_AVERAGE"), 0, 120000))) {
					return returnAndlogPrint("APP_AVERAGE");
				}
				if (isExistField(log, "APP_VERSION") || is_null_black(log.getString("APP_VERSION"))) {
					return returnAndlogPrint("APP_VERSION");
				}
				if (isExistField(log, "APP_SHAKE_NUMBER") || out_num1_num2_black(log.getString("APP_SHAKE_NUMBER"), 0, 5000)) {
					return returnAndlogPrint("APP_SHAKE_NUMBER");
				}
			}
		}
		return strTemp;
	}

	/**
	 * @param 如果为空返回true
	 * @return
	 */
	private static boolean is_null_black(String field) {
		if (isStrNullUtil(field)) {
			return true;
		}
		return false;
	}

	/**
	 * @param 如果转化为空或者负数返回true
	 * @return
	 */
	private static boolean null_negative_black(String field) {
		if (isStrNullUtil(field)) {
			return true;
		} else {
			if (!RegexCheck.checkNumber(field)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param 如果为空或不在类型范围内返回true
	 *            WLAN,GPRS,EDGE,UMTS,CDMA,EVDO,1xRTT,HSDPA,HSUPA,HSPA,IDEN,LTE,
	 *            HSPA,UNKNOWN
	 * @return
	 */
	private static boolean out_netType_black(String field) {
		if (isStrNullUtil(field)) {
			return true;
		} else if ((NETTYPE_ALL).indexOf("," + field + ",") == -1) {
			return true;
		}
		return false;
	}

	/**
	 * @param APP_MCC
	 *            100-999 超出范围返回true
	 * @return
	 */
	private static boolean out_APP_MCC_black(String field, String netType) {
		if ("WLAN".equals(netType) && isStrNullUtil(field)) {
			return false;
		} else if (!"WLAN".equals(netType) && isStrNullUtil(field)) {
			return true;
		} else {
			if (RegexCheck.checkNumber(field)) {
				int app_mcc = Integer.parseInt(field);
				if (app_mcc < 100 || app_mcc > 999) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param APP_MNC
	 *            0到99 超出范围返回true
	 * @return
	 */
	private static boolean out_APP_MNC_black(String field, String netType) {
		if ("WLAN".equals(netType) && isStrNullUtil(field)) {
			return false;
		} else if (!"WLAN".equals(netType) && isStrNullUtil(field)) {
			return true;
		} else {
			if (RegexCheck.checkNumber(field)) {
				int app_mcc = Integer.parseInt(field);
				if (app_mcc < 0 || app_mcc > 99) {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param 超出负数f1到f2范围返回true
	 *            ，空返回false
	 * 
	 * @return
	 */
	private static boolean out_negative_f1_f2_black(String field, float f1, float f2) {
		if (isStrNullUtil(field)) {
			return false;
		}
		if (RegexCheck.checkNegativeFloat(field)) {
			float app_rxlevel = Float.parseFloat(field);
			if (app_rxlevel < f1 || app_rxlevel > f2) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @param 超出正整数正小数num1到num2范围返回true
	 *            ，空返回false
	 * 
	 * @return
	 */
	private static boolean out_num1_num2_black(String field, float num1, float num2) {
		if (isStrNullUtil(field)) {
			return false;
		}
		if (RegexCheck.checkIntOrDecimal(field)) {
			float app_field_time = Float.parseFloat(field);
			if ((app_field_time > 0 && num1 == 0 && num2 == 0) || app_field_time < num1 || app_field_time > num2) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param 超出正整数正小数num1到num2
	 *            (含)范围返回true，空返回false
	 * @return
	 */
	private static boolean out_num1_num2_notend_black(String field, float num1, float num2) {
		if (isStrNullUtil(field)) {
			return false;
		}
		if (RegexCheck.checkIntOrDecimal(field)) {
			float app_field_time = Float.parseFloat(field);
			if ((app_field_time > 0 && num1 == 0 && num2 == 0) || app_field_time < num1 || app_field_time >= num2) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @param 超出正整数正小数num1
	 *            (含)到num2范围返回true，空返回false
	 * @return
	 */
	private static boolean out_num1_num2_notstart_black(String field, float num1, float num2) {
		if (isStrNullUtil(field)) {
			return false;
		}
		if (RegexCheck.checkIntOrDecimal(field)) {
			float app_field_time = Float.parseFloat(field);
			if ((app_field_time > 0 && num1 == 0 && num2 == 0) || app_field_time <= num1 || app_field_time > num2) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * @param 如果Min大于MAX返回true
	 *            ，空返回false
	 * 
	 * @return
	 */
	private static boolean bigger_APP_MAX_black(String fieldMax, String fieldMin) {
		if (isStrNullUtil(fieldMax) || isStrNullUtil(fieldMin)) {
			return false;
		} else {
			if (RegexCheck.checkIntOrDecimal(fieldMax) && RegexCheck.checkIntOrDecimal(fieldMin)) {
				float app_max = Float.parseFloat(fieldMax);
				float app_min = Float.parseFloat(fieldMin);
				if (app_min > app_max) {
					return true;
				}
			} else {
				return true;
			}

		}
		return false;
	}

	/**
	 * @param 分网络判断平均下载速率
	 *            ，超出范围返回true，空返回false
	 * @return
	 */
	private static boolean out_APP_AVERAGE_black(String field, String net_type, String mnc) {
		if (isStrNullUtil(field)) {
			return false;
		} else {
			if (RegexCheck.checkIntOrDecimal(field)) {
				float app_average = Float.parseFloat(field);
				// GSM网络 GPRS,EDGE 280kbps
				if (("GPRS".equals(net_type) || "EDGE".equals(net_type)) && (app_average < 0 || app_average > 280)) {
					return true;
				}
				// TD网络:MNC=00/02/07时UMTS,HSDPA,HSUPA,HSPA,HSPA+ 2900kbps
				if (("00".equals(mnc) || "02".equals(mnc) || "07".equals(mnc)) && (NETTYPE_TD_WCDMA.indexOf("," + net_type + ",") == -1)
						&& (app_average < 0 || app_average > 2900)) {
					return true;
				}
				// WCDMA网络：MNC=01时，UMTS,HSDPA,HSUPA,HSPA,HSPA+ 43000kbps
				if ("01".equals(mnc) && (NETTYPE_TD_WCDMA.indexOf("," + net_type + ",") == -1) && (app_average < 0 || app_average > 43000)) {
					return true;
				}
				// EVDO网络：EVDO 7000kbps
				if ("EVDO".equals(net_type) && (app_average < 0 || app_average > 7000)) {
					return true;
				}
				// CDMA网络：CDMA，1XRRT 280kbps
				if (("CDMA".equals(net_type) || "1XRRT".equals(net_type)) && (app_average < 0 || app_average > 280)) {
					return true;
				}
				// LTE网络：LTE 154000kbps
				if (("LTE".equals(net_type)) && (app_average < 0 || app_average > 154000)) {
					return true;
				}
			} else {
				return true;
			}

		}
		return false;
	}

	/**
	 * 如果转化后为负数返回true，空返回false
	 * 
	 * @param
	 * @return
	 */
	private static boolean negative_number_black(String field) {
		if (isStrNullUtil(field)) {
			return false;
		} else {
			if (!RegexCheck.checkIntOrDecimal(field)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @param itselfTime
	 *            (本身参数)如果转化后为负数或晚于 paraTime(比较参数)返回true，空返回false
	 * @return
	 */
	private static boolean bigger_paraTime_black(String itselfTime, String paraTime) {
		if (isStrNullUtil(itselfTime)) {
			return false;
		}
		if (negative_number_black(itselfTime)) {
			return true;
		}
		if (RegexCheck.checkIntOrDecimal(itselfTime) && RegexCheck.checkIntOrDecimal(paraTime)) {
			long app_itselfTime = Long.parseLong(removeDecimalPointBack(itselfTime));
			long app_paraTime = Long.parseLong(removeDecimalPointBack(paraTime));
			if (app_itselfTime > app_paraTime) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @param itselfTime
	 *            (本身参数)如果转化后为负数或早于 paraTime(比较参数)返回true，空返回false
	 * @return
	 */
	private static boolean litter_paraTime_black(String itselfTime, String paraTime) {
		if (negative_number_black(itselfTime)) {
			return true;
		}
		if (RegexCheck.checkNumber(itselfTime) && RegexCheck.checkNumber(paraTime)) {
			long app_itselfTime = Long.parseLong(removeDecimalPointBack(itselfTime));
			long app_paraTime = Long.parseLong(removeDecimalPointBack(paraTime));
			if (app_itselfTime < app_paraTime) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符串是否为空 空放回true 不为空返回false
	 * 
	 * @param str
	 */
	private static boolean isStrNullUtil(String str) {
		boolean f = true;
		if (str != null && !str.equals("")) {
			f = false;
		}
		return f;
	}

	/**
	 * 去掉字符串后的小数点的数字，返回小数点前的数字
	 * 
	 * @param str
	 * @return
	 */
	public static String removeDecimalPointBack(String str) {
		String s = "";
		if (str != null && !str.equals("") && str.indexOf(".") > -1) {
			s = StringUtils.split(str, ".")[0];
			return s;
		} else {
			return str;
		}

	}

	/**
	 * String转float
	 * 
	 * @param str
	 * @return
	 */
	public static float string2float(String str) {
		float app_max = 0;
		if (RegexCheck.checkIntOrDecimal(str)) {
			app_max = Float.parseFloat(str);
		}
		return app_max;
	}

	/**
	 * 返回3（标黑）、log打印、插入数据库
	 * 
	 * @param str
	 */
	private static String[] returnAndlogPrint(String str) {
		String[] strTemp = { STATUS_BLACK, str };
		return strTemp;
	}

	/**
	 * 判断是否存在这个字段,判断是否为string，如果存在返回true，不存在返回false
	 * 
	 * @param str
	 */
	private static boolean isExistField(JSONObject log, String... field) {
		boolean flag = true;
		if (field != null && field.length > 0) {
			for (int i = 0; i < field.length; i++) {
				if (log.has(field[i]) && log.get(field[i]) instanceof String) {
					flag = flag && false;
				} else {
					flag = true;
					break;
				}
			}
		} else {
			flag = true;
		}

		return flag;
	}

	public static void main(String[] args) {

		try {
			String s = "中文";
			String s1 = new String(s.getBytes("GBK"), "iso-8859-1");
			String s8 = new String(s1.getBytes("iso-8859-1"), "UTF-8");
			System.out.println(new String(s.getBytes(), "GBK"));
			System.out.println(new String(s1.getBytes("iso-8859-1"), "GBK"));
			System.out.println(new String(s8.getBytes("UTF-8"), "GBK"));
			System.out.println(s.equals(new String(s.getBytes(), "GBK")));
			System.out.println(s.equals(new String(s1.getBytes("iso-8859-1"), "GBK")));
			System.out.println(s.equals(new String(new String(s8.getBytes("UTF-8"), "iso-8859-1").getBytes("iso-8859-1"), "GBK")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// System.out.println(JsonDataQualityManage.out_num1_num2_notend_black("100",0,600));
	}
	
	/**
	 * 
	 * @param 11位手机号码验证
	 * @return
	 */
	private static boolean isMsisdn(String msisdn) {
		Pattern pattern = Pattern.compile("/[0-9]{11}");
		  Matcher matcher = pattern.matcher(msisdn);
		  boolean b= matcher.matches();
		return b;
	}
	
	/**
	 * 
	 * 
	 * @param 超出正整数正小数num1
	 *            (含)到num2范围返回true，空返回false
	 * @return
	 */
	private static boolean out_num1_num2_notstart_black1(String field, float num1, float num2) {
		if (isStrNullUtil(field)) {
			return false;
		}
		if (RegexCheck.checkIntOrDecimal(field)) {
			float app_field_time = Float.parseFloat(field);
			if (app_field_time < num1 || app_field_time > num2) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
}
