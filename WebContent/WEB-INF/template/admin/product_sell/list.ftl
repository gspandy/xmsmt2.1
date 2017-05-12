[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>商品销售报表</title>
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
	var $selectAll = $("#selectAll");
	var $ids=$("input[name='ids']");

	[@flash_message /]
    $search.click(function() {
   		  listForm.action="list.jhtml";
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
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 商品销售报表 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list.jhtml" method="get">
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
			选择时间：<input id="pageDate" type="text" name="pageDate" value="${pageDate}" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM'});" />
			<input type="submit" class="button" value="搜索" />
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<span>商品名称</span>
				</th>
				<th>
					<span>规格</span>
				</th>
				<th>
					<span>单位</span>
				</th>
				<th>
					<span>进价</span>
				</th>
				<th>
					<span>出库价</span>
				</th>
				<th>
					<span>销售数量</span>
				</th>
				<th>
					<span>库存</span>
				</th>
				<th>
					<span>运费</span>
				</th>
				<th>
					<span>销售额</span>
				</th>
			</tr>
			[#list page.content as row]
				<tr>
					<td>
						${row_index + 1}
					</td>
					<td>
						${row[0]}
					</td>
					<td>
						${row[1]}
					</td>
					<td>
						${row[2]}
					</td>
					<td>
						${currency(row[3], true)}
					</td>
					<td>
						${currency(row[4], true)}
					</td>
					<td>
						${row[5]}
					</td>
					<td>
						${row[6]}
					</td>
					<td>
						
					</td>
					<td>
						${row[7]}
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