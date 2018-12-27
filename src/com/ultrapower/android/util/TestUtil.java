package com.ultrapower.android.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ultrapower.android.model.LacModel;

public class TestUtil {

	private static final Map<String, LacModel> lacMap;
	private static final Map<String, LacModel> cityGroupIdMap;
	private static final Map<String, LacModel> cityGroupIdByGroupIdMap;
	private static final List<LacModel> cityGroupIdByOtherNameMap;

	static {
		lacMap = new HashMap<String, LacModel>();
		cityGroupIdMap = new HashMap<String, LacModel>();
		cityGroupIdByGroupIdMap = new HashMap<String, LacModel>();
		cityGroupIdByOtherNameMap = new ArrayList<LacModel>();
		fillLacMap("GPRS");
		fillLacMap("EDGE");
		fillLacMap("HSDPA");
		fillLacMap("WLAN");
		fillLacMap("HSPA");
		fillCityGroupIdMap();
		fillCityGroupIdByOtherNameMap();
		fillCityGroupIdByGroupIdMap();
	}

	private static void fillLacMap(String netType) {
		String lacFileName = null;
		if (netType.equals("GPRS") || netType.equals("EDGE")) {
			lacFileName = "lac_gsm.txt";
		} else if (netType.equals("HSDPA") || netType.equals("WLAN") || netType.equals("HSPA")) {
			lacFileName = "lac_td.txt";
		}
		URL url = Thread.currentThread().getContextClassLoader().getResource(lacFileName);
		File source = new File(url.toString().split("file:")[1]);
		BufferedReader reader = null;
		String dataLine = null;
		try {
			reader = new BufferedReader(new FileReader(source));
			while ((dataLine = reader.readLine()) != null) {
				String data[] = dataLine.split(",");
				LacModel model = new LacModel();
				model.setLac(data[0]);
				model.setSubGroupId(data[1]);
				model.setSubGroupName(data[2]);
				model.setGroupId(data[3]);
				model.setGroupName(data[4]);

				lacMap.put(model.getLac() + "_" + netType, model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void fillCityGroupIdMap() {
		String cityGroupId = "city_groupid.txt";
		URL url = Thread.currentThread().getContextClassLoader().getResource(cityGroupId);
		File source = new File(url.toString().split("file:")[1]);
		BufferedReader reader = null;
		String dataLine = null;
		try {
			reader = new BufferedReader(new FileReader(source));
			while ((dataLine = reader.readLine()) != null) {
				String data[] = dataLine.split(",");
				LacModel model = new LacModel();
				model.setSubGroupId(data[0]);
				model.setSubGroupName(data[1]);
				model.setGroupId(data[2]);
				model.setGroupName(data[3]);
				model.setOtherName(data[4]);
				cityGroupIdMap.put(model.getOtherName(), model);
				cityGroupIdMap.put(model.getSubGroupName(), model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void fillCityGroupIdByOtherNameMap() {
		String cityGroupId = "city_groupid.txt";
		URL url = Thread.currentThread().getContextClassLoader().getResource(cityGroupId);
		File source = new File(url.toString().split("file:")[1]);
		BufferedReader reader = null;
		String dataLine = null;
		try {
			reader = new BufferedReader(new FileReader(source));
			while ((dataLine = reader.readLine()) != null) {
				String data[] = dataLine.split(",");
				LacModel model = new LacModel();
				model.setSubGroupId(data[0]);
				model.setSubGroupName(data[1]);
				model.setGroupId(data[2]);
				model.setGroupName(data[3]);
				model.setOtherName(data[4]);
				cityGroupIdByOtherNameMap.add(model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void fillCityGroupIdByGroupIdMap() {
		String cityGroupId = "city_groupid.txt";
		URL url = Thread.currentThread().getContextClassLoader().getResource(cityGroupId);
		File source = new File(url.toString().split("file:")[1]);
		BufferedReader reader = null;
		String dataLine = null;
		try {
			reader = new BufferedReader(new FileReader(source));
			while ((dataLine = reader.readLine()) != null) {
				String data[] = dataLine.split(",");
				LacModel model = new LacModel();
				model.setSubGroupId(data[0]);
				model.setSubGroupName(data[1]);
				model.setGroupId(data[2]);
				model.setGroupName(data[3]);
				model.setOtherName(data[4]);
				cityGroupIdByGroupIdMap.put(model.getSubGroupId(), model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static LacModel getLacModel(String netType, String lac) {
		return lacMap.get(lac + "_" + netType);
	}

	public static LacModel getCityGroupIdMap(String cityName) {
		return cityGroupIdMap.get(cityName);
	}

	public static List<LacModel> getCityGroupIdByOtherNameMap() {
		return cityGroupIdByOtherNameMap;
	}

	public static LacModel getCityGroupIdByGroupIdMap(String groupId) {
		return cityGroupIdByGroupIdMap.get(groupId);
	}

}
