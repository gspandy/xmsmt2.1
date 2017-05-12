<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.role.list")} - Powered By rsico -</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	[@flash_message /]

    var $selIds = $("#selIds");

    $selIds.click(function () {
        var ids="";
        $('input[type="checkbox"][name="ids"]:checked').each(
                function() {
                    if ("checked" == $(this).attr("checked")) {
                        ids += $(this).attr('value')+',';
                    }
                }
        );
        parent.window.setSmIds(ids.substr(0, ids.length - 1));
    });
});

</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 短信模板选择器 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="listSel.jhtml" method="get">
		<div class="bar">
			<a href="javascript:;" class="iconButton" id="selIds">
				<span class="addIcon">&nbsp;</span>发送
			</a>
			<div class="buttonWrap">

				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>

                <div class="search">
                    <span id="searchPropertySelect" class="arrow">&nbsp;</span>
                    <input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
                    <button type="submit">&nbsp;</button>
                </div>
                <div class="popupMenu">
                    <ul id="searchPropertyOption">
                        <li>
                            <a href="javascript:;"[#if page.searchProperty == "content"] class="current"[/#if] val="content">短信内容</a>
                        </li>
                    </ul>
                </div>
			</div>
			<div class="menuWrap">
				<div>
                    &nbsp;
				</div>
				<div>
                    &nbsp;
				</div>
			</div>
		</div>
		<div>
		  &nbsp;&nbsp;&nbsp;&nbsp;<font color="red">说明：每次发送人数不可少于100人，否则无法发送邀请</font>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="content">短信内容</a>
				</th>
				<th></th>
				<!--<th><a href="javascript:;" class="sort" name="updateDate">维护时间</a></th>-->
<!--<th><a href="javascript:;" class="sort" name="updateDate">维护时间</a></th> -->
         

			</tr>
			[#list page.content as tenantSmContent]
				<tr>
					<td>
                    <input type="checkbox" name="ids" value="${tenantSmContent.id}" />
					</td>
					<td style="width:60%">
						${tenantSmContent.content}
					</td>
                   <!-- <td style="width: 60%">
                    ${tenantSmContent.updateDate}
                    </td>-->

				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>