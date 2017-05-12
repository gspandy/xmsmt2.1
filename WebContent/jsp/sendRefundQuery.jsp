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
	String refundrequestid		= formatString(request.getParameter("refundrequestid"));	

	Map<String, String> requestParams = new HashMap<String, String>();
	requestParams.put("orderrequestid", orderrequestid);
	requestParams.put("refundrequestid", refundrequestid);

	Map<String, String> requestResult = ZGTService.refundQuery(requestParams);
	String customernumber			  = formatString(requestResult.get("customernumber"));
	String orderrequestidYeepay		  = formatString(requestResult.get("orderrequestid"));
	String code 					  = formatString(requestResult.get("code"));
	String externalid				  = formatString(requestResult.get("externalid"));
	String refundinfo				  = formatString(requestResult.get("refundinfo"));
	String hmac						  = formatString(requestResult.get("hmac"));
	String msg						  = formatString(requestResult.get("msg"));
	String customError				  = formatString(requestResult.get("customError"));

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
				<td width="50%" align="left"> <%=customernumber%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">customernumber</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;商户订单号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=orderrequestidYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">orderrequestid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">code</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;易宝流水号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=externalid%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">externalid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;退款详情</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=refundinfo%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="25%" align="left">refundinfo</td> 
			</tr>

		</table>

	</body>
</html>
