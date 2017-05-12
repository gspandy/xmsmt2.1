[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title></title>
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
	
	// 提现筛选
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
	
	//筛选
	var $xxx=$("#xxx");
	$xxx.mouseover( function() {
		var $this = $(this);
		var offset = $this.offset();
		var $menuWrap = $this.closest("div.menuWrap");
		var $popupMenu = $menuWrap.children("div.popupMenu");
		$popupMenu.css({left: offset.left, top: offset.top + $this.height() + 2}).show();
		$menuWrap.mouseleave(function() {
			$popupMenu.hide();
		});
	});
	});
	
	function ok(){
		var chk_value =[];
		$('input[name="ids"]:checked').each(function(){
			chk_value.push($(this).val());
		}); 
		 var size=$("input[type='checkbox'][name=ids]:checked").length;
	   	  if(size==0)
	   	  {
	   	     alert("请选中记录！")
	   	  }else{
		     listForm.action="charge_ok.jhtml?ids="+chk_value;
		     listForm.submit();
		  }
		//window.location.href="/admin/charge/charge_ok.jhtml?ids="+chk_value;
	}

</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 分享佣金提现信息 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list.jhtml" method="get">
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
			<div class="buttonWrap">
				<div class="menuWrap">
					<a href="javascript:;" id="xxx" class="button">
						${message("提现状态")}<span class="arrow">&nbsp;</span>
					</a>
					<div class="popupMenu">
						<ul>
							<li>
								<a href="${base}/admin/charge/charge_list.jhtml?status=1">全部</a>
							</li>
							<li>
								<a href="${base}/admin/charge/charge_list.jhtml?status=receiving">申请中</a>
							</li>
							<li>
								<a href="${base}/admin/charge/charge_list.jhtml?status=received">已发放</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<a href="javascript:void(0)" onclick="ok();" id="refreshButton" class="iconButton">
				<span class="refreshIcon">&nbsp;</span>确认发放
			</a>
			<div class="menuWrap">
				<div class="search">
					<span id="searchPropertySelect" class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<div class="popupMenu">
					<ul id="searchPropertyOption">
						<li>
							<a href="javascript:;"[#if page.searchProperty == "name"] class="current"[/#if] val="name">${message("店长名称")}</a>
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
					<span>店长名称</span>
				</th>
				<th>
					<span>分享佣金金额</span>
				</th>
				<th>
					<span>分享佣金结算日期</span>
				</th>
				<th>
					<span>提现状态</span>
				</th>
				<th>
					<span>交易编号</span>
				</th>
				<th>
					<span>${message("admin.common.handle")}</span>
				</th>
			</tr>
			[#list page.content as charge]
				<tr>
					<td>
						<input type="hidden" name="tenantId" value="${charge.tenant.id}" />
						<input type="checkbox" id="ids" name="ids" value="${charge.id}" />
					</td>
					<td>
						${charge.member.name}
					</td>
					<td>
						${charge.charge}
					</td>
					<td>
						${charge.chargeDate}
					</td>
					<td>
						${charge.statusName}
					</td>
					<td>
						${charge.txNo}
					</td>
					[@shiro.hasPermission name = "admin:print"]
					<td>
						<div class="admin_seach">
							<strong>${message("admin.common.choose")}</strong>
							<div class="admin_slist">
								<ul>
									<li><a href="javascript:;" class="print" url="../print/charge.jhtml?id=${charge.id}">${message("admin.charge.chargePrint")}</a></li>
									<li><a href="javascript:;" class="print" url="">${message("admin.charge.shippingPrint")}</a>
										<div class="admin_slist_cont">
											[#list charge.trades as trade]
											<a href="javascript:;" class="print" url="../print/shipping.jhtml?id=${trade.id}">${message("admin.charge.shippingPrint")}-${trade.tenant.name}</a>
											[/#list]
										</div>
									</li>
									<li><a href="javascript:;" class="print" url="../print/product.jhtml?id=${charge.id}">${message("admin.charge.productPrint")}</a></li>
									<li><a href="javascript:;" class="print" url="../print/delivery.jhtml?chargeId=${charge.id}">${message("admin.charge.deliveryPrint")}</a></li>
								</ul>
							</div>
						</div>
					</td>
					[/@shiro.hasPermission]
					<td>
						<a href="charge_detail.jhtml?id=${charge.id}">[${message("admin.common.view")}]</a>
						[#if !charge.expired && charge.chargeStatus == "unconfirmed"]
							<a href="edit.jhtml?id=${charge.id}">[${message("admin.common.edit")}]</a>
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