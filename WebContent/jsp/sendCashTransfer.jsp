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

	String requestid			= formatString(request.getParameter("requestid"));	
	String ledgerno				= formatString(request.getParameter("ledgerno"));	
	String amount				= formatString(request.getParameter("amount"));	
	String callbackurl			= formatString(request.getParameter("callbackurl"));	

	Map<String, String> params	= new HashMap<String, String>();
	params.put("requestid",   requestid);
	params.put("ledgerno", 	  ledgerno);
	params.put("amount", 	  amount);
	params.put("callbackurl", callbackurl);

	Map<String, String> result 	= ZGTService.cashTransfer(params);
	String customernumber		= formatString(result.get("customernumber"));
	String requestidYeepay		= formatString(result.get("requestid"));
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
	<meta charset="UTF-8">
	<title>提现接口同步返回</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					提现接口同步返回
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
				<td width="15%" align="left">&nbsp;商户订单号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=requestidYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">requestid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">code</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;返回码</td>
				<td width="60%" align="left" rowspan="6"> <%=bindcardlist%></td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">bindcardlist</td> 
			</tr>

		</table>

	</body>
</html>
