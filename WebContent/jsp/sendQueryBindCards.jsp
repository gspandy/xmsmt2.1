<%@ page language="java" contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" import="java.util.HashMap" import="net.wit.service.impl.*"%>
<!DOCTYPE html>
<%! String formatString(String text){
		return text==null ? "" : text.trim();
	}
%>
<%
	request.setCharacterEncoding("UTF-8");

	String userno				= formatString(request.getParameter("userno"));	

	Map<String, String> result 	= ZGTService.queryBindCards(userno);
	String customernumber		= formatString(result.get("customernumber"));
	String usernoYeepay			= formatString(result.get("userno"));
	String code 				= formatString(result.get("code"));
	String bindcardlist 		= formatString(result.get("bindcardlist"));
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
	<meta charset="UTF-8">
	<title>绑卡查询接口</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="2" bgcolor="#6BBE18">
					绑卡查询接口
				</th>
		  	</tr>

			<tr>
				<td width="30%" align="right">&nbsp;商户编号[customernumber]:</td>
				<td width="70%" align="left"> <%=customernumber%> </td>
			</tr>

			<tr>
				<td width="30%" align="right">&nbsp;用户标识[userno]:</td>
				<td width="70%" align="left"> <%=usernoYeepay%> </td>
			</tr>

			<tr>
				<td width="30%" align="right">&nbsp;返回码[code]:</td>
				<td width="70%" align="left"> <%=code%> </td>
			</tr>

			<tr>
				<td width="30%" align="right">&nbsp;绑卡列表信息[bindcardlist]:</td>
				<td width="70%" align="left" rowspan="6"> <%=bindcardlist%> </td>
			</tr>

		</table>

	</body>
</html>
