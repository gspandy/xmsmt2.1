<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.returns.view")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {
	
	[@flash_message /]
	$(function(){
		var $agreed=$("#agreed");
		$agreed.click(function(){
			if(confirm("是否同意退货?")){
				$("#agreeForm").submit();
			}
		});
		var $reject=$("#reject");
		$reject.click(function(){
			if(confirm("是否拒绝退货?")){
				$("#rejectForm").submit();
			}
		});
		var $returning=$("#returning");
		$returning.click(function(){
			if(confirm("是否确认收货?")){
				$("#returnsForm").submit();
			}
		});	
	});
	
});
</script>
</head>
<body>
	<form id="agreeForm" action="agree.jhtml" method="post">
		<input type="hidden" name="id" value="${returns.id}" />
	</form>
	<form id="rejectForm" action="reject.jhtml" method="post">
		<input type="hidden" name="id" value="${returns.id}" />
	</form>
	<form id="returnsForm" action="../order/confirmReturns.jhtml" method="post">
		<input type="hidden" name="id" value="${returns.id}" />
	</form>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.returns.view")}
	</div>
	<table class="input tabContent">
		<tr>
			<th>
				${message("Returns.sn")}:
			</th>
			<td>
				${returns.sn}
			</td>
			<th>
				${message("admin.common.createDate")}:
			</th>
			<td>
				${returns.createDate?string("yyyy-MM-dd HH:mm:ss")}
			</td>
		</tr>
		<tr>
			<th>
				${message("Returns.trackingNo")}:
			</th>
			<td>
				${(returns.trackingNo)!"-"}
			</td>
			<th>
				${message("Returns.order")}:
			</th>
			<td>
				${returns.order.sn}
			</td>
		</tr>
		<tr>
			<th>
				${message("Returns.shipper")}:
			</th>
			<td>
				${returns.shipper}
			</td>
			<th>
				${message("Returns.phone")}:
			</th>
			<td>
				${returns.phone}
			</td>
		</tr>
		<tr>
			<th>
				${message("Returns.address")}:
			</th>
			<td>
				${returns.address}
			</td>
		</tr>
		<tr>
			<th>
				退货状态:
			</th>
			<td>
				${message("ReturnStatus."+returns.orderStat)}
			</td>
			<th>
				${message("Returns.freight")}:
			</th>
			<td>
				${currency(returns.freight, true)!"-"}
			</td>
		</tr>
		<tr>
			<th>
				下单价格:
			</th>
			<td>
				${returns.price}
			</td>
		</tr>
		<tr>
			<th>
				退货数量:
			</th>
			<td>
				${returns.quatity}
			</td>
			<th>
				退货金额:
			</th>
			<td>
				${currency(returns.amount,true)}
			</td>
		</tr>
		<tr>
			<th>
				退货理由:
			</th>
			<td>
				${returns.reason}
			</td>
			<th>
				${message("Returns.memo")}:
			</th>
			<td>
				${returns.memo}
			</td>
		</tr>
	</table>
	<table class="input">
		<tr>
			<th>
				&nbsp;
			</th>
			<td>
				<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='../order/returnOrRefundsList.jhtml'" />
				[#if returns.orderStat=='apply']
				<input type="button" class="button" value="同意" id="agreed" />
				<input type="button" class="button" value="拒绝" id="reject" />
				[/#if]
				[#if returns.orderStat=='return_ing']
				<input type="submit" class="button" value="确认退货" id="returning" />
				[/#if]
				
			</td>
		</tr>
	</table>
</body>
</html>