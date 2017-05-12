<%@ page language="java" contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" import="java.util.HashMap" import="net.wit.service.impl.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%! String formatString(String text){
		return text==null ? "" : text.trim();
	}
%>
<%
	request.setCharacterEncoding("UTF-8");

	String requestid			= formatString(request.getParameter("requestid"));	

	Map<String, String> result 	= ZGTService.queryUpdate(requestid);
	String customernumber	 	= formatString(result.get("customernumber"));
	String requestidYeepay	  	= formatString(result.get("requestid"));
	String code 				= formatString(result.get("code"));
	String status				= formatString(result.get("status"));
	String desc					= formatString(result.get("desc"));
	String hmac					= formatString(result.get("hmac"));
	String msg					= formatString(result.get("msg"));
	String customError			= formatString(result.get("customError"));

	if(!"1".equals(code) || !"".equals(customError)) {
		out.println("<br>customError : " + customError);
		out.println("<br><br>code : " + code);
		out.println("<br><br>msg  : " + msg);
		return;
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>账户信息修改查询接口-返回</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">

			<tr>
		  		<th align="center" height="30" colspan="4" bgcolor="#6BBE18">
					账户信息修改查询接口-返回
				</th>
		  	</tr>

			<tr>
				<td width="20%" align="right">商户编号：&nbsp;&nbsp;</td>
				<td width="50%" align="left"> <%=customernumber%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">customernumber</td> 
			</tr>

			<tr>
				<td width="20%" align="right">修改请求号：&nbsp;&nbsp;</td>
				<td width="50%" align="left"> <%=requestidYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">requestid</td> 
			</tr>

			<tr>
				<td width="20%" align="right">返回码：&nbsp;&nbsp;</td>
				<td width="50%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">code</td> 
			</tr>

			<tr>
				<td width="20%" align="right">状态：&nbsp;&nbsp;</td>
				<td width="50%" align="left"> <%=status%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">status</td> 
			</tr>

			<tr>
				<td width="20%" align="right">状态描述：&nbsp;&nbsp;</td>
				<td width="50%" align="left"> <%=desc%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">desc</td> 
			</tr>

		</table>

	</body>
</html>
