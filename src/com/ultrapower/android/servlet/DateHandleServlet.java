package com.ultrapower.android.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ultrapower.android.util.json.JSONException;
import com.ultrapower.android.util.json.JSONObject;

/**
 * 根据传的日期范围返回范围内所有的日期
 */
public class DateHandleServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(DateHandleServlet.class);
	
	public DateHandleServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	//根据传的日期范围返回范围内所有的日期
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String dateStr = null;
			//获取页面传过来的值
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			if(startTime.equals(endTime)){
				dateStr = startTime;
				sentResponse(response, dateStr.getBytes());
			}else{
				//两个格式化时间
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				//截取日期（将开始时间和结束时间都截取成年、月、日三个数字）
				//开始时间截取
				String[] stime=startTime.split("-");
				int syear=0;int smonth=0;int sday=0;
				for(int i=0;i<stime.length;i++){
					if(i==0){
						syear = Integer.parseInt(stime[i]);
					}
					if(i==1){
						//将月份减一是为了下面的方法使用
						smonth = Integer.parseInt(stime[i])-1;
					}
					if(i==2){
						sday = Integer.parseInt(stime[i]);
					}
				}
				//结束时间截取
				String[] etime=endTime.split("-");
				int eyear=0;int emonth=0;int eday=0;
				for(int i=0;i<etime.length;i++){
					if(i==0){
						eyear = Integer.parseInt(etime[i]);
					}
					if(i==1){
						//将月份减一是为了下面的方法使用
						emonth = Integer.parseInt(etime[i])-1;
					}
					if(i==2){
						eday = Integer.parseInt(etime[i]);
					}
				}
				//查询出来从开始时间到结束时间内的所有日期，然后遍历所有的日期，每个日期进行查询更新，防止所有日期查询更新时数据量太大
				//请注意月份是从0-11（上面的月份处理减一原因）
		        Calendar start = Calendar.getInstance();
		        start.set(syear,smonth,sday);
		        Calendar end = Calendar.getInstance();
		        end.set(eyear,emonth,eday);
		        
		        while(start.compareTo(end) <= 0) {
		        	String dateTime = format.format(start.getTime());
		        	if((dateTime).equals(startTime)){
		        		dateStr = dateTime+ ",";
		        	}else if((dateTime).equals(endTime)){
		        		dateStr += dateTime;
		        	}else{
		        		dateStr += dateTime+",";
		        	}
		            //循环，每次天数加1
		            start.set(Calendar.DATE, start.get(Calendar.DATE) + 1);
		        }
		        sentResponse(response, dateStr.getBytes());
			}
		}catch (JSONException ex) {
			ex.printStackTrace();
			JSONObject jo = new JSONObject().put("error", "JsonValueException");
			sentResponse(response, jo.toString().getBytes());
			return;
		}
	}
	
	private void sentResponse(HttpServletResponse response, byte[] data) throws IOException {
		response.addHeader("Content-Length", "" + data.length);
		OutputStream toClient = response.getOutputStream();
		response.setContentType("application/octet-stream");
		toClient.write(data);
		toClient.flush();
		toClient.close();
		logger.debug("服务器响应数据："+new String(data, "UTF-8"));
	}
}
