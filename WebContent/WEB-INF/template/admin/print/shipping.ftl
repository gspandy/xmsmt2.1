<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.print.shipping")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<style type="text/css">
.bar {
	height: 30px;
	line-height: 30px;
	border-bottom: 1px solid #d7e8f8;
	background-color: #eff7ff;
}

table {
	width: 100%;
}

table th {
	font-weight: bold;
	text-align: left;
}

table td, table th {
	line-height: 30px;
	padding: 0px 4px;
	font-size: 10pt;
	color: #000000;
}

.separated th, .separated td {
	border-top: 1px solid #000000;
	border-bottom: 1px solid #000000;
}
.Print_table{border:1px solid #ccc;border-right:0;border-bottom:0;}
.Print_table td{border-right:1px solid #ccc;border-bottom:1px solid #ccc;padding:0 5px;text-align:center;font-size:15px;}
.Print_table td h1{font-size:18px;color:#f00;display:inline-block;}
.Print_table tr.Print_even{background:#f7f7f7}
.Print_table tr.Print_left td{text-align:left;}
.Print_table tr td.noboder{border-right:0;}
.Print_table tr td p{font-size:18px;font-weight:bold;min-height:10px}
.Print_table tr.Print_even{background:#f7f7f7}
</style>
<style type="text/css" media="print">
.bar {
	display: none;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $print = $("#print");

	$print.click(function() {
		window.print();
		return false;
	});

});
</script>
</head>
<body>
	<div class="bar">
		<a href="javascript:;" id="print" class="button">打印</a>
	</div>
	<div class="content">
		<table class="Print_table">
			<tr>
				<td colspan="1">
					<img src="${setting.logo}" />
				</td>
				<td colspan="3">
					<p></p>
					<img src="${base}/common/qbarcode.jhtml?contents=${trade.sn}" width="200"/>
					<p>${trade.sn}</p>
				</td>
				<td colspan="2">
					配送方式：${message("admin.shipping." + trade.order.shippingMethod.method)}  ${trade.order.appointment.name}
				</td>
			</tr>
			<tr class="Print_left Print_even">
				<td colspan="3">
					商家名称：${trade.tenant.name}
				</td>
				<td colspan="3">
					客户名称：${trade.order.consignee}
				</td>
			</tr>
			<tr class="Print_left">
				<td colspan="3">商家地址（发货地址）：${trade.tenant.address}</td>
				<td colspan="3">客户地址：${trade.order.address}</td>
			</tr>
			<tr class="Print_left Print_even">
				<td colspan="3">联系电话（非常重要）：<h1>${trade.tenant.telephone}<h1></td>
				<td colspan="3">联系电话（非常重要）：<h1>${trade.order.phone}</h1></td>
			</tr>
			<tr class="Print_left">
				<td colspan="2">商品品名：</td>
				<td>商品件数：${trade.orderItems?size}</td>
				<td colspan="2">送货员签名：</td>
				<td>客户签收：</td>
			</tr>
			<tr class="Print_left">
				<td width="16.6%"></td>
				<td width="16.6%"></td>
				<td width="16.6%"></td>
				<td width="16.6%"></td>
				<td width="16.6%"></td>
				<td width="16.6%"></td>
			</tr>
		</table>
		<div style="width:100%;height:1px;margin:10px auto;border-top:2px dashed #333;"></div>
		<table class="Print_table">
			<tr>
				<td colspan="1">
					<img src="${setting.logo}" />
				</td>
				<td colspan="3">
					<p></p>
					<img src="${base}/common/qbarcode.jhtml?contents=${trade.sn}" width="200"/>
					<p>${trade.sn}</p>
				</td>
				<td colspan="2">
					配送方式：${message("admin.shipping." + trade.order.shippingMethod.method)}  ${trade.order.appointment.name}
				</td>
			</tr>
			<tr>
				<td colspan="1">序号</td>
				<td colspan="3">
					商品名称
				</td>
				<td>
					数量
				</td>
				<td>
					金额（元）
				</td>
			</tr>
			[#list trade.orderItems as item]
			<tr class="Print_even">
				<td colspan="1">${item_index + 1}</td>
				<td colspan="3">
					${item.fullName}
				</td>
				<td>
					${item.quantity}
				</td>
				<td>
					${currency(item.price, true)}
				</td>
			</tr>
			[/#list]
			<tr class="Print_left">
				<td colspan="1">配送费（元）：${currency(trade.freight, true)}</td>
				<td colspan="3">&nbsp;</td>
				<td colspan="2">订单总额（元）：${currency(trade.price, true)}</td>
			</tr>
			<tr class="Print_left">
				<td width="16.6%"></td>
				<td width="16.6%"></td>
				<td width="16.6%"></td>
				<td width="16.6%"></td>
				<td width="16.6%"></td>
				<td width="16.6%"></td>
			</tr>
		</table>
	</div>
</body>
</html>