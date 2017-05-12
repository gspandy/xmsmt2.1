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

	String requestid			= formatString(request.getParameter("requestid"));	

	Map<String, String> result  = ZGTService.paymentQuery(requestid);
	String customernumber		= formatString(result.get("customernumber"));
	String requestidFromYeepay	= formatString(result.get("requestid")); 
	String code 				= formatString(result.get("code"));
	String externalid			= formatString(result.get("externalid")); 
	String amount				= formatString(result.get("amount")); 
	String productname			= formatString(result.get("productname")); 
	String productcat			= formatString(result.get("productcat")); 
	String productdesc			= formatString(result.get("productdesc")); 
	String status				= formatString(result.get("status")); 
	String ordertype			= formatString(result.get("ordertype")); 
	String busitype				= formatString(result.get("busitype")); 
	String orderdate			= formatString(result.get("orderdate")); 
	String createdate			= formatString(result.get("createdate")); 
	String bankid				= formatString(result.get("bankid")); 
	String bankcode				= formatString(result.get("bankcode")); 
	String userno				= formatString(result.get("userno")); 
	String memberno				= formatString(result.get("memberno")); 
	String fee					= formatString(result.get("fee"));
	String name					= formatString(result.get("name")); 
	String lastno				= formatString(result.get("lastno")); 
	String phone				= formatString(result.get("phone")); 
	String cardtype				= formatString(result.get("cardtype")); 
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
<title>订单查询结果</title>
</head>
	<body>	
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" style="border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					订单查询结果
				</th>
		  	</tr>

			<tr >
				<td width="25%" align="left">&nbsp;商户编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="45"  align="left"> <%=customernumber%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">customernumber</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;商户订单号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=requestidFromYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">requestid</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;返回码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=code%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">code</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;易宝流水号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=externalid%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">externalid</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;订单金额</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=amount%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">amount</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;商品名称</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=productname%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">productname</td> 
			</tr> 

			<tr>
				<td width="25%" align="left">&nbsp;商品类别</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=productcat%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">productcat</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;商品描述</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=productdesc%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">productdesc</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;订单状态</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=status%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">status</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;订单类型</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=ordertype%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">ordertype</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;业务类型</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=busitype%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">busitype</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;订单创建时间</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=createdate%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">createdate</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;下单时间</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=orderdate%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">orderdate</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;易宝通道编码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=bankid%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">bankid</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;银行编码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=bankcode%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">bankcode</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;用户标识</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=userno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">userno</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;易宝会员编号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=memberno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">memberno</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;商户手续费</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=fee%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">fee</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;姓名</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=name%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">name</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;手机号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=phone%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">phone</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;卡号后4位</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=lastno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">lastno</td> 
			</tr>

			<tr>
				<td width="25%" align="left">&nbsp;银行卡类型</td>
				<td width="5%"  align="center"> : </td> 
				<td width="35%" align="left"> <%=cardtype%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="30%" align="left">cardtype</td> 
			</tr>

		</table>
	</body>
</html>
