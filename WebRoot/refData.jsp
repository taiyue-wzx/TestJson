<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String result1 = (String)request.getAttribute("result1");
	if(null==result1){
		result1="";
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	
	function btn_click() {
        //创建XMLHttpRequest对象
        var xmlHttp = new XMLHttpRequest();
        //获取值
        var username = document.getElementById("update1").value;
        //配置XMLHttpRequest对象
        xmlHttp.open("get", "<%=path%>/UpdateDataServlet");
        //设置回调函数
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                document.getElementById("result2").innerHTML = xmlHttp.responseText;
            }
        }
        //发送请求
        xmlHttp.send(null);
    }
</script>
<title>Insert title here</title>
</head>
	<body>
		<div style="font-family:'宋体';font-size:12px;color:#333;">
			<div style="font-family:'宋体';font-size:12px;color:#333;font-weight:bold;">城市名称更新：</div>
			<form name="form1"></form>
			<form name="form2" method="post" ENCTYPE="multipart/form-data">
				<table>
					
					<tr>
						<td align="right"><input id = "update1" type="button" onclick = "btn_click();" value="确定" /></td>
						<td align="right"><div id="result2"/></td>
					</tr>
				</table> 
			</form>
		</div>
	</body>
</html>