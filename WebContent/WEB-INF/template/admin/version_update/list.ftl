<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>商家列表 - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	[@flash_message /]

});
function search()
{
	listForm.action="search.jhtml";
	listForm.submit();
};
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 版本更新设置 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="search.jhtml" method="get">
		<div class="bar">
			<div class="buttonWrap">
			<a href="add.jhtml" class="iconButton">
				<span class="addIcon">&nbsp;</span>添加
			</a>
				<a href="javascript:;"  class="iconButton">
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
			<div style="float: right;">
			        <input id="startTime" type="text" name="startTime" value="" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd  HH:mm:ss', maxDate: '#F{$dp.$D(\'endTime\')}'});" />
				     -
					<input id="endTime" type="text" name="endTime" value=""class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd  HH:mm:ss', minDate: '#F{$dp.$D(\'startTime\')}'});" />
					<input type="button" class="button" onclick="search()" value="搜索" />
			</div>
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="name">手机端</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="shortName">版本号</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="address">更新内容</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="linkman">版本时间</a>
				</th>
			</tr>
			[#list page.content as version]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${version.id}" />
					</td>
					<td>
					    [#if version.versionType==0]
					             安卓版本
					    [/#if]
						[#if version.versionType==1]
						IOS 版本
					    [/#if]
					</td>
					<td>
						${version.versionNo}
					</td>
					<td>
						${version.versionContent}
					</td>
					<td>
						${version.versionDate}
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