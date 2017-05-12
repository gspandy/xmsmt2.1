<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>商家员工[#if systemShowPowered] - Powered By rsico[/#if]</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/font.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/account.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/member.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/admin.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/common/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/common/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/common/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/common/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/shop/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $delete = $("#list a.delete");
	
	[@flash_message /]

	// 删除
	$delete.click(function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.deleteConfirm")}",
			onOk: function() {
				$.ajax({
					url: "delete.jhtml",
					type: "POST",
					data: {id: $this.attr("val")},
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							$this.closest("tr").remove();
						}
					}
				});
			}
		});
		return false;
	});

});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 员工列表 <span>(${message("admin.page.total", page.total)})</span>
	</div>
       <div class="page-nav page-nav-app vip-guide-nav" id="app_head_nav">
          <ul class="links" id="mod_menus">
            <li  ><a class="on" hideFocus="" href="${base}/admin/tenant/memberlist.jhtml?id=${id}">员工列表</a></li>
          	<li  ><a class="" hideFocus="" href="${base}/admin/tenant/salesMan.jhtml?id=${id}">业务员</a></li>
          </ul>
    	 </div>
    	 <div class="bar">
		<a href="javascript:;" id="refreshButton" class="iconButton">
			<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
		</a>
	</div>
			<div class="list">
				<table class="list" id="list">
					<tr>
						<th>
							注册日期
						</th>
						<th>
							用户名
						</th>
						<th>
							会员姓名
						</th>
						<th>
							余额
						</th>
						<th>
							积分
						</th>
						<th>
							最近登录时间
						</th>
						<th>
							状态
						</th>
					</tr>
					[#list page.content as member ]
						<tr[#if !credit_has_next] class="last"[/#if]>
							<td>
								<span title="${member.createDate?string("yyyy-MM-dd HH:mm:ss")}">${member.createDate?string("yyyy-MM-dd HH:mm:ss")}</span>
							</td>
							<td>
								${member.username}
							</td>
							<td>
								${member.name}
							</td>
							<td style="text-align:right;">
								${currency(member.balance, true)}
							</td>
							<td style="text-align:right;">
								${member.point}
							</td>
							<td>
								${member.loginDate?string("yyyy-MM-dd HH:mm:ss")!"-"}
							</td>
							<td  style="border-right: 0px">
							  [#if member.isEnabled ]
							     正常
							  [#else]
								   禁用
								[/#if]
							</td>
							
						</tr>
					[/#list]
				</table>
				[#if !page.content?has_content]
					<p>${message("shop.member.noResult")}</p>
				[/#if]
			</div>
			[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "?pageNumber={pageNumber}"]
				[#include "/shop/include/pagination.ftl"]
			[/@pagination]
		</div>
	</div>
</body>
</html>