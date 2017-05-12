[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>店长申请审核</title>
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
	//var $search = $("#search");
	var $selectAll = $("#selectAll");
	var $ids=$("input[name='ids']");

	[@flash_message /]
    //$search.click(function() {
   	//	  listForm.action="list.jhtml";
	//      listForm.submit();
    // });
	
	
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
	function search(ids,isPass){
		if(ids==null || ids == ""){
		  $('input:checkbox[name=ids]:checked').each(function(i){
		   if(0==i){
		    ids = $(this).val();
		   }else{
		    ids += (","+$(this).val());
		   }
		  });
		}
   		window.location.href="vipInviteList.jhtml?ids="+ids+"&isPass="+isPass;
     }
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 店长申请审核 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="vipInviteList.jhtml" method="get">
		<div>
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
			<input type="button" class="button" id="searchBatchTrue" onClick="search('','true')" value="批量同意" />
			<input type="button" class="button" id="searchBatchFalse" onClick="search('','false')" value="批量拒绝" />
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
					<span>昵称</span>
				</th>
				<th>
					<span>手机号码</span>
				</th>
				<th>
					<span>成交总额</span>
				</th>
				<th>
					<span>操作</span>
				</th>
			</tr>
			[#list page.content as tenantShopkeeperVO]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${tenantShopkeeperVO.id}" />
					</td>
					<td>
						${tenantShopkeeperVO.id}
					</td>
					<td>
						${tenantShopkeeperVO.name}
					</td>
					<td>
						${tenantShopkeeperVO.mobile}
					</td>
					<td>
						${tenantShopkeeperVO.amount}
					</td>
					<td>
						<input type="button" class="button" id="searchTrue" onClick="search('${tenantShopkeeperVO.id}','true')" value="同意" />
						<input type="button" class="button" id="searchFalse" onClick="search('${tenantShopkeeperVO.id}','false')" value="拒绝" />
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