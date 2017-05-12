[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.order.list")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $filterSelect = $("#filterSelect");
	var $filterOption = $("#filterOption a");
	var $print = $(".print");

	[@flash_message /]
	
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
		$listForm.submit();
		return false;
	});
	
	// 打印选择
	$print.on("click", function() {
		var $this = $(this);
		if ($this.attr("url") != "") {
			window.open($this.attr("url"));
		}
	});
	
	//配送方式
	$('.admin_seach').on('mouseover',function(){
		$('.admin_seach .admin_slist').hide();
		$(this).find('.admin_slist').show();
	});
	$('.admin_slist li').on('mouseover',function(){
		$('.admin_slist .admin_slist_cont').hide();
		$(this).find('.admin_slist_cont').show();
		$(this).find('a').addClass('down');
	});
	$('.admin_slist li').on('mouseout',function(){
		$(this).find('a').removeClass('down');
	});
	$('.admin_slist li a').on('click',function(){
		var p=$(this).text();
		$(this).parents('.admin_seach').find('strong').text(p);
	});
	$('.admin_slist_cont span').on('click',function(){
		var p=$(this).text();
		$(this).parents('.admin_seach').find('strong').text(p);
	});
	$('.admin_seach').on('mouseleave',function(){
		$('.admin_seach .admin_slist').hide();
	});
	
	var $selectAll = $("#selectAll");
	var $ids=$("input[name='ids']");
	var $printButton=$("#exportButton");
	// 全选
	$selectAll.click( function() {
		var $this = $(this);
		var $enabledIds = $("#listTable input[name='ids']:enabled");
		if ($this.prop("checked")) {
			$enabledIds.prop("checked", true);
			if ($enabledIds.filter(":checked").size() > 0) {
				$printButton.removeClass("disabled");
			} else {
				$deleteButton.addClass("disabled");
			}
		} else {
			$enabledIds.prop("checked", false);
			$printButton.addClass("disabled");
		}
	});
		
	// 选择
	$ids.click( function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$this.closest("tr").addClass("selected");
			$printButton.removeClass("disabled");
		} else {
			$this.closest("tr").removeClass("selected");
			if ($("#listTable input[name='ids']:enabled:checked").size() > 0) {
				$printButton.removeClass("disabled");
			} else {
				$printButton.addClass("disabled");
			}
		}
	});
	$printButton.click(function(){
		var $this =$(this);
		if($this.hasClass("disabled")){
			return false;
		}
		
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "是否导出Excel?",
			ok: message("admin.dialog.ok"),
			cancel: message("admin.dialog.cancel"),
			onOk: function() {
				$checkedIds.each(function(){
					$("#exportForm").append('<input type="hidden" name="ids" value="'+$(this).val()+'">');
				});
				$("#exportForm").submit();
			}
		});
		
	});
});
</script>
</head>
<body>
	 <form action="${base}/admin/order/exportExcel.jhtml" id="exportForm" method="post">
    </form>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 已发货订单 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="shippedList.jhtml" method="get">
		<input type="hidden" id="orderStatus" name="orderStatus" value="${orderStatus}" />
		<input type="hidden" id="paymentStatus" name="paymentStatus" value="${paymentStatus}" />
		<input type="hidden" id="shippingStatus" name="shippingStatus" value="${shippingStatus}" />
		<input type="hidden" id="hasExpired" name="hasExpired" value="[#if hasExpired??]${hasExpired?string("true", "false")}[/#if]" />
		<div class="bar">
			<div class="buttonWrap">
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div class="menuWrap">
					<a href="javascript:;" id="pageSizeSelect" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<div class="popupMenu">
						<ul id="pageSizeOption">
							<li>
								<a href="javascript:;"[#if page.pageSize == 10] class="current"[/#if] val="10">10</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 20] class="current"[/#if] val="20">20</a>
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
			<div class="menuWrap">
				<div class="search">
					<span id="searchPropertySelect" class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<div class="popupMenu">
					<ul id="searchPropertyOption">
						<li>
							<a href="javascript:;"[#if page.searchProperty == "sn"] class="current"[/#if] val="sn">${message("Order.sn")}</a>
						</li>
					</ul>
				</div>
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="sn">${message("Order.sn")}</a>
				</th>
				<th>
					<span>运单编号</span>
				</th>
				<th>
					<span>订单金额</span>
				</th>
				<th>
					<span>物流公司</span>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="member">会员</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="consignee">收货人</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="paymentMethodName">支付方式</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="shippingMethodName">配送方式</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="orderStatus">订单状态</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createDate">创建日期</a>
				</th>
				[@shiro.hasPermission name = "admin:print"]
					<th>
						<span>${message("admin.order.print")}</span>
					</th>
				[/@shiro.hasPermission]
				<th>
					<span>${message("admin.common.handle")}</span>
				</th>
			</tr>
			[#list page.content as order]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${order.id}" />
					</td>
					<td>
						${order.sn}
					</td>
					<td>
						[#list order.shippings as shipping]
							${shipping.trackingNo}
						[/#list]
					</td>
					<td>
						${currency(order.amountPaid, true)}
					</td>
					<td>
						[#list order.shippings as shipping]
							${shipping.deliveryCorp}
						[/#list]
					</td>
					<td>
						${order.member.nickName}
					</td>
					<td>
						${order.consignee}
					</td>
					<td>
						${order.paymentMethodName}
					</td>
					<td>
						${order.shippingMethodName}
					</td>
					<td>
						${order.orderStatusName}
					</td>
					<td>
						<span title="${order.createDate?string("yyyy-MM-dd HH:mm:ss")}">${order.createDate}</span>
					</td>
					[@shiro.hasPermission name = "admin:print"]
					<td>
						<div class="admin_seach">
							<strong>${message("admin.common.choose")}</strong>
							<div class="admin_slist">
								<ul>
									<li><a href="javascript:;" class="print" url="../print/order.jhtml?id=${order.id}">${message("admin.order.orderPrint")}</a></li>
									<li><a href="javascript:;" class="print" url="">${message("admin.order.shippingPrint")}</a>
										<div class="admin_slist_cont">
											[#list order.trades as trade]
											<a href="javascript:;" class="print" url="../print/shipping.jhtml?id=${trade.id}">${message("admin.order.shippingPrint")}-${trade.tenant.name}</a>
											[/#list]
										</div>
									</li>
									<li><a href="javascript:;" class="print" url="../print/product.jhtml?id=${order.id}">${message("admin.order.productPrint")}</a></li>
									<li><a href="javascript:;" class="print" url="../print/delivery.jhtml?orderId=${order.id}">${message("admin.order.deliveryPrint")}</a></li>
								</ul>
							</div>
						</div>
					</td>
					[/@shiro.hasPermission]
					<td>
						<a href="viewShipped.jhtml?id=${order.id}">[${message("admin.common.view")}]</a>
						[#if !order.expired && order.orderStatus == "unconfirmed"]
							<a href="edit.jhtml?id=${order.id}">[${message("admin.common.edit")}]</a>
						[#else]
							<!--<span title="${message("admin.order.editNotAllowed")}">[${message("admin.common.edit")}]</span>-->
						[/#if]
					</td>
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>