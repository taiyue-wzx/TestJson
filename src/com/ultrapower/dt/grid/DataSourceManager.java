package com.ultrapower.dt.grid;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

public class DataSourceManager {
	private static Logger logger = Logger.getLogger(DataSourceManager.class);
	private static final String configFile = "dbconfig/dbcp.properties";
	private static DataSource dataSource;
	static {
		Properties dbProperties = new Properties();
		try {
			dbProperties.load(DBCPManager.class.getClassLoader().getResourceAsStream(configFile));
			dataSource = BasicDataSourceFactory.createDataSource(dbProperties);
			 
		} catch (Exception e) {
			logger.error("��ʼ�����ӳ�ʧ�ܣ�" + e);
		}
	}
	
	public static DataSource getDataSource(){
		 
			return dataSource;
		 
	}
}
