[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>内购券领取明细</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $search = $("#search");
	var $exportExcel = $("#exportExcel");
	var $selectAll = $("#selectAll");
	var $ids=$("input[name='ids']");

	[@flash_message /]
    $search.click(function() {
   		  listForm.action="list.jhtml";
	      listForm.submit();
     });
	
	
    $exportExcel.click(function() {
    	listForm.action="exportExcel.jhtml";
	      listForm.submit();
     });
	
	
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
	});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 内购券领取明细 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list.jhtml" method="get">
		<div>
			<div style="margin:10px">
			选择时间：<input id="ticketModifyDate" type="text" name="ticketModifyDate" value="${ticketModifyDate}" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM'});" />
			内购券状态：&nbsp;<select name="ticketStatusParam" id="ticketStatusParam" style="width:140px">
							<option value="">--请选择--</option>
							<option value="nouse"  [#if ("nouse" == ticketStatusParam)] selected[/#if]>未使用</option>
							<option value="recevied" [#if ("recevied" == ticketStatusParam)] selected[/#if] >已领取</option>
							<option value="used" [#if ("used" == ticketStatusParam)] selected[/#if] >已使用</option>
							<option value="expired"  [#if ("expired" == ticketStatusParam)] selected[/#if]>已失效</option>
						</select>
			会员手机：<input type="text" id="shopkeeperMobile" name="shopkeeperMobile" class="text" value="${shopkeeperMobile}"  />
				<input type="button" class="button" id="search" value="查询" />
			</div>
			<div style="margin:10px">
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
			<input type="button" id="exportExcel"  class="button" value="导出领券报表" style="height:28px" />
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="id">序号</a>
				</th>
				<th>
					<span>领券时间</span>
				</th>
				<th>
					<span>领取人手机</span>
				</th>
				<th>
					<span>领取人状态</span>
				</th>
				<th>
					<span>券券状态</span>
				</th>
				<th>
					<span>店长姓名</span>
				</th>
				<th>
					<span>店长手机</span>
				</th>
			</tr>
			[#list page.content as ticket]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${ticket.id}" />
					</td>
					<td>
						${ticket.id}
					</td>
					<td>
						<span title="${ticket.modifyDate?string("yyyy-MM-dd HH:mm:ss")}">${ticket.modifyDate}</span>
					</td>
					<td>
					[#if ticket.member!=null]
						${ticket.member.mobile}
					[/#if]
					</td>
					
					<td>
						[#if ticket.member!=null]
							[#if ticket.member.loginDate ==null ]
								未注册
							[/#if]
							[#if ticket.member.loginDate !=null]
							已注册
							[/#if]
						[/#if]
					</td>
					<td>
						[#if ticket.status == 'nouse']
							未使用
						[/#if]
						[#if ticket.status == 'recevied']
							已领取
						[/#if]
						[#if ticket.status == 'used']
							已使用
						[/#if]
						[#if ticket.status == 'expired']
							已失效
						[/#if]
					</td>
					<td>
						[#if ticket.shopkeeper!=null]
							${ticket.shopkeeper.name}
						[/#if]
					</td>
					<td>
						[#if ticket.shopkeeper!=null]
							${ticket.shopkeeper.mobile}
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