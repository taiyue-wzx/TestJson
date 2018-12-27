package com.ultrapower.android.dao;

import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class AndroidTestSqlMapFactory {
	private static String resource = "SqlMapConfig.xml";
	private static SqlMapClient sqlMap;
	private static Reader reader;
	protected static Logger logger = LoggerFactory.getLogger(AndroidTestSqlMapFactory.class);

	static {
		try {
			reader = Resources.getResourceAsReader(resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			reader.close();

		} catch (Throwable e) {
			logger.error("SqlMapFactory≥ı ºªØ“Ï≥££∫", e);
			throw new RuntimeException("Error initializing SqlMapFactory class. Cause: " + e);
		}

	}

	public static SqlMapClient getSqlMap() {
		return sqlMap;
	}
}