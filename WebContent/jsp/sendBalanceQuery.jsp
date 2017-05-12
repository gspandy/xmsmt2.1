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

	Map<String, String> result	= ZGTService.balanceQuery(ledgerno);
	String customernumber		= formatString(result.get("customernumber"));
	String code 				= formatString(result.get("code"));
	String balance				= formatString(result.get("balance")); 
	String ledgerbalance		= formatString(result.get("ledgerbalance")); 
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
<title>余额查询结果</title>
</head>
	<body>	
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
												style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					余额查询结果
				</th>
		  	</tr>

			<tr >
				<td width="25%" align="left">&nbsp;主商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="45"  align="left"> <%=customernumber%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">customernumber</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">code</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;主商编余额</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=balance%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">balance</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;子商编余额</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=ledgerbalance%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">ledgerbalance</td> 
			</tr>

		</table>
	</body>
</html>
