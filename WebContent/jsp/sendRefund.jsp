<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" import="java.util.HashMap" import="net.wit.service.impl.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%! String formatString(String text){
		return text==null ? "" : text.trim();
	}
%>
<%
	request.setCharacterEncoding("UTF-8");

	String requestid			= formatString(request.getParameter("requestid"));	
	String orderrequestid		= formatString(request.getParameter("orderrequestid"));	
    String amount				= formatString(request.getParameter("amount"));
    String divideinfo			= formatString(request.getParameter("divideinfo"));
    String confirm				= formatString(request.getParameter("confirm"));
    String memo		  			= formatString(request.getParameter("memo"));       

	Map<String, String> requestParams = new HashMap<String, String>();
	requestParams.put("requestid", 		requestid);
	requestParams.put("orderrequestid", orderrequestid);
	requestParams.put("amount", 		amount);
	requestParams.put("divideinfo", 	divideinfo);
	requestParams.put("confirm", 		confirm);
	requestParams.put("memo", 			memo);

	Map<String, String> requestResult = ZGTService.refund(requestParams);
	String customernumber		= formatString(requestResult.get("customernumber"));
	String requestidFromYeepay	= formatString(requestResult.get("requestid"));
	String code 				= formatString(requestResult.get("code"));
	String refundexternalid		= formatString(requestResult.get("refundexternalid"));
	String hmac					= formatString(requestResult.get("hmac"));
	String msg					= formatString(requestResult.get("msg"));
	String customError			= formatString(requestResult.get("customError"));

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
<title>订单退款结果</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					订单退款结果
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
				<td width="15%" align="left">&nbsp;商户退款请求号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=requestidFromYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">requestid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;易宝退款流水号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=refundexternalid%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">refundexternalid</td> 
			</tr>

		</table>

	</body>
</html>
