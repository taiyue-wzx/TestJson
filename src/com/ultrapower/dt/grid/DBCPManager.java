package com.ultrapower.dt.grid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DBCPManager {
	private  Logger logger = Logger.getLogger(DBCPManager.class);
	private  Connection conn;
	 

	 

	/**
	 * ��ȡ���ӣ������ǵùر�
	 * 
	 * @see {@link DBCPManager#closeConn(Connection)}
	 * @return
	 */
	public   Connection getConn() {

		try {
			conn =DataSourceManager.getDataSource().getConnection();
		} catch (SQLException e) {
			logger.error("��ȡ���ݿ�����ʧ�ܣ�", e);
		}
		return conn;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  List queryForList(String sql, Object... params) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		List list=new ArrayList();
		try {
			
			 ps = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 1; i <= params.length; i++) {
					ps.setObject(i, params[i - 1]);
				}
			}

			 rs = ps.executeQuery();
			 ResultSetMetaData rsmd= rs.getMetaData();
			 int columnCount=rsmd.getColumnCount();
			 while(rs.next()){
				 Map map=new HashMap();
				 for(int i=1;i<=columnCount;i++){
					 map.put(rsmd.getColumnName(i).toUpperCase(),  rs.getObject(i));
				 }
				 list.add(map);
			 }
			rs.close();
			ps.close();
		} catch (Exception e) {
			logger.error("��ѯ����ʧ��", e);
			try {
				if(!rs.isClosed()){
					rs.close();
				}
				if(!ps.isClosed()){
					ps.close();
				}
			} catch (SQLException e1) {
				logger.error("�ر�psʧ�ܣ�", e1);
			}
		}

		return list;
	}

	/**
	 * �ر�����
	 * 
	 * @param conn
	 *            ��Ҫ�رյ�����
	 */
	public  void closeConn() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (SQLException e) {
			logger.error("�ر����ݿ�����ʧ�ܣ�", e);
		} 
	}
}