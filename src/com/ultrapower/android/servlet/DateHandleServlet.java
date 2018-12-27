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
 * ���ݴ������ڷ�Χ���ط�Χ�����е�����
 */
public class DateHandleServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(DateHandleServlet.class);
	
	public DateHandleServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	//���ݴ������ڷ�Χ���ط�Χ�����е�����
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String dateStr = null;
			//��ȡҳ�洫������ֵ
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			if(startTime.equals(endTime)){
				dateStr = startTime;
				sentResponse(response, dateStr.getBytes());
			}else{
				//������ʽ��ʱ��
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				//��ȡ���ڣ�����ʼʱ��ͽ���ʱ�䶼��ȡ���ꡢ�¡����������֣�
				//��ʼʱ���ȡ
				String[] stime=startTime.split("-");
				int syear=0;int smonth=0;int sday=0;
				for(int i=0;i<stime.length;i++){
					if(i==0){
						syear = Integer.parseInt(stime[i]);
					}
					if(i==1){
						//���·ݼ�һ��Ϊ������ķ���ʹ��
						smonth = Integer.parseInt(stime[i])-1;
					}
					if(i==2){
						sday = Integer.parseInt(stime[i]);
					}
				}
				//����ʱ���ȡ
				String[] etime=endTime.split("-");
				int eyear=0;int emonth=0;int eday=0;
				for(int i=0;i<etime.length;i++){
					if(i==0){
						eyear = Integer.parseInt(etime[i]);
					}
					if(i==1){
						//���·ݼ�һ��Ϊ������ķ���ʹ��
						emonth = Integer.parseInt(etime[i])-1;
					}
					if(i==2){
						eday = Integer.parseInt(etime[i]);
					}
				}
				//��ѯ�����ӿ�ʼʱ�䵽����ʱ���ڵ��������ڣ�Ȼ��������е����ڣ�ÿ�����ڽ��в�ѯ���£���ֹ�������ڲ�ѯ����ʱ������̫��
				//��ע���·��Ǵ�0-11��������·ݴ����һԭ��
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
		            //ѭ����ÿ��������1
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
		logger.debug("��������Ӧ���ݣ�"+new String(data, "UTF-8"));
	}
}
