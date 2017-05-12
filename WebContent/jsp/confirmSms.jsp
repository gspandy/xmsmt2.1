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
	String smscode				= formatString(request.getParameter("smscode"));	

	Map<String, String> result	= ZGTService.confirmSms(orderrequestid, smscode);
	String customernumber		= formatString(result.get("customernumber"));
	String orderrequestidYeepay	= formatString(result.get("orderrequestid"));
	String code 				= formatString(result.get("code"));
	String amount 				= formatString(result.get("amount"));
	String bindid 				= formatString(result.get("bindid"));
	String bankcode 			= formatString(result.get("bankcode"));
	String cardno 				= formatString(result.get("cardno"));
	String cardtype 			= formatString(result.get("cardtype"));
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
<title>短信验证码确认接口</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					短信验证码确认接口返回参数
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

			<tr>
				<td width="40%" align="right">&nbsp;订单金额[amount]：</td>
				<td width="60%" align="left"> <%=amount%> </td>
			</tr>

			<tr>
				<td width="40%" align="right">&nbsp;绑卡ID[bindid]：</td>
				<td width="60%" align="left"> <%=bindid%> </td>
			</tr>

			<tr>
				<td width="40%" align="right">&nbsp;银行编码[bankcode]：</td>
				<td width="60%" align="left"> <%=bankcode%> </td>
			</tr>

			<tr>
				<td width="40%" align="right">&nbsp;卡号后四位[cardno]：</td>
				<td width="60%" align="left"> <%=cardno%> </td>
			</tr>

			<tr>
				<td width="40%" align="right">&nbsp;银行卡类型[cardtype]：</td>
				<td width="60%" align="left"> <%=cardtype%> </td>
			</tr>

		</table>

	</body>
</html>
