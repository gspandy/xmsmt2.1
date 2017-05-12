[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.order.list")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/static/admin/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.dragndrop.min.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $filterSelect = $("#filterSelect");
	var $filterOption = $("#filterOption a");
	var $print = $(".print");
	var $excel = $("#excel");
	var $updateStat = $("#updateStat");
	var $editStat = $("#editStat");
	var $query = $("#query");
	var $view = $("#view");

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
	$updateStat.click(function() {
      var size=$("input[type='checkbox'][name=ids]:checked").length;
   	  if(size==0)
   	  {
   	     alert("请选中一条记录！")
   	  }else{
   	        listForm.action="invoice_management_updateStat.jhtml";
        	listForm.submit();
   	   }
	});
	
	$editStat.click(function() {
      var size=$("input[type='checkbox'][name=ids]:checked").length;
   	  if(size==0)
   	  {
   	     alert("请选中一条记录！")
   	  }else{
   	        listForm.action="invoice_management_editStat.jhtml";
        	listForm.submit();
   	   }
	});
	
	$excel.click(function() {
   	        listForm.action="invoice_management_exportExcel.jhtml";
        	listForm.submit();
	});
	
	 $query.click(function() {
   		  listForm.action="invoice_management_queryByFilterCriteria.jhtml";
	      listForm.submit();
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
	//打印
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
	<form action="${base}/invoice_management_exportExcel" id="exportForm" method="get">
    </form>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 发票管理
	</div>
	<form id="listForm" action="invoice_management_find.jhtml" method="get">
	
		<div>
			<div style="margin:10px">
				选择时间：<input id="createDateParam" type="text" name="createDateParam" value="${createDateParam}" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM'});" />
				订单状态：&nbsp;<select name="orderStatusParam" id="orderStatusParam" style="width:140px">
							<option value="">--请选择--</option>
							<option value="unpaid"  [#if ("unpaid" == orderStatusParam)] selected[/#if]>待支付</option>
							<option value="unshipped" [#if ("unshipped" == orderStatusParam)] selected[/#if] >待发货</option>
							<option value="shipped" [#if ("shipped" == orderStatusParam)] selected[/#if] >已发货</option>
							<option value="apply"  [#if ("apply" == orderStatusParam)] selected[/#if]>退货中</option>
							<option value="refundapply"[#if ("refundapply" == orderStatusParam)] selected[/#if]  >退款中</option>
							<option value="refunded"  [#if ("refunded" == orderStatusParam)] selected[/#if]>已退款</option>
							<option value="accept" [#if ("accept" == orderStatusParam)] selected[/#if] >已签收</option>
							<option value="cancelled"  [#if ("cancelled" == orderStatusParam)] selected[/#if]>交易关闭</option>
							<option value="completed"  [#if ("completed" == orderStatusParam)] selected[/#if]>交易成功</option>
						</select>
				发票状态：&nbsp;<select name="invoiceStatusParam" id="invoiceStatusParam" style="width:140px">
							<option value="">--请选择--</option>
							<option value="no"  [#if ("no" == invoiceStatusParam)] selected[/#if]>未开票</option>
							<option value="yes" [#if ("yes" == invoiceStatusParam)] selected[/#if] >已开票</option>
							<option value="cancel" [#if ("cancel" == invoiceStatusParam)] selected[/#if] >取消开票</option>
						</select>
				<input type="button" class="button" id="query" value="查询" />
			</div>
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
				<input type="button" id="updateStat" class="button" value="确认开票"  style="height:28px;"/>
				<input type="button" id="editStat" class="button" value="取消开票"  style="height:28px;"/>
				<input type="button" id="excel" class="button" value="导出Excel"  style="height:28px;"/>
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
							<a href="javascript:;"[#if page.searchProperty == "sn"] class="current"[/#if] val="sn">订单编号</a>
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
					<span>订单编号</span>
				</th>
				<th>
					<span>订单状态</span>
				</th>
				<th>
					<span>企业名称</span>
				</th>
				<th>
					<span>发票内容</span>
				</th>
				<th>
					<span>发票金额</span>
				</th>
				<th>
					<span>发票抬头</span>
				</th>
				<th>
					<span>发票状态</span>
				</th>
				<th>
					<span>操作</span>
				</th>
			</tr>
			[#list page.content as invoiceManagement]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${invoiceManagement.id}" />
					</td>
					<td>
						${invoiceManagement.order.sn}
					</td>
					<td>
						${invoiceManagement.order.orderStatusName}
					</td>
					<td>
						${invoiceManagement.tenant.name}
					</td>
					<td>
						${invoiceManagement.invoiceContent.content}
					</td>
					<td>
						${invoiceManagement.invoiceValue}
					</td>
					<td>
						${invoiceManagement.invoiceTitle}
					</td>
					<td>
						[#if invoiceManagement.invoiceStat == 'no']未开票[/#if] 
						[#if invoiceManagement.invoiceStat == 'yes']已开票[/#if] 
						[#if invoiceManagement.invoiceStat == 'cancel']已取消开票[/#if]
					</td>
					<td>
						<a href="${base}/admin/order/view.jhtml?id=${invoiceManagement.order.id}&flag=2">[查看]</a>
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