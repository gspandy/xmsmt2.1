<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.order.view")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $confirmForm = $("#confirmForm");
	var $confirmRefundapplyForm = $("#confirmRefundapplyForm");
	var $completeForm = $("#completeForm");
	var $cancelForm = $("#cancelForm");
	var $confirmButton = $("#confirmButton");
	var $paymentButton = $("#paymentButton");
	var $shippingButton = $("#shippingButton");
	var $completeButton = $("#completeButton");
	var $refundsButton = $("#refundsButton");
	var $returnsButton = $("#returnsButton");
	var $cancelButton = $("#cancelButton");
	var isLocked = false;
	
	[@flash_message /]
	
	// 检查锁定
	function checkLock() {
		if (!isLocked) {
			$.ajax({
				url: "check_lock.jhtml",
				type: "POST",
				data: {id: ${order.id}},
				dataType: "json",
				cache: false,
				success: function(message) {
					if (message.type != "success") {
						$.message(message);
						$confirmButton.prop("disabled", true);
						$shippingButton.prop("disabled", true);
						$completeButton.prop("disabled", true);
						$cancelButton.prop("disabled", true);
						isLocked = true;
					}
				}
			});
		}
	}
	
	// 检查锁定
	checkLock();
	setInterval(function() {
		checkLock();
	}, 10000);
	
	// 确认
	$confirmButton.click(function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.order.confirmDialog")}",
			onOk: function() {
				$confirmForm.submit();
			}
		});
	});
	
	// 取消
	$cancelButton.click(function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.order.cancelDialog")}",
			onOk: function() {
				$cancelForm.submit();
			}
		});
	});
	
	[#if !order.expired && order.orderStatus == "confirmed" && (order.shippingStatus == "unshipped" || order.shippingStatus == "partialShipment")]
		// 发货
		$shippingButton.click(function() {
			$.dialog({
				title: "${message("admin.order.shipping")}",
				[@compress single_line = true]
					content: '
					<form id="shippingForm" action="shipping.jhtml" method="post">
						<input type="hidden" name="token" value="${token}" \/>
						<input type="hidden" name="orderId" value="${order.id}" \/>
						<div style="height: 380px; overflow-x: hidden; overflow-y: auto;">
							<table class="input" style="margin-bottom: 30px;">
								<tr>
									<th>
										${message("Order.sn")}:
									<\/th>
									<td width="300">
										${order.sn}
									<\/td>
									<th>
										${message("admin.common.createDate")}:
									<\/th>
									<td>
										${order.createDate?string("yyyy-MM-dd HH:mm:ss")}
									<\/td>
								<\/tr>
								<tr>
									<th>
										${message("Shipping.shippingMethod")}:
									<\/th>
									<td>
										<select name="shippingMethodId">
											<option value="">${message("admin.common.choose")}<\/option>
											[#list shippingMethods as shippingMethod]
												<option value="${shippingMethod.id}"[#if shippingMethod == order.shippingMethod] selected="selected"[/#if]>${shippingMethod.name}<\/option>
											[/#list]
										<\/select>
									<\/td>
									<th>
										${message("Shipping.deliveryCorp")}:
									<\/th>
									<td>
										<select name="deliveryCorpId">
											[#list deliveryCorps as deliveryCorp]
												<option value="${deliveryCorp.id}"[#if order.shippingMethod?? && deliveryCorp == order.shippingMethod.defaultDeliveryCorp] selected="selected"[/#if]>${deliveryCorp.name}<\/option>
											[/#list]
										<\/select>
									<\/td>
								<\/tr>
								<tr>
									<th>
										${message("Shipping.trackingNo")}:
									<\/th>
									<td>
										<input type="text" name="trackingNo" class="text" maxlength="200" \/>
									<\/td>
									<th>
										${message("Shipping.freight")}:
									<\/th>
									<td>
										<input type="text" name="freight" class="text" maxlength="16" \/>
									<\/td>
								<\/tr>
								<tr>
									<th>
										${message("Shipping.consignee")}:
									<\/th>
									<td>
										<input type="text" name="consignee" class="text" value="${order.consignee}" maxlength="200" \/>
									<\/td>
									<th>
										${message("Shipping.zipCode")}:
									<\/th>
									<td>
										<input type="text" name="zipCode" class="text" value="${order.zipCode}" maxlength="200" \/>
									<\/td>
								<\/tr>
								<tr>
									<th>
										${message("Shipping.area")}:
									<\/th>
									<td>
										<input type="text" name="areaName" class="text" value="${order.areaName}" maxlength="200" \/>
									<\/td>
									<th>
										${message("Shipping.address")}:
									<\/th>
									<td>
										<input type="text" name="address" class="text" value="${order.address}" maxlength="200" \/>
									<\/td>
								<\/tr>
								<tr>
									<th>
										${message("Shipping.phone")}:
									<\/th>
									<td>
										<input type="text" name="phone" class="text" value="${order.phone}" maxlength="200" \/>
									<\/td>
									<th>
										${message("Shipping.memo")}:
									<\/th>
									<td>
										<input type="text" name="memo" class="text" maxlength="200" \/>
									<\/td>
								<\/tr>
							<\/table>
							<table class="input">
								<tr class="title">
									<th>
										${message("ShippingItem.sn")}
									<\/th>
									<th>
										${message("ShippingItem.name")}
									<\/th>
									<th>
										${message("admin.order.productQuantity")}
									<\/th>
									<th>
										${message("OrderItem.shippedQuantity")}
									<\/th>
									<th>
										${message("admin.order.shippingQuantity")}
									<\/th>
								<\/tr>
								[#list order.orderItems as orderItem]
									<tr>
										<td>
											<input type="hidden" name="shippingItems[${orderItem_index}].sn" value="${orderItem.sn}" \/>
											${orderItem.sn}
										<\/td>
										<td width="300">
											<span title="${orderItem.fullName}">${abbreviate(orderItem.fullName, 50, "...")}<\/span>
											[#if orderItem.isGift]
												<span class="red">[${message("admin.order.gift")}]<\/span>
											[/#if]
										<\/td>
										<td>
											${orderItem.quantity}
										<\/td>
										<td>
											${orderItem.shippedQuantity}
										<\/td>
										<td>
											[#if orderItem.product?? && orderItem.product.stock??]
												[#if orderItem.product.stock <= 0 || orderItem.quantity - orderItem.shippedQuantity <= 0]
													<input type="text" name="shippingItems[${orderItem_index}].quantity" class="text" value="0" style="width: 30px;" disabled="disabled" \/>
												[#elseif orderItem.product.stock < orderItem.quantity - orderItem.shippedQuantity]
													<input type="text" name="shippingItems[${orderItem_index}].quantity" class="text shippingItemsQuantity" value="${orderItem.product.stock}" maxlength="9" style="width: 30px;" max="${orderItem.product.stock}" \/>
												[#else]
													<input type="text" name="shippingItems[${orderItem_index}].quantity" class="text shippingItemsQuantity" value="${orderItem.quantity - orderItem.shippedQuantity}" maxlength="9" style="width: 30px;" max="${orderItem.quantity - orderItem.shippedQuantity}" \/>
												[/#if]
											[#else]
												<input type="text" name="shippingItems[${orderItem_index}].quantity" class="text shippingItemsQuantity" value="${orderItem.quantity - orderItem.shippedQuantity}" maxlength="9" style="width: 30px;" max="${orderItem.quantity - orderItem.shippedQuantity}" \/>
											[/#if]
										<\/td>
									<\/tr>
								[/#list]
								<tr>
									<td colspan="6" style="border-bottom: none;">
										&nbsp;
									<\/td>
								<\/tr>
							<\/table>
						<\/div>
					<\/form>',
				[/@compress]
				width: 900,
				modal: true,
				ok: "${message("admin.dialog.ok")}",
				cancel: "${message("admin.dialog.cancel")}",
				onShow: function() {
					$("#areaId").lSelect({
						url: "${base}/common/area.jhtml"
					});
					$.validator.addClassRules({
						shippingItemsQuantity: {
							required: true,
							digits: true
						}
					});
					$("#shippingForm").validate({
						rules: {
							shippingMethodId: "required",
							deliveryCorpId: "required",
							freight: {
								min: 0,
								decimal: {
									integer: 12,
									fraction: ${setting.priceScale}
								}
							},
							consignee: "required",
							zipCode: "required",
							areaId: "required",
							address: "required",
							phone: "required"
						}
					});
				},
				onOk: function() {
					var total = 0;
					$("#shippingForm input.shippingItemsQuantity").each(function() {
						var quantity = $(this).val();
						if ($.isNumeric(quantity)) {
							total += parseInt(quantity);
						}
					});
					if (total <= 0) {
						$.message("warn", "${message("admin.order.shippingQuantityPositive")}");
					} else {
						$("#shippingForm").submit();
					}
					return false;
				}
			});
		});
	[/#if]
	

});
</script>
</head>
<body>
	<form id="confirmForm" action="confirm.jhtml" method="post">
		<input type="hidden" name="id" value="${order.id}" />
	</form>
	<form id="cancelForm" action="cancel.jhtml" method="post">
		<input type="hidden" name="id" value="${order.id}" />
	</form>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.order.view")}
	</div>
	<ul id="tab" class="tab">
		<li>
			<input type="button" value="${message("admin.order.orderInfo")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.order.productInfo")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.order.paymentInfo")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.order.shippingInfo")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.order.refundsInfo")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.order.returnsInfo")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.order.orderLog")}" />
		</li>
		<li>
			<input type="button" value="物流跟踪" />
		</li>
	</ul>
	<table class="input tabContent">
		<tr>
			<td>
				&nbsp;
			</td>
			<td>
				<input type="button" id="confirmButton" class="button" value="${message("admin.order.confirm")}"[#if order.expired || order.orderStatus != "unconfirmed"] disabled="disabled"[/#if] />
				<input type="button" id="shippingButton" class="button" value="${message("admin.order.shipping")}"[#if order.expired || order.orderStatus != "confirmed" || (order.shippingStatus != "unshipped" && order.shippingStatus != "partialShipment") || order.paymentStatus == "refundapply" || order.paymentStatus == "refunded" ||order.paymentStatus=="unpaid"] disabled="disabled"[/#if] />
			</td>
			<td>
				&nbsp;
			</td>
			<td>
				<input type="button" id="cancelButton" class="button" value="${message("admin.order.cancel")}"[#if order.expired || order.orderStatus != "unconfirmed"] disabled="disabled"[/#if] />
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.sn")}:
			</th>
			<td width="360">
				${order.sn}
			</td>
			<th>
				${message("admin.common.createDate")}:
			</th>
			<td>
				${order.createDate?string("yyyy-MM-dd HH:mm:ss")}
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.orderStatus")}:
			</th>
			<td>
				${message("Order.OrderStatus." + order.orderStatus)}
				[#if order.expired]
					<span title="${message("Order.expire")}: ${order.expire?string("yyyy-MM-dd HH:mm:ss")}">(${message("admin.order.hasExpired")})</span>
				[#elseif order.expire??]
					<span title="${message("Order.expire")}: ${order.expire?string("yyyy-MM-dd HH:mm:ss")}">(${message("Order.expire")}: ${order.expire})</span>
				[/#if]
			</td>
			<th>
				${message("Order.paymentStatus")}:
			</th>
			<td>
				${message("Order.PaymentStatus." + order.paymentStatus)}
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.shippingStatus")}:
			</th>
			<td>
				${message("Order.ShippingStatus." + order.shippingStatus)}
			</td>
			<th>
				${message("Member.username")}:
			</th>
			<td>
				${order.member.nickName}
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.amount")}:
			</th>
			<td>
				${currency(order.amountPaid, true)}
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.weight")}:
			</th>
			<td>
				${order.weight}
			</td>
			<th>
				${message("Order.quantity")}:
			</th>
			<td>
				${order.quantity}
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.paymentMethod")}:
			</th>
			<td>
				${order.paymentMethodName}
			</td>
			<th>
				${message("Order.shippingMethod")}:
			</th>
			<td>
				${order.shippingMethodName}
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.consignee")}:
			</th>
			<td>
				${order.consignee}
			</td>
			<th>
				${message("Order.area")}:
			</th>
			<td>
				${order.areaName}
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.address")}:
			</th>
			<td>
				${order.address}
			</td>
			<th>
				${message("Order.zipCode")}:
			</th>
			<td>
				${order.zipCode}
			</td>
		</tr>
		<tr>
			<th>
				${message("Order.phone")}:
			</th>
			<td>
				${order.phone}
			</td>
			<th>
				${message("Order.memo")}:
			</th>
			<td>
				${order.memo}
			</td>
		</tr>
	</table>
	<table class="input tabContent">
		<tr class="title">
			<th>
				${message("OrderItem.sn")}
			</th>
			<th>
				${message("OrderItem.name")}
			</th>
			<th>
				${message("OrderItem.price")}
			</th>
			<th>
				${message("OrderItem.quantity")}
			</th>
			<th>
				${message("OrderItem.shippedQuantity")}
			</th>
			<th>
				${message("OrderItem.returnQuantity")}
			</th>
			<th>
				${message("OrderItem.subtotal")}
			</th>
		</tr>
		[#list order.orderItems as orderItem]
			<tr>
				<td>
					${orderItem.sn}
				</td>
				<td width="400">
					<span title="${orderItem.fullName}">${abbreviate(orderItem.fullName, 50, "...")}</span>
					[#if orderItem.isGift]
						<span class="red">[${message("admin.order.gift")}]</span>
					[/#if]
				</td>
				<td>
				   ${orderItem.price}
				</td>
				<td>
					${orderItem.quantity}
				</td>
				<td>
					${orderItem.shippedQuantity}
				</td>
				<td>
					${orderItem.returnQuantity}
				</td>
				<td>
					${currency(orderItem.subtotal, true)}
				</td>
			</tr>
		[/#list]
	</table>
	<table class="input tabContent">
		<tr class="title">
			<th>
				${message("Payment.sn")}
			</th>
			<th>
				${message("Payment.method")}
			</th>
			<th>
				${message("Payment.paymentMethod")}
			</th>
			<th>
				${message("Payment.amount")}
			</th>
			<th>
				${message("Payment.status")}
			</th>
			<th>
				${message("Payment.paymentDate")}
			</th>
		</tr>
		[#list order.payments as payment]
			<tr>
				<td>
					${payment.sn}
				</td>
				<td>
					${message("Payment.Method." + payment.method)}
				</td>
				<td>
					${(payment.paymentMethod)!"-"}
				</td>
				<td>
					${currency(payment.amount, true)}
				</td>
				<td>
					${message("Payment.Status." + payment.status)}
				</td>
				<td>
					[#if payment.paymentDate??]
						<span title="${payment.paymentDate?string("yyyy-MM-dd HH:mm:ss")}">${payment.paymentDate}</span>
					[#else]
						-
					[/#if]
				</td>
			</tr>
		[/#list]
	</table>
	<table class="input tabContent">
		<tr class="title">
			<th>
				${message("Shipping.sn")}
			</th>
			<th>
				${message("Shipping.shippingMethod")}
			</th>
			<th>
				${message("Shipping.deliveryCorp")}
			</th>
			<th>
				${message("Shipping.trackingNo")}
			</th>
			<th>
				${message("Shipping.consignee")}
			</th>
			<th>
				${message("admin.common.createDate")}
			</th>
		</tr>
		[#list order.shippings as shipping]
			<tr>
				<td>
					${shipping.sn}
				</td>
				<td>
					${shipping.shippingMethod}
				</td>
				<td>
					${shipping.deliveryCorp}
				</td>
				<td>
					${(shipping.trackingNo)!"-"}
				</td>
				<td>
					${shipping.consignee}
				</td>
				<td>
					<span title="${shipping.createDate?string("yyyy-MM-dd HH:mm:ss")}">${shipping.createDate}</span>
				</td>
			</tr>
		[/#list]
	</table>
	<table class="input tabContent">
		<tr class="title">
			<th>
				${message("Refunds.sn")}
			</th>
			<th>
				${message("Refunds.method")}
			</th>
			<th>
				${message("Refunds.paymentMethod")}
			</th>
			<th>
				${message("Refunds.amount")}
			</th>
			<th>
				${message("admin.common.createDate")}
			</th>
		</tr>
		[#list order.refunds as refunds]
			<tr>
				<td>
					${refunds.sn}
				</td>
				<td>
					${message("Refunds.Method." + refunds.method)}
				</td>
				<td>
					${refunds.paymentMethod!"-"}
				</td>
				<td>
					${currency(refunds.amount, true)}
				</td>
				<td>
					<span title="${refunds.createDate?string("yyyy-MM-dd HH:mm:ss")}">${refunds.createDate}</span>
				</td>
			</tr>
		[/#list]
	</table>
	<table class="input tabContent">
		<tr class="title">
			<th>
				${message("Returns.sn")}
			</th>
			<th>
				${message("Returns.shippingMethod")}
			</th>
			<th>
				${message("Returns.deliveryCorp")}
			</th>
			<th>
				${message("Returns.trackingNo")}
			</th>
			<th>
					退货理由
				</th>
				<th>
					退货凭证
				</th>
			<th>
				${message("Returns.shipper")}
			</th>
			<th>
				${message("admin.common.createDate")}
			</th>
		</tr>
		[#list order.returns as returns]
			<tr>
				<td>
					${returns.sn}
				</td>
				<td>
					${(returns.shippingMethod)!"-"}
				</td>
				<td>
					${(returns.deliveryCorp)!"-"}
				</td>
				<td>
					${(returns.trackingNo)!"-"}
				</td>
				<td>
					${(returns.reason)!"-"}
				</td>
				<td>
					[#if returns.img1??]
						<img src="${returns.img1.smallUrl}"/>
					[/#if]	
					[#if returns.img2??]
						<img src="${returns.img2.smallUrl}"/>
					[/#if]

					[#if returns.img3??]
						<img src="${returns.img3.smallUrl}"/>
					[/#if]	
				</td>
				<td>
					${returns.shipper}
				</td>
				<td>
					<span title="${returns.createDate?string("yyyy-MM-dd HH:mm:ss")}">${returns.createDate}</span>
				</td>
			</tr>
		[/#list]
	</table>
	<table class="input tabContent">
		<tr class="title">
			<th>
				${message("OrderLog.type")}
			</th>
			<th>
				${message("OrderLog.operator")}
			</th>
			<th style="width: 54%;">
				${message("OrderLog.content")}
			</th>
			<th>
				${message("admin.common.createDate")}
			</th>
		</tr>
		[#list order.orderLogs as orderLog]
			<tr>
				<td>
					${message("OrderLog.Type." + orderLog.type)}
				</td>
				<td>
					[#if orderLog.operator??]
						${orderLog.operator}
					[#else]
						<span class="green">${message("admin.order.member")}</span>
					[/#if]
				</td>
				<td>
					[#if orderLog.content??]
						<span title="${orderLog.content}">${abbreviate(orderLog.content, 80, "...")}</span>
					[/#if]
				</td>
				<td>
					<span title="${orderLog.createDate?string("yyyy-MM-dd HH:mm:ss")}">${orderLog.createDate}</span>
				</td>
			</tr>
		[/#list]
	</table>
	</table>
		<table class="input tabContent">
		[#if deliveryTrackings['message']??]
			<tr class="title">
				<td align="center">${deliveryTrackings['message']}</td>
			</tr>
		[#else]
			<tr class="title">
			<th>
				物流时间
			</th>
			<th>
				物流地点
			</th>
		</tr>
			[#list deliveryTrackings as deliveryTracking]
					<tr>
						<td>
							${deliveryTracking['time']}
						</td>
						<td>
							${deliveryTracking['context']}
						</td>
					</tr>
				[/#list]
			[/#if]
	</table>
	<table class="input">
		<tr>
			<th>
				&nbsp;
			</th>
			<td>
				<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='returnOrRefundsList.jhtml'" />
			</td>
		</tr>
	</table>
</body>
</html>