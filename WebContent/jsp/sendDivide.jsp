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
    String orderrequestid	  	= formatString(request.getParameter("orderrequestid"));       
    String divideinfo			= formatString(request.getParameter("divideinfo"));

	Map<String, String> divideParams = new HashMap<String, String>();
	divideParams.put("requestid", 	 requestid);
	divideParams.put("orderrequestid", orderrequestid);
	divideParams.put("divideinfo", 	 divideinfo);

	ZGTService.divide(divideParams);

	Map<String, String> divideResult = ZGTService.divide(divideParams);
	String customernumber		= formatString(divideResult.get("customernumber"));
	String requestidFromYeepay	= formatString(divideResult.get("requestid"));
	String code 				= formatString(divideResult.get("code"));
	String hmac					= formatString(divideResult.get("hmac"));
	String msg					= formatString(divideResult.get("msg"));
	String customError			= formatString(divideResult.get("customError"));

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
<title>分账结果</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					分账返回参数
				</th>
		  	</tr>

			<tr>
				<td width="25%" align="left">&nbsp;主账户商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=customernumber%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">customernumber</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;分账请求号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=requestidFromYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">requestid</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">code</td> 
			</tr>

		</table>

	</body>
</html>
