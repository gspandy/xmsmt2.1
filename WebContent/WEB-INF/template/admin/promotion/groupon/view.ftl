<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.promotion.edit")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<style type="text/css">
.memberRank label, .productCategory label, .brand label, .coupon label {
	min-width: 120px;
	_width: 120px;
	display: block;
	float: left;
	padding-right: 4px;
	_white-space: nowrap;
}
</style>
<script type="text/javascript">
$().ready(function() {
	var $selectAll = $("#selectAll");
	var $inputForm = $("#listForm");
	var $productTable = $("#productTable");
	var $productTitle = $("#productTitle");
	var $filterSelect = $("#filterSelect");
	var $filterOption = $("#filterOption a");
	var $receiver = $("#listForm a.receiver");
	var $confirmButton = $("#confirmButton");
	var $cancelButton = $("#cancelButton");
	var $finishedButton = $("#finishedButton");
	var $revocationButton = $("#revocationButton");
	var $shippingSelect = $("#shippingSelect");
	var $backButton = $("#backButton");
	var $id = $("#id");
	
	[@flash_message /]

	// 全选
	$selectAll.click( function() {
		var $this = $(this);
		var $enabledIds = $("#listTable input[name='ids']:enabled");
		if ($this.prop("checked")) {
			$enabledIds.prop("checked", true);
		} else {
			$enabledIds.prop("checked", false);
		}
	});
	// 订单筛选
	$filterSelect.mouseover(function() {
		var $this = $(this);
		var offset = $this.offset();
		var $menuWrap = $this.closest("div.menuWrap");
		var $popupMenu = $menuWrap.children("div.popupMenu");
		$popupMenu.css({left: offset.left, top: offset.top + $this.height() + 2}).show();
		$menuWrap.mouseleave(function() {
			$popupMenu.hide();
		});
	});
	
	// 筛选选项
	$filterOption.click(function() {
		var $this = $(this);
		var $dest = $("#" + $this.attr("name"));
		if ($this.hasClass("checked")) {
			$dest.val("");
		} else {
			$dest.val($this.attr("val"));
		}
		$inputForm.submit();
		return false;
	});
	
	//修改地址
	$receiver.click(function() {
		var $this = $(this);
		$.ajax({
			url: "${base}/admin/promotion/groupon/getReceiverList.jhtml",
			type: "GET",
			data: {id:$this.attr("val")},
			dataType: "json",
			cache: false,
			success: function(receiverList) {
				var optionHtml = '';
				$.each(receiverList, function(i, receiver) {
					[@compress single_line = true]
					optionHtml = optionHtml + '
						<tr class="productTr">
							<td><input type="radio" name="receiverId" address="'+receiver.address+'" value="'+receiver.id+'"\/> &nbsp;<\/td>
							<td>' + receiver.consignee + '<\/td>
							<td>' + receiver.areaName + '<\/td>
							<td>' + receiver.address + '<\/td>
							<td>' + receiver.phone + '<\/td>
						<\/tr>';
					[/@compress]
				});
				$.dialog({
					title: "${message("admin.order.shipping")}",
					[@compress single_line = true]
						content: '
							<div style="height: 240px; overflow-x: hidden; overflow-y: auto;">
								<table class="input" style="margin-bottom: 30px;">
									<tr class="title">
										<th style="width:7%;">&nbsp;<\/th>
										<th style="width:15%;">收货人<\/th>
										<th style="width:25%;">区域<\/th>
										<th style="width:33%;">地址<\/th>
										<th style="width:20%;">电话<\/th>
									<\/tr>
									' + optionHtml + '
								<\/table>
							<\/div>',
					[/@compress]
					width: 700,
					modal: true,
					ok: "${message("admin.dialog.ok")}",
					cancel: "${message("admin.dialog.cancel")}",
					onOk: function() {
						var receiverId = $("input[name='receiverId']:checked").val();
						$.ajax({
							cache : false,
							type : "POST",
							url : "updateReceiver.jhtml",
							data : {promotionMemberId:$this.attr("val"),receiverId:receiverId},
							async : false,
							error : function(request) {
								$.message("error", "Connection Error");
							},
							success: function(message) {
								if (message.type == "success") {
									var strong = document.getElementById("address_" + $this.attr("val")); 
									var addr = $("input[name='receiverId']:checked").attr("address");
									strong.innerText = addr;
								} else {
									$.message(message);
								}
							}
						});
					}
				});
			}
		});
	});
	
	//人员确认
	$confirmButton.click(function() {
		$inputForm.attr("action","confirm.jhtml");
		$inputForm.submit();
	});
	
	//人员取消
	$cancelButton.click(function() {
		$inputForm.attr("action","cancel.jhtml");
		$inputForm.submit();
	});
	
	//团购完成
	$finishedButton.click(function() {
		$inputForm.attr("action","finished.jhtml");
		$inputForm.submit();
	});
	
	//团购撤销
	$revocationButton.click(function() {
		$inputForm.attr("action","rescind.jhtml");
		$inputForm.submit();
	});
	
	//返回
	$backButton.click(function() {
		location.href='list.jhtml'
	});
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 查看团购
	</div>
	<form id="listForm" action="edit.jhtml">
		<input type="hidden" id="status" name="status" value="${status}" />
		<input type="hidden" name="id" value="${promotion.id}" />
		<input type="hidden" name="redirect" value="${redirect}" />
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="参与情况" />
			</li>
			<li>
				<input type="button" value="团购信息" />
			</li>
		</ul>
		<div class="tabContent">
			<div class="bar">
				<div class="buttonWrap">
					<a href="javascript:;" id="backButton" class="iconButton">
						${message("admin.common.back")}
					</a>
					<a href="javascript:;" id="refreshButton" class="iconButton">
						<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
					</a>
					<div class="menuWrap">
						<a href="javascript:;" id="filterSelect" class="button">
							${message("admin.order.filter")}<span class="arrow">&nbsp;</span>
						</a>
						<div class="popupMenu">
							<ul id="filterOption" class="check">
								<li>
									<a href="javascript:;" name="status" val="partake" [#if status == "partake"] class="checked"[/#if]>参与中</a>
								</li>
								<li>
									<a href="javascript:;" name="status" val="finished" [#if status == "finished"] class="checked"[/#if]>完成</a>
								</li>
								<li>
									<a href="javascript:;" name="status" val="cancel" [#if status == "cancel"] class="checked"[/#if]>撤销</a>
								</li>
							</ul>
						</div>
					</div>
					<div class="menuWrap">
						<a href="javascript:;" id="pageSizeSelect" class="button">
							${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
						</a>
						<div class="popupMenu">
							<ul id="pageSizeOption">
								<li>
									<a href="javascript:;"[#if page.pageSize == 10] class="current"[/#if] val="1">10</a>
								</li>
								<li>
									<a href="javascript:;"[#if page.pageSize == 20] class="current"[/#if] val="2">20</a>
								</li>
								<li>
									<a href="javascript:;"[#if page.pageSize == 50] class="current"[/#if] val="50">50</a>
								</li>
								<li>
									<a href="javascript:;"[#if page.pageSize == 100] class="current"[/#if] val="100">100</a>
								</li>
							</ul>
						</div>
					</div>
				</div>
				<div class="buttonWrap" style="float:right;">
					<a href="javascript:;" id="confirmButton" class="iconButton">
						<span class="refreshIcon">&nbsp;</span>参团确认
					</a>
					<a href="javascript:;" id="cancelButton" class="iconButton">
						<span class="refreshIcon">&nbsp;</span>撤销参团
					</a>
					<a href="javascript:;" id="finishedButton" class="iconButton">
						<span class="refreshIcon">&nbsp;</span>完成
					</a>
					<a href="javascript:;" id="revocationButton" class="iconButton">
						<span class="refreshIcon">&nbsp;</span>取消
					</a>
					<div class="menuWrap" style="padding-left: 10px;">
						<span>发货方式：
						<select name="shippingMethodId">
							[#list shippingMethods as shippingMethod]
								<option value="${shippingMethod.id}"[#if receiver_index == 0] selected="selected"[/#if]>${shippingMethod.name}</option>
							[/#list]
						</select>
						</span>
					</div>
				</div>
			</div>
			<table id="listTable" class="list">
				<tr>
					<th class="check" width="5%">
						<input type="checkbox" id="selectAll" />
					</th>
					<th width="10%">
						<span>序号</span>
					</th>
					<th width="20%">
						<span>参与人员</span>
					</th>
					<th width="15%">
						<span>数量</span>
					</th>
					<th width="15%">
						<span>状态</span>
					</th>
					<th width="20%">
						<span>收货地址</span>
					</th>
					<th width="15%">
						<span>操作</span>
					</th>
				</tr>
				[#list page.content as promotionmember]
				<tr class="productTr">
					<td class="check">
						<input type="checkbox" name="ids" value="${promotionmember.id}"/>
					</td>
					<td>
						${promotionmember_index}
					</td>
					<td>
						${promotionmember.member.username}
					</td>
					<td>
						${promotionmember.quantity}
					</td>
					<td>
						${message("Promotionmember.Status." + promotionmember.status)}
					</td>
					<td>
						<strong id="address_${promotionmember.id}" style="font-weight:normal">${promotionmember.receiver.address}</strong>
					</td>
					<td>
						[#if promotionmember.status == "partake"]
						<a href="javascript:;" class="receiver" val="${promotionmember.id}">[修改]</a>
						[/#if]
					</td>
				</tr>
				[/#list]
			</table>
			[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
				[#include "/admin/include/pagination.ftl"]
			[/@pagination]
		</div>
		<div class="tabContent">
			<table class="input">
				<tr>
					<th>
						<span class="requiredField">*</span>类型:
					</th>
					<td>
						${message("Promotion.Classify." + promotion.classify)}
					</td>
					<th>
						名称:
					</th>
					<td>
						${promotion.name}
					</td>
				</tr>
				<tr>
					<th>
						标题:
					</th>
					<td colspan="3">
						${promotion.title}
					</td>
				</tr>
				<tr>
					<th>
						开团起始时间:
					</th>
					<td>
						[#if promotion.beginDate??]${promotion.beginDate?string("yyyy-MM-dd HH:mm:ss")}[/#if]
					</td>
					<th>
						团购结束时间:
					</th>
					<td>
						[#if promotion.endDate??]${promotion.endDate?string("yyyy-MM-dd HH:mm:ss")}[/#if]
					</td>
				</tr>
				<tr>
					<th>
						开团最低人数:
					</th>
					<td>
						${promotion.minimumQuantity}
					</td>
					<th>
						团购人数上限:
					</th>
					<td>
						${promotion.maximumQuantity}
					</td>
				</tr>
				<tr>
					<th>
						附赠积分计算:
					</th>
					<td>
						${promotion.pointExpression}
					</td>
					<th>
						排序:
					</th>
					<td>
						${promotion.order}
					</td>
				</tr>
				<tr>
					<th>
						配送方式:
					</th>
					<td colspan="3">
						[#if promotion.shippingMethod??]
						${promotion.shippingMethod.name}
						[/#if]
					</td>
				</tr>
				[#if promotion.brokerage??]
				<tr>
					<th>
						分享佣金比率:
					</th>
					<td colspan="3">
						${promotion.brokerage}
					</td>
				</tr>
				[/#if]
				[#if promotion.tenant??]
				<tr>
					<th>
						开团商家:
					</th>
					<td colspan="3">
						${promotion.tenant.name}
					</td>
				</tr>
				[/#if]
				[#if promotion.member??]
				<tr>
					<th>
						申请会员:
					</th>
					<td colspan="3">
						${promotion.member.username}
					</td>
				</tr>
				[/#if]
			</table>
			<table id="productTable" class="input">
				<tr id="productTitle" class="title[#if !promotion.promotionProducts?has_content] hidden[/#if]">
					<th>
						&nbsp;
					</th>
					<td>
						${message("Product.name")}
					</td>
					<td>
						数量
					</td>
					<td>
						官网价格
					</td>
					<td>
						${message("admin.common.handle")}
					</td>
				</tr>
				[#list promotion.promotionProducts as promoteproduct]
				<tr class="productTr">
					<th>
						&nbsp;
					</th>
					<td>
						<span title="${promoteproduct.product.fullName}">${abbreviate(promoteproduct.product.fullName, 50)}</span>
					</td>
					<td>
						${promoteproduct.quantity}
					</td>
					<td>
						${promoteproduct.price}
					</td>
					<td>
						<a href="${base}${promoteproduct.product.path}" target="_blank">[${message("admin.common.view")}]</a>
					</td>
				</tr>
				[/#list]
			</table>
			<table class="input">
				<tr>
					<th>
						&nbsp;
					</th>
					<td>
						<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml'" />
					</td>
				</tr>
			</table>
		</div>
	</form>
</body>
</html>