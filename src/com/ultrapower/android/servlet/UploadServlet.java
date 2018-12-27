package com.ultrapower.android.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UploadServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// �ж��ύ�����ı��Ƿ�Ϊ�ļ��ϴ��˵�
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			// ����һ���ļ��ϴ��������
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			Iterator items;
			try {
				// ���������ύ�������ļ�����
				items = upload.parseRequest(request).iterator();
				while (items.hasNext()) {
					FileItem item = (FileItem) items.next();
					if (!item.isFormField()) {
						InputStream in = item.getInputStream();
						int length = 0;
						byte[] buf = new byte[1024];
						System.out.println("��ȡ�ϴ��ļ����ܹ���������" + item.getSize());
						while ((length = in.read(buf)) != -1) {
							System.out.println(length);
						}
						in.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
