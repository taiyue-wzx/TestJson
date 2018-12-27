package com.ultrapower.android.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ultrapower.android.dao.AndroidTestDAO;
import com.ultrapower.android.model.LacModel;
import com.ultrapower.android.util.TestUtil;

public class UpdateDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	List<LacModel> list = new ArrayList<LacModel>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		list = TestUtil.getCityGroupIdByOtherNameMap();
		for (LacModel lacModel : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("GROUP_ID", lacModel.getGroupId());
			map.put("GROUP_NAME", lacModel.getGroupName());
			map.put("SUBGROUP_ID", lacModel.getSubGroupId());
			map.put("SUBGROUP_NAME", lacModel.getSubGroupName());
			map.put("OTHERNAME", lacModel.getOtherName());
			AndroidTestDAO.getInstance().updateDataFTP(map);
			AndroidTestDAO.getInstance().updateDataHTTP(map);
			AndroidTestDAO.getInstance().updateDataPING(map);
			AndroidTestDAO.getInstance().updateDataVIDEO(map);
			AndroidTestDAO.getInstance().updateDataPUBLIC(map);
		}
		PrintWriter out = response.getWriter();
		out.write("ok!");
	}
}
