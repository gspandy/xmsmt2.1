<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.index.title")} - Powered By z8ls</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no">
<meta name="description" content="">
<meta name="author" content="">
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${base}/static/css/cloud-admin.css" >	
<style type="text/css">
.input .powered {
	font-size: 11px;
	text-align: right;
	color: #cccccc;
}
</style>
</head>
<body>

	<div class="path">
		${message("admin.index.title")}
	</div>
	<table class="input">
		<tr>
			<td colspan="4">
				&nbsp;
			</td>
		</tr>
		<tr>
			<th>
				${message("admin.index.marketableProductCount")}:
			</th>
			<td>
				${marketableProductCount}
			</td>
			<th>
				${message("admin.index.notMarketableProductCount")}:
			</th>
			<td>
				${notMarketableProductCount}
			</td>
		</tr>
		<tr>
			<th>
				${message("admin.index.stockAlertProductCount")}:
			</th>
			<td>
				${stockAlertProductCount} &nbsp;&nbsp;&nbsp;&nbsp;  <font color="red">库存小于5件进入库存报警</font>
			</td>
			<th>
				${message("admin.index.outOfStockProductCount")}:
			</th>
			<td>
				${outOfStockProductCount}
			</td>
		</tr>
		<tr>
			<th>
				${message("admin.index.waitingPaymentOrderCount")}:
			</th>
			<td>
				${waitingPaymentOrderCount}
			</td>
			<th>
				${message("admin.index.waitingShippingOrderCount")}:
			</th>
			<td>
				${waitingShippingOrderCount}
			</td>
		</tr>
		<tr>
			<th>
				${message("admin.index.memberCount")}:
			</th>
			<td>
				${memberCount}
			</td>
			<th>
				${message("admin.index.unreadMessageCount")}:
			</th>
			<td>
				${unreadMessageCount}
			</td>
		</tr>
	</table>
</body>
</html>