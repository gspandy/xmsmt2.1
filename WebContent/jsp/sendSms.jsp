<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" import="java.util.HashMap" import="net.wit.service.impl.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%! String formatString(String text){
		return text==null ? "" : text.trim();
	}
%>
<%
	request.setCharacterEncoding("UTF-8");

	String orderrequestid		= formatString(request.getParameter("orderrequestid"));	

	Map<String, String> result	= ZGTService.sendSms(orderrequestid);
	String customernumber		= formatString(result.get("customernumber"));
	String orderrequestidYeepay	= formatString(result.get("orderrequestid"));
	String code 				= formatString(result.get("code"));
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
<title>短信验证码发送</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					短信验证码发送接口返回参数
				</th>
		  	</tr>

			<tr>
				<td width="40%" align="right">&nbsp;商户编号[customernumber]：</td>
				<td width="60%" align="left"> <%=customernumber%> </td>
			</tr>

			<tr>
				<td width="40%" align="right">&nbsp;返回码[code]：</td>
				<td width="60%" align="left"> <%=code%> </td>
			</tr>

			<tr>
				<td width="40%" align="right">&nbsp;商户订单号[orderrequestid]：</td>
				<td width="60%" align="left"> <%=orderrequestidYeepay%> </td>
			</tr>

		</table>

	</body>
</html>
