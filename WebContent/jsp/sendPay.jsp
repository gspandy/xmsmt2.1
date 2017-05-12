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
    String amount				= formatString(request.getParameter("amount"));
    String assure				= formatString(request.getParameter("assure"));
    String productname			= formatString(request.getParameter("productname"));
    String productcat			= formatString(request.getParameter("productcat"));
    String productdesc			= formatString(request.getParameter("productdesc"));
    String divideinfo			= formatString(request.getParameter("divideinfo"));
    String callbackurl			= formatString(request.getParameter("callbackurl"));
    String webcallbackurl		= formatString(request.getParameter("webcallbackurl"));
    String bankid				= formatString(request.getParameter("bankid"));
    String period				= formatString(request.getParameter("period"));
    String memo		  			= formatString(request.getParameter("memo"));       
    String payproducttype		= formatString(request.getParameter("payproducttype"));       
    String userno	  			= formatString(request.getParameter("userno"));       
    String isbind	  			= formatString(request.getParameter("isbind"));       
    String bindid	  			= formatString(request.getParameter("bindid"));       
    String ip		  			= formatString(request.getParameter("ip"));       
    String cardname		  		= formatString(request.getParameter("cardname"));       
    String idcard		  		= formatString(request.getParameter("idcard"));       
    //String idcardtype		  	= formatString(request.getParameter("idcardtype"));       
    String bankcardnum			= formatString(request.getParameter("bankcardnum"));       
    String mobilephone		  	= formatString(request.getParameter("mobilephone"));       
    String cvv2		  			= formatString(request.getParameter("cvv2"));       
    String expiredate		  	= formatString(request.getParameter("expiredate"));       
    String mcc		  			= formatString(request.getParameter("mcc"));       
    String areacode		  		= formatString(request.getParameter("areacode"));       

	Map<String, String> requestParams = new HashMap<String, String>();
	requestParams.put("requestid", 		requestid);
	requestParams.put("amount", 		amount);
	requestParams.put("assure", 		assure);
	requestParams.put("productname", 	productname);
	requestParams.put("productcat", 	productcat);
	requestParams.put("productdesc", 	productdesc);
	requestParams.put("divideinfo", 	divideinfo);
	requestParams.put("callbackurl", 	callbackurl);
	requestParams.put("webcallbackurl", webcallbackurl);
	requestParams.put("bankid",			bankid);
	requestParams.put("period", 		period);
	requestParams.put("memo", 			memo);
	requestParams.put("payproducttype", payproducttype);
	requestParams.put("userno", 		userno);
	requestParams.put("isbind", 		isbind);
	requestParams.put("bindid", 		bindid);
	requestParams.put("ip", 			ip);
	requestParams.put("cardname", 		cardname);
	requestParams.put("idcard", 		idcard);
	//requestParams.put("idcardtype", 	idcardtype);
	requestParams.put("bankcardnum", 	bankcardnum);
	requestParams.put("mobilephone", 	mobilephone);
	requestParams.put("cvv2", 			cvv2);
	requestParams.put("expiredate", 	expiredate);
	requestParams.put("mcc", 			mcc);
	requestParams.put("areacode", 		areacode);

	//out.println("requestParams : " + requestParams + "<br><br>");

	Map<String, String> requestResult = ZGTService.paymentRequest(requestParams);
	String customernumber		= formatString(requestResult.get("customernumber"));
	String requestidFromYeepay	= formatString(requestResult.get("requestid"));
	String code 				= formatString(requestResult.get("code"));
	String externalid			= formatString(requestResult.get("externalid"));
	String amountFromYeepay		= formatString(requestResult.get("amount"));
	String payurl				= formatString(requestResult.get("payurl"));
	String bindidFromYeepay		= formatString(requestResult.get("bindid"));
	String bankcode				= formatString(requestResult.get("bankcode"));
	String cardno				= formatString(requestResult.get("cardno"));
	String cardtype				= formatString(requestResult.get("cardno"));
	String hmac					= formatString(requestResult.get("hmac"));
	String msg					= formatString(requestResult.get("msg"));
	String customError			= formatString(requestResult.get("customError"));

	if(!"1".equals(code) || !"".equals(customError)) {
		out.println("<br>customError : " + customError);
		out.println("<br><br>code : " + code);
		out.println("<br><br>msg  : " + msg);
		return;
	}

	if(!"".equals(payurl)) {
		response.sendRedirect(payurl);
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>4.4订单支付接口-同步返回参数</title>
</head>
	<body>
		<br /> <br />
		<table width="70%" border="0" align="center" cellpadding="5" cellspacing="0" 
							style="word-break:break-all; border:solid 1px #107929">
			<tr>
		  		<th align="center" height="30" colspan="5" bgcolor="#6BBE18">
					4.4订单支付接口-同步返回参数
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
				<td width="15%" align="left">&nbsp;商户订单号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=requestidFromYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">requestid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;易宝流水号</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=externalid%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">externalid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;订单金额</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=amountFromYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">amount</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;易宝绑卡标识</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=bindidFromYeepay%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">bindid</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;银行编码</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=bankcode%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">bankcode</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;卡号后四位</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=cardno%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">cardno</td> 
			</tr>

			<tr>
				<td width="15%" align="left">&nbsp;银行卡类型</td>
				<td width="5%"  align="center"> : </td> 
				<td width="60%" align="left"> <%=cardtype%> </td>
				<td width="5%"  align="center"> - </td> 
				<td width="15%" align="left">cardtype</td> 
			</tr>

			<tr>
				<td width="15%" align="left" rowspan="6">&nbsp;支付链接</td>
				<td width="5%"  align="center" rowspan="6"> : </td> 
				<td width="60%" align="left" rowspan="6"> 
					<a href=<%=payurl%> style="text-decoration:none" target="_blank"> 
						<%=payurl%>
					</a> 
				</td>
				<td width="5%"  align="center" rowspan="6"> - </td> 
				<td width="15%" align="left" rowspan="6">payurl</td> 
			</tr>

		</table>

	</body>
</html>
