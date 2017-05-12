<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.member.list")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
    <LINK rel=stylesheet type=text/css href="${base}/resources/admin/css/jquery.msgbox.css">
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
    <SCRIPT type=text/javascript src="${base}/resources/admin/js/jquery.dragndrop.min.js"></SCRIPT>
    <SCRIPT type=text/javascript src="${base}/resources/admin/js/jquery.msgbox.min.js"></SCRIPT>

    <style type="text/css">
        .moreTable th {
            width: 80px;
            line-height: 25px;
            padding: 5px 10px 5px 0px;
            text-align: right;
            font-weight: normal;
            color: #333333;
            background-color: #f8fbff;
        }

        .moreTable td {
            line-height: 25px;
            padding: 5px;
            color: #666666;
        }

        .promotion {
            color: #cccccc;
        }
    </style>
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
<input type="hidden" value="" id="memberIds">
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 店长管理 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="search.jhtml" method="post">
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
							<a href="javascript:;"[#if page.searchProperty == "name"] class="current"[/#if] val="name">店长姓名</a>
						</li>
						<li>
							<a href="javascript:;"[#if page.searchProperty == "mobile"] class="current"[/#if] val="mobile">手机</a>
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
					<a href="javascript:;" class="sort" name="username">${message("Member.name")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="email">${message("Member.mobile")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="recommendMember">推荐人姓名</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="openDate">开通店长日期</a>
				</th>
				[#if tenant==null]
				<th>
					<a href="javascript:;" class="sort" name="openDate">企业名称</a>
				</th>
				[/#if]
				<th>
					<a href="javascript:;" class="sort" name="店长Level">店长等级</a>
				</th>
				<th>
					操作
				</th>
			</tr>
			[#if page.total!=0]
			[#list page.content as shopkeeper]
				<tr>
					<td>
						<input type="checkbox" name="ids"  value="${shopkeeper.id}" />
					</td>
					<td>
						${shopkeeper.member.name}
					</td>
					<td>
						${shopkeeper.member.mobile}
					</td>
					<td>
					   [#if shopkeeper.recommendMember!=null]
						${shopkeeper.recommendMember.name}
						[/#if]
					</td>
                     <td>
						${shopkeeper.openDate}
					</td>
					[#if tenant==null]
					<td>
						${shopkeeper.tenant.shortName}
					</td>
					[/#if]
					<td>
						${shopkeeper.vipLevel.levelName}
					</td>
					<td>
						<a href="${base}/admin/tenant_shopkeeper/view.jhtml?id=${shopkeeper.id}">[查看]</a>
					</td>
				</tr>
			[/#list]
			[/#if]
		</table>
		[#if page.total!=0]
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
		[/#if]
	</form>
</body>
</html>