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

	String requestid		= formatString(request.getParameter("requestid"));	
	
	Map<String, String> transferQueryResult = ZGTService.transferQuery(requestid);
	String customernumber	= formatString(transferQueryResult.get("customernumber"));
	String requestidYeepay	= formatString(transferQueryResult.get("requestid")); 
	String code 			= formatString(transferQueryResult.get("code"));
	String ledgerno			= formatString(transferQueryResult.get("ledgerno")); 
	String amount			= formatString(transferQueryResult.get("amount")); 
	String status			= formatString(transferQueryResult.get("status")); 
	String sourceledgerno	= formatString(transferQueryResult.get("sourceledgerno")); 
	String message			= formatString(transferQueryResult.get("message")); 
	String hmac				= formatString(transferQueryResult.get("hmac")); 
	String msg				= formatString(transferQueryResult.get("msg")); 
	String customError		= formatString(transferQueryResult.get("customError"));

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
<title>转账查询</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					转账查询接口
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
				<td width="25%" align="left">&nbsp;转账请求号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=requestidYeepay%> </td>
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

			<tr>
				<td width="25%" align="left">&nbsp;子账户商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=ledgerno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">ledgerno</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;转账金额</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=amount%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">amount</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;转账状态</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=status%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">status</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;sourceledgerno</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=sourceledgerno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">sourceledgerno</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;message</td>
				<td width="5%"  align="center"> : </td> 
				<td width="50%" align="left"> <%=message%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">message</td> 
			</tr>

		</table>

	</body>
</html>
