<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>付款单 - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/tenant/js/ePayBank.js"></script>
<script type="text/javascript">
$().ready(function() {
	
	var bankInfo = getBankInfo(${(credit.bank)});
  $("#bankName").html(bankInfo.bankname);
	
	[@flash_message /]
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 付款单
	</div>
	<table class="input">
		<tr>
			<th>
				编号:
			</th>
			<td>
				${credit.sn}
			</td>
			<th>
				${message("admin.common.createDate")}:
			</th>
			<td>
				${credit.createDate?string("yyyy-MM-dd HH:mm:ss")}
			</td>
		</tr>
		<tr>
			<th>
				付款类型:
			</th>
			<td>
				${message("Credit.Type." + credit.type)}
			</td>
			<th>
				付款方式:
			</th>
			<td>
				${message("Credit.Method." + credit.method)}
			</td>
		</tr>
		<tr>
			<th>
				银行名称:
			</th>
			<td id="bankName">
				${(credit.bank)!"-"}
			</td>
			<th>
				银行账号:
			</th>
			<td>
				[#if credit.status == "wait"]
				   ${credit.account}
				[#else]
				...(${credit.shortAcct})
				[/#if]
			</td>
		</tr>
		<tr>
			<th>
				账户名称:
			</th>
			<td>
				${(credit.payer)!"-"}
			</td>
			<th>
				金额:
			</th>
			<td>
				${currency(credit.amount, true)}
				[#if credit.fee > 0]
					(手续费: ${currency(credit.fee, true)})
				[/#if]
			</td>
		</tr>
		<tr>
			<th>
				状态:
			</th>
			<td>
				${message("Credit.Status." + credit.status)}
			</td>
			<th>
				付款日期:
			</th>
			<td>
				${(credit.creditDate?string("yyyy-MM-dd HH:mm:ss"))!"-"}
			</td>
		</tr>
		<tr>
			<th>
				申请人:
			</th>
			<td>
				${(credit.member.name)!"-"}
			</td>
			<th>
				操作人:
			</th>
			<td>
				${(credit.operator)!"-"}
			</td>
		</tr>
		<tr>
			<th>
				备注:
			</th>
			<td colspan="3">
				${credit.memo}
			</td>
		</tr>
		<tr>
			<th>
				&nbsp;
			</th>
			<td colspan="3">
				[#if credit.status == "wait"]
				<input type="button" class="button" value="提交银行" onclick="location.href='${base}/admin/credit/paybank.jhtml?id=${credit.id}'" />
				<input type="button" class="button" value="人工汇款" onclick="location.href='${base}/admin/credit/success.jhtml?id=${credit.id}'" />
				<input type="button" class="button" value="撤消" onclick="location.href='${base}/admin/credit/cancel.jhtml?id=${credit.id}'" />
				[/#if]
				[#if credit.status == "wait_success"]
				<input type="button" class="button" value="到账检查" onclick="location.href='${base}/admin/credit/checkbank.jhtml?id=${credit.id}'" />
				[/#if]
				[#if credit.status == "wait_failure"]
				<input type="button" class="button" value="手工退款" onclick="location.href='${base}/admin/credit/failure.jhtml?id=${credit.id}'" />
				[/#if]
				<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml'" />
			</td>
		</tr>
	</table>
</body>
</html>