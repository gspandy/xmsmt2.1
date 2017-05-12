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

	String ledgerno				= formatString(request.getParameter("ledgerno"));	
	String date					= formatString(request.getParameter("date"));	

	Map<String, String> result	= ZGTService.querySettlement(ledgerno, date);
	String customernumber		= formatString(result.get("customernumber"));
	String code				 	= formatString(result.get("code"));
	String info             	= formatString(result.get("info"));
	String msg					= formatString(result.get("msg"));
	String hmac					= formatString(result.get("hmac"));
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
<title>结算结果查询接口</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					结算结果查询接口
				</th>
		  	</tr>

			<tr>
				<td width="15%" align="left">&nbsp;商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=customernumber%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">customernumber</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">code</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;结算结果信息</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=info%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">info</td> 
			</tr>

		</table>

	</body>
</html>
