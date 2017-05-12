<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" import="java.util.HashMap" import="java.io.File" import="net.wit.service.impl.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%! String formatString(String text){
		return text==null ? "" : text.trim();
	}
%>
<%
	request.setCharacterEncoding("UTF-8");

	String checkDate	= formatString(request.getParameter("checkDate"));
	String orderType	= formatString(request.getParameter("orderType"));
	String fileType		= formatString(request.getParameter("fileType"));
	
	String realPath 	= request.getRealPath("");	//获得当前路径
	String sysPath		= realPath + File.separator + "clearData";

	Map<String, String> result	= ZGTService.getPathOfClearData(checkDate, orderType, fileType, sysPath);
	String customError	= formatString(result.get("customError"));
	String filePath		= formatString(result.get("filePath"));

	if(!"".equals(customError)) {
		out.println("customError: " + customError);
		return;
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>对账文件下载</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					<%=checkDate%> - <%=orderType%> ：对账文件
				</th>
		  	</tr>
			
			<tr>
				<td width="20%" align="left">&nbsp;</td>
				<td width="80%" align="left">&nbsp;</td>
			</tr>

			<tr>
				<td width="20%" align="right">&nbsp;文件路径: </td>
				<td width="80%" align="left"> 
						<%=filePath%> 
				</td>
			</tr>

			<tr>
				<td width="20%" align="left">&nbsp;</td>
				<td width="80%" align="left">&nbsp;</td>
			</tr>

		</table>

	</body>
</html>
